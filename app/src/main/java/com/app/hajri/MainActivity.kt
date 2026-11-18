package com.app.hajri

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import kotlin.math.pow
import kotlin.math.roundToInt
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

// ---------- palette (theme-driven: applyTheme() rewrites these) ----------
var Navy by mutableStateOf(Color(0xFF16213E))
var Indigo by mutableStateOf(Color(0xFF2D3A8F))
var Teal by mutableStateOf(Color(0xFF0E7C7B))
var InkSoft by mutableStateOf(Color(0xFF5A6478))
var GreenMark by mutableStateOf(Color(0xFF0F5C2A))
var RedMark by mutableStateOf(Color(0xFFB71C1C))
var AmberMark by mutableStateOf(Color(0xFF9E3800))
var CardWhite by mutableStateOf(Color(0xFFFFFFFF))
var FieldBg by mutableStateOf(Color(0xFFF4F6FB))
var HeaderTonal by mutableStateOf(Color(0xFFEDF1F9))
var DividerColor by mutableStateOf(Color(0xFFD5DBEA))
var CardBorder by mutableStateOf(Color.Transparent)
var OnAccent by mutableStateOf(Color.White)
var DialogBg by mutableStateOf(Color(0xFFFFFFFF))
var IsGlassTheme by mutableStateOf(false)

// ---------- themes ----------
data class ThemeStyle(
    val wallpaperRes: Int? = null,
    val bgBrush: Brush? = null,
    val bgColor: Color = Color.White,
    val scrim: Color = Color.Transparent,
    val card: Color = Color.White,
    val fieldBg: Color = Color(0xFFF4F6FB),
    val headerTonal: Color = Color(0xFFEDF1F9),
    val divider: Color = Color(0xFFD5DBEA),
    val ink: Color = Color(0xFF16213E),
    val inkSoft: Color = Color(0xFF5A6478),
    val accent: Color = Color(0xFF2D3A8F),
    val accent2: Color = Color(0xFF0E7C7B),
    val cardBorder: Color = Color.Transparent,
    val onAccent: Color = Color.White,
    val greenMark: Color = Color(0xFF0F5C2A),
    val redMark: Color = Color(0xFFB71C1C),
    val amberMark: Color = Color(0xFF9E3800)
)

fun relativeLuminance(color: Color): Float {
    fun adjust(channel: Float): Float {
        return if (channel <= 0.03928f) channel / 12.92f else ((channel + 0.055f) / 1.055f).pow(2.4f)
    }
    val r = adjust(color.red)
    val g = adjust(color.green)
    val b = adjust(color.blue)
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

fun contrastRatio(c1: Color, c2: Color): Float {
    val l1 = relativeLuminance(c1)
    val l2 = relativeLuminance(c2)
    val lighter = maxOf(l1, l2)
    val darker = minOf(l1, l2)
    return (lighter + 0.05f) / (darker + 0.05f)
}

fun themeStyle(
    theme: AppTheme,
    isSystemDark: Boolean = false,
    context: Context? = null
): ThemeStyle = when (theme) {
    // 1. GLASS — Frosted translucent glass panels over deep sapphire-teal backdrop with specular rim borders
    AppTheme.GLASS -> ThemeStyle(
        bgBrush = Brush.verticalGradient(
            listOf(Color(0xFF0B192C), Color(0xFF132E4A), Color(0xFF0E3B43))
        ),
        bgColor = Color(0xFF0B192C),
        card = Color(0x2BFFFFFF),          // Frosted translucent glass card
        fieldBg = Color(0x22FFFFFF),       // Translucent glass input & day cell
        headerTonal = Color(0x38FFFFFF),   // Frosted header pill
        divider = Color(0x4DFFFFFF),
        ink = Color(0xFFF5FAFF),
        inkSoft = Color(0xFFBFD9EE),
        accent = Color(0xFF38BDF8),
        accent2 = Color(0xFF2DD4BF),
        cardBorder = Color(0x66FFFFFF),    // Crisp glass edge highlight
        onAccent = Color(0xFF041C2C),
        greenMark = Color(0xFF4ADE80),
        redMark = Color(0xFFF87171),
        amberMark = Color(0xFFFBBF24)
    )

    // 2. DARK — True deep charcoal/slate surfaces with high-contrast text & vibrant accents
    AppTheme.DARK -> ThemeStyle(
        bgColor = Color(0xFF0D1117),
        card = Color(0xFF161B22),
        fieldBg = Color(0xFF21262D),
        headerTonal = Color(0xFF28303D),
        divider = Color(0xFF364152),
        ink = Color(0xFFF0F6FC),
        inkSoft = Color(0xFF9BA8B9),
        accent = Color(0xFF58A6FF),
        accent2 = Color(0xFF3FB950),
        cardBorder = Color(0xFF30363D),
        onAccent = Color(0xFF06101E),
        greenMark = Color(0xFF3FB950),
        redMark = Color(0xFFFF7B72),
        amberMark = Color(0xFFD29922)
    )

    // 3. MATERIAL U 3 — Live Android 12+ wallpaper-derived Monet palette (with M3 tonal fallback)
    AppTheme.MATERIAL_YOU -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context != null) {
            val dyn = if (isSystemDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            ThemeStyle(
                bgColor = dyn.background,
                card = dyn.surfaceContainerLow,
                fieldBg = dyn.surfaceContainer,
                headerTonal = dyn.secondaryContainer,
                divider = dyn.outlineVariant,
                ink = dyn.onSurface,
                inkSoft = dyn.onSurfaceVariant,
                accent = dyn.primary,
                accent2 = dyn.tertiary,
                cardBorder = dyn.outlineVariant,
                onAccent = dyn.onPrimary,
                greenMark = if (isSystemDark) Color(0xFF6DD58C) else Color(0xFF146C2E),
                redMark = dyn.error,
                amberMark = if (isSystemDark) Color(0xFFFFB74D) else Color(0xFF9E3800)
            )
        } else {
            ThemeStyle(
                bgBrush = Brush.verticalGradient(
                    listOf(Color(0xFFEDE7F6), Color(0xFFF5EFFC), Color(0xFFE8DEF8))
                ),
                bgColor = Color(0xFFF5EFFC),
                card = Color(0xFFF7F2FA),
                fieldBg = Color(0xFFECE6F0),
                headerTonal = Color(0xFFE8DEF8),
                divider = Color(0xFFCAC4D0),
                ink = Color(0xFF1D1B20),
                inkSoft = Color(0xFF49454F),
                accent = Color(0xFF6750A4),
                accent2 = Color(0xFF625B71),
                cardBorder = Color(0xFFCAC4D0),
                onAccent = Color.White,
                greenMark = Color(0xFF146C2E),
                redMark = Color(0xFFB3261E),
                amberMark = Color(0xFF9E3800)
            )
        }
    }

    // 4. DAY — Crisp bright daylight surfaces, clean slate borders, deep ink typography
    AppTheme.DAY -> ThemeStyle(
        bgColor = Color(0xFFF8FAFC),
        card = Color(0xFFFFFFFF),
        fieldBg = Color(0xFFF1F5F9),
        headerTonal = Color(0xFFE2E8F0),
        divider = Color(0xFFCBD5E1),
        ink = Color(0xFF0F172A),
        inkSoft = Color(0xFF475569),
        accent = Color(0xFF2563EB),
        accent2 = Color(0xFF0D9488),
        cardBorder = Color(0xFFE2E8F0),
        onAccent = Color.White,
        greenMark = Color(0xFF15803D),
        redMark = Color(0xFFDC2626),
        amberMark = Color(0xFFB45309)
    )
}

val THEME_OPTIONS = listOf(
    AppTheme.GLASS to "Glass",
    AppTheme.DARK to "Dark",
    AppTheme.MATERIAL_YOU to "Material U 3",
    AppTheme.DAY to "Day"
)

