package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.TextWhite

/**
 * Screen opened when clicking the top "প্লাস" (Plus / পেলাস) button in App Edit.
 * Faithfully designed according to the uploaded reference picture:
 * - Top & Bottom flowing neon waves in Cyan and Pink
 * - Top: Glowing rounded avatar / app picture box (126dp x 126dp)
 * - "Upload Picture" stadium button with cloud upload icon
 * - "App Name" stadium input pill with stylized [A] badge
 * - "User Name" stadium input pill with person icon
 * - Bottom right: "Next →" button sitting on top of the glowing cyber wave
 */
@Composable
fun CreateAppSetupScreen(
    initialUserName: String = "Rubel",
    onClose: () -> Unit,
    onNext: (appName: String, userName: String, imageUri: Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var appNameInput by remember { mutableStateOf("") }
    var userNameInput by remember { mutableStateOf(initialUserName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "setup_wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_phase"
    )

    // Android Zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Toast.makeText(context, "অ্যাপের ছবি সিলেক্ট করা হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    // Intercept back button to close setup page
    BackHandler {
        onClose()
    }

    fun handleProceed() {
        val appName = if (appNameInput.isNotBlank()) appNameInput.trim() else "My Cyber App"
        val userName = if (userNameInput.isNotBlank()) userNameInput.trim() else "Rubel"
        onNext(appName, userName, selectedImageUri)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF03071E))
    ) {
        // Glowing Neon Background Waves & Light Reflections matching picture
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val waveOffset = wavePhase * 16f

            // Top Cyan & Pink Wave
            val topWaveCyan = Path().apply {
                moveTo(0f, height * 0.05f + waveOffset)
                cubicTo(
                    width * 0.35f, height * 0.08f - waveOffset,
                    width * 0.65f, height * 0.02f + waveOffset,
                    width, height * 0.06f
                )
            }
            drawPath(
                path = topWaveCyan,
                brush = Brush.horizontalGradient(
                    listOf(NeonCyan.copy(alpha = 0.95f), ElectricBlue.copy(alpha = 0.8f))
                ),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            val topWavePink = Path().apply {
                moveTo(width * 0.45f, height * 0.03f)
                cubicTo(
                    width * 0.72f, height * 0.06f + waveOffset,
                    width * 0.88f, height * 0.04f - waveOffset,
                    width, height * 0.05f
                )
            }
            drawPath(
                path = topWavePink,
                brush = Brush.horizontalGradient(
                    listOf(NeonMagenta.copy(alpha = 0.5f), NeonPink.copy(alpha = 0.95f), NeonPinkLight)
                ),
                style = Stroke(width = 7f, cap = StrokeCap.Round)
            )

            // Bottom Flowing Waves
            val bottomWaveCyan = Path().apply {
                moveTo(0f, height * 0.91f + waveOffset * 0.4f)
                cubicTo(
                    width * 0.32f, height * 0.88f - waveOffset * 0.4f,
                    width * 0.68f, height * 0.93f + waveOffset * 0.3f,
                    width, height * 0.90f
                )
            }
            drawPath(
                path = bottomWaveCyan,
                brush = Brush.horizontalGradient(
                    listOf(NeonCyan.copy(alpha = 0.9f), ElectricBlue.copy(alpha = 0.85f))
                ),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            val bottomWavePink = Path().apply {
                moveTo(0f, height * 0.90f)
                cubicTo(
                    width * 0.25f, height * 0.88f + waveOffset * 0.3f,
                    width * 0.55f, height * 0.92f - waveOffset * 0.3f,
                    width * 0.85f, height * 0.91f
                )
            }
            drawPath(
                path = bottomWavePink,
                brush = Brush.horizontalGradient(
                    listOf(NeonPink.copy(alpha = 0.95f), NeonMagenta.copy(alpha = 0.7f), Color.Transparent)
                ),
                style = Stroke(width = 7f, cap = StrokeCap.Round)
            )

            // Floor neon light reflections
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(NeonPink.copy(alpha = 0.4f), Color.Transparent),
                    radius = width * 0.35f
                ),
                center = Offset(width * 0.2f, height * 0.97f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(NeonCyan.copy(alpha = 0.45f), Color.Transparent),
                    radius = width * 0.35f
                ),
                center = Offset(width * 0.55f, height * 0.97f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(NeonMagenta.copy(alpha = 0.4f), Color.Transparent),
                    radius = width * 0.35f
                ),
                center = Offset(width * 0.85f, height * 0.97f)
            )
        }

        // Top-left Close/Back Icon Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 14.dp, top = 8.dp)
                .size(40.dp)
                .testTag("back_button_create_app")
                .clip(CircleShape)
                .background(Color(0x330A184E))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // 1. Glowing Avatar / App Picture Box (126dp x 126dp)
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .testTag("app_avatar_frame")
                    .drawBehind {
                        // Outer diffuse neon halo
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(
                                    NeonCyan.copy(alpha = 0.5f),
                                    NeonPink.copy(alpha = 0.4f),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.95f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(32.dp.toPx()),
                            style = Stroke(width = 8f)
                        )
                    }
                    .border(
                        width = 2.5.dp,
                        brush = Brush.sweepGradient(
                            listOf(NeonCyan, NeonPink, NeonMagenta, ElectricBlue, NeonCyan)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0x990A1747))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Inner glowing circle with person avatar silhouette
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .border(
                                width = 2.dp,
                                brush = Brush.sweepGradient(listOf(NeonCyan, ElectricBlue, NeonCyan)),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(Color(0x6608143C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar Placeholder",
                            tint = NeonCyan,
                            modifier = Modifier.size(58.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. "Upload Picture" Stadium Button
            Box(
                modifier = Modifier
                    .testTag("upload_picture_pill_button")
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = NeonPink),
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(NeonPink.copy(alpha = 0.5f), Color.Transparent),
                                radius = size.width * 0.9f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(23.dp.toPx()),
                            style = Stroke(width = 4f)
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(NeonCyan, ElectricBlue, NeonMagenta, NeonPink)
                        ),
                        shape = RoundedCornerShape(23.dp)
                    )
                    .background(Color(0x990A1B52))
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "|",
                        fontSize = 15.sp,
                        color = NeonCyan.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedImageUri != null) "Change Picture" else "Upload Picture",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 3. "App Name" Stadium Input Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("app_name_input_pill")
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(
                                    NeonCyan.copy(alpha = 0.4f),
                                    NeonPink.copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.95f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(30.dp.toPx()),
                            style = Stroke(width = 5f)
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(NeonCyan, ElectricBlue, NeonMagenta, NeonPink)
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xEE091136))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stylized [A] badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(NeonCyan, NeonMagenta, NeonPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "|",
                        fontSize = 18.sp,
                        color = Color(0x66A5B8DC)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (appNameInput.isEmpty()) {
                            Text(
                                text = "App Name",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA5B8DC)
                            )
                        }
                        BasicTextField(
                            value = appNameInput,
                            onValueChange = { appNameInput = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = TextWhite,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(NeonPink),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_name_text_field")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. "User Name" Stadium Input Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("user_name_input_pill")
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(
                                    NeonCyan.copy(alpha = 0.4f),
                                    NeonPink.copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.95f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(30.dp.toPx()),
                            style = Stroke(width = 5f)
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(NeonCyan, ElectricBlue, NeonMagenta, NeonPink)
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xEE091136))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Person Icon in cyan
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x4400F0FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = NeonCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "|",
                        fontSize = 18.sp,
                        color = Color(0x66A5B8DC)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (userNameInput.isEmpty()) {
                            Text(
                                text = "User Name",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA5B8DC)
                            )
                        }
                        BasicTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = TextWhite,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(NeonCyan),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { handleProceed() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_name_text_field")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. Bottom Right [ Next → ] Button matching picture
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .testTag("next_step_button")
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = NeonCyan),
                            onClick = { handleProceed() }
                        )
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.radialGradient(
                                    listOf(NeonPink.copy(alpha = 0.55f), Color.Transparent),
                                    radius = size.width * 0.9f
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(23.dp.toPx()),
                                style = Stroke(width = 5f)
                            )
                        }
                        .border(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                listOf(NeonCyan, ElectricBlue, NeonMagenta, NeonPink)
                            ),
                            shape = RoundedCornerShape(23.dp)
                        )
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF091C5C), Color(0xFF140B48))
                            )
                        )
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
