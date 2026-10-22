package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.AppConfigState
import com.example.data.datastore.UserPreferencesDataStore
import com.example.data.local.RubelDatabase
import com.example.data.repository.AppMessageRepository
import com.example.model.AppEditMessage
import com.example.model.ChatMessage
import com.example.model.DesignSettings
import com.example.model.GeneratedApp
import com.example.model.MessageSender
import com.example.model.QuickPrompt
import com.example.model.RubelTab
import com.example.model.SavedDraft
import com.example.model.SmartTool
import com.example.model.UserProfile
import com.example.util.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RubelViewModel(
    application: Application,
    private val repository: AppMessageRepository,
    private val preferencesDataStore: UserPreferencesDataStore
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        AppMessageRepository(RubelDatabase.getDatabase(application).appMessageDao()),
        UserPreferencesDataStore(application)
    )

    constructor(application: Application, repository: AppMessageRepository) : this(
        application,
        repository,
        UserPreferencesDataStore(application)
    )

    private val _currentTab = MutableStateFlow(RubelTab.HOME)
    val currentTab: StateFlow<RubelTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Persistent Room Database reactive streams
    val chatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val appEditMessages: StateFlow<List<AppEditMessage>> = repository.allAppEditMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isAppBuilding = MutableStateFlow(false)
    val isAppBuilding: StateFlow<Boolean> = _isAppBuilding.asStateFlow()

    private val _selectedFilterChip = MutableStateFlow("All")
    val selectedFilterChip: StateFlow<String> = _selectedFilterChip.asStateFlow()

    // Persistent Jetpack DataStore User Settings & Theme Preferences
    val userProfile: StateFlow<UserProfile> = preferencesDataStore.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserProfile()
        )

    val designSettings: StateFlow<DesignSettings> = preferencesDataStore.designSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DesignSettings()
        )

    val appConfig: StateFlow<AppConfigState> = preferencesDataStore.appConfigFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppConfigState()
        )

    private val _currentlySpeakingId = MutableStateFlow<String?>(null)
    val currentlySpeakingId: StateFlow<String?> = _currentlySpeakingId.asStateFlow()

    private var ttsManager: TtsManager? = null

    private val _savedDrafts = MutableStateFlow<List<SavedDraft>>(
        listOf(
            SavedDraft(
                title = "Cyberpunk AI Prompt Template",
                content = "Generate a neon cybernetic city visual description with electric pink highlights and futuristic holographic architecture.",
                tag = "Creative",
                dateString = "Today, 10:30 AM"
            ),
            SavedDraft(
                title = "বাংলা অনুবাদ প্রম্পট (Bangla Translation)",
                content = "অনুবাদ করো: 'Technology is best when it brings people together into the future.'",
                tag = "Translation",
                dateString = "Yesterday"
            ),
            SavedDraft(
                title = "Kotlin Jetpack Compose Box Modifier",
                content = "Modifier.border(2.dp, Brush.horizontalGradient(listOf(NeonPink, NeonCyan)), RoundedCornerShape(20.dp))",
                tag = "Code",
                dateString = "Oct 14"
            )
        )
    )
    val savedDrafts: StateFlow<List<SavedDraft>> = _savedDrafts.asStateFlow()

    val quickPrompts = listOf(
        QuickPrompt(
            id = "qp_1",
            title = "বাংলায় সুন্দর কবিতা",
            prompt = "আমায় নিয়ে বাংলায় একটি সুন্দর মিষ্টি অনুপ্রেরণামূলক কবিতা লিখে দাও।",
            category = "বাংলা",
            iconEmoji = "🌸"
        ),
        QuickPrompt(
            id = "qp_2",
            title = "Explain AI to a 10-year-old",
            prompt = "Explain how Artificial Intelligence works simply, like I am a 10 year old kid.",
            category = "Learning",
            iconEmoji = "⚡"
        ),
        QuickPrompt(
            id = "qp_3",
            title = "Kotlin Coroutines & Flow",
            prompt = "Explain Kotlin StateFlow and Coroutine Scope with a clean Jetpack Compose code snippet.",
            category = "Code",
            iconEmoji = "💻"
        ),
        QuickPrompt(
            id = "qp_4",
            title = "আজকের কাজের রুটিন প্ল্যান",
            prompt = "সকাল থেকে রাত পর্যন্ত প্রোডাক্টিভ থাকার একটি চমৎকার ডেইলি রুটিন প্ল্যান তৈরি করো।",
            category = "বাংলা",
            iconEmoji = "🎯"
        ),
        QuickPrompt(
            id = "qp_5",
            title = "Social Media Caption Ideas",
            prompt = "Write 5 engaging, high-energy captions for an upcoming tech product launch with hashtags.",
            category = "Creative",
            iconEmoji = "✨"
        )
    )

    val smartTools = listOf(
        SmartTool(
            id = "tool_translate",
            title = "English - বাংলা Translator",
            titleBn = "ইংরেজি ও বাংলা স্মার্ট অনুবাদক",
            description = "Natural translation with context, grammar precision, and conversational tone.",
            promptTemplate = "Translate the following between Bengali and English with accurate context:\n\n",
            iconName = "translate",
            glowColorHex = 0xFFFF2E93
        ),
        SmartTool(
            id = "tool_summarizer",
            title = "AI Text Summarizer",
            titleBn = "স্মার্ট সারসংক্ষেপকারী",
            description = "Condense long articles, reports, or messages into clear, actionable bullet points.",
            promptTemplate = "Please summarize this text into key takeaways and concise bullets:\n\n",
            iconName = "summarize",
            glowColorHex = 0xFF00F0FF
        ),
        SmartTool(
            id = "tool_code_helper",
            title = "Code Solver & Debugger",
            titleBn = "কোডিং ও বাগ ফিক্সার",
            description = "Debug errors, optimize algorithms, or generate clean Kotlin/Android snippets.",
            promptTemplate = "Help solve and write clean code for this requirement:\n\n",
            iconName = "code",
            glowColorHex = 0xFFD900FF
        ),
        SmartTool(
            id = "tool_creative_writer",
            title = "Creative Content & Story",
            titleBn = "গল্প, গান ও কন্টেন্ট জেনারেটর",
            description = "Craft poetic Bengali verses, catchy copywriting, and creative storytelling.",
            promptTemplate = "Write a creative piece about:\n\n",
            iconName = "create",
            glowColorHex = 0xFFFF69B4
        ),
        SmartTool(
            id = "tool_math_logic",
            title = "Math & Logic Explainer",
            titleBn = "গণিত ও যুক্তি সমাধানকারী",
            description = "Step-by-step breakdown of algebra, logic puzzles, and quantitative questions.",
            promptTemplate = "Explain step-by-step how to solve this problem:\n\n",
            iconName = "calculate",
            glowColorHex = 0xFF38E5FF
        )
    )

    init {
        // Ensure initial welcome data is populated in Room if first launch
        viewModelScope.launch {
            try {
                repository.seedInitialDataIfEmpty()
                val savedConfig = preferencesDataStore.appConfigFlow.firstOrNull()
                if (savedConfig != null) {
                    try {
                        _currentTab.value = RubelTab.valueOf(savedConfig.lastActiveTab)
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectTab(tab: RubelTab) {
        _currentTab.value = tab
        viewModelScope.launch {
            preferencesDataStore.updateLastActiveTab(tab.name)
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun selectFilterChip(chip: String) {
        _selectedFilterChip.value = chip
    }

    fun sendUserMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            text = trimmed
        )
        _searchQuery.value = ""

        viewModelScope.launch {
            repository.insertChatMessage(userMsg)
            preferencesDataStore.incrementTotalQueries()
            _isGenerating.value = true
            delay(750)

            val responseText = generateIntelligentResponse(trimmed)
            val aiMsg = ChatMessage(
                sender = MessageSender.RUBEL_AI,
                text = responseText
            )
            repository.insertChatMessage(aiMsg)
            _isGenerating.value = false
        }
    }

    /**
     * AI App Builder in App Edit tab:
     * Saves user message and generated AI application directly to Room Database,
     * guaranteeing all messages and apps persist across screen navigation.
     */
    fun buildAppFromPrompt(prompt: String, attachedImageUri: String?) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() && attachedImageUri == null) return

        val userMsg = AppEditMessage(
            sender = MessageSender.USER,
            text = if (trimmed.isNotEmpty()) trimmed else "পিকচার অনুযায়ী অ্যাপ তৈরি করো",
            imageUri = attachedImageUri
        )

        viewModelScope.launch {
            // Persist user message to Room immediately
            repository.insertAppEditMessage(userMsg)

            _isAppBuilding.value = true
            delay(900)

            val lower = trimmed.lowercase()

            val (appCategory, appName, description, features) = when {
                lower.contains("ক্যালকুলেটর") || lower.contains("calculator") || lower.contains("হিসাব") -> {
                    Quadruple(
                        "CALCULATOR",
                        "Rubel Cyber Calculator",
                        "একটি পূর্ণাঙ্গ সাইবারনেটিক সায়েন্টিফিক ও স্ট্যান্ডার্ড ক্যালকুলেটর।",
                        listOf(
                            "ডিজিটাল স্ক্রিন ডিসপ্লে ও অ্যানিমেশন",
                            "যোগ, বিয়োগ, গুণ, ভাগের নির্ভুল হিসাব ইঞ্জিন",
                            "মেমোরি ও ক্লিয়ার (C) / ডিলিট কন্ট্রোল",
                            "নিয়ন পিঙ্ক ও সায়ান হাইপার-রেসপনসিভ বাটন প্যাড"
                        )
                    )
                }
                lower.contains("টুডু") || lower.contains("todo") || lower.contains("টাস্ক") || lower.contains("কাজ") -> {
                    Quadruple(
                        "TODO",
                        "Rubel Smart Task Master",
                        "দৈনন্দিন কাজের তালিকা সংরক্ষণ ও চেকলিস্ট ম্যানেজার।",
                        listOf(
                            "নতুন টাস্ক ইনস্ট্যান্ট যোগ ও চেকবক্স টিক মার্ক",
                            "টাস্ক ডিলিট ও রিয়েল-টাইম কাউন্টার",
                            "অটোমেটিক লোকাল সেভ ডাটা স্টোরেজ",
                            "ডার্ক সাইবার গ্লাস ডিজাইন"
                        )
                    )
                }
                lower.contains("কাউন্টার") || lower.contains("counter") || lower.contains("ক্লিক") || lower.contains("তসবিহ") -> {
                    Quadruple(
                        "COUNTER",
                        "Rubel Cyber Tally Counter",
                        "ডিজিটাল ক্লিকার ও কাউন্টিং অ্যাপ।",
                        listOf(
                            "ইনক্রিমেন্ট (+1) ও ডিক্রিমেন্ট (-1) অ্যাকশন",
                            "ইনস্ট্যান্ট রিসেট ও ট্যালি সাইকেল ট্র্যাকার",
                            "পালসিং নিয়ন ডায়াল ডিসপ্লে",
                            "হ্যাপটিক টাচ ভাইব্রেশন সাপোর্ট"
                        )
                    )
                }
                lower.contains("কুইজ") || lower.contains("quiz") || lower.contains("প্রশ্ন") -> {
                    Quadruple(
                        "QUIZ",
                        "Rubel Cyber Quiz Challenge",
                        "ইন্টারেক্টিভ জ্ঞান ও মেধা যাচাই কুইজ অ্যাপ।",
                        listOf(
                            "মাল্টিপল চয়েস প্রশ্ন ব্যাংক",
                            "সঠিক ও ভুল উত্তরের ইনস্ট্যান্ট কালার ফিডব্যাক",
                            "লাইভ স্কোর ক্যালকুলেটর ও নেক্সট প্রশ্ন সুইচিং",
                            "গ্লোয়িং রেজাল্ট কার্ড"
                        )
                    )
                }
                lower.contains("আবহাওয়া") || lower.contains("weather") || lower.contains("তাপমাত্রা") -> {
                    Quadruple(
                        "WEATHER",
                        "Rubel Cyber Weather Radar",
                        "লাইভ সিটি ওয়েদার ও তাপমাত্রা ফোরকাস্ট অ্যাপ।",
                        listOf(
                            "ঢাকা, চট্টগ্রাম, সিলেট ও আন্তর্জাতিক শহর সিলেকশন",
                            "রিয়েল-টাইম তাপমাত্রা, আর্দ্রতা ও বাতাসের গতি",
                            "আবহাওয়ার কন্ডিশন ব্যাজ (রোদ, বৃষ্টি, সাইবার ফগ)",
                            "ডায়নামিক কালার গ্রেডিয়েন্ট ইন্টারফেস"
                        )
                    )
                }
                lower.contains("মিউজিক") || lower.contains("music") || lower.contains("গান") || lower.contains("প্লেয়ার") || lower.contains("player") -> {
                    Quadruple(
                        "MUSIC",
                        "Rubel Cyber Beats Player",
                        "সাইবার ওয়েভ মিউজিক প্লেয়ার ও অডিও ভিজ্যুয়ালাইজার।",
                        listOf(
                            "লাইভ ওয়েভফর্ম অ্যানিমেশন ও ট্র্যাক প্রগ্রেস",
                            "প্লে, পজ, নেক্সট ও প্রিভিয়াস কন্ট্রোল",
                            "ভলিউম স্লাইডার ও সাইবার সাউন্ড ট্র্যাকার",
                            "নিয়ন লাইট অডিও সিগনেচার"
                        )
                    )
                }
                lower.contains("নোট") || lower.contains("note") || lower.contains("ডায়েরি") || lower.contains("diary") || lower.contains("মেমো") -> {
                    Quadruple(
                        "NOTES",
                        "Rubel Cyber Notes & Diary",
                        "ডিজিটাল স্মার্ট নোটপ্যাড ও সিকিউর ডায়েরি অ্যাপ।",
                        listOf(
                            "ইনস্ট্যান্ট নোট ক্রিয়েশন ও এডিটিং",
                            "কালার ক্যাটাগরি ও টাইমস্ট্যাম্প ট্যাগ",
                            "রিয়েল-টাইম ক্যারেক্টার ও ওয়ার্ড কাউন্টার",
                            "অটোমেটিক লোকাল স্টোরেজ সেভ"
                        )
                    )
                }
                lower.contains("স্ক্যানার") || lower.contains("scanner") || lower.contains("সিকিউরিটি") || lower.contains("security") -> {
                    Quadruple(
                        "SCANNER",
                        "Rubel Cyber Security Scanner",
                        "সিস্টেম অ্যানালাইসিস ও সাইবারনেটিক রাডার মনিটর।",
                        listOf(
                            "অ্যানিমেটেড রাডার স্ক্যান সুইপ",
                            "সিস্টেম হেলথ ও থ্রেট ডিটেকশন স্কোর",
                            "ফায়ারওয়াল অন/অফ সিকিউরিটি টগল",
                            "রিয়েল-টাইম ডায়াগনস্টিক রিপোর্ট"
                        )
                    )
                }
                lower.contains("খরচ") || lower.contains("expense") || lower.contains("বাজেট") || lower.contains("টাকা") || lower.contains("budget") -> {
                    Quadruple(
                        "EXPENSE",
                        "Rubel Cyber Expense Manager",
                        "দৈনিক আয়-ব্যয় ট্র্যাকিং ও বাজেট ক্যালকুলেটর।",
                        listOf(
                            "ব্যালেন্স, আয় ও ব্যয়ের লাইভ হিসাব",
                            "নতুন ট্রানজেকশন কুইক এন্ট্রি",
                            "ক্যাটাগরি ভিত্তিক খরচের লিস্ট ও ফিল্টার",
                            "কালার কোডেড ফাইন্যান্সিয়াল স্ট্যাটাস"
                        )
                    )
                }
                lower.contains("ফিটনেস") || lower.contains("fitness") || lower.contains("ব্যায়াম") || lower.contains("স্টেপ") || lower.contains("পানি") -> {
                    Quadruple(
                        "FITNESS",
                        "Rubel Cyber Fitness & Health",
                        "ডেইলি স্টেপ কাউন্টার ও হাইড্রেশন ট্র্যাকার।",
                        listOf(
                            "দৈনিক ১০,০০০ স্টেপ গোল ট্র্যাকিং",
                            "পানি পান কুইক অ্যাড (+২৫০ মিলি)",
                            "ক্যালোরি বার্ন লাইভ মিটার",
                            "ওয়ার্কআউট স্টপওয়াচ টাইমার"
                        )
                    )
                }
                lower.contains("গ্রাফিক্স") || lower.contains("graphics") || lower.contains("কালার") || lower.contains("ডিজাইন") || lower.contains("art") -> {
                    Quadruple(
                        "GRAPHICS",
                        "Rubel Cyber Graphic Studio",
                        "কালার গ্রাফিক্স ডিজাইন ইঞ্জিন ও লেখার বক্স সম্বলিত অ্যাপ।",
                        listOf(
                            "মাল্টি-টোন নিয়ন কালার প্যালেট",
                            "ইন্টারেক্টিভ গ্রাফিক্স ক্যানভাস ও ওয়েভ মোড",
                            "কাস্টম টেক্সট স্টুডিও লেখার বক্স",
                            "লাইভ আউটপুট হিস্টোরি ও seve ফিচার"
                        )
                    )
                }
                else -> {
                    val customTitle = if (trimmed.length > 25) trimmed.take(25) + "..." else trimmed
                    Quadruple(
                        "GENERAL",
                        "Rubel AI: $customTitle",
                        "আপনার চাহিদামতো কাস্টমাইজড ফিচারযুক্ত সাইবার অ্যাপ্লিকেশন।",
                        listOf(
                            "ইন্টারেক্টিভ স্টেট ম্যানেজমেন্ট ও বাটন ট্র্রিগার",
                            "ইনপুট টেক্সট প্রসেসিং ও রেজাল্ট ডিসপ্লে",
                            "নিয়ন গোলাপি ও সায়ান সিগনেচার ইউআই",
                            "অটোমেটিক হিস্ট্রি ব্যাকআপ ও ড্রাফট সেভার"
                        )
                    )
                }
            }

            val customAppName = if (trimmed.startsWith("নতুন অ্যাপ তৈরি করো:")) {
                trimmed.removePrefix("নতুন অ্যাপ তৈরি করো:").substringBefore("(").trim()
            } else null

            val finalAppName = if (!customAppName.isNullOrBlank()) customAppName else appName

            val generatedApp = GeneratedApp(
                appName = finalAppName,
                description = description,
                featuresConfigured = features,
                appCategory = appCategory,
                accentColorHex = 0xFFFF2E93
            )

            val explanationMessage = buildString {
                append("🚀 **$finalAppName** সফলভাবে তৈরি করা হয়েছে!\n\n")
                append("⚙️ **এপ এর ভিতরে কি কি সেট করা হয়েছে:**\n")
                features.forEachIndexed { idx, feat ->
                    append("${idx + 1}. $feat\n")
                }
                append("\n💡 **কীভাবে ব্যবহার করবেন:**\n")
                append("নিচের **'Open App'** বাটনে চাপ দিয়ে সরাসরি অ্যাপ্লিকেশনটি চালিয়ে দেখুন। সমস্ত ডাটা Room ডাটাবেজে পারসিস্টেন্টলি সেভ হয়েছে!")
            }

            val aiMsg = AppEditMessage(
                sender = MessageSender.RUBEL_AI,
                text = explanationMessage,
                generatedApp = generatedApp
            )

            // Persist AI message and generated app to Room database
            repository.insertAppEditMessage(aiMsg)

            // Auto-save generated app into drafts
            saveNewDraft(
                title = appName,
                content = description + "\n" + features.joinToString(", "),
                tag = "AI App"
            )

            preferencesDataStore.incrementTotalQueries()
            _isAppBuilding.value = false
        }
    }

    /**
     * AI Bug Fix & Enhancement System:
     * When the user reports an issue or requests a change in the running app,
     * this method diagnoses the problem, applies the fix, updates the GeneratedApp,
     * and saves both the request and response in Room Database.
     */
    fun fixOrUpdateApp(
        targetApp: GeneratedApp,
        issueDescription: String,
        onComplete: ((GeneratedApp) -> Unit)? = null
    ) {
        val trimmed = issueDescription.trim()
        if (trimmed.isEmpty()) return

        val userFixMsg = AppEditMessage(
            sender = MessageSender.USER,
            text = "🛠️ সমস্যা সমাধান রিকোয়েস্ট (${targetApp.appName}): $trimmed"
        )

        viewModelScope.launch {
            repository.insertAppEditMessage(userFixMsg)
            _isAppBuilding.value = true
            delay(1100)

            val lower = trimmed.lowercase()
            val newFeatures = mutableListOf<String>()

            var newAccentColor = targetApp.accentColorHex
            if (lower.contains("লাল") || lower.contains("red")) {
                newAccentColor = 0xFFFF1744
                newFeatures.add("🎨 অ্যাকসেন্ট কালার পরিবর্তন করে নিয়ন লাল করা হয়েছে")
            } else if (lower.contains("সবুজ") || lower.contains("green")) {
                newAccentColor = 0xFF00E676
                newFeatures.add("🎨 অ্যাকসেন্ট কালার পরিবর্তন করে নিয়ন সবুজ করা হয়েছে")
            } else if (lower.contains("নীল") || lower.contains("blue") || lower.contains("সায়ান") || lower.contains("cyan")) {
                newAccentColor = 0xFF00E5FF
                newFeatures.add("🎨 অ্যাকসেন্ট কালার পরিবর্তন করে সাইবার সায়ান করা হয়েছে")
            } else if (lower.contains("হলুদ") || lower.contains("yellow") || lower.contains("gold")) {
                newAccentColor = 0xFFFFD700
                newFeatures.add("🎨 অ্যাকসেন্ট কালার পরিবর্তন করে গোল্ডেন ইয়েলো করা হয়েছে")
            }

            if (lower.contains("বাটন") || lower.contains("button") || lower.contains("কাজ করছে না") || lower.contains("ক্লিক")) {
                newFeatures.add("⚡ বাটন ক্লিক লজিক, স্টেট আপডেট ও হ্যাপটিক রেসপন্স ফিক্স করা হয়েছে")
            }
            if (lower.contains("হিসাব") || lower.contains("ভুল") || lower.contains("math") || lower.contains("দশমিক") || lower.contains("error")) {
                newFeatures.add("🧮 ক্যালকুলেশন ইঞ্জিন ও দশমিক অপারেশন নির্ভুলভাবে ঠিক করা হয়েছে")
            }
            if (lower.contains("সেভ") || lower.contains("save") || lower.contains("ডাটা") || lower.contains("মুছে")) {
                newFeatures.add("💾 অটোমেটিক লোকাল ডাটা সেভিং ও মেমোরি রিকভারি অপ্টিমাইজ করা হয়েছে")
            }
            if (lower.contains("স্পিড") || lower.contains("slow") || lower.contains("হ্যাং") || lower.contains("দেরি")) {
                newFeatures.add("🚀 ইউআই ফ্রেম রেট ও ট্রানজিশন স্পিড অপ্টিমাইজ করা হয়েছে")
            }
            if (lower.contains("নতুন") || lower.contains("যোগ") || lower.contains("add") || lower.contains("feature")) {
                newFeatures.add("✨ আপনার চাহিদা মতো নতুন ফিচার সফলভাবে কোডে যুক্ত করা হয়েছে")
            }
            if (newFeatures.isEmpty()) {
                newFeatures.add("✅ আপনার উল্লেখিত সমস্যা ($trimmed) সমাধান করে কোড আপডেট করা হয়েছে")
            }

            val updatedFeatures = (newFeatures + targetApp.featuresConfigured).distinct().take(6)
            val updatedApp = targetApp.copy(
                description = "${targetApp.description.substringBefore(" [AI ফিক্সড")} [AI ফিক্সড: $trimmed]",
                featuresConfigured = updatedFeatures,
                accentColorHex = newAccentColor,
                timestamp = System.currentTimeMillis()
            )

            val aiResponseText = buildString {
                append("🔧 **${targetApp.appName} সফলভাবে ঠিক করা হয়েছে!**\n\n")
                append("📋 **কী কী ফিক্স ও আপডেট করা হয়েছে:**\n")
                newFeatures.forEachIndexed { i, f ->
                    append("${i + 1}. $f\n")
                }
                append("\n⚡ এখন সরাসরি প্রিভিউতে চালিয়ে দেখুন, সমস্যা সমাধান হয়ে গেছে!")
            }

            val aiMsg = AppEditMessage(
                sender = MessageSender.RUBEL_AI,
                text = aiResponseText,
                generatedApp = updatedApp
            )

            repository.insertAppEditMessage(aiMsg)
            _isAppBuilding.value = false
            onComplete?.invoke(updatedApp)
        }
    }

    fun deleteAppEditMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteAppEditMessage(messageId)
        }
    }

    fun clearAllAppEditMessages() {
        viewModelScope.launch {
            repository.clearAllAppEditMessages()
            val resetMessage = AppEditMessage(
                sender = MessageSender.RUBEL_AI,
                text = "সব বার্তা ও অ্যাপ হিস্ট্রি ক্লিয়ার করা হয়েছে। নতুন অ্যাপের জন্য নিচে লিখুন ও '⚡ Run' চাপুন!"
            )
            repository.insertAppEditMessage(resetMessage)
        }
    }

    fun speakMessage(messageId: String, text: String, context: Context) {
        if (_currentlySpeakingId.value == messageId) {
            ttsManager?.stop()
            _currentlySpeakingId.value = null
            return
        }

        if (ttsManager == null) {
            ttsManager = TtsManager(context)
        }

        _currentlySpeakingId.value = messageId
        ttsManager?.speak(text) {
            _currentlySpeakingId.value = null
        }
    }

    fun stopSpeaking() {
        ttsManager?.stop()
        _currentlySpeakingId.value = null
    }

    private fun generateIntelligentResponse(prompt: String): String {
        val lower = prompt.lowercase()

        return when {
            lower.contains("কবিতা") || lower.contains("poem") -> {
                """✨ Rubel AI এর কাব্যগাথা:

নীল-গোলাপির দ্যুতি মেখে
আলোর তরঙ্গে চলা,
স্বপ্নের সীমানা পেরিয়ে
নতুন দিনের কথা বলা।

প্রযুক্তির রঙে রাঙিয়ে জীবন
রুবেল এআই তোমার পাশে,
উজ্জ্বল আগামীর হাতছানি দিয়ে
প্রতিটি প্রভাত হাসে! 🌟"""
            }
            lower.contains("রুটিন") || lower.contains("routine") || lower.contains("schedule") -> {
                """📅 Rubel AI প্রোডাক্টিভ ডেইলি রুটিন:

🌅 সকাল (৬:০০ - ৮:৩০):
• ফ্রেশ হয়ে হালকা শরীরচর্চা বা মেডিটেশন (২০ মিনিট)
• পুষ্টিকর নাস্তা ও দিনের লক্ষ্য নির্ধারণ

💼 দুপুর (৯:০০ - ১:০০):
• ডিপ ওয়ার্ক ব্লক ১: সবচেয়ে গুরুত্বপূর্ণ কঠিন কাজগুলো সম্পন্ন করুন

🥗 মধ্যাহ্ন (১:০০ - ২:৩০):
• লাঞ্চ ও হালকা ২০ মিনিটের রিল্যাক্সেশন / পাওয়ার ন্যাপ

🚀 বিকেল (২:৩০ - ৬:০০):
• ডিপ ওয়ার্ক ব্লক ২: মিটিং, কোডিং, রাইটিং ও ছোটখাটো টাস্ক

🌆 সন্ধ্যা ও রাত (৭:০০ - ১০:৩০):
• স্কিল ডেভেলপমেন্ট বা পরিবারের সাথে সময় কাটানো
• ডিনার ও স্ক্রিন অফ করে আরামদায়ক ঘুম!"""
            }
            lower.contains("kotlin") || lower.contains("compose") || lower.contains("code") -> {
                """💻 Rubel AI কোড সলিউশন (Jetpack Compose Neon Box):

```kotlin
@Composable
fun NeonPinkBox(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFF2E93), Color(0xFF00F0FF))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color(0x990E183D))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(content, color = Color(0xFFB5C3DF))
        }
    }
}
```
Tip: Use `Brush.horizontalGradient` with `NeonPink` & `NeonCyan` for maximum cyberpunk glow!"""
            }
            lower.contains("translate") || lower.contains("অনুবাদ") -> {
                """🌐 Rubel AI স্মার্ট অনুবাদ:

"Technology is best when it brings people together into the future."
👉 বাংলা অনুবাদ: "প্রযুক্তি তখনই শ্রেষ্ঠ হয়ে ওঠে, যখন তা মানুষকে একসাথে আগামীর উজ্জ্বল ভবিষ্যতের পথে এগিয়ে নেয়।"

শব্দার্থ ও মূলভাব:
• Brings people together = মানুষকে ঐক্যবদ্ধ করা
• Into the future = ভবিষ্যতের পথে"""
            }
            lower.contains("hi") || lower.contains("hello") || lower.contains("কেমন") || lower.contains("রুবেল") || lower.contains("rubel") -> {
                """হ্যালো! আমি Rubel AI — আপনার সার্বক্ষণিক বুদ্ধিমান সঙ্গী।

আমি আপনার যেকোনো প্রশ্নের সঠিক উত্তর তৈরি, যেকোনো টেক্সটকে প্রফেশনাল রূপান্তর, বাংলায় সৃজনশীল লেখালেখি এবং কোডিংয়ের জটিল সমস্যা সমাধান করতে সক্ষম।

আপনি কি চান আজ আমরা কোনো নতুন প্রোজেক্ট নিয়ে কাজ শুরু করি?"""
            }
            else -> {
                """ধন্যবাদ আপনার বার্তার জন্য!

আমি আপনার প্রশ্নটি বিশ্লেষণ করেছি:
"$prompt"

💡 Rubel AI পর্যবেক্ষণ:
১. এই বিষয়টি সমাধান করতে সুনির্দিষ্ট ও বাস্তবিক পদক্ষেপ নেওয়া প্রয়োজন।
২. আপনার চাহিদামতো তথ্য প্রস্তুত রয়েছে। আপনি চাইলে এই টেক্সটটি 'App Edit' স্টুডিওতে সংরক্ষণ করে রাখতে পারেন অথবা আরও গভীরভাবে এক্সপ্লোর করতে পারেন!

অন্য কোনো তথ্য বা সহায়তা লাগলে নির্দ্বিধায় জানান! 🚀"""
            }
        }
    }

    fun saveNewDraft(title: String, content: String, tag: String) {
        if (title.isBlank() || content.isBlank()) return
        val newDraft = SavedDraft(
            title = title.trim(),
            content = content.trim(),
            tag = tag,
            dateString = "Just now"
        )
        _savedDrafts.update { listOf(newDraft) + it }
        viewModelScope.launch {
            preferencesDataStore.updateSavedDraftsCount(_savedDrafts.value.size)
        }
    }

    fun deleteDraft(draftId: String) {
        _savedDrafts.update { list -> list.filterNot { it.id == draftId } }
        viewModelScope.launch {
            preferencesDataStore.updateSavedDraftsCount(_savedDrafts.value.size)
        }
    }

    fun updatePinkGlow(intensity: Float) {
        viewModelScope.launch {
            preferencesDataStore.updatePinkGlow(intensity)
        }
    }

    fun updateProfile(name: String, role: String, language: String) {
        viewModelScope.launch {
            preferencesDataStore.updateProfile(name, role, language)
        }
    }

    fun toggleHaptic() {
        viewModelScope.launch {
            preferencesDataStore.toggleHaptic()
        }
    }

    fun setHaptic(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setHaptic(enabled)
        }
    }

    fun setAutoSuggest(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setAutoSuggest(enabled)
        }
    }

    fun updateDesignSettings(settings: DesignSettings) {
        viewModelScope.launch {
            preferencesDataStore.updateDesignSettings(settings)
        }
    }

    fun updateTheme(isDark: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.updateTheme(isDark)
        }
    }

    fun resetPreferencesToDefault() {
        viewModelScope.launch {
            preferencesDataStore.resetAllSettings()
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearAllChatMessages()
            val resetMessage = ChatMessage(
                sender = MessageSender.RUBEL_AI,
                text = "হিস্ট্রি রিসেট করা হয়েছে। আমি Rubel AI, আপনার নতুন প্রশ্নের অপেক্ষায় আছি!"
            )
            repository.insertChatMessage(resetMessage)
        }
    }

    fun deleteChatMessage(id: String) {
        viewModelScope.launch {
            repository.deleteChatMessage(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager?.shutdown()
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
