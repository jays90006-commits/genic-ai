package com.example.ui

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GenicAccentGreen
import com.example.ui.theme.GenicAccentRed
import com.example.ui.theme.GenicBackground
import com.example.ui.theme.GenicCard
import com.example.ui.theme.GenicCardBorder
import com.example.ui.theme.GenicPrimary
import com.example.ui.theme.GenicPrimaryLight
import com.example.ui.theme.GenicSecondary
import com.example.ui.theme.GenicSurface
import com.example.ui.theme.GenicTertiary
import com.example.ui.theme.GenicTextMuted
import com.example.ui.theme.GenicTextPrimary
import com.example.ui.theme.GenicTextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenicAiScreen(
  viewModel: GenicAiViewModel,
  modifier: Modifier = Modifier
) {
  val storyText by viewModel.storyText.collectAsState()
  val webhookUrl by viewModel.webhookUrl.collectAsState()
  val uiState by viewModel.uiState.collectAsState()
  val activeModal by viewModel.activeModal.collectAsState()
  val infoBanner by viewModel.infoBanner.collectAsState()

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(infoBanner) {
    infoBanner?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearInfoBanner()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = GenicBackground,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 680.dp)
          .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
      ) {
        // App Header & Brand
        item(key = "header") {
          GenicHeader(
            webhookUrl = webhookUrl,
            onConfigureWebhookClick = { viewModel.openModal(GenicModal.ConfigureWebhook) }
          )
        }

        // 4 Main Action Buttons Grid
        item(key = "four_buttons_grid") {
          Text(
            text = "ACTIONS & TOOLS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GenicSecondary,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(bottom = 6.dp)
          )

          FourButtonsGrid(
            onCreateProjectClick = { viewModel.openModal(GenicModal.CreateProject) },
            onEditWithAiClick = { viewModel.openModal(GenicModal.EditWithAi) },
            onGenerateVideoClick = {
              coroutineScope.launch {
                listState.animateScrollToItem(index = 2)
              }
              if (storyText.isNotBlank()) {
                viewModel.generateVideoFromStory()
              }
            },
            onGenerateImageClick = { viewModel.openModal(GenicModal.GenerateImage) }
          )
        }

        // Story Input Section below Generate Video
        item(key = "story_input_section") {
          StoryInputArea(
            storyText = storyText,
            webhookUrl = webhookUrl,
            uiState = uiState,
            onStoryChange = { viewModel.onStoryChange(it) },
            onPasteClipboard = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
              val clip = clipboard?.primaryClip
              if (clip != null && clip.itemCount > 0) {
                val pasted = clip.getItemAt(0).text?.toString().orEmpty()
                if (pasted.isNotEmpty()) {
                  viewModel.onStoryChange(
                    if (storyText.isEmpty()) pasted else "$storyText\n\n$pasted"
                  )
                }
              }
            },
            onLoadSampleStory = { viewModel.loadSampleStory((0..2).random()) },
            onClearStory = { viewModel.clearStory() },
            onConfigureWebhook = { viewModel.openModal(GenicModal.ConfigureWebhook) },
            onExecutePost = { viewModel.generateVideoFromStory() }
          )
        }

        // API Result Status Details (if available)
        item(key = "api_status_section") {
          AnimatedVisibility(
            visible = uiState !is WebhookUiState.Idle,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
          ) {
            ApiExecutionStatusCard(
              uiState = uiState,
              onConfigureUrl = { viewModel.openModal(GenicModal.ConfigureWebhook) },
              onDismiss = { viewModel.clearStatus() },
              onRetry = { viewModel.generateVideoFromStory() }
            )
          }
        }
      }
    }
  }

  // Modals for the 4 button actions & Webhook Config
  when (activeModal) {
    is GenicModal.CreateProject -> {
      CreateProjectDialog(
        onDismiss = { viewModel.closeModal() },
        onConfirm = { name ->
          viewModel.addCreatedProject(name)
          viewModel.closeModal()
        }
      )
    }
    is GenicModal.EditWithAi -> {
      EditWithAiDialog(
        onDismiss = { viewModel.closeModal() }
      )
    }
    is GenicModal.GenerateImage -> {
      GenerateImageDialog(
        onDismiss = { viewModel.closeModal() }
      )
    }
    is GenicModal.ConfigureWebhook -> {
      ConfigureWebhookDialog(
        initialUrl = webhookUrl,
        onDismiss = { viewModel.closeModal() },
        onSave = { newUrl ->
          viewModel.onWebhookUrlChange(newUrl)
          viewModel.closeModal()
        },
        onReset = {
          viewModel.resetWebhookUrl()
          viewModel.closeModal()
        }
      )
    }
    null -> {}
  }
}

