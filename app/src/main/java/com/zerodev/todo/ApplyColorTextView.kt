package com.zerodev.todo

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView

class ApplyColorTextView {
     fun applyColorToTextView(textWithColor: String , textView: TextView) {
        try {
            val spannableString = parseAndApplyColor(textWithColor)
            textView.text = spannableString
        } catch (e : Exception) {
            textView.text = textWithColor
        }
    }
    private fun parseAndApplyColor(inputText: String): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(inputText)

        val regex = "\\[color:(#[0-9A-Fa-f]{6})](.*?)\\[/color]".toRegex()
        var matchResult = regex.find(inputText)

        while (matchResult != null) {
            val colorHex = matchResult.groupValues[1]
            val textInsideTag = matchResult.groupValues[2]

            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1

            // Apply color only to the text inside the color tags
            val colorSpan = ForegroundColorSpan(Color.parseColor(colorHex))
            spannableStringBuilder.setSpan(
                colorSpan,
                startIndex + "[color:$colorHex]".length,
                startIndex + "[color:$colorHex]".length + textInsideTag.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Remove the color change tags
            spannableStringBuilder.delete(endIndex - "[/color]".length, endIndex)
            spannableStringBuilder.delete(startIndex, startIndex + "[color:$colorHex]".length)

            // Find the next match
            matchResult = regex.find(spannableStringBuilder)
        }

        return spannableStringBuilder
    }
}