package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.CustomerViewModel
import com.example.data.NotificationLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoNotifScreen(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val logs by viewModel.logs.collectAsState()
    val customers by viewModel.customers.collectAsState()

    val pendingLogs = logs.filter { log -> log.sentStatus == "PENDING" }
    val completedLogs = logs.filter { log -> log.sentStatus != "PENDING" }

    var selectedTab by remember { mutableStateOf(0) } // 0: 대기 알림, 1: 처리 완료 이력

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDFBF7))
            .padding(24.dp)
    ) {
        // [Clean Minimalism] upper title and scan trigger button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "KOOKSOONDANG AUTOMATED INTELLIGENCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB8860B),
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "자동 알림 보드",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Serif
                    ),
                    color = Color(0xFF1A1A1A)
                )
            }

            Button(
                onClick = { 
                    viewModel.runAutoAlertDetector()
                    Toast.makeText(context, "단골 분석 경보기가 가동되었습니다!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_re_scan")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "재탐색", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("실시간 재탐색", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Summary banner trigger parameters
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F2ED))
                .border(
                    width = 1.dp,
                    color = Color(0xFFE8E2D9),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "설명",
                    tint = Color(0xFFB8860B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "국순당 DB 규칙 분석기:\n1. 30일 이상 무방문 시 → 휴면 단골 케어 경보\n2. 일반 고객이 10회 이상/30만원 소비 달성 → VIP 등급 추천 경보\n3. 선호 주종 맞춤형 단골 파격 혜택 제안 경보",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = Color(0xFF1A1A1A),
                    lineHeight = 16.sp
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1A1A1A),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFFD4AF37)
                )
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("대기 중 알리미 (${pendingLogs.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_pending")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("발송 처리 이력 (${completedLogs.size})") },
                modifier = Modifier.testTag("tab_completed")
            )
        }

        val activeLogs = if (selectedTab == 0) pendingLogs else completedLogs

        if (activeLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "완료",
                        modifier = Modifier.size(56.dp),
                        tint = Color(0xFF8E8A84).copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == 0) "대기 중인 자동 관리 대상이 없습니다.\n모두 안전하게 연동되었거나 방문 주기가 충족됩니다."
                               else "처리 이력이 존재하지 않습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8E8A84),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(activeLogs, key = { it.id }) { log ->
                    AlertLogCard(
                        log = log,
                        onSendKakao = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Kooksoondang Alarm Message", log.message)
                            clipboard.setPrimaryClip(clip)

                            // Mark as COMPLETED in database
                            viewModel.markLogAsSentKakao(log)

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "[국순당 데이터 자동 케어 서비스]")
                                putExtra(Intent.EXTRA_TEXT, log.message)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "안부 편지 전송할 메신저 선택"))
                            Toast.makeText(context, "단골 안부문자 복사 및 전송창이 준비되었습니다!", Toast.LENGTH_SHORT).show()
                        },
                        onSendSystemPush = {
                            viewModel.triggerSystemNotification(context, log)
                            Toast.makeText(context, "안드로이드 상단바에 시스템 마케팅 푸시를 전송했습니다!", Toast.LENGTH_SHORT).show()
                        },
                        onDismiss = {
                            viewModel.dismissLog(log.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertLogCard(
    log: NotificationLog,
    onSendKakao: () -> Unit,
    onSendSystemPush: () -> Unit,
    onDismiss: () -> Unit
) {
    val badgeColor = when (log.type) {
        "DORMANT" -> Color(0xFFC62828)
        "VIP_ANNI" -> Color(0xFFB8860B)
        else -> Color(0xFF10B981)
    }

    val badgeTitle = when (log.type) {
        "DORMANT" -> "장기 무방문 주의"
        "VIP_ANNI" -> "VIP 등급 승급 추천"
        else -> "선호 주종 혜택"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFE8E2D9),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("alert_log_card_${log.id}"),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header Block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeTitle,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = badgeColor
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = log.customerName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1A1A1A)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "삭제",
                        tint = Color(0xFF8E8A84),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message text styled clean
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE8E2D9).copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (log.sentStatus == "PENDING") {
                    // System push trigger
                    OutlinedButton(
                        onClick = onSendSystemPush,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("btn_system_push_${log.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF1A1A1A)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE8E2D9))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CircleNotifications,
                            contentDescription = "푸시",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("시스템 푸시", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1A1A1A))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // KakaoTalk Action
                    Button(
                        onClick = onSendKakao,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("btn_send_kakao_log_${log.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE500), // Kakao brand color
                            contentColor = Color(0xFF3C1E1E)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "카카오톡",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF3C1E1E)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "카카오톡 전송", 
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF3C1E1E)
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "완료",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (log.sentStatus == "NOTIFIED") "시스템 알림 전송 완료" else "카카오 전송 완료",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF047857)
                        )
                    }
                }
            }
        }
    }
}
