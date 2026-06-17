package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Customer
import com.example.data.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredCustomers = customers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery) ||
        it.preferredAlcohol.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            // [Clean Minimalism] Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "국순당 고객 대장",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "총 ${customers.size}명의 전통주 단골 보관 중",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8E8A84)
                    )
                }
                
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1A1A)
                    ),
                    modifier = Modifier.testTag("add_customer_fab")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "추가", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("고객 등록", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Elegant Rounded Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("search_field"),
                placeholder = { Text("고객 이름, 연락처, 선호 주종 검색") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "검색", tint = Color(0xFF8E8A84)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "지우기")
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD4AF37),
                    unfocusedBorderColor = Color(0xFFE8E2D9),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            )

            // Customer List Builder
            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = "빈 내역",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF8E8A84).copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "일치하는 단골 고객이 없습니다." else "등록된 보관 고객정보가 없습니다.\n우측 상단 대장 추가 단추로 등재해주세요.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8E8A84)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        CustomerItemCard(
                            customer = customer,
                            onClick = { viewModel.selectCustomer(customer) }
                        )
                    }
                }
            }
        }
    }

    // Add Customer Form Modal Overlay Dialog
    if (showAddDialog) {
        AddCustomerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, alcohol, visits, spend, daysAgo, notes, vip ->
                viewModel.addCustomer(name, phone, alcohol, visits, spend, daysAgo, notes, vip)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CustomerItemCard(
    customer: Customer,
    onClick: () -> Unit
) {
    val lastVisitedDays = ((System.currentTimeMillis() - customer.lastVisitDate) / (24 * 3600 * 1000L)).coerceAtLeast(0)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFE8E2D9),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .testTag("customer_item_${customer.id}"),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // High-fidelity profile avatar & name block
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(Color(0xFFF5F2ED))
                            .border(1.dp, Color(0xFFE8E2D9), RoundedCornerShape(19.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.name.take(1),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color(0xFFB8860B)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = customer.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1A1A1A)
                            )
                            if (customer.isVip) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFD4AF37).copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
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
                    }
                }

                // Phone number
                Text(
                    text = customer.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8E8A84)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub Metadata info with clean layout
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = "🍶 선호 전통주",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8E8A84)
                    )
                    Text(
                        text = customer.preferredAlcohol,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1A1A1A)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "누적 연간 결제액",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8E8A84)
                    )
                    Text(
                        text = "₩${formatKoreanWon(customer.totalSpending)} (${customer.visitCount}회)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE8E2D9).copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Last visit info badge & Notes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "방문일",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF8E8A84)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "마지막 방문: ${lastVisitedDays}일 전",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (lastVisitedDays >= 30) Color(0xFFC62828) else Color(0xFF8E8A84)
                    )
                    if (lastVisitedDays >= 30) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "휴면 주의",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "상세",
                    tint = Color(0xFF8E8A84),
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, Long, Int, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var preferredAlcohol by remember { mutableStateOf("백세주") }
    var visitCountStr by remember { mutableStateOf("1") }
    var spendingStr by remember { mutableStateOf("") }
    var lastVisitDaysAgoStr by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    var isVip by remember { mutableStateOf(false) }

    val coreLiquors = listOf("백세주", "국순당 생막걸리", "려 (고구마 증류소주)", "100억 프리바이오 막걸리", "자양백세주", "예담")
    var menuExpanded by remember { mutableStateOf(false) }

    var hasNameError by remember { mutableStateOf(false) }
    var hasPhoneError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        title = {
            Text(
                "🌾 국순당 하이엔드 단골 등록",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1A1A1A)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_customer_form"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            hasNameError = it.trim().isEmpty()
                        },
                        label = { Text("성명 (필수)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFFE8E2D9)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_name"),
                        isError = hasNameError,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            hasPhoneError = it.trim().isEmpty()
                        },
                        label = { Text("연락처 (필수, 예: 010-XXXX-XXXX)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFFE8E2D9)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_phone"),
                        isError = hasPhoneError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = menuExpanded,
                        onExpandedChange = { menuExpanded = !menuExpanded }
                    ) {
                        OutlinedTextField(
                            value = preferredAlcohol,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("선호하는 국순당 전통주") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color(0xFFE8E2D9)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            coreLiquors.forEach { liquor ->
                                DropdownMenuItem(
                                    text = { Text(liquor) },
                                    onClick = {
                                        preferredAlcohol = liquor
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = visitCountStr,
                            onValueChange = { visitCountStr = it.filter { c -> c.isDigit() } },
                            label = { Text("내방 횟수") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color(0xFFE8E2D9)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_visits"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = lastVisitDaysAgoStr,
                            onValueChange = { lastVisitDaysAgoStr = it.filter { c -> c.isDigit() } },
                            label = { Text("방문 시기 (며칠 전)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color(0xFFE8E2D9)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_days_ago"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = spendingStr,
                        onValueChange = { spendingStr = it.filter { c -> c.isDigit() } },
                        label = { Text("누적 소비액 (₩ 원)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFFE8E2D9)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_spending"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("마케팅 참고 사항 / 메모") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFFE8E2D9)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("form_notes"),
                        maxLines = 3
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "프리미엄 VIP 단골로 활성화",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = isVip,
                            onCheckedChange = { isVip = it },
                            modifier = Modifier.testTag("form_vip_switch")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isEmpty()) {
                        hasNameError = true
                        return@Button
                    }
                    if (phone.trim().isEmpty()) {
                        hasPhoneError = true
                        return@Button
                    }
                    val visits = visitCountStr.toIntOrNull() ?: 1
                    val spend = spendingStr.toLongOrNull() ?: 0L
                    val daysAgo = lastVisitDaysAgoStr.toIntOrNull() ?: 0
                    onConfirm(name, phone, preferredAlcohol, visits, spend, daysAgo, notes, isVip)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                modifier = Modifier.testTag("submit_customer_button")
            ) {
                Text("등재", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_customer_button")
            ) {
                Text("취소", color = Color(0xFF8E8A84))
            }
        }
    )
}
