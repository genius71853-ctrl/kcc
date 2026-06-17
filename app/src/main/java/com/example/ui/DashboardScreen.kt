package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Customer
import com.example.data.CustomerViewModel
import com.example.data.NotificationLog
import com.example.data.Screen

@Composable
fun DashboardScreen(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val pendingAlarms = logs.filter { it.sentStatus == "PENDING" }
    val totalSales = customers.sumOf { it.totalSpending }
    val totalCustomers = customers.size
    val vipCount = customers.count { it.isVip }
    val vipRatio = if (totalCustomers > 0) (vipCount * 100 / totalCustomers) else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // [Clean Minimalism] Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KOOKSOONDANG MANAGEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8860B),
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "고객 관리 보드",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Decorative Minimal Search/Profile circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF5F2ED))
                        .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFB8860B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_scroll"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Banner styled subtly
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sool_banner),
                        contentDescription = "국순당 전통주 장인의 테이블",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0x66000000), Color(0xAA1A1A1A))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "국순당 보관대장 Premium",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37),
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "품격과 전통이 숨 쉬는 1:1 자동 단골 안부 솔루션",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFF5F2ED),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // [Clean Minimalism] Key Metrics Dashboard Grid
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    Text(
                        text = "운영 핵심 데이터",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E8A84)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Card 1: Total Customers (White minimalist with PaperBorder)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFE8E2D9)),
                            modifier = Modifier
                                .weight(1f)
                                .height(115.dp)
                                .testTag("metric_total_customer"),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "전체 고객",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF8E8A84),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "$totalCustomers",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 24.sp
                                        ),
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = "+12%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        ),
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Card 2: Auto Notifications (Solid Dark Minimalist Card)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1.0f)
                                .height(115.dp)
                                .clickable { viewModel.navigateTo(Screen.AutoNotifications) }
                                .testTag("metric_alarms"),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "자동 알림",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFAFAFAF),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${pendingAlarms.size}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 24.sp
                                        ),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic,
                                            color = Color(0xFFD4AF37)
                                        ),
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Card 3: VIP Customers (Minimalist White)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFE8E2D9)),
                            modifier = Modifier
                                .weight(1f)
                                .height(95.dp)
                                .testTag("metric_vip")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "단골 단고객 (VIP)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF8E8A84),
                                            fontSize = 10.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$vipCount 명",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF1A1A1A)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFDFBF7))
                                        .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "VIP",
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Card 4: Total Revenue (Minimalist White)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFE8E2D9)),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(95.dp)
                                .testTag("metric_total_sales")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "전체 보관 누적액",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF8E8A84),
                                            fontSize = 10.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₩${formatKoreanWon(totalSales)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF1A1A1A)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFDFBF7))
                                        .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = "Sales",
                                        tint = Color(0xFF8E8A84),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // [Clean Minimalism] Weekly reports/charts section
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE8E2D9),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "주간 활동 분포 리포트",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8E8A84)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🍶 인기 선호 주종 현황",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = Color(0xFF1A1A1A)
                            )
                        }
                        Text(
                            text = "상세 분석",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFB8860B),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val defaultLiquors = listOf("백세주", "국순당 생막걸리", "려 (고구마 증류소주)", "100억 프리바이오 막걸리", "자양백세주", "기타 전통 명주")
                    val countMap = defaultLiquors.associateWith { liquor ->
                        customers.count { it.preferredAlcohol == liquor || (liquor == "기타 전통 명주" && !defaultLiquors.contains(it.preferredAlcohol)) }
                    }

                    val maxCount = countMap.values.maxOrNull()?.coerceAtLeast(1) ?: 1

                    countMap.forEach { (liquor, count) ->
                        Column(modifier = Modifier.padding(vertical = 5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = liquor,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = "${count}명",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFB8860B)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Simple high-fidelity minimalist bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF5F2ED))
                            ) {
                                val proportion = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(proportion)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFD4AF37),
                                                    Color(0xFF1A1A1A)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Block
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    Text(
                        text = "빠른 단골 액션",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E8A84)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.CustomerList) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("action_cust_list"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.List, contentDescription = "목록")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("고객 기록 대장", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { viewModel.navigateTo(Screen.AutoNotifications) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("action_alerts"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD4AF37)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "알람", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("알림 발송 보드", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

fun formatKoreanWon(amount: Long): String {
    return String.format("%,d", amount)
}