/**
 * Top brand header with modern styling, subtitle, and webhook status indicator.
 */
@Composable
fun GenicHeader(
  webhookUrl: String,
  onConfigureWebhookClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = GenicCard),
    border = BorderStroke(1.dp, GenicCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.linearGradient(
                  listOf(GenicPrimary, GenicSecondary)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Genic AI Logo Icon",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          Column {
            Text(
              text = "Genic AI",
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold,
              color = GenicTextPrimary,
              letterSpacing = (-0.5).sp
            )
            Text(
              text = "Next-Gen AI Media & Video Studio",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = GenicTextSecondary
            )
          }
        }

        // Webhook config chip
        Surface(
          onClick = onConfigureWebhookClick,
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier.testTag("configure_webhook_chip")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = "Webhook Settings",
              tint = GenicSecondary,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Webhook",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = GenicTextPrimary
            )
          }
        }
      }

      HorizontalDivider(color = Color(0xFF1F293D), thickness = 1.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val isCustom = !webhookUrl.contains("[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]")
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (isCustom) GenicAccentGreen else Color(0xFFF59E0B))
          )
          Text(
            text = if (isCustom) "Pipedream Connected" else "Pipedream: Template URL",
            fontSize = 11.sp,
            color = GenicTextSecondary,
            fontWeight = FontWeight.Medium
          )
        }

        Text(
          text = "PWA Ready • Mobile UI",
          fontSize = 11.sp,
          color = GenicTextMuted
        )
      }
    }
  }
}

/**
 * The 4 main requested buttons arranged in a clean, modern 2x2 grid layout:
 * 1. "Create project" (Plus icon)
 * 2. "Edit with AI" (Clapperboard icon)
 * 3. "Generate video" (Video play icon)
 * 4. "Generate image" (Magic sparkle icon)
 */
@Composable
fun FourButtonsGrid(
  onCreateProjectClick: () -> Unit,
  onEditWithAiClick: () -> Unit,
  onGenerateVideoClick: () -> Unit,
  onGenerateImageClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Row 1: Create project & Edit with AI
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      GenicActionButton(
        modifier = Modifier
          .weight(1f)
          .testTag("btn_create_project"),
        title = "Create project",
        subtitle = "New canvas & scene",
        icon = Icons.Default.Add,
        accentColor = GenicPrimary,
        gradientStart = Color(0xFF311042),
        gradientEnd = Color(0xFF1A132F),
        onClick = onCreateProjectClick
      )

      GenicActionButton(
        modifier = Modifier
          .weight(1f)
          .testTag("btn_edit_with_ai"),
        title = "Edit with AI",
        subtitle = "Clapperboard copilot",
        icon = Icons.Default.Movie,
        accentColor = Color(0xFF6366F1),
        gradientStart = Color(0xFF1E1B4B),
        gradientEnd = Color(0xFF14192F),
        onClick = onEditWithAiClick
      )
    }

    // Row 2: Generate video & Generate image
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      GenicActionButton(
        modifier = Modifier
          .weight(1f)
          .testTag("btn_generate_video"),
        title = "Generate video",
        subtitle = "Story to Pipedream",
        icon = Icons.Default.PlayArrow,
        accentColor = GenicSecondary,
        gradientStart = Color(0xFF063342),
        gradientEnd = Color(0xFF0F1E33),
        isFeatured = true,
        onClick = onGenerateVideoClick
      )

      GenicActionButton(
        modifier = Modifier
          .weight(1f)
          .testTag("btn_generate_image"),
        title = "Generate image",
        subtitle = "Magic sparkle art",
        icon = Icons.Default.AutoAwesome,
        accentColor = GenicTertiary,
        gradientStart = Color(0xFF38102A),
        gradientEnd = Color(0xFF1F142A),
        onClick = onGenerateImageClick
      )
    }
  }
}

/**
 * Individual action card for the 4-button layout.
 */
