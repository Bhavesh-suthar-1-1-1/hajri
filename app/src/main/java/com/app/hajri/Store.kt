package com.app.hajri

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors

enum class WorkStatus { NONE, WORKED, ABSENT, HALF }

fun WorkStatus.next(): WorkStatus = when (this) {
    WorkStatus.NONE -> WorkStatus.WORKED
    WorkStatus.WORKED -> WorkStatus.ABSENT
    WorkStatus.ABSENT -> WorkStatus.HALF
    WorkStatus.HALF -> WorkStatus.NONE
}

fun WorkStatus.mark(): String = when (this) {
    WorkStatus.NONE -> ""
    WorkStatus.WORKED -> "\u2713"
    WorkStatus.ABSENT -> "\u2717"
    WorkStatus.HALF -> "\u00BD"
}

fun WorkStatus.dayValue(): Double = when (this) {
    WorkStatus.NONE -> 0.0
    WorkStatus.WORKED -> 1.0
    WorkStatus.ABSENT -> 0.0
    WorkStatus.HALF -> 0.5
}

data class DayEntry(
    val status: WorkStatus = WorkStatus.NONE,
    val ot: Boolean = false,
    val withdrawal: Double = 0.0
) {
    fun isBlank(): Boolean = status == WorkStatus.NONE && !ot && withdrawal == 0.0

    fun toJson(): JSONObject = JSONObject().apply {
        put("s", status.ordinal)
        put("o", ot)
        put("w", withdrawal)
    }

    companion object {
        fun fromJson(o: JSONObject): DayEntry {
            val s = WorkStatus.values().getOrNull(o.optInt("s", 0)) ?: WorkStatus.NONE
            val w = o.optDouble("w", 0.0)
            return DayEntry(s, o.optBoolean("o", false), if (w.isNaN() || w < 0.0) 0.0 else w)
        }
    }
}

data class HajriCard(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val month: Int = LocalDate.now().monthValue,
    val year: Int = LocalDate.now().year,
    val dailyRate: Double = 0.0,
    val days: List<DayEntry> = List(31) { DayEntry() }
) {
    fun hasContent(): Boolean =
        name.isNotBlank() || dailyRate > 0.0 || days.any { !it.isBlank() }

    // Real number of days in this card's month (28-31). Rows beyond it are ignored.
    fun daysInMonth(): Int = try {
        YearMonth.of(year, month).lengthOfMonth()
    } catch (e: Exception) {
        31
    }

    fun validDays(): List<DayEntry> = days.take(daysInMonth())

    fun baseWorkDays(): Double = validDays().sumOf { it.status.dayValue() }

    // Overtime rule: 2 OT ticks = 1 work day (each OT tick counts 0.5 day)
    fun otDays(): Double = 0.5 * validDays().count { it.ot }

    fun totalWorkDays(): Double = baseWorkDays() + otDays()

    fun totalWithdrawal(): Double = validDays().sumOf { it.withdrawal }

    fun totalPay(): Double = totalWorkDays() * dailyRate

    fun arrears(): Double = totalPay() - totalWithdrawal()

    fun monthLabel(): String = MONTH_NAMES[month - 1] + " " + year

    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("month", month)
        put("year", year)
        put("rate", dailyRate)
        val arr = JSONArray()
        for (d in days) arr.put(d.toJson())
        put("days", arr)
    }

    companion object {
        fun fromJson(o: JSONObject): HajriCard {
            val days = mutableListOf<DayEntry>()
            val arr = o.optJSONArray("days")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val d = arr.optJSONObject(i)
                    days.add(if (d != null) DayEntry.fromJson(d) else DayEntry())
                }
            }
            while (days.size < 31) days.add(DayEntry())
            return HajriCard(
                id = o.optString("id", UUID.randomUUID().toString()),
                name = o.optString("name", ""),
                month = o.optInt("month", LocalDate.now().monthValue).coerceIn(1, 12),
                year = o.optInt("year", LocalDate.now().year),
                dailyRate = o.optDouble("rate", 0.0).let { if (it.isNaN() || it < 0.0) 0.0 else it },
                days = days.take(31)
            )
        }
    }
}

