package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.PipedreamWebhookService
import com.example.network.WebhookResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WebhookUiState {
  object Idle : WebhookUiState()
  data class Loading(val message: String = "Sending story to Pipedream webhook...") : WebhookUiState()
  data class Success(
    val statusCode: Int,
    val responseBody: String,
    val payloadPreview: String,
    val timestamp: Long
  ) : WebhookUiState()
  data class Error(
    val message: String,
    val statusCode: Int? = null,
    val details: String? = null
  ) : WebhookUiState()
  data class NeedsUrl(
    val message: String,
    val currentUrl: String
  ) : WebhookUiState()
}

sealed class GenicModal {
  object CreateProject : GenicModal()
  object EditWithAi : GenicModal()
  object GenerateImage : GenicModal()
  object ConfigureWebhook : GenicModal()
}

class GenicAiViewModel(
  private val webhookService: PipedreamWebhookService = PipedreamWebhookService()
) : ViewModel() {

  companion object {
    const val DEFAULT_WEBHOOK_URL = "https://[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]"
    
    val SAMPLE_STORIES = listOf(
      """The Neon Archipelago: In the year 2184, floating cyber-atolls shimmer across the Pacific basin. Kaelen, an apprentice data-diver, plunges into the bioluminescent depths of Old San Francisco. There, buried among silicate ruins, sleeps the Core Codex—the last decentralized archive of human emotion. When a rogue neural swarm awakens to claim it, Kaelen must pilot his skim-craft through razor storms and neon geysers to deliver the truth to the outer colonies before the grid goes dark forever.""".trimIndent(),
      """The Chrono Weaver of Sol: Deep inside the solar observatories orbiting Mercury, Lyra weaves strands of polarized solar wind into chronological tapestries. Each photon sequence carries whispers from parallel timelines. When an unauthorized signal manifests as a repeating golden loop, she realizes someone is altering the timeline of Earth's first interstellar voyage. With her AI chronometer racing against decay, she prepares to make the ultimate jump.""".trimIndent(),
      """Whispers in the Obsidian Valley: An ancient geological expedition on Kepler-452b discovers towering monoliths of black obsidian that emit harmonic vibrations at dusk. Dr. Aris Cole records the resonance, only to discover that the rock structures are alive—a planetary neural network that records every star that has ever died in the Orion Arm.""".trimIndent()
    )
  }

  private val _storyText = MutableStateFlow("")
  val storyText: StateFlow<String> = _storyText.asStateFlow()

  private val _webhookUrl = MutableStateFlow(DEFAULT_WEBHOOK_URL)
  val webhookUrl: StateFlow<String> = _webhookUrl.asStateFlow()

  private val _uiState = MutableStateFlow<WebhookUiState>(WebhookUiState.Idle)
  val uiState: StateFlow<WebhookUiState> = _uiState.asStateFlow()

  private val _activeModal = MutableStateFlow<GenicModal?>(null)
  val activeModal: StateFlow<GenicModal?> = _activeModal.asStateFlow()

  private val _infoBanner = MutableStateFlow<String?>(null)
  val infoBanner: StateFlow<String?> = _infoBanner.asStateFlow()

  // Project state
  private val _recentProjects = MutableStateFlow(
    listOf("Cyberpunk Neon Odyssey", "Quantum Echoes Docu", "Solaris Expedition")
  )
  val recentProjects: StateFlow<List<String>> = _recentProjects.asStateFlow()

  fun onStoryChange(newText: String) {
    _storyText.value = newText
    if (_uiState.value is WebhookUiState.Error || _uiState.value is WebhookUiState.NeedsUrl) {
      _uiState.value = WebhookUiState.Idle
    }
  }

  fun onWebhookUrlChange(newUrl: String) {
    _webhookUrl.value = newUrl
  }

  fun resetWebhookUrl() {
    _webhookUrl.value = DEFAULT_WEBHOOK_URL
    _infoBanner.value = "Webhook URL reset to default placeholder."
  }

  fun loadSampleStory(index: Int = 0) {
    val selected = SAMPLE_STORIES.getOrElse(index) { SAMPLE_STORIES.first() }
    _storyText.value = selected
    _infoBanner.value = "Sample story loaded (${selected.split("\\s+".toRegex()).size} words)."
  }

  fun clearStory() {
    _storyText.value = ""
    _uiState.value = WebhookUiState.Idle
  }

  fun openModal(modal: GenicModal) {
    _activeModal.value = modal
  }

  fun closeModal() {
    _activeModal.value = null
  }

  fun clearInfoBanner() {
    _infoBanner.value = null
  }

  fun clearStatus() {
    _uiState.value = WebhookUiState.Idle
  }

  fun addCreatedProject(name: String) {
    if (name.isNotBlank()) {
      _recentProjects.value = listOf(name.trim()) + _recentProjects.value
      _infoBanner.value = "Project \"$name\" created successfully!"
    }
  }

  fun generateVideoFromStory() {
    val story = _storyText.value.trim()
    if (story.isEmpty()) {
      _uiState.value = WebhookUiState.Error("Please enter or paste a story first.")
      return
    }

    val url = _webhookUrl.value.trim()
    _uiState.value = WebhookUiState.Loading("Dispatching story to Pipedream webhook...")

    viewModelScope.launch {
      when (val result = webhookService.sendStoryToWebhook(url = url, story = story)) {
        is WebhookResult.Success -> {
          _uiState.value = WebhookUiState.Success(
            statusCode = result.statusCode,
            responseBody = result.responseBody,
            payloadPreview = result.payloadPreview,
            timestamp = result.timestamp
          )
        }
        is WebhookResult.Error -> {
          _uiState.value = WebhookUiState.Error(
            message = result.message,
            statusCode = result.statusCode,
            details = result.rawDetails
          )
        }
        is WebhookResult.ConfigurationRequired -> {
          _uiState.value = WebhookUiState.NeedsUrl(
            message = result.message,
            currentUrl = result.currentUrl
          )
        }
      }
    }
  }
}