@Composable
fun GenicActionButton(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  gradientStart: Color,
  gradientEnd: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isFeatured: Boolean = false
) {
  Card(
    modifier = modifier
      .height(115.dp)
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = GenicCard),
    border = BorderStroke(
      width = if (isFeatured) 1.5.dp else 1.dp,
      color = if (isFeatured) accentColor.copy(alpha = 0.7f) else GenicCardBorder
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(gradientStart.copy(alpha = 0.6f), gradientEnd.copy(alpha = 0.9f))
          )
        )
        .padding(14.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = "$title Icon",
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
          }

          if (isFeatured) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = accentColor.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
            ) {
              Text(
                text = "ACTIVE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Column {
          Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = GenicTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = GenicTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

/**
 * Text input area positioned below the "Generate video" button so users can paste long stories.
 * Includes character/word counter, quick sample loader, clear button, and the POST button.
 */
@Composable
fun StoryInputArea(
  storyText: String,
  webhookUrl: String,
  uiState: WebhookUiState,
  onStoryChange: (String) -> Unit,
  onPasteClipboard: () -> Unit,
  onLoadSampleStory: () -> Unit,
  onClearStory: () -> Unit,
  onConfigureWebhook: () -> Unit,
  onExecutePost: () -> Unit,
  modifier: Modifier = Modifier
) {
  val wordCount = if (storyText.isBlank()) 0 else storyText.trim().split(Regex("\\s+")).size
  val charCount = storyText.length
  val isSending = uiState is WebhookUiState.Loading

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = GenicCard),
    border = BorderStroke(1.2.dp, GenicSecondary.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header of the story input section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = GenicSecondary,
            modifier = Modifier.size(20.dp)
          )
          Column {
            Text(
              text = "Story & Script Input",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = GenicTextPrimary
            )
            Text(
              text = "Paste narrative for 'Generate video' POST webhook",
              fontSize = 11.sp,
              color = GenicTextSecondary
            )
          }
        }

        // Word count badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B)
        ) {
          Text(
            text = "$wordCount words • $charCount chars",
            fontSize = 11.sp,
            color = if (wordCount > 0) GenicSecondary else GenicTextMuted,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Large multi-line story input field
      OutlinedTextField(
        value = storyText,
        onValueChange = onStoryChange,
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .testTag("story_input_field"),
        placeholder = {
          Text(
            text = "Paste your long story, screenplay, scene prompt, or script here...\n\nExample: In the year 2184, floating cyber-atolls shimmer across the Pacific basin...",
            fontSize = 13.sp,
            color = GenicTextMuted,
            lineHeight = 19.sp
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF0D1321),
          unfocusedContainerColor = Color(0xFF0D1321),
          focusedBorderColor = GenicSecondary,
          unfocusedBorderColor = GenicCardBorder,
          focusedTextColor = GenicTextPrimary,
          unfocusedTextColor = GenicTextPrimary,
          cursorColor = GenicSecondary
        ),
        shape = RoundedCornerShape(14.dp)
      )

      // Quick action controls row: Sample story, Paste, Clear
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Paste from clipboard button
          OutlinedButton(
            onClick = onPasteClipboard,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.testTag("btn_paste_story")
          ) {
            Icon(
              imageVector = Icons.Default.ContentPaste,
              contentDescription = "Paste Clipboard",
              tint = GenicTextSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Paste",
              fontSize = 12.sp,
              color = GenicTextSecondary
            )
          }

          // Sample story button
          OutlinedButton(
            onClick = onLoadSampleStory,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.testTag("btn_sample_story")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Load Sample Story",
              tint = GenicPrimaryLight,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Sample",
              fontSize = 12.sp,
              color = GenicPrimaryLight
            )
          }
        }

        if (storyText.isNotEmpty()) {
          TextButton(
            onClick = onClearStory,
            modifier = Modifier.testTag("btn_clear_story")
          ) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear story",
              tint = GenicTextMuted,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Clear",
              fontSize = 12.sp,
              color = GenicTextMuted
            )
          }
        }
      }

      // Current Webhook Target Banner
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = null,
              tint = GenicSecondary,
              modifier = Modifier.size(16.dp)
            )
            Column {
              Text(
                text = "Target Pipedream Webhook:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = GenicTextMuted
              )
              Text(
                text = webhookUrl,
                fontSize = 12.sp,
                color = if (webhookUrl.contains("[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]")) Color(0xFFFBBF24) else GenicSecondary,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          IconButton(
            onClick = onConfigureWebhook,
            modifier = Modifier.size(32.dp).testTag("btn_edit_webhook_icon")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit webhook URL",
              tint = GenicTextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Big Primary POST Button for "Generate Video"
      Button(
        onClick = onExecutePost,
        enabled = !isSending,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor = GenicSecondary, spotColor = GenicSecondary)
          .testTag("btn_execute_generate_video"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = GenicSecondary,
          disabledContainerColor = Color(0xFF1E293B)
        )
      ) {
        if (isSending) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Color.White,
            strokeWidth = 2.5.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Sending to Pipedream...",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        } else {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Generate Video (POST API)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
          )
        }
      }
    }
  }
}