fun applyTheme(t: ThemeStyle, theme: AppTheme = AppTheme.MATERIAL_YOU) {
    Navy = t.ink
    Indigo = t.accent
    Teal = t.accent2
    InkSoft = t.inkSoft
    CardWhite = t.card
    FieldBg = t.fieldBg
    HeaderTonal = t.headerTonal
    DividerColor = t.divider
    CardBorder = t.cardBorder
    OnAccent = t.onAccent
    GreenMark = t.greenMark
    RedMark = t.redMark
    AmberMark = t.amberMark
    IsGlassTheme = (theme == AppTheme.GLASS)
    DialogBg = if (theme == AppTheme.GLASS) Color(0xEB11263C) else t.card
}

// Shared text-field colors: explicit theme colors on every OutlinedTextField.
// Material defaults are light-scheme and go invisible on dark theme surfaces.
@Composable
fun hajriFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Navy,
    unfocusedTextColor = Navy,
    disabledTextColor = Navy,
    focusedLabelColor = InkSoft,
    unfocusedLabelColor = InkSoft,
    disabledLabelColor = InkSoft,
    focusedBorderColor = Indigo,
    unfocusedBorderColor = DividerColor,
    disabledBorderColor = DividerColor,
    focusedContainerColor = FieldBg,
    unfocusedContainerColor = FieldBg,
    disabledContainerColor = FieldBg,
    cursorColor = Indigo
)

sealed interface Screen {
    data object Home : Screen
    data object CardList : Screen
    data class Detail(val cardId: String) : Screen
    data class Total(val cardId: String) : Screen
    data object Shots : Screen
    data object Notices : Screen
    data object Premium : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = HajriStore(this)
        val isNight = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        val initialTheme = store.getTheme()
        applyTheme(themeStyle(initialTheme, isNight, this), initialTheme)
        setContent { HajriApp(store) }
    }
}

@Composable
fun HajriApp(store: HajriStore) {
    val context = LocalContext.current
    val cards = remember {
        mutableStateListOf<HajriCard>().apply { addAll(store.loadAll()) }
    }
    val extrasStore = remember { ExtrasStore(context) }
    val shots = remember {
        mutableStateListOf<PaymentShot>().apply { addAll(extrasStore.loadShots()) }
    }
    val notices = remember {
        mutableStateListOf<NoticeItem>().apply { addAll(extrasStore.loadNotices()) }
    }
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var unlocked by remember { mutableStateOf<Set<String>>(emptySet()) }
    var pinCardId by remember { mutableStateOf<String?>(null) }
    var pinAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<HajriCard?>(null) }

    // Use resolver for initial state
    val isSystemDark = isSystemInDarkTheme()
    var theme by remember { mutableStateOf(store.getTheme()) }
    var currentThemeStyle by remember(theme, isSystemDark) {
        mutableStateOf(themeStyle(theme, isSystemDark, context).also { applyTheme(it, theme) })
    }

    val activity = context as? ComponentActivity
    LaunchedEffect(theme, currentThemeStyle) {
        activity?.window?.let { window ->
            val barColor = currentThemeStyle.bgColor
            window.statusBarColor = barColor.toArgb()
            window.navigationBarColor = barColor.toArgb()
            val lightBars = relativeLuminance(barColor) > 0.5f
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = lightBars
                isAppearanceLightNavigationBars = lightBars
            }
        }
    }
    var showPaywall by remember { mutableStateOf(false) }
    var showReminders by remember { mutableStateOf(false) }
    // Hoisted unlock state so the Premium screen refreshes right after an unlock.
    var premiumUnlocked by remember { mutableStateOf(store.isPremiumUnlocked()) }
    // True when the paywall was opened from a theme entry (open the editor after unlock).

    fun persist() = store.saveAll(cards)

    fun persistShots() = extrasStore.saveShots(shots)

    fun persistNotices() = extrasStore.saveNotices(notices)

    fun updateCard(updated: HajriCard) {
        val i = cards.indexOfFirst { it.id == updated.id }
        if (i >= 0) {
            cards[i] = updated
            persist()
        }
    }

    fun cardById(id: String): HajriCard? = cards.firstOrNull { it.id == id }

    fun requestPin(card: HajriCard, action: () -> Unit) {
        if (pinAction != null) return // a PIN prompt is already open
        if (!card.hasContent() || card.id in unlocked) {
            action()
        } else {
            pinCardId = card.id
            pinAction = action
        }
    }

    fun onPinOk() {
        val id = pinCardId
        if (id != null) unlocked = unlocked + id
        val action = pinAction
        pinCardId = null
        pinAction = null
        action?.invoke()
    }

    // Leaving a card that is still completely empty removes it, so pressing "Add New Card" and
    // going back never leaves "Unnamed card" clutter behind.
    fun leaveDetail(id: String) {
        val c = cardById(id)
        if (c != null && !c.hasContent()) {
            cards.removeAll { it.id == id }
            persist()
        }
        screen = Screen.CardList
    }

    BackHandler(enabled = screen != Screen.Home) {
        when (val s = screen) {
            is Screen.Detail -> leaveDetail(s.cardId)
            is Screen.Total -> screen = Screen.Detail(s.cardId)
            else -> screen = Screen.Home
        }
    }

    // Local copy: smart cast does not work on the delegated currentThemeStyle property.
    val style = currentThemeStyle
    val isDarkSurface = relativeLuminance(style.ink) > 0.5f
    val m3Colors = if (isDarkSurface) {
        darkColorScheme(
            primary = Indigo,
            onPrimary = OnAccent,
            secondary = Teal,
            background = style.bgColor,
            surface = DialogBg,
            onSurface = Navy,
            onSurfaceVariant = InkSoft,
            outline = DividerColor
        )
    } else {
        lightColorScheme(
            primary = Indigo,
            onPrimary = OnAccent,
            secondary = Teal,
            background = style.bgColor,
            surface = DialogBg,
            onSurface = Navy,
            onSurfaceVariant = InkSoft,
            outline = DividerColor
        )
    }

    MaterialTheme(colorScheme = m3Colors) {
    Box(modifier = Modifier.fillMaxSize()) {
        // background layer for the current theme
        when {
            style.wallpaperRes != null -> {
                Image(
                    painter = painterResource(id = style.wallpaperRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (style.scrim != Color.Transparent) {
                    Box(Modifier.fillMaxSize().background(style.scrim))
                }
            }
            style.bgBrush != null -> Box(Modifier.fillMaxSize().background(style.bgBrush))
            else -> Box(Modifier.fillMaxSize().background(style.bgColor))
        }
        when (val s = screen) {
            is Screen.Home -> HomeScreen(
                cards = cards,
                onOpenCards = { screen = Screen.CardList },
                onOpenShots = {
                    if (premiumUnlocked) screen = Screen.Shots
                    else { showPaywall = true }
                },
                onOpenNotices = { screen = Screen.Notices },
                onOpenPremium = { screen = Screen.Premium },
                onOpenSettings = { showSettings = true },
                onOpenAbout = { showAbout = true },
                showAds = !premiumUnlocked
            )
            is Screen.CardList -> CardListScreen(
                cards = cards,
                onBack = { screen = Screen.Home },
                onAddCard = {
                    val blank = HajriCard()
                    cards.add(0, blank)
                    unlocked = unlocked + blank.id
                    persist()
                    screen = Screen.Detail(blank.id)
                },
                onOpenCard = { screen = Screen.Detail(it.id) },
                onDeleteCard = { deleteTarget = it }
            )
            is Screen.Shots -> ShotsScreen(
                shots = shots,
                shotsDir = extrasStore.shotsDir,
                onBack = { screen = Screen.Home },
                onAddShot = { shots.add(0, it); persistShots() },
                onDeleteShot = { shot ->
                    shots.removeAll { it.id == shot.id }
                    try { File(extrasStore.shotsDir, shot.fileName).delete() } catch (e: Exception) {}
                    persistShots()
                }
            )
            is Screen.Notices -> NoticesScreen(
                notices = notices,
                onBack = { screen = Screen.Home },
                onAddNotice = { notices.add(0, it); persistNotices() },
                onDeleteNotice = { n ->
                    notices.removeAll { it.id == n.id }
                    persistNotices()
                }
            )
            is Screen.Premium -> PremiumScreen(
                premiumUnlocked = premiumUnlocked,
                onBack = { screen = Screen.Home },
                onOpenPaywall = { showPaywall = true },
                onOpenReminders = { showReminders = true }
            )
            is Screen.Detail -> {
                val card = cardById(s.cardId)
                if (card == null) {
                    LaunchedEffect(Unit) { screen = Screen.CardList }
                } else {
                    DetailScreen(
                        card = card,
                        onBack = { leaveDetail(card.id) },
                        onSave = {
                            persist()
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = { deleteTarget = card },
                        onTotal = { screen = Screen.Total(card.id) },
                        onUpdate = { updateCard(it) },
                        onRequestPin = { action -> requestPin(card, action) },
                        isUnlocked = card.id in unlocked
                    )
                }
            }
            is Screen.Total -> {
                val card = cardById(s.cardId)
                if (card == null) {
                    LaunchedEffect(Unit) { screen = Screen.CardList }
                } else {
                    TotalScreen(
                        card = card,
                        onBack = { screen = Screen.Detail(card.id) },
                        premiumUnlocked = premiumUnlocked,
                        onOpenPaywall = { showPaywall = true }
                    )
                }
            }
        }
    }
    }

    if (pinAction != null) {
        PinDialog(
            store = store,
            onOk = { onPinOk() },
            onCancel = { pinCardId = null; pinAction = null }
        )
    }

    if (showSettings) {
        SettingsDialog(
            store = store,
            onClose = { showSettings = false },
            currentTheme = theme,
            onThemeChange = { selected ->
                theme = selected
                store.setTheme(selected)
                val newStyle = themeStyle(selected, isSystemDark, context)
                currentThemeStyle = newStyle
                applyTheme(newStyle, selected)
            },
            onOpenPaywall = { showPaywall = true },
        )
    }

    if (showPaywall) {
        PremiumPaywallDialog(
            store = store,
            onClose = { showPaywall = false },
            onUnlocked = {
                premiumUnlocked = true
                showPaywall = false
            }
        )
    }

    if (showReminders) {
        ReminderDialog(store = store, onClose = { showReminders = false })
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            containerColor = DialogBg,
            confirmButton = {
                TextButton(onClick = { showAbout = false }) { Text("OK", color = Indigo) }
            },
            title = { Text("1/31 Days", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Hajri cards for daily-wage workers.\nOne card per worker per month, 31 days.\nAll data stays on this phone.",
                    color = InkSoft
                )
            }
        )
    }

    if (deleteTarget != null) {
        val target = deleteTarget!!
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = DialogBg,
            title = { Text("Delete this card?", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Card \"${target.name.ifBlank { "Unnamed card" }}\" (${target.monthLabel()}) will be removed from this phone.",
                    color = InkSoft
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    requestPin(target) {
                        cards.removeAll { it.id == target.id }
                        persist()
                        deleteTarget = null
                        if (screen is Screen.Detail || screen is Screen.Total) {
                            screen = Screen.CardList
                        }
                    }
                }) { Text("Delete", color = RedMark, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = InkSoft) }
            }
        )
    }
}

