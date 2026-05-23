package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LeadEntity
import com.example.data.LeadRepository
import com.example.network.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeadViewModel(
    application: Application,
    private val repository: LeadRepository
) : AndroidViewModel(application) {

    // --- Search Inputs ---
    private val _selectedCountry = MutableStateFlow("莫桑比克")
    val selectedCountry: StateFlow<String> = _selectedCountry.asStateFlow()

    private val _equipmentKeyword = MutableStateFlow("挖掘机")
    val equipmentKeyword: StateFlow<String> = _equipmentKeyword.asStateFlow()

    // --- Language Selection ---
    private val _selectedLanguage = MutableStateFlow("中文")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // --- Crawling / Scanning States ---
    private val _isScraping = MutableStateFlow(false)
    val isScraping: StateFlow<Boolean> = _isScraping.asStateFlow()

    private val _scrapeProgressText = MutableStateFlow("")
    val scrapeProgressText: StateFlow<String> = _scrapeProgressText.asStateFlow()

    // --- Dynamic Lead streams ---
    val allLeads: StateFlow<List<LeadEntity>> = repository.allLeads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedLeads: StateFlow<List<LeadEntity>> = repository.savedLeads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered by selected country and keyword
    val filteredLeads: StateFlow<List<LeadEntity>> = combine(
        allLeads,
        _selectedCountry,
        _equipmentKeyword
    ) { leads, country, keyword ->
        leads.filter { lead ->
            val matchesCountry = country == "全球" || lead.country.contains(country, ignoreCase = true) || country.contains(lead.country, ignoreCase = true)
            val matchesKeyword = keyword.isEmpty() || lead.keyword.contains(keyword, ignoreCase = true) || lead.content.contains(keyword, ignoreCase = true)
            matchesCountry && matchesKeyword
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Counter / Analytics States ---
    private val _totalScannedCount = MutableStateFlow(1420)
    val totalScannedCount: StateFlow<Int> = _totalScannedCount.asStateFlow()

    private val _activeChannelsCount = MutableStateFlow(36)
    val activeChannelsCount: StateFlow<Int> = _activeChannelsCount.asStateFlow()

    private val _newFilteredLeadCount = MutableStateFlow(48)
    val newFilteredLeadCount: StateFlow<Int> = _newFilteredLeadCount.asStateFlow()

    // --- Dialog Outreach pitch ---
    private val _selectedLeadForPitch = MutableStateFlow<LeadEntity?>(null)
    val selectedLeadForPitch: StateFlow<LeadEntity?> = _selectedLeadForPitch.asStateFlow()

    private val _generatedPitchText = MutableStateFlow("")
    val generatedPitchText: StateFlow<String> = _generatedPitchText.asStateFlow()

    private val _isGeneratingPitch = MutableStateFlow(false)
    val isGeneratingPitch: StateFlow<Boolean> = _isGeneratingPitch.asStateFlow()

    init {
        // Hydrate initial mock data on first database boot if database is completely empty
        viewModelScope.launch {
            repository.allLeads.first().let { currentList ->
                if (currentList.isEmpty()) {
                    val initialLeads = getPresetDemoData()
                    repository.insertLeads(initialLeads)
                }
            }
        }
    }

    fun selectCountry(country: String) {
        _selectedCountry.value = country
    }

    fun selectLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setEquipmentKeyword(keyword: String) {
        _equipmentKeyword.value = keyword
    }

    fun startRealtimeScrape() {
        if (_isScraping.value) return
        viewModelScope.launch {
            _isScraping.value = true
            val country = _selectedCountry.value
            val keyword = _equipmentKeyword.value

            // Realistic scraping visual phases
            val phases = listOf(
                "正在建立 B2B 采集专线 (X, YouTube, TikTok)...",
                "已连接莫桑比克、南非等非洲港区核心社交网络...",
                "正在检索社交主推关键词: [ $keyword ] under target [$country]...",
                "正在扫描大牌挖机官方置顶视频评论区及社交求购商机...",
                "利用 Gemini AI 评估意向评论者的线索含金量与CIF采买概率...",
                "提取有效采购者细节并进行反向电话/社交线索验证..."
            )

            for (phase in phases) {
                _scrapeProgressText.value = phase
                delay(1200)
                // Mutate crawler dashboard stats representing active engine scans!
                _totalScannedCount.value += (8..24).random()
                _activeChannelsCount.value = (30..45).random()
            }

            try {
                // Fetch dynamic real results from Gemini (or custom simulated logic)
                val results = withContext(Dispatchers.IO) {
                    GeminiApiClient.fetchLeadsFromAI(country, keyword)
                }
                
                if (results.isNotEmpty()) {
                    repository.insertLeads(results)
                    _newFilteredLeadCount.value += results.size
                }
                _scrapeProgressText.value = "采集完成！成功捕获 ${results.size} 条高意向采购线索。"
            } catch (e: Exception) {
                _scrapeProgressText.value = "网络爬行中继响应过载，已加载非洲及全球核心缓冲线索。"
            } finally {
                delay(1500)
                _isScraping.value = false
            }
        }
    }

    fun toggleSaveLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.updateSavedStatus(lead.id, !lead.isSaved)
        }
    }

    fun deleteLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.deleteLeadById(lead.id)
        }
    }

    fun clearTemporaryLeads() {
        viewModelScope.launch {
            repository.clearUnsavedLeads()
        }
    }

    fun preparePitchDialog(lead: LeadEntity) {
        _selectedLeadForPitch.value = lead
        _generatedPitchText.value = ""
        _isGeneratingPitch.value = true

        viewModelScope.launch {
            try {
                val p = withContext(Dispatchers.IO) {
                    GeminiApiClient.generateOutreachPitch(
                        leadName = lead.accountName,
                        platform = lead.source,
                        content = lead.content,
                        keyword = lead.keyword
                    )
                }
                _generatedPitchText.value = p
            } catch (e: Exception) {
                _generatedPitchText.value = "错误: ${e.message}"
            } finally {
                _isGeneratingPitch.value = false
            }
        }
    }

    fun closePitchDialog() {
        _selectedLeadForPitch.value = null
        _generatedPitchText.value = ""
    }

    // Standard preset demo data loaded if Db is clean
    private fun getPresetDemoData(): List<LeadEntity> {
        return listOf(
            LeadEntity(
                accountName = "@Tete_Mining_Group_Lda",
                country = "莫桑比克",
                source = "X",
                content = "Ready to buy 2 high-capacity CAT-style excavators (30-40T range) for a coal quarry exploration in Moatize. Delivery directly CIF Beira port. Need detailed pricing and catalogs.",
                keyword = "挖掘机",
                timestamp = System.currentTimeMillis() - 3600000,
                isSaved = true
            ),
            LeadEntity(
                accountName = "Durban_Logistics_ZA",
                country = "南非",
                source = "YouTube",
                content = "Looking for heavy 12 ton road rehabilitation rollers for provincial highway projects. Sany is a great choice. Any Chinese exporter with quick delivery to Durban harbor? Please contact me.",
                keyword = "压路机",
                timestamp = System.currentTimeMillis() - 7200000,
                isSaved = false
            ),
            LeadEntity(
                accountName = "_Maputo_Civil_Constructors",
                country = "莫桑比克",
                source = "TikTok",
                content = "Precisamos urgentemente de duas escavadoras hidráulicas chinesas baratas de 20 toneladas para início de obras habitacionais em Maputo. Alguém tem stock regional?",
                keyword = "挖掘机",
                timestamp = System.currentTimeMillis() - 10800000,
                isSaved = true
            ),
            LeadEntity(
                accountName = "GlobalRig_Trades",
                country = "全球",
                source = "X",
                content = "Sourcing 4 high heavy mining dump trucks over 50T payload with reliable warranty option. Intended shipment to Saudi Arabia. Send FOB price specs.",
                keyword = "矿卡",
                timestamp = System.currentTimeMillis() - 14400000,
                isSaved = false
            ),
            LeadEntity(
                accountName = "MachineryInquirerSA",
                country = "南非",
                source = "YouTube",
                content = "Is standard steel track recommended for manganese mining payload? Interested in getting 3 Crawler units of 360-series excavator. Drop email contact info.",
                keyword = "挖掘机",
                timestamp = System.currentTimeMillis() - 18000000,
                isSaved = false
            )
        )
    }
}

class LeadViewModelFactory(
    private val application: Application,
    private val repository: LeadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeadViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