/**
 * Result display card showing live response details, status codes, payload preview,
 * or error instructions.
 */
@Composable
fun ApiExecutionStatusCard(
  uiState: WebhookUiState,
  onConfigureUrl: () -> Unit,
  onDismiss: () -> Unit,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  when (uiState) {
    is WebhookUiState.Success -> {
      Card(
        modifier = modifier.fillMaxWidth().testTag("api_status_success_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF06281E)),
        border = BorderStroke(1.dp, GenicAccentGreen.copy(alpha = 0.6f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = GenicAccentGreen,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Webhook Dispatched (HTTP ${uiState.statusCode})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GenicAccentGreen
              )
            }
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = GenicTextMuted,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Text(
            text = "Timestamp: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(uiState.timestamp))}",
            fontSize = 11.sp,
            color = GenicTextSecondary
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF041812),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "Response Body:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GenicAccentGreen
              )
              Text(
                text = uiState.responseBody,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = GenicTextPrimary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }

    is WebhookUiState.NeedsUrl -> {
      Card(
        modifier = modifier.fillMaxWidth().testTag("api_status_needs_url_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1B07)),
        border = BorderStroke(1.dp, Color(0xFFF59E0B))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Configuration required",
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Pipedream Webhook URL Setup",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFBBF24)
            )
          }

          Text(
            text = "The webhook URL is currently set to the placeholder `[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]`. Click below to paste your actual live Pipedream webhook endpoint.",
            fontSize = 12.sp,
            color = Color(0xFFFDE68A),
            lineHeight = 18.sp
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            TextButton(onClick = onDismiss) {
              Text("Dismiss", color = GenicTextMuted, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onConfigureUrl,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
            ) {
              Text("Paste Webhook URL", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    is WebhookUiState.Error -> {
      Card(
        modifier = modifier.fillMaxWidth().testTag("api_status_error_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF281014)),
        border = BorderStroke(1.dp, GenicAccentRed.copy(alpha = 0.6f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = GenicAccentRed,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Dispatch Failed",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GenicAccentRed
              )
            }
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = GenicTextMuted,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Text(
            text = uiState.message,
            fontSize = 12.sp,
            color = Color(0xFFFECACA),
            lineHeight = 17.sp
          )

          if (!uiState.details.isNullOrBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1B0A0D),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = uiState.details,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFFCA5A5),
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = onConfigureUrl,
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, Color(0xFF4B1C24))
            ) {
              Text("Check URL", fontSize = 12.sp, color = GenicTextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onRetry,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GenicAccentRed)
            ) {
              Text("Retry", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    }

    is WebhookUiState.Loading, WebhookUiState.Idle -> {}
  }
}

// ==========================================
// Dialogs for the 4 button actions & Webhook
// ==========================================

@Composable
fun CreateProjectDialog(
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var projectName by remember { mutableStateOf("") }
  var selectedRatio by remember { mutableStateOf("16:9") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = GenicCard,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = GenicPrimary
        )
        Text(
          text = "Create Project",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = GenicTextPrimary
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
          text = "Set up a new AI media generation project or canvas.",
          fontSize = 13.sp,
          color = GenicTextSecondary
        )

        OutlinedTextField(
          value = projectName,
          onValueChange = { projectName = it },
          label = { Text("Project Title") },
          placeholder = { Text("e.g., Cyberpunk Odyssey") },
          modifier = Modifier.fillMaxWidth().testTag("input_project_title"),
          shape = RoundedCornerShape(12.dp)
        )

        Text(
          text = "Aspect Ratio:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = GenicTextSecondary
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("16:9", "9:16", "1:1").forEach { ratio ->
            val isSelected = selectedRatio == ratio
            Surface(
              onClick = { selectedRatio = ratio },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) GenicPrimary.copy(alpha = 0.2f) else Color(0xFF1E293B),
              border = BorderStroke(1.dp, if (isSelected) GenicPrimary else Color(0xFF334155)),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = ratio,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) GenicPrimaryLight else GenicTextSecondary
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(projectName.ifBlank { "Untitled Project" })
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GenicPrimary),
        modifier = Modifier.testTag("btn_confirm_create_project")
      ) {
        Text("Create", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = GenicTextMuted)
      }
    }
  )
}

@Composable
fun EditWithAiDialog(
  onDismiss: () -> Unit
) {
  var selectedTool by remember { mutableStateOf("Smart Trim") }
  val tools = listOf("Smart Trim", "AI Voiceover", "Color Grading", "Subtitles Auto-Sync", "Background Removal")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = GenicCard,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Movie,
          contentDescription = null,
          tint = Color(0xFF6366F1)
        )
        Text(
          text = "Edit with AI",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = GenicTextPrimary
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Select an intelligent editing tool to enhance your media clips:",
          fontSize = 13.sp,
          color = GenicTextSecondary
        )

        tools.forEach { tool ->
          val isSelected = selectedTool == tool
          Surface(
            onClick = { selectedTool = tool },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) Color(0xFF312E81) else Color(0xFF1E293B),
            border = BorderStroke(1.dp, if (isSelected) Color(0xFF818CF8) else Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = tool,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else GenicTextSecondary
              )
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF818CF8),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
      ) {
        Text("Apply Tool", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = GenicTextMuted)
      }
    }
  )
}

@Composable
fun GenerateImageDialog(
  onDismiss: () -> Unit
) {
  var prompt by remember { mutableStateOf("") }
  var selectedStyle by remember { mutableStateOf("Cinematic") }
  val styles = listOf("Cinematic", "Anime", "Cyberpunk", "3D Render")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = GenicCard,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = GenicTertiary
        )
        Text(
          text = "Generate Image",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = GenicTextPrimary
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Generate concept art and visual keyframes with magic sparkles.",
          fontSize = 13.sp,
          color = GenicTextSecondary
        )

        OutlinedTextField(
          value = prompt,
          onValueChange = { prompt = it },
          label = { Text("Image Prompt") },
          placeholder = { Text("A futuristic neon skyline with soaring skybuses...") },
          modifier = Modifier.fillMaxWidth().testTag("input_image_prompt"),
          shape = RoundedCornerShape(12.dp)
        )

        Text(
          text = "Style Preset:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = GenicTextSecondary
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          styles.forEach { style ->
            val isSelected = selectedStyle == style
            Surface(
              onClick = { selectedStyle = style },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) GenicTertiary.copy(alpha = 0.2f) else Color(0xFF1E293B),
              border = BorderStroke(1.dp, if (isSelected) GenicTertiary else Color(0xFF334155)),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = style,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color(0xFFF472B6) else GenicTextSecondary
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GenicTertiary)
      ) {
        Text("Generate", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = GenicTextMuted)
      }
    }
  )
}

@Composable
fun ConfigureWebhookDialog(
  initialUrl: String,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit,
  onReset: () -> Unit
) {
  var urlInput by remember { mutableStateOf(initialUrl) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = GenicCard,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Link,
          contentDescription = null,
          tint = GenicSecondary
        )
        Text(
          text = "Pipedream Webhook URL",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = GenicTextPrimary
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Enter your custom Pipedream HTTP webhook URL to receive story payloads whenever 'Generate video' is executed.",
          fontSize = 13.sp,
          color = GenicTextSecondary,
          lineHeight = 18.sp
        )

        OutlinedTextField(
          value = urlInput,
          onValueChange = { urlInput = it },
          label = { Text("Webhook Endpoint URL") },
          placeholder = { Text("https://eo...pipedream.net") },
          modifier = Modifier.fillMaxWidth().testTag("input_webhook_url"),
          shape = RoundedCornerShape(12.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onReset) {
            Text(
              text = "Reset to placeholder",
              fontSize = 12.sp,
              color = GenicTextMuted
            )
          }

          Text(
            text = "POST / JSON",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GenicSecondary
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(urlInput.trim()) },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GenicSecondary)
      ) {
        Text("Save URL", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = GenicTextMuted)
      }
    }
  )
}
