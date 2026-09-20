package com.example

import com.example.network.PipedreamWebhookService
import com.example.network.WebhookResult
import com.example.ui.GenicAiViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPlaceholderUrlDetection() = runTest {
    val service = PipedreamWebhookService()
    val result = service.sendStoryToWebhook(
      url = "https://[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]",
      story = "Once upon a time in a cyberpunk metropolis..."
    )
    assertTrue("Should detect placeholder URL", result is WebhookResult.ConfigurationRequired)
  }

  @Test
  fun testViewModelSampleStoryLoading() {
    val viewModel = GenicAiViewModel()
    assertEquals("", viewModel.storyText.value)

    viewModel.loadSampleStory(0)
    assertTrue("Sample story should be loaded", viewModel.storyText.value.isNotEmpty())
    assertTrue("Story should contain narrative", viewModel.storyText.value.contains("Neon Archipelago"))

    viewModel.clearStory()
    assertEquals("", viewModel.storyText.value)
  }

  @Test
  fun testViewModelRecentProjects() {
    val viewModel = GenicAiViewModel()
    viewModel.addCreatedProject("Nebula Journey")
    assertTrue(viewModel.recentProjects.value.contains("Nebula Journey"))
  }
}
