package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LeadEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// --- APP LOCALIZATION TRANSLATOR UTILITY ---
object AppTranslator {
    private val translations = mapOf(
        "中文" to mapOf(
            "b2b_lead_engine" to "B2B智能获客引擎",
            "clear_toast" to "已清理非收藏缓存线索",
            "config_title" to "商机探测配置",
            "target_country" to "目标国家",
            "select_country" to "选择国家",
            "equipment_keyword" to "设备关键词",
            "scraping_text" to "正在实时采集线索...",
            "start_radar_text" to "一键启动 AI 全网获客雷达",
            "tab_realtime" to "实时采集",
            "tab_saved" to "线索收藏库",
            "empty_realtime_desc" to "暂未采集到匹配的实时询价线索",
            "empty_saved_desc" to "您的收藏库目前空空如也",
            "empty_realtime_sub" to "请更换关键词（例如：矿卡/挖掘机/压路机）并启动AI雷达，全网抓取实时海外询价。",
            "empty_saved_sub" to "在“实时采集”标签下点击卡片右下角的❤️喜爱标记，即可永久保存到收藏库。",
            "pitch_dialog_title" to "🤖 AI B2B 精准拓客信函",
            "pitch_buyer_needs" to "求购诉求",
            "pitch_template_title" to "定制开发跟进模板:",
            "pitch_generating_loading" to "Gemini 正在针对该条询价内容\n分析买家母语、关切点并生成高转化率信函...",
                 "lead_item_time" to "采集时间",
            "lead_btn_pitch" to "AI开发信",
            "export_btn_text" to "导出客户表格",
            "lead_count_realtime" to "实时客源总计",
            "lead_count_saved" to "永久收藏总计"
        ),
        "English" to mapOf(
            "b2b_lead_engine" to "B2B LEAD ENGINE",
            "clear_toast" to "Cleared temporary unsaved cache leads",
            "config_title" to "Scanner Configuration",
            "target_country" to "Target Country",
            "select_country" to "Select Country",
            "equipment_keyword" to "Keyword",
            "scraping_text" to "Crawling live leads...",
            "start_radar_text" to "Launch AI Lead Radar",
            "tab_realtime" to "Real-time Leads",
            "tab_saved" to "Saved Pitch Vault",
            "empty_realtime_desc" to "No matched inquiry leads found",
            "empty_saved_desc" to "Your Saved Vault is empty",
            "empty_realtime_sub" to "Please change the keywords (e.g. excavator, mining truck) and click 'Launch AI Lead Radar' to initiate real-time scanning.",
            "empty_saved_sub" to "Tap the favorite ❤️ indicator on any lead card under the Real-time tab to securely store high-intent buyers permanently.",
            "pitch_dialog_title" to "🤖 AI B2B Outreach Pitch",
            "pitch_buyer_needs" to "Buyer Demand",
            "pitch_template_title" to "Tailored Follow-up Template:",
            "pitch_generating_loading" to "Gemini AI is analyzing concerns and generating a personalized high-conversion letter...",
            "pitch_btn_back" to "Back",
            "pitch_btn_copy" to "Copy Outreach Script",
            "pitch_copy_toast" to "Outreach template copied to clipboard!",
            "metric_scanned_label" to "Radar Monitored Leads",
            "metric_scanned_sub_scraping" to "Radar pulsing...",
            "metric_scanned_sub_idle" to "Sensors Active",
            "metric_val_label" to "High-Value Deals",
            "metric_val_sub" to "Captured This Week",
            "metric_nodes_label" to "Platform Nodes",
            "lead_item_time" to "Discovered",
            "lead_btn_pitch" to "AI outreach",
            "export_btn_text" to "Export Table",
            "lead_count_realtime" to "Live Scanned Leads",
            "lead_count_saved" to "Saved Lead Archon"
        ),
        "日本語" to mapOf(
            "b2b_lead_engine" to "B2B 顧客開拓システム",
            "clear_toast" to "未保存のキャッシュリードをクリアしました",
            "config_title" to "商機サーチ設定",
            "target_country" to "対象地域",
            "select_country" to "対象地域を選択",
            "equipment_keyword" to "設備キーワード",
            "scraping_text" to "リード取得中...",
            "start_radar_text" to "AI顧客獲得レーダー起動",
            "tab_realtime" to "リアルタイム取得",
            "tab_saved" to "保存されたリード",
            "empty_realtime_desc" to "一致するリードがありません",
            "empty_saved_desc" to "お気に入りのリードはありません",
            "empty_realtime_sub" to "設備キーワード（例：鉱山トラック、ショベル）を変更、またはAI顧客獲得レーダーを起動してください。",
            "empty_saved_sub" to "「リアルタイム取得」タブで❤️マークをタップすると、ここに保存されます。",
            "pitch_dialog_title" to "🤖 AI B2B 提案作成ハブ",
            "pitch_buyer_needs" to "バイヤーの要望",
            "pitch_template_title" to "自動フォローアップメッセージ:",
            "pitch_generating_loading" to "Gemini AIが買い手の言語と関心事を分析して、カスタマイずされた提案を生成しています...",
            "pitch_btn_back" to "戻る",
            "pitch_btn_copy" to "提案メッセージをコピー",
            "pitch_copy_toast" to "提案メッセージがコピーされました！",
            "metric_scanned_label" to "システム精査数",
            "metric_scanned_sub_scraping" to "レーダー起動中...",
            "metric_scanned_sub_idle" to "センサー稼働中",
            "metric_val_label" to "優良B2B顧客",
            "metric_val_sub" to "今週の集計総数",
            "metric_nodes_label" to "ソーシャルノード",
            "lead_item_time" to "検出日時",
            "lead_btn_pitch" to "AI提案",
            "export_btn_text" to "テーブルにエクスポート",
            "lead_count_realtime" to "リアルタイムリード",
            "lead_count_saved" to "お気に入りリード"
        ),
        "Português" to mapOf(
            "b2b_lead_engine" to "Motor de Prospecção B2B",
            "clear_toast" to "Cache de leads não salvos limpo",
            "config_title" to "Configuração do Scanner",
            "target_country" to "País de Destino",
            "select_country" to "Selecionar País",
            "equipment_keyword" to "Palavra-chave",
            "scraping_text" to "Rastreando leads ao vivo...",
            "start_radar_text" to "Iniciar Radar de Leads",
            "tab_realtime" to "Captação Ativa",
            "tab_saved" to "Cofre de Leads",
            "empty_realtime_desc" to "Nenhum lead correspondente encontrado",
            "empty_saved_desc" to "O seu portfólio de salvos está vazio",
            "empty_realtime_sub" to "Altere os termos do equipamento (ex: caminhão, escavadeira) e ative o Radar de Leads IA para buscar novas negociações.",
            "empty_saved_sub" to "Clique no ícone de coração ❤️ para armazenar permanentemente os compradores nesta área.",
            "pitch_dialog_title" to "🤖 Prospecção de Precisão IA B2B",
            "pitch_buyer_needs" to "Necessidade de Compra",
            "pitch_template_title" to "Modelo Personalizado de Vendas:",
            "pitch_generating_loading" to "O Gemini AI está estruturando a proposta de alto retorno no idioma nativo do comprador...",
            "pitch_btn_back" to "Voltar",
            "pitch_btn_copy" to "Copiar Texto de Vendas",
            "pitch_copy_toast" to "Mensagem de vendas copiada com sucesso!",
            "metric_scanned_label" to "Inquéritos Monitorados",
            "metric_scanned_sub_scraping" to "Varrendo rede...",
            "metric_scanned_sub_idle" to "Sensores Ativos",
            "metric_val_label" to "Leads de Alto Impacto",
            "metric_val_sub" to "Coletados esta semana",
            "metric_nodes_label" to "Canais Sociais",
            "lead_item_time" to "Coletado em",
            "lead_btn_pitch" to "Prospecção IA",
            "export_btn_text" to "Exportar Tabela",
            "lead_count_realtime" to "Leads Rastreados",
            "lead_count_saved" to "Leads Salvos"
        ),
        "العربية" to mapOf(
            "b2b_lead_engine" to "محرك توليد عملاء B2B",
            "clear_toast" to "تم مسح ذاكرة التخزين المؤقت للعملاء غير المحفوظين",
            "config_title" to "إعدادات الرادار",
            "target_country" to "البلد المستهدف",
            "select_country" to "اختر البلد",
            "equipment_keyword" to "الكلمة المفتاحية للمعدات",
            "scraping_text" to "جاري سحب العملاء...",
            "start_radar_text" to "تشغيل رادار عملاء الذكاء الاصطناعي",
            "tab_realtime" to "التقاط مباشر",
            "tab_saved" to "العملاء المحفوظون",
            "empty_realtime_desc" to "لم يتم العثور على عملاء مطابقين",
            "empty_saved_desc" to "قائمة المحفوظات فارغة حاليًا",
            "empty_realtime_sub" to "يرجى تغيير الكلمات المفتاحية للمعدات (مثل الحفارات، شاحنات التعدين) ثم انقر على 'تشغيل رادار عملاء الذكاء الاصطناعي'.",
            "empty_saved_sub" to "اضغط على زر القلب ❤️ لحفظ المشترين وتخزينهم بأمان هنا بشكل دائم.",
            "pitch_dialog_title" to "🤖 رسالة تواصل B2B ذكية",
            "pitch_buyer_needs" to "متطلبات المشتري",
            "pitch_template_title" to "قالب المتابعة المخصص:",
            "pitch_generating_loading" to "يقوم Gemini AI بتحليل لغة ومطالب المشتري لإنتاج خطة تواصل مرتفعة التأثير...",
            "pitch_btn_back" to "رجوع",
            "pitch_btn_copy" to "نسخ نموذج الرسالة",
            "pitch_copy_toast" to "تم نسخ الرسالة إلى الحافظة بنجاح!",
            "metric_scanned_label" to "العملاء المرقبون بالرادار",
            "metric_scanned_sub_scraping" to "الرادار ينبض...",
            "metric_scanned_sub_idle" to "أجهزة الاستشعار نشطة",
            "metric_val_label" to "العملاء الاستراتيجيين",
            "metric_val_sub" to "الملتقطة هذا الأسبوع",
            "metric_nodes_label" to "قنوات التشغيل",
            "lead_item_time" to "تاريخ الالتقاط",
            "lead_btn_pitch" to "تواصل ذكي",
            "export_btn_text" to "تصدير الجدول (CSV)",
            "lead_count_realtime" to "العملاء المكتشفون المباشرون",
            "lead_count_saved" to "قائمة المحفوظات الاستراتيجية"
        )
    )

