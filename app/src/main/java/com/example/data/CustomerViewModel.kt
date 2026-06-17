package com.example.data

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = CustomerRepository(database.customerDao())

    // All active customers observed from Database
    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All notification logs
    val logs: StateFlow<List<NotificationLog>> = repository.allNotificationLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Selected Customer for Details
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    // AI Report Generation Status
    private val _aiReportText = MutableStateFlow<String>("")
    val aiReportText: StateFlow<String> = _aiReportText.asStateFlow()

    private val _isGeneratingReport = MutableStateFlow(false)
    val isGeneratingReport: StateFlow<Boolean> = _isGeneratingReport.asStateFlow()

    init {
        // Populate initial sample customers if DB is totally empty
        viewModelScope.launch {
            repository.allCustomers.first().let { list ->
                if (list.isEmpty()) {
                    populateSampleData()
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
        _aiReportText.value = "" // Reset past report
        navigateTo(Screen.CustomerDetail(customer))
    }

    // CRUD Customer
    fun addCustomer(name: String, phone: String, preferredAlcohol: String, visitCount: Int, totalSpending: Long, lastVisitDaysAgo: Int, notes: String, isVip: Boolean) {
        viewModelScope.launch {
            val visitTimestamp = System.currentTimeMillis() - (lastVisitDaysAgo * 24 * 60 * 60 * 1000L)
            val newCustomer = Customer(
                name = name,
                phone = phone,
                preferredAlcohol = preferredAlcohol,
                visitCount = visitCount,
                totalSpending = totalSpending,
                lastVisitDate = visitTimestamp,
                notes = notes,
                isVip = isVip
            )
            repository.insertCustomer(newCustomer)
            runAutoAlertDetector() // Run detector whenever data changes
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            // Update selected customer pointer if viewing
            if (_selectedCustomer.value?.id == customer.id) {
                _selectedCustomer.value = customer
            }
            runAutoAlertDetector()
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (_selectedCustomer.value?.id == customer.id) {
                _selectedCustomer.value = null
                navigateTo(Screen.CustomerList)
            }
            runAutoAlertDetector()
        }
    }

    // AI Personalized Report generator using Gemini API
    fun generateAiReportForCustomer(customer: Customer) {
        viewModelScope.launch {
            _isGeneratingReport.value = true
            _aiReportText.value = "AI가 국순당 고객님의 마케팅 요약 및 맞춤형 편지를 작성하는 중..."

            val systemInstruction = """
                당신은 아가씨와 선비들이 가득한 전통적인 한국 전통주 매장 '국순당'의 마케팅 전략 수석 컨설턴트 및 점장입니다.
                고객의 데이터(방문 횟수, 총 결제 금액, 마지막 방문 일자, 선호하는 국순당 술종 등)를 분석하여 깊이 있고 매력적인 한글 보고서 및 1:1 개인화된 국순당만의 따뜻한 카카오톡 맞춤 메시지 템플릿(전통적인 격식 조)을 한 번에 작성해야 합니다.
                
                출력 형식은 다음과 같은 3개의 깔끔하게 분리된 섹션으로 정비된 한국어로 작성하세요:
                1. [고객 행동 분석 및 인사이트] (친근하면서도 전문적인 톤)
                2. [맞춤 제안 전통주 및 추천 가치] (선택한 국순당 전통주와 매칭되는 최상의 가치 추천)
                3. [카카오톡 전송용 추천 안부 편지 템플릿] (복사해서 바로 전송할 수 있도록 존댓말로 작성된 친근하고 예의바른 감동적인 메시지)
                
                존엄하고 품격있는 전통주 브랜드 국순당의 톤앤매너를 지켜주세요.
            """.trimIndent()

            val lastVisitedDays = ((System.currentTimeMillis() - customer.lastVisitDate) / (24 * 3600 * 1000L)).coerceAtLeast(0)
            val prompt = """
                아래 고객의 데이터를 완벽하게 분석하여 맞춤 보고서와 카카오톡 안부 편지를 작성해줘:
                - 고객 이름: ${customer.name}
                - 연락처: ${customer.phone}
                - 주로 즐겨찾는 국순당 술: ${customer.preferredAlcohol}
                - 총 방문 횟수: ${customer.visitCount}회
                - 총 구매 액수: ${customer.totalSpending}원
                - 마지막 방문: ${lastVisitedDays}일 전
                - 메모 사항: ${customer.notes}
                - VIP 여부: ${if (customer.isVip) "VIP 고객님" else "일반 회원"}
            """.trimIndent()

            val result = GeminiClient.generateReport(prompt, systemInstruction)
            _aiReportText.value = result
            _isGeneratingReport.value = false
        }
    }

    // Auto data-driven Alert detector (자동 알림 발송 서비스)
    fun runAutoAlertDetector() {
        viewModelScope.launch {
            val customerList = customers.value
            val currentLogs = logs.value
            val now = System.currentTimeMillis()

            for (c in customerList) {
                val lastVisitedDays = ((now - c.lastVisitDate) / (24 * 3600 * 1000L)).coerceAtLeast(0)

                // 1. Dormant Warning (휴면 30일 경과)
                if (lastVisitedDays >= 30) {
                    val logType = "DORMANT"
                    val message = "장기 무방문 고객 케어: ${c.name} 고객님의 마지막 방문일이 ${lastVisitedDays}일 전입니다. 맞춤 안부 카카오톡을 보내 분위기를 복돋아보세요!"
                    val exists = currentLogs.any { it.customerId == c.id && it.type == logType }
                    if (!exists) {
                        repository.insertNotificationLog(
                            NotificationLog(
                                customerId = c.id,
                                customerName = c.name,
                                type = logType,
                                message = message
                            )
                        )
                    }
                }

                // 2. VIP Candidate Anniversary (방문 10회 돌파 혹은 30만 원 이상 구매자인데 일반 등급인 경우)
                if ((c.visitCount >= 10 || c.totalSpending >= 300000) && !c.isVip) {
                    val logType = "VIP_ANNI"
                    val message = "VIP 등급 승급 추천: ${c.name} 고객님이 방문 횟수 ${c.visitCount}회, 총 구매 ${c.totalSpending}원을 달성하셨습니다. VIP 감사 선물권 제안 및 승급해 주세요!"
                    val exists = currentLogs.any { it.customerId == c.id && it.type == logType }
                    if (!exists) {
                        repository.insertNotificationLog(
                            NotificationLog(
                                customerId = c.id,
                                customerName = c.name,
                                type = logType,
                                message = message
                            )
                        )
                    }
                }

                // 3. Special Preference Drink Promo (백세주나 려 처럼 프리미엄 전통주 매니아를 향한 추천 알림)
                if (c.totalSpending >= 150000 && (c.preferredAlcohol == "백세주" || c.preferredAlcohol == "려" || c.preferredAlcohol == "자양백세주")) {
                    val logType = "PREFERENCE_PROMO"
                    val message = "프리미엄 전통주 매니아 혜택 알림: ${c.preferredAlcohol} 선호 고객인 ${c.name}님께 국순당 특별 전용 굿즈(도자기 잔) 증정 메시지를 제안합니다."
                    val exists = currentLogs.any { it.customerId == c.id && it.type == logType }
                    if (!exists) {
                        repository.insertNotificationLog(
                            NotificationLog(
                                customerId = c.id,
                                customerName = c.name,
                                type = logType,
                                message = message
                            )
                        )
                    }
                }
            }
        }
    }

    // Trigger standard Android status bar notification & log update
    fun triggerSystemNotification(context: Context, log: NotificationLog) {
        viewModelScope.launch {
            val updatedLog = log.copy(sentStatus = "NOTIFIED")
            repository.updateNotificationLog(updatedLog)

            withContext(Dispatchers.Main) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "sool_crm_alerts"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "국순당 CRM 알림 발송 서비스",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "국순당 매장 데이터 기반 자동 마케팅 경보 알림 채널"
                    }
                    notificationManager.createNotificationChannel(channel)
                }

                val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentTitle("[국순당 자동 알림] ${log.customerName}님 대상")
                    .setContentText(log.message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(log.message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                notificationManager.notify(log.id.toInt(), notificationBuilder.build())
            }
        }
    }

    // Update log status to SENT KAKAOTALK
    fun markLogAsSentKakao(log: NotificationLog) {
        viewModelScope.launch {
            repository.updateNotificationLog(log.copy(sentStatus = "COMPLETED"))
        }
    }

    fun dismissLog(id: Long) {
        viewModelScope.launch {
            repository.deleteNotificationLogById(id)
        }
    }

    fun clearAllNotificationLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
        }
    }

    private suspend fun populateSampleData() {
        val now = System.currentTimeMillis()
        val samples = listOf(
            Customer(
                name = "김태희",
                phone = "010-1234-5678",
                preferredAlcohol = "백세주",
                visitCount = 12,
                totalSpending = 380000,
                lastVisitDate = now - (5 * 24 * 60 * 60 * 1000L), // 5일 전
                notes = "매주 금요일 저녁 한옥 사랑방 예약 손님. 백세주를 매우 사랑하시며 어르신 모시고 자주 내방.",
                isVip = true
            ),
            Customer(
                name = "이철수",
                phone = "010-8765-4321",
                preferredAlcohol = "국순당 생막걸리",
                visitCount = 8,
                totalSpending = 112000,
                lastVisitDate = now - (35 * 24 * 60 * 60 * 1000L), // 35일 전 (휴면 조건 충족)
                notes = "비오는 날 비 정기 방문. 해물파전과 생막걸리 세트 항상 주문.",
                isVip = false
            ),
            Customer(
                name = "박민지",
                phone = "010-3333-7777",
                preferredAlcohol = "려 (고구마 증류소주)",
                visitCount = 11,
                totalSpending = 450000,
                lastVisitDate = now - (2 * 24 * 60 * 60 * 1000L), // 2일 전
                notes = "비즈니스 소모임 회장님. 프리미엄 증류소주 려 매니아. 깔끔한 안주류와 깊은 전통 대화 선호.",
                isVip = false // VIP 대상자 알림 충족용
            ),
            Customer(
                name = "최영섭",
                phone = "010-9999-8888",
                preferredAlcohol = "100억 프리바이오 막걸리",
                visitCount = 3,
                totalSpending = 480000,
                lastVisitDate = now - (12 * 24 * 60 * 60 * 1000L), // 12일 전
                notes = "웰빙 건강에 극단적 관심. 100억 프리바이오 막걸리 상자 단위로 구입 및 포장.",
                isVip = true
            ),
            Customer(
                name = "정소윤",
                phone = "010-4444-2222",
                preferredAlcohol = "자양백세주",
                visitCount = 5,
                totalSpending = 180000,
                lastVisitDate = now - (42 * 24 * 60 * 60 * 1000L), // 42일 전 (휴면 조건 충족)
                notes = "전통 한약재 향에 선호도 높은 단골 후보. 예의 바르고 항상 구석 테이블 애용.",
                isVip = false
            )
        )

        for (s in samples) {
            repository.insertCustomer(s)
        }
        runAutoAlertDetector()
    }
}

// Sealed screen structures for state-driven navigation
sealed class Screen {
    object Dashboard : Screen()
    object CustomerList : Screen()
    data class CustomerDetail(val customer: Customer) : Screen()
    object AutoNotifications : Screen()
}
