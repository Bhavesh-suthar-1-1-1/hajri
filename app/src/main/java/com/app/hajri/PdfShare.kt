package com.app.hajri

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/** Exports one month's card as a PDF and opens the Android share sheet (e.g. WhatsApp). */
fun shareMonthPdf(context: Context, card: HajriCard) {
    try {
        val dir = File(context.cacheDir, "pdf").apply { mkdirs() }
        // drop exports older than a day so the cache does not grow forever
        val cutoff = System.currentTimeMillis() - 24L * 60 * 60 * 1000
        dir.listFiles()?.forEach { if (it.lastModified() < cutoff) it.delete() }

        val safeName = card.name.ifBlank { "hajri" }
            .replace(Regex("[^A-Za-z0-9_-]+"), "_")
            .trim('_')
            .ifBlank { "hajri" }
            .take(40)
        val file = File(dir, "${safeName}_${String.format(Locale.US, "%02d", card.month)}_${card.year}.pdf")
        FileOutputStream(file).use { out ->
            val doc = buildMonthPdf(card)
            try {
                doc.writeTo(out)
            } finally {
                doc.close()
            }
        }
        val uri = FileProvider.getUriForFile(context, "com.app.hajri.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri("", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share month as PDF")
        if (context !is Activity) chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not create or share the PDF", Toast.LENGTH_LONG).show()
    }
}

private fun fit(text: String, paint: Paint, maxW: Float): String {
    if (paint.measureText(text) <= maxW) return text
    var s = text
    while (s.isNotEmpty() && paint.measureText("$s\u2026") > maxW) s = s.dropLast(1)
    return "$s\u2026"
}

private fun buildMonthPdf(card: HajriCard): PdfDocument {
    val doc = PdfDocument()
    val pageW = 595
    val pageH = 842
    val margin = 40f
    val rowH = 17f
    val titlePaint = Paint().apply { color = Color.BLACK; textSize = 20f; isFakeBoldText = true }
    val headPaint = Paint().apply { color = Color.BLACK; textSize = 13f; isFakeBoldText = true }
    val rowPaint = Paint().apply { color = Color.BLACK; textSize = 12f }
    val notePaint = Paint().apply { color = Color.DKGRAY; textSize = 11f }
    val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
    val colDate = margin
    val colWork = margin + 70f
    val colOt = margin + 210f
    val colWdr = margin + 320f
    val textW = pageW - 2 * margin

    var pageNo = 1
    var page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create())
    var canvas = page.canvas
    var y = 64f

    fun drawHeader() {
        canvas.drawText("1/31 Days", margin, y, titlePaint)
        y += 26f
        canvas.drawText(
            fit("${card.name.ifBlank { "Unnamed card" }}  \u2022  ${card.monthLabel()}", headPaint, textW),
            margin, y, headPaint
        )
        y += 20f
        canvas.drawText("Daily rate ${formatMoney(card.dailyRate)}", margin, y, rowPaint)
        y += 22f
        canvas.drawText("Date", colDate, y, headPaint)
        canvas.drawText("Work", colWork, y, headPaint)
        canvas.drawText("Overtime", colOt, y, headPaint)
        canvas.drawText("Withdrawal", colWdr, y, headPaint)
        y += 8f
        canvas.drawLine(margin, y, pageW - margin, y, linePaint)
        y += 20f
    }

    fun newPage() {
        doc.finishPage(page)
        pageNo++
        page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create())
        canvas = page.canvas
        y = 64f
        drawHeader()
    }

    drawHeader()
    for (i in 0 until card.daysInMonth()) {
        if (y > pageH - 60f) newPage()
        val e = card.days[i]
        val status = when (e.status) {
            WorkStatus.WORKED -> "Worked"
            WorkStatus.ABSENT -> "Day off"
            WorkStatus.HALF -> "Half day"
            WorkStatus.NONE -> "-"
        }
        canvas.drawText((i + 1).toString(), colDate, y, rowPaint)
        canvas.drawText(status, colWork, y, rowPaint)
        canvas.drawText(if (e.ot) "Yes" else "-", colOt, y, rowPaint)
        canvas.drawText(
            if (e.withdrawal > 0.0) formatMoney(e.withdrawal) else "-",
            colWdr, y, rowPaint
        )
        y += rowH
    }
    // keep the whole summary block together on one page
    if (y + 120f > pageH - 20f) newPage()
    y += 8f
    canvas.drawLine(margin, y, pageW - margin, y, linePaint)
    y += 24f
    canvas.drawText("Total work days: ${formatDays(card.totalWorkDays())}", margin, y, headPaint)
    y += 18f
    canvas.drawText(
        "${formatDays(card.baseWorkDays())} work days + ${formatDays(card.otDays())} OT days",
        margin, y, notePaint
    )
    y += 22f
    canvas.drawText("Total pay: ${formatMoney(card.totalPay())}", margin, y, headPaint)
    y += 22f
    canvas.drawText("Total withdrawal: ${formatMoney(card.totalWithdrawal())}", margin, y, headPaint)
    y += 22f
    canvas.drawText("Arrears of salary: ${formatMoney(card.arrears())}", margin, y, headPaint)
    doc.finishPage(page)
    return doc
}