// ---------- logo: 1 top-left, 31 bottom-right, curved swoosh ----------
@Composable
fun LogoBox(sizeDp: Int = 56) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .background(CardWhite, RoundedCornerShape(14.dp))
            .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(14.dp)) else Modifier)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Teal,
                startAngle = 200f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(size.width * 0.08f, size.height * 0.10f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.85f, size.height * 0.85f),
                style = Stroke(width = size.width * 0.07f)
            )
        }
        Text(
            text = "1",
            color = Navy,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 7.dp, top = 5.dp)
        )
        Text(
            text = "31",
            color = Navy,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 7.dp, bottom = 5.dp)
        )
    }
}

// ---------- page 1: home (Design A: 5 options) ----------
@Composable
fun HomeScreen(
    cards: List<HajriCard>,
    onOpenCards: () -> Unit,
    onOpenShots: () -> Unit,
    onOpenNotices: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    showAds: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showAdvance by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val totalAdvance = cards.sumOf { it.totalWithdrawal() }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoBox(56)
            Text(
                text = "1/31 Days",
                color = Navy,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Navy)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = DialogBg
                ) {
                    DropdownMenuItem(
                        text = { Text("Settings", color = Navy) },
                        onClick = { menuExpanded = false; onOpenSettings() }
                    )
                    DropdownMenuItem(
                        text = { Text("About", color = Navy) },
                        onClick = { menuExpanded = false; onOpenAbout() }
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        HomeOptionButton(text = "Add New Card", onClick = onOpenCards)
        HomeOptionButton(text = "Add Payment Screenshot", onClick = onOpenShots)
        HomeOptionButton(text = "Add Notice", onClick = onOpenNotices)

        // option 4: total advance across all cards, shown on the home page
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .background(HeaderTonal, RoundedCornerShape(28.dp))
                .clickable { showAdvance = true }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total Advance", color = InkSoft, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Text(formatMoney(totalAdvance), color = Teal, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }

        HomeOptionButton(text = "Premium Future", onClick = onOpenPremium)
        HomeOptionButton(
            text = "Connect with",
            onClick = {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/iopXtiop_bot"))
                )
            }
        )
        Spacer(Modifier.height(16.dp))
        }
        if (showAds) BannerAd()
    }

    if (showAdvance) {
        AlertDialog(
            onDismissRequest = { showAdvance = false },
            containerColor = DialogBg,
            title = { Text("Total Advance", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val withAdvance = cards.filter { it.totalWithdrawal() > 0.0 }
                    if (withAdvance.isEmpty()) {
                        Text("No advances yet.", color = InkSoft)
                    } else {
                        withAdvance.forEach { card ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(card.name.ifBlank { "Unnamed card" }, color = Navy, fontSize = 15.sp)
                                Text(formatMoney(card.totalWithdrawal()), color = Navy, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        DashedDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", color = Navy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(formatMoney(totalAdvance), color = Teal, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAdvance = false }) { Text("Close", color = Indigo) }
            }
        )
    }
}

@Composable
fun HomeOptionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Indigo),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .height(56.dp)
    ) {
        Text(text, color = OnAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

// ---------- card list (Design 1: all cards, name + date) ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardListScreen(
    cards: List<HajriCard>,
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onOpenCard: (HajriCard) -> Unit,
    onDeleteCard: (HajriCard) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(FieldBg, RoundedCornerShape(50))
                    .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(50)) else Modifier)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Home", color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "All Cards",
                color = Navy,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Button(
            onClick = onAddCard,
            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = OnAccent)
            Spacer(Modifier.width(8.dp))
            Text("Add New Card", color = OnAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (cards.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No cards yet.\nTap Add New Card to start.",
                    color = InkSoft,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = if (CardBorder != Color.Transparent) BorderStroke(1.dp, CardBorder) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .combinedClickable(
                                onClick = { onOpenCard(card) },
                                onLongClick = { onDeleteCard(card) }
                            )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = card.name.ifBlank { "Unnamed card" },
                                color = Navy,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = card.monthLabel(),
                                color = InkSoft,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${formatDays(card.totalWorkDays())} days  ·  ${formatMoney(card.totalPay())}",
                                color = Teal,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ---------- payment screenshots (Design 3) ----------
@Composable
fun ShotsScreen(
    shots: List<PaymentShot>,
    shotsDir: File,
    onBack: () -> Unit,
    onAddShot: (PaymentShot) -> Unit,
    onDeleteShot: (PaymentShot) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var pendingKind by remember { mutableStateOf("image") }
    var pendingFile by remember { mutableStateOf<String?>(null) }
    var pendingDate by remember { mutableStateOf(todayLabel()) }
    var viewShot by remember { mutableStateOf<PaymentShot?>(null) }
    var deleteShot by remember { mutableStateOf<PaymentShot?>(null) }
    var cameraFile by remember { mutableStateOf<File?>(null) }
    var cameraRequested by remember { mutableStateOf(false) }

    fun openPdf(shot: PaymentShot) {
        val file = File(shotsDir, shot.fileName)
        val uri = FileProvider.getUriForFile(context, "com.app.hajri.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            pendingKind = "image"
            pendingUri = uri
        }
    }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            pendingKind = "pdf"
            pendingUri = uri
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val f = cameraFile
        if (ok && f != null) {
            pendingKind = "image"
            pendingFile = f.name
        } else {
            try { f?.delete() } catch (e: Exception) {}
        }
        cameraFile = null
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            cameraRequested = true
        } else {
            Toast.makeText(context, "Camera permission is needed to take a photo", Toast.LENGTH_SHORT).show()
        }
    }

    // copy a picked gallery/PDF file into the app's own shots folder
    LaunchedEffect(pendingUri) {
        val uri = pendingUri ?: return@LaunchedEffect
        val kind = pendingKind
        scope.launch(Dispatchers.IO) {
            try {
                val name = UUID.randomUUID().toString() + if (kind == "pdf") ".pdf" else ".jpg"
                val out = File(shotsDir, name)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(out).use { output -> input.copyTo(output) }
                }
                withContext(Dispatchers.Main) { pendingFile = name }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Could not attach that file", Toast.LENGTH_SHORT).show()
                }
            }
        }
        pendingUri = null
    }

    // camera permission granted: take the photo now
    LaunchedEffect(cameraRequested) {
        if (!cameraRequested) return@LaunchedEffect
        cameraRequested = false
        val file = File(shotsDir, UUID.randomUUID().toString() + ".jpg")
        val uri = FileProvider.getUriForFile(context, "com.app.hajri.fileprovider", file)
        cameraFile = file
        cameraLauncher.launch(uri)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(FieldBg, RoundedCornerShape(50))
                    .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(50)) else Modifier)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Home", color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Payment Screenshots",
                color = Navy,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Button(
            onClick = {
                pendingFile = null
                pendingDate = todayLabel()
                showAdd = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = OnAccent)
            Spacer(Modifier.width(8.dp))
            Text("Add New Screenshot", color = OnAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (shots.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No payment screenshots yet.\nTap Add New Screenshot to attach one.",
                    color = InkSoft,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shots, key = { it.id }) { shot ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = if (CardBorder != Color.Transparent) BorderStroke(1.dp, CardBorder) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .combinedClickable(
                                onClick = {
                                    if (shot.kind == "pdf") openPdf(shot) else viewShot = shot
                                },
                                onLongClick = { deleteShot = shot }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shot.label.ifBlank { "Screenshot" },
                                color = Navy,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(shot.date, color = InkSoft, fontSize = 14.sp)
                                Text(
                                    if (shot.kind == "pdf") "PDF" else "Photo",
                                    color = Teal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    // add area: camera / gallery / PDF & document + custom date
    if (showAdd) {
        AlertDialog(
            onDismissRequest = {
                showAdd = false
                pendingFile?.let { try { File(shotsDir, it).delete() } catch (e: Exception) {} }
                pendingFile = null
            },
            containerColor = DialogBg,
            title = { Text("Add Payment Screenshot", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            text = "Camera",
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraRequested = true
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        )
                        OutlinedButton(
                            text = "Gallery",
                            onClick = { galleryLauncher.launch("image/*") }
                        )
                        OutlinedButton(
                            text = "PDF & Document",
                            onClick = { pdfLauncher.launch("application/pdf") }
                        )
                    }
                    Text(
                        text = if (pendingFile != null) "Attached ✓ (${if (pendingKind == "pdf") "PDF" else "photo"})" else "Nothing attached yet — pick Camera, Gallery or PDF.",
                        color = if (pendingFile != null) GreenMark else InkSoft,
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = pendingDate,
                        onValueChange = { pendingDate = it },
                        label = { Text("Date (DD-MM-YYYY)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = hajriFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val file = pendingFile
                        if (file == null) {
                            Toast.makeText(context, "Attach a photo or PDF first", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        onAddShot(
                            PaymentShot(
                                label = "Screenshot ${shots.size + 1}",
                                date = pendingDate.ifBlank { todayLabel() },
                                fileName = file,
                                kind = pendingKind
                            )
                        )
                        pendingFile = null
                        showAdd = false
                    }
                ) { Text("Save", color = Indigo, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdd = false
                    pendingFile?.let { try { File(shotsDir, it).delete() } catch (e: Exception) {} }
                    pendingFile = null
                }) { Text("Cancel", color = InkSoft) }
            }
        )
    }

    // full-screen photo viewer
    if (viewShot != null) {
        val shot = viewShot!!
        val bitmap = remember(shot) {
            try {
                BitmapFactory.decodeFile(File(shotsDir, shot.fileName).absolutePath)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
        AlertDialog(
            onDismissRequest = { viewShot = null },
            containerColor = DialogBg,
            title = { Text("${shot.label} — ${shot.date}", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = shot.label,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                } else {
                    Text("Could not open this photo.", color = RedMark)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewShot = null }) { Text("Close", color = Indigo) }
            }
        )
    }

    if (deleteShot != null) {
        val target = deleteShot!!
        AlertDialog(
            onDismissRequest = { deleteShot = null },
            containerColor = DialogBg,
            title = { Text("Delete this screenshot?", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "\"${target.label}\" (${target.date}) will be removed from this phone.",
                    color = InkSoft
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteShot(target)
                    deleteShot = null
                }) { Text("Delete", color = RedMark, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deleteShot = null }) { Text("Cancel", color = InkSoft) }
            }
        )
    }
}

@Composable
fun RowScope.OutlinedButton(text: String, onClick: () -> Unit) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DividerColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo),
        modifier = Modifier.weight(1f)
    ) {
        Text(text, color = Indigo, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ---------- notices ----------
@Composable
fun NoticesScreen(
    notices: List<NoticeItem>,
    onBack: () -> Unit,
    onAddNotice: (NoticeItem) -> Unit,
    onDeleteNotice: (NoticeItem) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(todayLabel()) }
    var deleteTarget by remember { mutableStateOf<NoticeItem?>(null) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(FieldBg, RoundedCornerShape(50))
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Home", color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Notices",
                color = Navy,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Button(
            onClick = {
                text = ""
                date = todayLabel()
                showAdd = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = OnAccent)
            Spacer(Modifier.width(8.dp))
            Text("Add Notice", color = OnAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (notices.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No notices yet.\nTap Add Notice to write one.",
                    color = InkSoft,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notices, key = { it.id }) { notice ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = if (CardBorder != Color.Transparent) BorderStroke(1.dp, CardBorder) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .combinedClickable(onClick = {}, onLongClick = { deleteTarget = notice })
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(notice.text, color = Navy, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text(notice.date, color = InkSoft, fontSize = 14.sp)
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            containerColor = DialogBg,
            title = { Text("Add Notice", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Notice") },
                        shape = RoundedCornerShape(16.dp),
                        colors = hajriFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (DD-MM-YYYY)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = hajriFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (text.isBlank()) {
                        Toast.makeText(context, "Write the notice first", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    onAddNotice(NoticeItem(text = text.trim(), date = date.ifBlank { todayLabel() }))
                    showAdd = false
                }) { Text("Save", color = Indigo, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false }) { Text("Cancel", color = InkSoft) }
            }
        )
    }

    if (deleteTarget != null) {
        val target = deleteTarget!!
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = DialogBg,
            title = { Text("Delete this notice?", color = Navy, fontWeight = FontWeight.Bold) },
            text = { Text("\"${target.text}\"", color = InkSoft) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteNotice(target)
                    deleteTarget = null
                }) { Text("Delete", color = RedMark, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = InkSoft) }
            }
        )
    }
}

// ---------- premium future ----------
@Composable
fun PremiumScreen(
    premiumUnlocked: Boolean,
    onBack: () -> Unit,
    onOpenPaywall: () -> Unit,
    onOpenReminders: () -> Unit
) {
    val features = listOf(
        "PDF export" to if (premiumUnlocked) "Available" else "Premium",
        "Remove Ads" to if (premiumUnlocked) "Available" else "Premium",
        "Payment Screenshot" to if (premiumUnlocked) "Available" else "Premium",
        "Reminders" to if (premiumUnlocked) "Available" else "Premium"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(FieldBg, RoundedCornerShape(50))
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Home", color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Premium Future",
                color = Navy,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.height(12.dp))
        features.forEach { (name, status) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable(enabled = name == "Reminders") {
                        when (name) {
                            "Reminders" -> if (premiumUnlocked) onOpenReminders() else onOpenPaywall()
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, color = Navy, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(
                        status,
                        color = if (status == "Available") Teal else InkSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Text(
            text = "₹49 once unlocks everything for lifetime — PDF export, Payment Screenshot and Reminders.",
            color = InkSoft,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 16.dp)
        )
    }
}

// ---------- page 2: card detail ----------
@Composable
fun DetailScreen(
    card: HajriCard,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onTotal: () -> Unit,
    onUpdate: (HajriCard) -> Unit,
    onRequestPin: ((() -> Unit) -> Unit),
    isUnlocked: Boolean
) {
    var amountDay by remember { mutableStateOf<Int?>(null) }
    var monthMenu by remember { mutableStateOf(false) }
    var yearMenu by remember { mutableStateOf(false) }
    var rateText by remember(card.id) {
        mutableStateOf(if (card.dailyRate == 0.0) "" else formatMoney(card.dailyRate).drop(1))
    }

    fun guarded(action: () -> Unit) {
        if (isUnlocked) action() else onRequestPin(action)
    }

    fun setDay(index: Int, entry: DayEntry) {
        val newDays = card.days.toMutableList()
        newDays[index] = entry
        onUpdate(card.copy(days = newDays))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(FieldBg, RoundedCornerShape(50))
                        .clickable { onBack() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("All Cards", color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete card", tint = RedMark)
                }
                TextButton(onClick = onSave) {
                    Icon(Icons.Filled.Save, contentDescription = null, tint = Indigo)
                    Spacer(Modifier.width(4.dp))
                    Text("Save", color = Indigo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = if (CardBorder != Color.Transparent) BorderStroke(1.dp, CardBorder) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = card.name,
                    onValueChange = { new ->
                        if (card.name == new) return@OutlinedTextField
                        guarded { onUpdate(card.copy(name = new)) }
                    },
                    label = { Text("Name") },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy),
                    shape = RoundedCornerShape(16.dp),
                    colors = hajriFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = card.monthLabel().substringBefore(" "),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Month") },
                            shape = RoundedCornerShape(16.dp),
                            colors = hajriFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(Modifier.matchParentSize().clickable { monthMenu = true })
                        DropdownMenu(
                            expanded = monthMenu,
                            onDismissRequest = { monthMenu = false },
                            containerColor = DialogBg
                        ) {
                            MONTH_NAMES.forEachIndexed { i, label ->
                                DropdownMenuItem(
                                    text = { Text(label, color = Navy) },
                                    onClick = {
                                        monthMenu = false
                                        if (card.month != i + 1) {
                                            guarded { onUpdate(card.copy(month = i + 1)) }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = card.year.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Year") },
                            shape = RoundedCornerShape(16.dp),
                            colors = hajriFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(Modifier.matchParentSize().clickable { yearMenu = true })
                        DropdownMenu(
                            expanded = yearMenu,
                            onDismissRequest = { yearMenu = false },
                            containerColor = DialogBg
                        ) {
                            (minOf(card.year, LocalDate.now().year - 5)..maxOf(card.year, LocalDate.now().year + 5)).forEach { y ->
                                DropdownMenuItem(
                                    text = { Text(y.toString(), color = Navy) },
                                    onClick = {
                                        yearMenu = false
                                        if (card.year != y) {
                                            guarded { onUpdate(card.copy(year = y)) }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { new ->
                        val clean = sanitizeAmount(new)
                        if (clean != rateText) {
                            val parsed = clean.toDoubleOrNull() ?: 0.0
                            // text only changes once the PIN check passes, so field and card never disagree
                            guarded {
                                rateText = clean
                                if (parsed != card.dailyRate) onUpdate(card.copy(dailyRate = parsed))
                            }
                        }
                    },
                    label = { Text("Daily rate (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = hajriFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Present ${card.validDays().count { it.status == WorkStatus.WORKED }}  ·  Day off ${card.validDays().count { it.status == WorkStatus.ABSENT }}  ·  Half day ${card.validDays().count { it.status == WorkStatus.HALF }}",
                        color = InkSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            // Premium table: every cell is its own separate rounded box
            // with gaps between cells — no vertical bars, no grid lines.
            // Date cell shows a large day number with the weekday beneath.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // header: text labels only, no boxes behind them
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("D", color = InkSoft, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(0.62f), textAlign = TextAlign.Center)
                    Spacer(Modifier.width(8.dp))
                    Text("Work", color = InkSoft, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Spacer(Modifier.width(8.dp))
                    Text("O. Time", color = InkSoft, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Spacer(Modifier.width(8.dp))
                    Text("Withdrawal", color = InkSoft, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1.25f), textAlign = TextAlign.Center)
                }
                for (i in 0 until card.daysInMonth()) {
                    val entry = card.days[i]
                    val weekday = try {
                        LocalDate.of(card.year, card.month, i + 1)
                            .dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    } catch (e: Exception) { "" }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // date cell: big day number + small weekday beneath
                        Box(
                            modifier = Modifier
                                .weight(0.62f)
                                .height(64.dp)
                                .background(FieldBg, RoundedCornerShape(14.dp))
                                .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(14.dp)) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = (i + 1).toString(),
                                    color = Navy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                                Text(
                                    text = weekday,
                                    color = InkSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        // work cell
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .background(FieldBg, RoundedCornerShape(14.dp))
                                .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(14.dp)) else Modifier)
                                .clickable {
                                    if (entry.isBlank() || isUnlocked) {
                                        setDay(i, entry.copy(status = entry.status.next()))
                                    } else {
                                        onRequestPin {
                                            setDay(i, entry.copy(status = entry.status.next()))
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (entry.status == WorkStatus.NONE) "−" else entry.status.mark(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (entry.status) {
                                    WorkStatus.WORKED -> GreenMark
                                    WorkStatus.ABSENT -> RedMark
                                    WorkStatus.HALF -> AmberMark
                                    WorkStatus.NONE -> InkSoft
                                }
                            )
                        }
                        // overtime cell
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .background(FieldBg, RoundedCornerShape(14.dp))
                                .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(14.dp)) else Modifier)
                                .clickable {
                                    if (entry.isBlank() || isUnlocked) {
                                        setDay(i, entry.copy(ot = !entry.ot))
                                    } else {
                                        onRequestPin { setDay(i, entry.copy(ot = !entry.ot)) }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (entry.ot) {
                                Icon(Icons.Filled.Check, contentDescription = "Overtime", tint = Teal)
                            } else {
                                Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = InkSoft)
                            }
                        }
                        // withdrawal cell
                        Box(
                            modifier = Modifier
                                .weight(1.25f)
                                .height(64.dp)
                                .background(FieldBg, RoundedCornerShape(14.dp))
                                .then(if (CardBorder != Color.Transparent) Modifier.border(1.dp, CardBorder, RoundedCornerShape(14.dp)) else Modifier)
                                .clickable {
                                    if (entry.isBlank() || isUnlocked) {
                                        amountDay = i
                                    } else {
                                        onRequestPin { amountDay = i }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (entry.withdrawal > 0.0) formatMoney(entry.withdrawal) else "−",
                                color = if (entry.withdrawal > 0.0) Navy else InkSoft,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = if (CardBorder != Color.Transparent) BorderStroke(1.dp, CardBorder) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    TotalRow("Total days × Daily rate", "${formatDays(card.totalWorkDays())} × ${formatMoney(card.dailyRate)} = ${formatMoney(card.totalPay())}")
                    TotalRow("Total withdrawal", formatMoney(card.totalWithdrawal()))
                    DashedDivider()
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Arrears of salary",
                            color = Navy,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatMoney(card.arrears()),
                            color = Teal,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onTotal,
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                ) {
                    Text("Total", color = OnAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .background(HeaderTonal, RoundedCornerShape(28.dp))
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Advance", color = InkSoft, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(10.dp))
                    Text(formatMoney(card.totalWithdrawal()), color = Teal, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (amountDay != null) {
        val idx = amountDay!!
        val entry = card.days[idx]
        AmountDialog(
            current = entry.withdrawal,
            onSave = { v ->
                setDay(idx, entry.copy(withdrawal = v))
                amountDay = null
            },
            onClear = {
                setDay(idx, entry.copy(withdrawal = 0.0))
                amountDay = null
            },
            onCancel = { amountDay = null }
        )
    }
}

@Composable
fun AmountDialog(
    current: Double,
    onSave: (Double) -> Unit,
    onClear: () -> Unit,
    onCancel: () -> Unit
) {
    var text by remember {
        mutableStateOf(if (current == 0.0) "" else formatMoney(current).drop(1))
    }
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = DialogBg,
        title = { Text("Withdrawal (₹)", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = sanitizeAmount(it) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = hajriFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text.toDoubleOrNull() ?: 0.0) }) {
                Text("Save", color = Indigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                if (current > 0.0) {
                    TextButton(onClick = onClear) { Text("Clear", color = RedMark) }
                }
                TextButton(onClick = onCancel) { Text("Cancel", color = InkSoft) }
            }
        }
    )
}

// ---------- page 3: totals ----------
@Composable
fun TotalScreen(
    card: HajriCard,
    onBack: () -> Unit,
    premiumUnlocked: Boolean,
    onOpenPaywall: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy)
            }
            Text(
                text = "Total",
                color = Navy,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = if (CardBorder != Color.Transparent) BorderStroke(1.dp, CardBorder) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                TotalRow("Name", card.name.ifBlank { "Unnamed card" })
                TotalRow("Date", card.monthLabel())
                TotalRow("Total work day", formatDays(card.totalWorkDays()))
                if (card.otDays() > 0.0) {
                    Text(
                        text = "${formatDays(card.baseWorkDays())} work days + ${formatDays(card.otDays())} OT days = ${formatDays(card.totalWorkDays())} days",
                        color = InkSoft,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                    )
                }
                TotalRow("Daily rate", formatMoney(card.dailyRate))
                TotalRow("Total pay", formatMoney(card.totalPay()))
                TotalRow("Total withdrawal", formatMoney(card.totalWithdrawal()))
                DashedDivider()
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Arrears of salary",
                        color = Navy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatMoney(card.arrears()),
                        color = Teal,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        Text(
            text = "Total pay = work days × daily rate\nArrears = total pay − total withdrawal",
            color = InkSoft,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (premiumUnlocked) shareMonthPdf(context, card) else onOpenPaywall()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Share", tint = OnAccent)
                Spacer(Modifier.width(8.dp))
                Text("Share as PDF", color = OnAccent, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ---------- dashed divider (premium bottom-sheet look) ----------
@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(vertical = 8.dp)
    ) {
        drawLine(
            color = DividerColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    }
}

@Composable
fun TotalRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = InkSoft, fontSize = 16.sp)
        Text(value, color = Navy, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ---------- PIN ----------
@Composable
fun PinDialog(store: HajriStore, onOk: () -> Unit, onCancel: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = DialogBg,
        title = { Text("Enter PIN", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Box(modifier = Modifier.offset { IntOffset(shake.value.roundToInt(), 0) }) {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = {
                            pin = it.filter { c -> c.isDigit() }.take(4)
                            error = false
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = hajriFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (error) {
                    Spacer(Modifier.height(8.dp))
                    Text("Wrong PIN. Try again.", color = RedMark, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (pin.length == 4 && pin == store.getPin()) {
                    onOk()
                } else {
                    error = true
                    attempts += 1
                    pin = ""
                    scope.launch {
                        shake.animateTo(-12f, tween(60))
                        shake.animateTo(12f, tween(60))
                        shake.animateTo(-8f, tween(60))
                        shake.animateTo(8f, tween(60))
                        shake.animateTo(0f, tween(60))
                    }
                }
            }) { Text("Unlock", color = Indigo, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel", color = InkSoft) }
        }
    )
}

// ---------- settings: change PIN ----------
@Composable
fun SettingsDialog(
    store: HajriStore,
    onClose: () -> Unit,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onOpenPaywall: () -> Unit,
) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var messageOk by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        containerColor = DialogBg,
        title = { Text("Settings", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Change PIN", color = Navy, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Current PIN") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = hajriFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = next,
                    onValueChange = { next = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("New PIN (4 digits)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = hajriFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (message.isNotEmpty()) {
                    Text(
                        message,
                        color = if (messageOk) GreenMark else RedMark,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text("Themes", color = Navy, fontWeight = FontWeight.SemiBold)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    THEME_OPTIONS.forEach { (option, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThemeChange(option) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(
                                        2.dp,
                                        if (option == currentTheme) Indigo else InkSoft,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (option == currentTheme) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Indigo, CircleShape)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(label, color = Navy, fontSize = 15.sp)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                val premiumUnlocked = store.isPremiumUnlocked()
                val savedCode = store.getPremiumCode()
                if (premiumUnlocked && savedCode != null) {
                    Text("Your code: $savedCode", color = InkSoft, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    current.length != 4 || next.length != 4 -> {
                        message = "Both PINs must be 4 digits."
                        messageOk = false
                    }
                    current != store.getPin() -> {
                        message = "Current PIN is wrong."
                        messageOk = false
                    }
                    else -> {
                        store.setPin(next)
                        message = "PIN changed."
                        messageOk = true
                        current = ""
                        next = ""
                    }
                }
            }) { Text("Save PIN", color = Indigo, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Close", color = InkSoft) }
        }
    )
}

// ---------- Color Picker Composable (T009) ----------
@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(0f) }
    var sat by remember { mutableStateOf(1f) }
    var val_ by remember { mutableStateOf(1f) }
    var hexInput by remember { mutableStateOf("") }

    // Initialize from color
    LaunchedEffect(initialColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsv)
        hue = hsv[0]
        sat = hsv[1]
        val_ = hsv[2]
        hexInput = String.format("#%06X", (0xFFFFFF and initialColor.toArgb()))
    }

    val currentColor = Color.hsv(hue, sat, val_)

    fun updateFromHex(hex: String) {
        val clean = hex.removePrefix("#")
        if (clean.length == 6 || clean.length == 8) {
            try {
                val parsed = clean.toLong(radix = 16)
                val color = if (clean.length == 6) Color(parsed or 0xFF000000) else Color(parsed)
                val hsv = FloatArray(3)
                android.graphics.Color.colorToHSV(color.toArgb(), hsv)
                hue = hsv[0]
                sat = hsv[1]
                val_ = hsv[2]
            } catch (e: Exception) {
                // Ignore invalid hex
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DialogBg,
        title = { Text("Pick Color", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(currentColor, RoundedCornerShape(8.dp))
                        .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                )

                // Hue Slider
                Text("Hue", color = InkSoft, fontSize = 12.sp)
                Slider(
                    value = hue,
                    onValueChange = {
                        hue = it
                        hexInput = String.format("#%06X", (0xFFFFFF and currentColor.toArgb()))
                    },
                    valueRange = 0f..360f,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = Color.hsv(hue, 1f, 1f)
                    )
                )

                // Saturation/Value Box (Simplified as sliders for offline build constraints without complex canvas drawing)
                Text("Saturation", color = InkSoft, fontSize = 12.sp)
                Slider(
                    value = sat,
                    onValueChange = {
                        sat = it
                        hexInput = String.format("#%06X", (0xFFFFFF and currentColor.toArgb()))
                    },
                    valueRange = 0f..1f
                )

                Text("Value", color = InkSoft, fontSize = 12.sp)
                Slider(
                    value = val_,
                    onValueChange = {
                        val_ = it
                        hexInput = String.format("#%06X", (0xFFFFFF and currentColor.toArgb()))
                    },
                    valueRange = 0f..1f
                )

                // Hex Input
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = {
                        hexInput = it
                        updateFromHex(it)
                    },
                    label = { Text("Hex") },
                    singleLine = true,
                    colors = hajriFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(currentColor) }) {
                Text("Select", color = Indigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = InkSoft)
            }
        }
    )
}

// ---------- Premium paywall: QR + ₹49 one-time, everything unlocked for lifetime ----------
// The buyer pays via the QR, messages Bhavesh on Telegram with the 12-digit UTR,
// and Bhavesh sends a 9-digit code from the batch. The app checks the code's SHA-256
// hash against the embedded set — no code, no unlock. (Batch 1: 100 codes.)

fun sha256Hex(input: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

// SHA-256 hashes of the 100 batch-1 Premium unlock codes (plain codes are NOT in the app).
private val PREMIUM_CODE_HASHES = setOf(
    "1d60acbe870e8c15268ce8ac5581d65533399aa9ae75fd9cff4c6d49a876e2e5",
    "5c9c7dff0c3e191cce6896bfdf6e165c406428a58be71d7bafea4dded3fd30a1",
    "20d58e5b67d96405eafd695ce58dad2ba7257a263d86b95855da4af5c1483fc0",
    "287fc2a8de84e40fdcb618abe668614b6a809f263ecc81f3641d03f99a1d4f56",
    "287c6d61ad282db126ef447b71334b9977bb3f4897a6b053808e365270b00af5",
    "fbe69ffb92b9a50f3553931d770d642212e0b1d5c144a54cdc155f59f821c21f",
    "9617dc53c753da390e6f77ea0bdcb1aec7345fb028c34883d8c527425256e4a6",
    "f079f142acbf7ce6118b0e7016e9c5bdcec52698ffb0d209afe82ebe0e09139e",
    "275a9523984adf54b93e8c7ae7387a4c942a06a8de79cca8e640aa14ea7f3016",
    "f46677b4c0daf6d3ab0b2ee2fc1b6eeebb38ab93e18fdf910cf5245183a070c6",
    "aed7d91e25ece2a79df6cba01e73fd81157716b9515bc9c06fd9623b16e045b0",
    "94dc1fa24b79e1ea3eb86d24cfa7942899b8b21610986b43b9096d6d615ede07",
    "0a2fc66a9d1a3f3d428e1a59e64c0ee4731cce08948795fe70cff62f582708f5",
    "35af492c5ec48af2640992dc86d9007b28c8573cfffc72ce68f6b2becd46a33b",
    "4c6a5bc17c340ea9ce41ab756139e95efbfe59ab694aa61305855f7ce3a40e95",
    "acf5c1c8464dcd0820c3aaa7492d10d9accc32c0ef4961bff09b83cf74d5b27e",
    "ac5b6a7cfef13dee5b860b33c22520edc96458489f4a2712508a3bcef937b1d5",
    "2c89f879bf2fcb6410129bee892a2bbdd217f8b4dffd89e2201b433eedc51b7b",
    "40efe031f26ba62d622b4f41a4c3e61ec9a8f391a2d5853ae109ab49a93e084d",
    "0a974324e212ada853a10aebb3e5dafb82768c442b5e1f1bea96e731c81b121a",
    "e2db8b1d6b7854f6028680a9deca27233e37cf8476a3a04ab95ff8c10efccf96",
    "6b044e1d91ac6a94b9bfd6045efd048d89c7cd4e3983b70cc6166c0138302d9b",
    "b20cce10be1da04321af7c1ec7e41c26b72dff8f9c3b1d1a8d6248869610a4a3",
    "f5a15abfe2fa0c806a07a74cf578e658a52d23b8dd1c20b73fa1fc43b1b7d5ec",
    "46a12e303e73e4b0fdc5c2a106aee375d43fa6626c4afb8da152b060ef608773",
    "a5b74718fceab467cd4dc9b2a5b51f49810bf5881bb3e0bba11a2673b0f6cd99",
    "b4109414dd023b3008f649a43e9ea51b223ca8362ada7b8c617ba105715f9bb3",
    "7fdcea6ef67948e2dc9f6bde808ae45d3bcf1544344e3d6f225060c56acaaf1c",
    "c10594dd4b6cbf32689de96d0fc28cf01248037beef645bdf3c8bd30485dd671",
    "0f8a34c951328d8bd36ccbee24e01b439c777edd5acf6ba163bc793dd3106c35",
    "c4438a4b0b5017ba4ac536e44708bf711824a16719aa68902fc0f14e0f48391a",
    "efba22cc44d29f58ef22af0453873f094da66d5ec72fa7c1631c88b9d1658cc1",
    "f773b17fa5aa10ff1c82366d43ad98ade9c654e08c2c6d0ac46a1dd74c0f37c8",
    "dd8099714fe33d6a864430015cde51a6c81dea2feb54601fcd5e42715fded195",
    "291e075d0dd7ebc89a0e65e52327cc218b3b081e32273b81114f46edcc7eb4d0",
    "95d34febf8ce121b2b91e11ca0da016edaef7d7dfdb35268606736ebc8885969",
    "ab498bd707e8a6e28f6c3b2a883e6e5c8ef1d339aa660691cc8c87e29ac43681",
    "b4a6ad45ce4903d8bd7b5ccbe75625ea688780d4359e0b1c04e16ceaa7f22cfd",
    "9860a0dda4d19ba5366f67a7a5b07fba8a2aaf1b9e69eea6cc572323644d2a14",
    "f7fe0c926d47553c6b570dcf6886276250b329dee9cacb784b371637dba91eb3",
    "0bfb93bab3607f77594140e6d55c9a5e5b9ed2edd7d979712f3be923ce889716",
    "168c0d4b851a2025ad2ddecaf643881c572d4635f7c1250022cff7850272b829",
    "cf7bdeeef1333d29d6c3d8a657ad002a0d6d84497b646244a8275d106bcbaac7",
    "1ca6e74f185d69828211d99cad67c2fb70e45d336fe5054b9c9785f2bd059e3f",
    "2d18b4022fe75d994320fc48018f9ae1c1b411d4386b726bcb1588a41288f748",
    "f20edef40c86227e97df95f5a18c3aeec009d072b7c87f653918bbcbaf613262",
    "2f6fcc25043abef5b9190c208a4bd359cf46c6e9c70a5379e8b969e3b3742e0f",
    "f967c4805ae5f5e84c1081e539ad4cf339459b3aa8101d5a423a71242a027a54",
    "7b161f8f2350e21ebc0b68c34c5844482f6f930a4b501be6b2a9924f0779d046",
    "d341a97bbf854e165343dca6bc023571847bd5fa4247a064d8a497f5788263d7",
    "9b26f233646352ed8f4015166ca144ddea5309bc93ec9390686b57e91898e837",
    "a4144a1cf6961b92fe6f980294287b3599ea036684337f1c6c79103fbd754307",
    "817981442975fc1a5b5d6e21737ae9fd89d3e03903b74c15b703885041166bc9",
    "d5a6cb425dbebac4a0ff9939636967a1288e40f3428000db2470e231ac1dabb1",
    "379871a044b29755e62c272135c9f51ab1394b33e3611400fa0538ed9bed05c4",
    "2bec822b5e4e318d8f4f606b78c7d5c6c2a5f2a2166772c827f3df5b061db02c",
    "e231ec786c3aec5108580c7c415266aecace60815be0ee24cbdc5202c232383f",
    "ceaf6d35cebd7fcceda96325a1de60ed82ef8cf1bae007bcaf450efe99f753ca",
    "602224a11d60fd9add6beaf832a761e17cfb7eea80c017338a25c97f1e185ada",
    "56c1287daa312835f3663fa63784f3acfe9fb957038f274ef7ea4a1d1f8c92d5",
    "0c08d9c789f8cbde61fd6beead3955fab5e594b027e35710146cb7f5d8aa642e",
    "cd09cd9a2b2ce1a3085a0d7613ca7769848e5fd48e4e3b022641101de0650292",
    "0d92f2448c8d47c20995b795f5415fb51fd03d9d345cd30526a10f97a914f4f7",
    "f9c84631bbf6cd4e1afb90907fafac919f868f0ee8a9b0e2f1d56b2e350827fb",
    "6c485c8011e04c6aace909862004cf56a871470ef6be4e2df49057fa39fa374e",
    "6a4fe8f4b44b58c238ee0a87ea78f3dc6867f39793c25afb1d44a97338055f1a",
    "6fa567308b7aa042d26b269394a6433f4c254f28318f3000886cdeb56f1683f5",
    "c6f6df127eca8962b18b4f1c22ecfc89efe8e3508f5e3b8069585fe2a7394035",
    "2281e74f02edfeaff65e823ec1293553654a671d15bda9ad5eb652965211987b",
    "b970a028ec0494e7b8a6a2710d2187bb775204c27e9edd0df54cbae1b0a6dbdf",
    "0045549852c8c6a7f744b356d36d652b82dd081786789e0a3ae922f67845b243",
    "e769b86987ea82293c25dc38bedb4ce7bec70bd2b58faa4b75dfdd5d5977c3d2",
    "046c94d2caad62c9123fe23f4da00b1d8a21cf1c98ed384cf9f9e340aff9991b",
    "e579143c6ad41bc6786743b05ec67bc56bae3534f6ff4f37328fc9af568d28dc",
    "a87c2617bae162923f6009661195b259bb6815a171074dcd6b1f365901d10a1a",
    "d2c95890fc60a9161b728dcbac35e5f865b4ac61c00c5dc76cd33229e42025af",
    "3b8d6e1fc7f7d871242f631a5d31862848f587f178d42ff7b5fd2b16ebe72cf2",
    "176333c67c51d3b4a5977aad19fd63e9d13d79381b4a4210e228f1f3c9876761",
    "0113d4e2e427e5704a1d6d5a0e605923827ea4763dbf5a1809c6a483a2f0b7c3",
    "e25922b937ea610829b5c08df449fa4b7c96b42ce179f5b141e33018835ba165",
    "c110866bb5af2ad14d3600559b8b79327dbc912afdeefac981ca8cbb53d13883",
    "beb07721d2ce5f0d3e933dde561f517d3231a3eaaef467a5c0725617d230865a",
    "679e2571c5cc6eface1cebcadddcf2ffab7314050bf8a2e5e75ed980972cc7ee",
    "2920587e4560afa828c4871aa8dd1753003e17d74d6446403d6608b029c0373a",
    "7ce9e3ff3f441cb26961b71cfc2ccaab5209a0368db5b188724296352b9175f8",
    "06a78c57f37ee4e92c9ab9ea17c055efad84c7bccdec59134f7cfa79885e3d21",
    "04d724bce531b5db34125ffc93003d974e1af3b200e760cf7b1bd35c973ead84",
    "68905958d8cd222937aaa680b5f6413122730bbeaa5c6da31db5c0319296e9b0",
    "9947575637da18a31fa72faadbecc8a49bb639e0a10a49a739b0fa529ba055d1",
    "87a1eef324928abb61248a4ad6c5f57ee87d706fe307a4057fb0d745528f7259",
    "5a0d67067f51c263db0219bd3de3ec1901272f2b8476589f6a8603caa59e1a05",
    "632646d74ed45b1ffc8823441de0473794675fd5945c4d4007df13d55702e2a9",
    "38f43003731beaa38fdfeb7f47b809cce1a48b64870eaae60efa16ee094b626e",
    "189d80835d5638ea2328aec9d95283b5fa311da75b77779d4f630da517f7579e",
    "5c8bfa397edb8ae099d402de04bf276a1ec73ba570b77936b24d6c0f2fae5dc0",
    "816ba426341d326939ef2952936ec93c351cd62a7da558cc8a7d6e7a126525fa",
    "5d6b900ec516b2c2ad9c00657e6e5a68c5767d6f3542aa57b2e22fe594cea1b9",
    "1971cac8e780017cdccb231b806fbcea7371d27434d0777df6f655ed9f43b8ff",
    "9f7ea2e247855b32eb106b67c5dd171650e3a48d029762fa7f3f0afdb130387a",
    "c87ac04a93f75ab8c5b577264c308fbddb06545fa4204f0b1db2826a400e9721",
)

@Composable
fun PremiumPaywallDialog(
    store: HajriStore,
    onClose: () -> Unit,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    var codeInput by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf(false) }
    var payError by remember { mutableStateOf(false) }
    var unlocked by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        containerColor = DialogBg,
        title = { Text("Premium — ₹49", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (unlocked) {
                    Text(
                        "Unlocked! Everything is now available for lifetime on this device.",
                        color = Navy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Your code: ${store.getPremiumCode() ?: ""}",
                        color = InkSoft,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onUnlocked) { Text("Done", fontWeight = FontWeight.Bold) }
                } else {
                    Text(
                        "One-time payment of ₹49 unlocks everything for lifetime on this device — PDF export, Payment Screenshot and Reminders.",
                        color = InkSoft,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Image(
                        painter = painterResource(id = R.drawable.upi_qr),
                        contentDescription = "UPI QR code to pay ₹49",
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text("₹49", color = Navy, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Button(onClick = {
                        try {
                            val uri = Uri.parse("upi://pay?pa=nsutharbhavesh-1@oksbi&pn=B%20Art%20Paintings%20(bhavesh)&am=49&cu=INR")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (e: Exception) {
                            payError = true
                        }
                    }) { Text("Pay ₹49", fontWeight = FontWeight.Bold) }
                    if (payError) {
                        Text("No UPI app found. Scan the QR code instead.", color = RedMark, fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/iopXtiop_bot")))
                    }) { Text("Message me on Telegram", color = Indigo, fontWeight = FontWeight.Bold) }
                    Text(
                        "After paying, send your 12-digit UTR number on Telegram and you will get your 9-digit unlock code. Enter it below.",
                        color = InkSoft,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = {
                            codeInput = it.filter { c -> c.isDigit() }.take(9)
                            codeError = false
                        },
                        label = { Text("9-digit unlock code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = hajriFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (codeError) {
                        Text("Wrong code. Check the code and try again.", color = RedMark, fontSize = 13.sp)
                    }
                    Button(onClick = {
                        if (codeInput.length == 9 && PREMIUM_CODE_HASHES.contains(sha256Hex(codeInput))) {
                            store.setPremiumCode(codeInput)
                            store.setPremiumUnlocked(true)
                            unlocked = true
                        } else {
                            codeError = true
                        }
                    }) { Text("Unlock", fontWeight = FontWeight.Bold) }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onClose) { Text("Close", color = InkSoft) }
        }
    )
}

// ---------- daily fill reminder (T027) ----------
@Composable
fun ReminderDialog(store: HajriStore, onClose: () -> Unit) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(store.isReminderEnabled()) }
    var hour by remember { mutableStateOf(store.getReminderHour()) }
    var minute by remember { mutableStateOf(store.getReminderMinute()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            store.setReminderEnabled(true)
            store.setReminderTime(hour, minute)
            ReminderScheduler.schedule(context, hour, minute)
            enabled = true
        }
    }
    fun applyEnabled(on: Boolean) {
        if (on) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                store.setReminderEnabled(true)
                store.setReminderTime(hour, minute)
                ReminderScheduler.schedule(context, hour, minute)
                enabled = true
            }
        } else {
            store.setReminderEnabled(false)
            ReminderScheduler.cancel(context)
            enabled = false
        }
    }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = DialogBg,
        title = { Text("Daily reminder", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Remind me to fill today's hajri",
                        color = Navy,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = enabled, onCheckedChange = { applyEnabled(it) })
                }
                if (enabled) {
                    Text(
                        "Every day at ${"%02d".format(hour)}:${"%02d".format(minute)}, only when today is still unmarked.",
                        color = InkSoft,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, m ->
                                hour = h
                                minute = m
                                store.setReminderTime(h, m)
                                ReminderScheduler.schedule(context, h, m)
                            },
                            hour, minute, true
                        ).show()
                    }) {
                        Text("Change time", color = Indigo, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text("Done", color = Indigo) }
        }
    )
}

// ---------- AdMob banner (Google test unit until real AdMob IDs land) ----------
@Composable
fun BannerAd() {
    val widthDp = LocalConfiguration.current.screenWidthDp
    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, widthDp))
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