val MONTH_NAMES = arrayOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

fun formatMoney(v: Double): String {
    val rounded = Math.round(v * 100.0) / 100.0
    val sign = if (rounded < 0.0) "-" else ""
    val abs = Math.abs(rounded)
    return if (abs == Math.floor(abs)) {
        sign + "\u20B9" + abs.toLong().toString()
    } else {
        sign + "\u20B9" + String.format(Locale.US, "%.2f", abs)
    }
}

fun formatDays(v: Double): String {
    return if (v == Math.floor(v)) v.toLong().toString() else String.format(Locale.US, "%.1f", v)
}

/** Keeps digits and a single '.', max 9 integer digits and 2 decimals (prevents overflow / "1.2.3"). */
fun sanitizeAmount(input: String): String {
    val sb = StringBuilder()
    var dot = false
    var intDigits = 0
    var decDigits = 0
    for (c in input) {
        when {
            c.isDigit() && !dot && intDigits < 9 -> { sb.append(c); intDigits++ }
            c.isDigit() && dot && decDigits < 2 -> { sb.append(c); decDigits++ }
            c == '.' && !dot -> { sb.append(c); dot = true }
        }
    }
    return sb.toString()
}

enum class AppTheme(val key: String) {
    MATERIAL_YOU("material_you"),
    DARK("dark"),
    GLASS("glass"),
    DAY("day");

    companion object {
        fun fromKey(key: String?): AppTheme = when (key) {
            "glass" -> GLASS
            "dark" -> DARK
            "material_you" -> MATERIAL_YOU
            "day", "white" -> DAY
            else -> MATERIAL_YOU
        }
    }
}

class HajriStore(private val context: Context) {
    private companion object {
        val writer = Executors.newSingleThreadExecutor { r ->
            Thread(r, "hajri-store").apply { isDaemon = true }
        }
    }

    private val file: File get() = File(context.filesDir, "cards.json")

    fun loadAll(): List<HajriCard> {
        val f = file
        if (!f.exists()) return emptyList()
        val text = try { f.readText() } catch (e: Exception) { return emptyList() }
        if (text.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(text)
            val out = mutableListOf<HajriCard>()
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                try { out.add(HajriCard.fromJson(o)) } catch (e: Exception) { /* skip one bad card */ }
            }
            out
        } catch (e: Exception) {
            // Unreadable file: keep a copy so the next save cannot destroy the only evidence.
            try { f.copyTo(File(context.filesDir, "cards.corrupt.json"), overwrite = true) } catch (ex: Exception) {}
            emptyList()
        }
    }

    // Serialises on the caller thread (cheap), writes on a background thread via temp file + atomic rename,
    // so typing never blocks the UI and a crash mid-write cannot leave a half-written cards.json.
    fun saveAll(cards: List<HajriCard>) {
        val text = try {
            val arr = JSONArray()
            for (c in cards) arr.put(c.toJson())
            arr.toString()
        } catch (e: Exception) {
            return
        }
        val target = file
        writer.execute {
            try {
                val tmp = File(target.parentFile, "cards.json.tmp")
                tmp.writeText(text)
                try {
                    Files.move(
                        tmp.toPath(), target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
                    )
                } catch (e: Exception) {
                    tmp.copyTo(target, overwrite = true)
                    tmp.delete()
                }
            } catch (e: Exception) {
                // storage failure: keep in-memory state, do not crash
            }
        }
    }

    // ---- PIN, default 1234 ----
    private val prefs get() = context.getSharedPreferences("hajri_prefs", Context.MODE_PRIVATE)

    fun getPin(): String = prefs.getString("pin", "1234") ?: "1234"

    fun setPin(newPin: String) {
        prefs.edit().putString("pin", newPin).apply()
    }

    // ---- Theme, default Material You ----
    fun getTheme(): AppTheme =
        AppTheme.fromKey(prefs.getString("theme", AppTheme.MATERIAL_YOU.key))

    fun setTheme(theme: AppTheme) {
        prefs.edit().putString("theme", theme.key).apply()
    }

    // ---- Premium State ----
    fun isPremiumUnlocked(): Boolean = prefs.getBoolean("premium_unlocked", false)

    fun setPremiumUnlocked(unlocked: Boolean) {
        prefs.edit().putBoolean("premium_unlocked", unlocked).apply()
    }

    // The 9-digit code generated on-device after the buyer pays (see research.md Decision 5).
    // It is a receipt of the purchase, shown to the buyer and in Settings — not a gate.
    fun getPremiumCode(): String? = prefs.getString("premium_code", null)

    fun setPremiumCode(code: String?) {
        if (code == null) {
            prefs.edit().remove("premium_code").apply()
        } else {
            prefs.edit().putString("premium_code", code).apply()
        }
    }

    // ---- Daily fill reminder ----
    fun isReminderEnabled(): Boolean = prefs.getBoolean("reminder_enabled", false)

    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("reminder_enabled", enabled).apply()
    }

    fun getReminderHour(): Int = prefs.getInt("reminder_hour", 21)

    fun getReminderMinute(): Int = prefs.getInt("reminder_minute", 0)

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit().putInt("reminder_hour", hour).putInt("reminder_minute", minute).apply()
    }
}

