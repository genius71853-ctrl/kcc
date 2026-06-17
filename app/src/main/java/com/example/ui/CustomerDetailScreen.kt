package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Customer
import com.example.data.CustomerViewModel
import com.example.data.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customer: Customer,
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val aiReportText by viewModel.aiReportText.collectAsState()
    val isGeneratingReport by viewModel.isGeneratingReport.collectAsState()

    val lastVisitedDays = ((System.currentTimeMillis() - customer.lastVisitDate) / (24 * 3600 * 1000L)).coerceAtLeast(0)

    val defaultKakaoTemplate = """
        [국순당 안부 편지]
        안녕하세요, ${customer.name} 고객님!
        국순당 매장의 점장입니다. 
        
        고객님께서 즐겨 찾으시는 향긋한 '${customer.preferredAlcohol}'이 정성스럽게 칠링되어 보관 보드에 대기 중입니다. 비 오는 날 저녁이나 따뜻한 안주가 생각나실 때 편안히 예약 방문해 주세요. 
        
        대단히 감사드리며, 오늘도 건강한 하루 보내십시오.
        - 국순당 올림
    """.trimIndent()

    val reportToShare = aiReportText.ifEmpty { defaultKakaoTemplate }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "고객 상세 대장", 
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF1A1A1A)
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.CustomerList) },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "이전",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDFBF7)
                ),
                modifier = Modifier.border(width = (0.5).dp, color = Color(0xFFE8E2D9))
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFBF7))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .testTag("detail_scroll")
        ) {
            // Section Header
            Text(
                text = "KOOKSOONDANG CRM DOSSIER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB8860B),
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Customer core Detail Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE8E2D9)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(27.dp))
                                    .background(Color(0xFFF5F2ED))
                                    .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(27.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.name.take(1),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB8860B)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = customer.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = customer.phone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF8E8A84)
                                )
                            }
                        }

                        if (customer.isVip) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD4AF37).copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "VIP 단골",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB8860B)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFE8E2D9).copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(20.dp))

                    // Detail items
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🍶 선호 전통주",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8E8A84)
                            )
                            Text(
                                text = customer.preferredAlcohol,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1A1A1A)
                            )
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(
                                text = "누적 소비액 (방문 횟수)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8E8A84)
                            )
                            Text(
                                text = "₩${formatKoreanWon(customer.totalSpending)} (${customer.visitCount}회)",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "마지막 방문 시점: ${lastVisitedDays}일 전",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (lastVisitedDays >= 30) Color(0xFFC62828) else Color(0xFF8E8A84),
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "보관번호: #${customer.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8E8A84).copy(alpha = 0.7f)
                        )
                    }

                    if (customer.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F2ED))
                                .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "📝 특별 참고 대장 내용",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFB8860B),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = customer.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                }
            }

            // AI Report Container Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE8E2D9)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🤖 AI 맞춤형 마케팅 리포트",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color(0xFF1A1A1A)
                        )

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (aiReportText.isNotEmpty()) Color(0xFF10B981).copy(alpha = 0.15f)
                                    else Color(0xFFF5F2ED)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (aiReportText.isNotEmpty()) "분석 완료" else "미작성",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (aiReportText.isNotEmpty()) Color(0xFF047857) else Color(0xFF8E8A84)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Gemini 인공지능이 고객 데이터를 분석하여 맞춤 단골 영입 안부 메시지를 실시간 생성합니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8E8A84)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generation Action Button
                    Button(
                        onClick = { viewModel.generateAiReportForCustomer(customer) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_generate_report"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isGeneratingReport
                    ) {
                        if (isGeneratingReport) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFD4AF37),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("비기(祕器) 마케팅 전략 수립 중...", color = Color.White)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFFD4AF37))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI 제안 안부 메시지 작성", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Display AI Output Report
                    if (isGeneratingReport || aiReportText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFDFBF7))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE8E2D9),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            if (isGeneratingReport) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFFB8860B),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "전략 마케팅 텍스트를 구성하고 있습니다.",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF8E8A84)
                                    )
                                }
                            } else {
                                Text(
                                    text = aiReportText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 22.sp,
                                        letterSpacing = 0.2.sp
                                    ),
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                }
            }

            // KakaoTalk Action / Share Console
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE8E2D9)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "💬 카카오톡 단골 메시지 연동",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "추천된 정성의 안부를 즉시 전송합니다. 복사 후 간편하게 소통하세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8E8A84)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Quick Copy Action
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Kooksoondang CRM Message", reportToShare)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "메시지 문안이 클립보드에 담겼습니다!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_copy_message"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF1A1A1A))
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "복사", tint = Color(0xFF1A1A1A))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("메시지 복사", color = Color(0xFF1A1A1A))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Real KakaoTalk Launch Intent Simulation & Universal Care Share
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Kooksoondang CRM Message", reportToShare)
                                clipboard.setPrimaryClip(clip)

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "[국순당 단골 안부]")
                                    putExtra(Intent.EXTRA_TEXT, reportToShare)
                                }

                                val chooserIntent = Intent.createChooser(shareIntent, "카카오톡 혹은 안부 채널 선택")
                                context.startActivity(chooserIntent)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("btn_send_kakao"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEE500) // Kakao yellow branding color
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "카카오톡 연동 전송",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3C1E1E) // Kakao brand text color
                            )
                        }
                    }
                }
            }
        }
    }
}