    fun translate(key: String, language: String): String {
        return translations[language]?.get(key) ?: translations["中文"]?.get(key) ?: key
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B2BLeadCaptureScreen(
    viewModel: LeadViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observe state from ViewModel
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val equipmentKeyword by viewModel.equipmentKeyword.collectAsStateWithLifecycle()
    val isScraping by viewModel.isScraping.collectAsStateWithLifecycle()
    val progressText by viewModel.scrapeProgressText.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    val filteredLeads by viewModel.filteredLeads.collectAsStateWithLifecycle()
    val savedLeads by viewModel.savedLeads.collectAsStateWithLifecycle()

    val totalScanned by viewModel.totalScannedCount.collectAsStateWithLifecycle()
    val activeChannels by viewModel.activeChannelsCount.collectAsStateWithLifecycle()
    val newFiltered by viewModel.newFilteredLeadCount.collectAsStateWithLifecycle()

    val selectedLeadForPitch by viewModel.selectedLeadForPitch.collectAsStateWithLifecycle()
    val generatedPitchText by viewModel.generatedPitchText.collectAsStateWithLifecycle()
    val isGeneratingPitch by viewModel.isGeneratingPitch.collectAsStateWithLifecycle()

    // Screen navigation tabs
    var currentTab by remember { mutableStateOf(0) } // 0: 实时监控采集, 1: 线索收藏夹

    // Dropdown state
    var showCountryDropdown by remember { mutableStateOf(false) }
    val countries = listOf("全球", "莫桑比克", "南非", "安哥拉", "沙特阿拉伯", "哈萨克斯坦", "俄罗斯")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlueBG),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Radiant Modern Gradient Icon for Machinery AI
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF00FF9D), Color(0xFF00A3FF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B1021)
                            )
                        }
                        
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Machinery AI",
                                    color = IceBlueText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.3).sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Tiny Live Status Dot (Pulse anim)
                                LiveStatusDot(isScraping = isScraping)
                            }
                            Text(
                                text = AppTranslator.translate("b2b_lead_engine", selectedLanguage).uppercase(),
                                color = AuroraGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlueBG,
                    titleContentColor = IceBlueText
                ),
                actions = {
                    var showLanguageDropdown by remember { mutableStateOf(false) }
                    val languages = listOf("中文", "English", "日本語", "Português", "العربية")
                    
                    Box {
                        TextButton(
                            onClick = { showLanguageDropdown = true },
                            modifier = Modifier.testTag("language_picker_button")
                        ) {
                            Text(
                                text = when (selectedLanguage) {
                                    "中文" -> "🇨🇳 中文"
                                    "English" -> "🇺🇸 EN"
                                    "日本語" -> "🇯🇵 JA"
                                    "Português" -> "🇵🇹 PT"
                                    "العربية" -> "🇸🇦 AR"
                                    else -> "🌐 $selectedLanguage"
                                },
                                color = AuroraGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "切换语言",
                                tint = AuroraGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showLanguageDropdown,
                            onDismissRequest = { showLanguageDropdown = false },
                            modifier = Modifier
                                .background(DeepBlueContainer)
                                .border(1.dp, BorderColor)
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = when (lang) {
                                                "中文" -> "🇨🇳 中文"
                                                "English" -> "🇺🇸 English"
                                                "日本語" -> "🇯🇵 日本語"
                                                "Português" -> "🇵🇹 Português"
                                                "العربية" -> "🇸🇦 العربية"
                                                else -> lang
                                            },
                                            color = IceBlueText,
                                            fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    onClick = {
                                        viewModel.selectLanguage(lang)
                                        showLanguageDropdown = false
                                        // A friendly feedback Toast
                                        val feedback = when (lang) {
                                            "中文" -> "语言已切换为：中文"
                                            "English" -> "Language switched to English"
                                            "日本語" -> "言語を日本語に切り替えました"
                                            "Português" -> "Idioma alterado para Português"
                                            "العربية" -> "تم تغيير اللغة إلى العربية"
                                            else -> "Language switched"
                                        }
                                        Toast.makeText(context, feedback, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            viewModel.clearTemporaryLeads()
                            val msg = AppTranslator.translate("clear_toast", selectedLanguage)
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("action_clear_leads")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "清理缓冲",
                            tint = MutedBlueText
                        )
                    }
                }
            )
        },
        containerColor = DeepBlueBG
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // --- HEADER SUMMARY METRIC TILES ---
            DashboardMetricsRow(
                totalScanned = totalScanned,
                activeChannels = activeChannels,
                newFiltered = newFiltered,
                isScraping = isScraping,
                selectedLanguage = selectedLanguage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- FILTER CONTROLS (COUNTRY SELECTOR & SEARCH BOX) ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepBlueSurface),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = AppTranslator.translate("config_title", selectedLanguage),
                        color = AuroraGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Country Dropdown Selector
                        Box(modifier = Modifier.weight(0.4f)) {
                            OutlinedCard(
                                onClick = { showCountryDropdown = true },
                                border = BorderStroke(1.dp, BorderColor),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.outlinedCardColors(containerColor = DeepBlueContainer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("country_picker_dropdown")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(AppTranslator.translate("target_country", selectedLanguage), color = MutedBlueText, fontSize = 10.sp)
                                        Text(
                                            selectedCountry,
                                            color = IceBlueText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = AppTranslator.translate("select_country", selectedLanguage),
                                        tint = AuroraGreen
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showCountryDropdown,
                                onDismissRequest = { showCountryDropdown = false },
                                modifier = Modifier
                                    .background(DeepBlueContainer)
                                    .border(1.dp, BorderColor)
                             ) {
                                countries.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c, color = IceBlueText) },
                                        onClick = {
                                            viewModel.selectCountry(c)
                                            showCountryDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Machinery search input TextField
                        OutlinedTextField(
                            value = equipmentKeyword,
                            onValueChange = { viewModel.setEquipmentKeyword(it) },
                            label = { Text(AppTranslator.translate("equipment_keyword", selectedLanguage), color = MutedBlueText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DeepBlueContainer,
                                unfocusedContainerColor = DeepBlueContainer,
                                focusedBorderColor = AuroraGreen,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = IceBlueText,
                                unfocusedTextColor = IceBlueText,
                                focusedLabelColor = AuroraGreen,
                                unfocusedLabelColor = MutedBlueText
                            ),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                keyboardController?.hide()
                                viewModel.startRealtimeScrape()
                            }),
                            modifier = Modifier
                                .weight(0.6f)
                                .testTag("machinery_input_field")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Radar button triggers live crawler mock / API sequence
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.startRealtimeScrape()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isScraping) DeepBlueContainer else AuroraGreen,
                            contentColor = DeepBlueBG
                        ),
                        border = if (isScraping) BorderStroke(1.dp, AuroraGreen) else null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("radar_trigger_button")
                    ) {
                        if (isScraping) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AuroraGreen,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                AppTranslator.translate("scraping_text", selectedLanguage),
                                color = AuroraGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "扫描",
                                tint = DeepBlueBG
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                AppTranslator.translate("start_radar_text", selectedLanguage),
                                fontWeight = FontWeight.Bold,
                                color = DeepBlueBG,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // --- ANIMATED PROGRESS STATUS FROM ROBOT ---
            AnimatedVisibility(visible = isScraping || progressText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF031023)),
                    border = BorderStroke(1.dp, AuroraGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "雷达监测详情",
                            tint = AuroraGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = progressText,
                            color = IceBlueText,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- STREAMING OR DATABASE PERSISTENCE SELECTION TABS ---
            TabRow(
                selectedTabIndex = currentTab,
                containerColor = DeepBlueBG,
                contentColor = AuroraGreen,
                divider = { HorizontalDivider(color = BorderColor) }
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                AppTranslator.translate("tab_realtime", selectedLanguage),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(p = Modifier.width(4.dp))
                            Badge(
                                containerColor = AuroraGreenDim,
                                contentColor = IceBlueText
                            ) {
                                Text(filteredLeads.size.toString())
                            }
                        }
                    },
                    selectedContentColor = AuroraGreen,
                    unselectedContentColor = MutedBlueText
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                AppTranslator.translate("tab_saved", selectedLanguage),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(p = Modifier.width(4.dp))
                            Badge(
                                containerColor = AccentOrange,
                                contentColor = DeepBlueBG
                            ) {
                                Text(savedLeads.size.toString())
                            }
                        }
                    },
                    selectedContentColor = AuroraGreen,
                    unselectedContentColor = MutedBlueText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- REAL-TIME SCROLLING LIST OF LEADS (ListView) ---
            val activeList = if (currentTab == 0) filteredLeads else savedLeads

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leadSummaryText = if (currentTab == 0) {
                    AppTranslator.translate("lead_count_realtime", selectedLanguage)
                } else {
                    AppTranslator.translate("lead_count_saved", selectedLanguage)
                }
                Text(
                    text = "$leadSummaryText (${activeList.size})",
                    color = MutedBlueText,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        LeadExporter.exportLeadsToCsv(context, activeList, selectedLanguage)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuroraGreenDim,
                        contentColor = AuroraGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("export_leads_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export Leads Table",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppTranslator.translate("export_btn_text", selectedLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (activeList.isEmpty()) {
                EmptyStateLayout(
                    tabIndex = currentTab,
                    selectedCountry = selectedCountry,
                    equipmentKeyword = equipmentKeyword,
                    selectedLanguage = selectedLanguage
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("leads_scroll_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = activeList,
                        key = { lead -> lead.id }
                    ) { lead ->
                        LeadCardItem(
                            lead = lead,
                            selectedLanguage = selectedLanguage,
                            onSaveToggle = { viewModel.toggleSaveLead(lead) },
                            onDelete = { viewModel.deleteLead(lead) },
                            onPitchClick = { viewModel.preparePitchDialog(lead) }
                        )
                    }
                }
            }
        }
    }

    // --- OUTREACH INTERACTION DIALOG PANEL (AI Pitch Hub) ---
    selectedLeadForPitch?.let { lead ->
        val dialogFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
        val dialogFormattedTime = remember(lead.timestamp) { dialogFormatter.format(Date(lead.timestamp)) }

        Dialog(onDismissRequest = { viewModel.closePitchDialog() }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepBlueSurface),
                border = BorderStroke(2.dp, AuroraGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("outreach_pitch_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppTranslator.translate("pitch_dialog_title", selectedLanguage),
                            color = AuroraGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(
                            onClick = { viewModel.closePitchDialog() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "关闭",
                                tint = IceBlueText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lead context synopsis - Optimized for complete buyer profile details
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepBlueContainer, RoundedCornerShape(8.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = when (selectedLanguage) {
                                    "中文" -> "📋 客户精密画像与社交联系方式"
                                    "English" -> "📋 Precision Buyer Dossier & Contacts"
                                    "日本語" -> "📋 精密バイヤープロファイルと連絡先"
                                    "Português" -> "📋 Perfil Detalhado e Contatos"
                                    "العربية" -> "📋 الملف التعريفي التفصيلي للعميل"
                                    else -> "📋 Precision Buyer Dossier"
                                },
                                color = AuroraGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1.3f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    DialogProfileItem(
                                        label = when (selectedLanguage) {
                                            "中文" -> "🌐 来源渠道"
                                            "English" -> "🌐 Source Platform"
                                            else -> "🌐 Source Platform"
                                        },
                                        value = lead.source
                                    )
                                    DialogProfileItem(
                                        label = when (selectedLanguage) {
                                            "中文" -> "👤 用户名称"
                                            "English" -> "👤 Username"
                                            else -> "👤 Username"
                                        },
                                        value = lead.accountName
                                    )
                                    DialogProfileItem(
                                        label = when (selectedLanguage) {
                                            "中文" -> "🆔 平台账号ID"
                                            "English" -> "🆔 Platform ID"
                                            else -> "🆔 Platform ID"
                                        },
                                        value = lead.platformId.ifEmpty { "N/A" }
                                    )
                                }
                                Column(modifier = Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    DialogProfileItem(
                                        label = when (selectedLanguage) {
                                            "中文" -> "📞 联系电话"
                                            "English" -> "📞 Phone"
                                            else -> "📞 Phone"
                                        },
                                        value = lead.phone.ifEmpty { "N/A" }
                                    )
                                    DialogProfileItem(
                                        label = when (selectedLanguage) {
                                            "中文" -> "✉️ 电子邮箱"
                                            "English" -> "✉️ Email Address"
                                            else -> "✉️ Email"
                                        },
                                        value = lead.email.ifEmpty { "N/A" }
                                    )
                                    DialogProfileItem(
                                        label = when (selectedLanguage) {
                                            "中文" -> "🕒 采集时间"
                                            "English" -> "🕒 Grabbed Time"
                                            else -> "🕒 Grabbed Time"
                                        },
                                        value = dialogFormattedTime
                                    )
                                }
                            }

                            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f), thickness = 1.dp)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepBlueContainer.copy(alpha = 0.5f))
                                    .pointerInput(lead.content) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                if (lead.content.isNotEmpty()) {
                                                    clipboardManager.setText(AnnotatedString(lead.content))
                                                    Toast.makeText(context, "✅ 询盘需求已复制", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onTap = {
                                                Toast.makeText(context, "💡 双击求购留言可直接复制全文", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = when (selectedLanguage) {
                                        "中文" -> "💬 买家求购留言 / 详细购买意向需求"
                                        "English" -> "💬 Buyer Inquiry / Detailed Demands"
                                        else -> "💬 Buyer Inquiry / Detailed Demands"
                                    },
                                    color = MutedBlueText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = lead.content,
                                    color = IceBlueText,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = AppTranslator.translate("pitch_template_title", selectedLanguage),
                        color = MutedBlueText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Generated outline text space
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color(0xFF030D19), RoundedCornerShape(8.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        if (isGeneratingPitch) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = AuroraGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = AppTranslator.translate("pitch_generating_loading", selectedLanguage),
                                    color = MutedBlueText,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Text(
                                        text = generatedPitchText,
                                        color = IceBlueText,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.testTag("pitch_template_content")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Copy action and exit buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.closePitchDialog() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IceBlueText),
                            border = BorderStroke(1.dp, BorderColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(AppTranslator.translate("pitch_btn_back", selectedLanguage))
                        }

                        Button(
                            onClick = {
                                if (generatedPitchText.isNotEmpty()) {
                                    clipboardManager.setText(AnnotatedString(generatedPitchText))
                                    Toast.makeText(context, AppTranslator.translate("pitch_copy_toast", selectedLanguage), Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isGeneratingPitch && generatedPitchText.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuroraGreen,
                                contentColor = DeepBlueBG
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("btn_copy_pitch")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "复制", tint = DeepBlueBG)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppTranslator.translate("pitch_btn_copy", selectedLanguage), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- TOP VISUAL ANALYTIC METRICS ---
@Composable
fun DashboardMetricsRow(
    totalScanned: Int,
    activeChannels: Int,
    newFiltered: Int,
    isScraping: Boolean,
    selectedLanguage: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_metrics_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricTile(
            label = AppTranslator.translate("metric_scanned_label", selectedLanguage),
            value = totalScanned.toString(),
            color = IceBlueText,
            subLabel = if (isScraping) AppTranslator.translate("metric_scanned_sub_scraping", selectedLanguage) else AppTranslator.translate("metric_scanned_sub_idle", selectedLanguage),
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            label = AppTranslator.translate("metric_val_label", selectedLanguage),
            value = "+$newFiltered",
            color = AuroraGreen,
            subLabel = AppTranslator.translate("metric_val_sub", selectedLanguage),
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            label = AppTranslator.translate("metric_nodes_label", selectedLanguage),
            value = activeChannels.toString(),
            color = PlatformTikTok,
            subLabel = "YouTube/TikTok/X",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MetricTile(
    label: String,
    value: String,
    color: Color,
    subLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlueSurface),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = MutedBlueText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subLabel,
                color = MutedBlueText.copy(alpha = 0.8f),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- PLATFORM COLOR CODED CAP CAPSULES ---
@Composable
fun PlatformPill(platform: String) {
    val (bgColor, textColor) = when (platform.uppercase()) {
        "YOUTUBE" -> PlatformYouTube.copy(alpha = 0.15f) to PlatformYouTube
        "TIKTOK" -> PlatformTikTok.copy(alpha = 0.15f) to PlatformTikTok
        "X" -> Color.White.copy(alpha = 0.12f) to Color.White
        else -> PlatformWeChat.copy(alpha = 0.15f) to PlatformWeChat
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(0.5.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = platform,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

// --- CUSTOM SCROLLING LEAD ELEMENT CARD ---
@Composable
fun LeadCardItem(
    lead: LeadEntity,
    selectedLanguage: String,
    onSaveToggle: () -> Unit,
    onDelete: () -> Unit,
    onPitchClick: () -> Unit
) {
    val context = LocalContext.current
    val formatter = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }
    val formattedTime = remember(lead.timestamp) { formatter.format(Date(lead.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lead_card_item_${lead.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlueSurface),
        border = BorderStroke(1.dp, if (lead.isSaved) AccentOrange.copy(alpha = 0.6f) else BorderColor.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row: User Identity information & Platform channel Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val url = LeadExporter.getPlatformSearchUrl(lead)
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                                Toast.makeText(
                                    context,
                                    when (selectedLanguage) {
                                        "中文" -> "正在精密跳转平台锁定此客户: ${lead.accountName}"
                                        "English" -> "Opening platform to locate buyer: ${lead.accountName}..."
                                        "日本語" -> "プラットフォームへジャンプして顧客を検索: ${lead.accountName}"
                                        "Português" -> "Indo para plataforma buscar cliente: ${lead.accountName}..."
                                        "العربية" -> "جاري الانتقال للبحث عن العميل: ${lead.accountName}"
                                        else -> "Locating buyer ${lead.accountName}..."
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "无法打开浏览器", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(4.dp)
                ) {
                    // Simple beautiful alpha user logo background
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepBlueContainer)
                            .border(0.5.dp, BorderColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lead.accountName.filter { it.isLetterOrDigit() }.take(1).uppercase(),
                            color = AuroraGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lead.accountName,
                                color = IceBlueText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "精准锁定此客户",
                                tint = AuroraGreen,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌍 ${lead.country}",
                                color = MutedBlueText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🏷️ ${lead.keyword}",
                                color = AuroraGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlatformPill(platform = lead.source)
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "删除线索",
                            tint = MutedBlueText.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body content description representing buying inquiries
            Text(
                text = lead.content,
                color = IceBlueText.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer controls and action lines
            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${AppTranslator.translate("lead_item_time", selectedLanguage)}: $formattedTime",
                    color = MutedBlueText,
                    fontSize = 11.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Toggle Save / Bookmark Lead
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("bookmark_button_${lead.id}")
                    ) {
                        Icon(
                            imageVector = if (lead.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "收藏线索",
                            tint = if (lead.isSaved) AccentOrange else MutedBlueText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Outreach pitch launcher button
                    Button(
                        onClick = onPitchClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepBlueContainer,
                            contentColor = AuroraGreen
                        ),
                        border = BorderStroke(1.dp, AuroraGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("pitch_launcher_${lead.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = AppTranslator.translate("lead_btn_pitch", selectedLanguage),
                            tint = AuroraGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppTranslator.translate("lead_btn_pitch", selectedLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- FRIENDLY EMPTY STATES ---
@Composable
fun EmptyStateLayout(
    tabIndex: Int,
    selectedCountry: String,
    equipmentKeyword: String,
    selectedLanguage: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(50))
                .background(DeepBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (tabIndex == 0) Icons.Default.Search else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = MutedBlueText,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (tabIndex == 0) AppTranslator.translate("empty_realtime_desc", selectedLanguage) else AppTranslator.translate("empty_saved_desc", selectedLanguage),
            color = IceBlueText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (tabIndex == 0) AppTranslator.translate("empty_realtime_sub", selectedLanguage) else AppTranslator.translate("empty_saved_sub", selectedLanguage),
            color = MutedBlueText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

// Helper spacer function to bypass name collisions
@Composable
fun Spacer(p: Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = p)
}

@Composable
fun LiveStatusDot(
    isScraping: Boolean,
    modifier: Modifier = Modifier
) {
    if (isScraping) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val radarGlowColor by infiniteTransition.animateColor(
            initialValue = AuroraGreen.copy(alpha = 0.3f),
            targetValue = AuroraGreen.copy(alpha = 0.8f),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "radarColor"
        )
        Box(
            modifier = modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(radarGlowColor)
        )
    } else {
        Box(
            modifier = modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(AuroraGreen)
        )
    }
}

@Composable
fun DialogProfileItem(label: String, value: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DeepBlueBG.copy(alpha = 0.3f))
            .pointerInput(value) {
                detectTapGestures(
                    onDoubleTap = {
                        if (value.isNotEmpty() && value != "N/A" && value != "加载中") {
                            clipboardManager.setText(AnnotatedString(value))
                            Toast.makeText(context, "✅ $label 已复制", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onTap = {
                        Toast.makeText(context, "💡 双击可复制: $value", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = MutedBlueText,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            color = IceBlueText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