// ---------- payment screenshots + notices (v1.5) ----------

fun todayLabel(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

data class PaymentShot(
    val id: String = UUID.randomUUID().toString(),
    val label: String = "",
    val date: String = "",
    val fileName: String = "",
    val kind: String = "image" // "image" | "pdf"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("label", label)
        put("date", date)
        put("file", fileName)
        put("kind", kind)
    }

    companion object {
        fun fromJson(o: JSONObject): PaymentShot = PaymentShot(
            id = o.optString("id", UUID.randomUUID().toString()),
            label = o.optString("label", ""),
            date = o.optString("date", ""),
            fileName = o.optString("file", ""),
            kind = o.optString("kind", "image")
        )
    }
}

data class NoticeItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val date: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("text", text)
        put("date", date)
    }

    companion object {
        fun fromJson(o: JSONObject): NoticeItem = NoticeItem(
            id = o.optString("id", UUID.randomUUID().toString()),
            text = o.optString("text", ""),
            date = o.optString("date", "")
        )
    }
}

/** Local-only storage for payment screenshots and notices. Files live in filesDir/shots/. */
class ExtrasStore(private val context: Context) {
    val shotsDir: File get() = File(context.filesDir, "shots").apply { mkdirs() }
    private val shotsFile: File get() = File(context.filesDir, "shots.json")
    private val noticesFile: File get() = File(context.filesDir, "notices.json")

    fun loadShots(): List<PaymentShot> = loadList(shotsFile) { PaymentShot.fromJson(it) }

    fun saveShots(shots: List<PaymentShot>) {
        val arr = JSONArray()
        for (s in shots) arr.put(s.toJson())
        writeAtomic(shotsFile, arr.toString())
    }

    fun loadNotices(): List<NoticeItem> = loadList(noticesFile) { NoticeItem.fromJson(it) }

    fun saveNotices(notices: List<NoticeItem>) {
        val arr = JSONArray()
        for (n in notices) arr.put(n.toJson())
        writeAtomic(noticesFile, arr.toString())
    }

    private fun <T> loadList(f: File, parse: (JSONObject) -> T): List<T> {
        if (!f.exists()) return emptyList()
        val text = try { f.readText() } catch (e: Exception) { return emptyList() }
        if (text.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(text)
            val out = mutableListOf<T>()
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                try { out.add(parse(o)) } catch (e: Exception) { /* skip one bad row */ }
            }
            out
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun writeAtomic(target: File, text: String) {
        try {
            val tmp = File(target.parentFile, target.name + ".tmp")
            tmp.writeText(text)
            try {
                Files.move(
                    tmp.toPath(), target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
                )
            } catch (e: Exception) {
                tmp.copyTo(target, overwrite = true)
                tmp.delete()
            }
        } catch (e: Exception) {
            // storage failure: keep in-memory state, do not crash
        }
    }
}
