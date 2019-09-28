package com.dede.devtools.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.dede.devtools.ext.isNull
import java.util.regex.Pattern

/**
 * Created by hsh on 2019-09-27 16:26
 */
object LogDecorator {

    /** 日期 */
    const val FLAG_DATE = 1
    /** 时间 */
    const val FLAG_TIME = 1 shl 1
    /** pid */
    const val FLAG_PID = 1 shl 2
    /** tid */
    const val FLAG_TID = 1 shl 3
    /** level */
    const val FLAG_LEVEL = 1 shl 4
    /** TAG */
    const val FLAG_TAG = 1 shl 5
    /** LOG content */
    const val FLAG_CONTENT = 1 shl 6

    /** Full log */
    const val FLAG_ALL = -1// ...11 11111111

    /** Exclude Date */
    const val FLAG_DEFAULT = FLAG_ALL and (FLAG_DATE or FLAG_PID or FLAG_TID).inv()

    /**
     * group 1           2         3     4       5      6       7
     *  ^(MM-dd) (HH:mm:ss.SSS) (PID) (TID) (LEVEL) (TAG) : (LOG_MSG)$
     * simple
     *  09-27 15:21:41.492  1056  1169 I LIGHT   : [LightSensor.cpp: processEvent: 331] light value is 407
     */
    private val pattern =
        Pattern.compile("^(\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2}.\\d{3})\\s+(\\d+)\\s+(\\d+)\\s+([VDIWEAvdiwea])\\s+(.*?):(\\s+.*?)$")

    /**
     * 装饰日志字符串
     *
     */
    fun decorate(log: CharSequence, flags: Int = FLAG_DEFAULT): CharSequence {
        val matcher = pattern.matcher(log)
        if (!matcher.find()) {
            return log
        }
        val builder = SpannableStringBuilder(log)

        var offset = 0
        val groupCount = matcher.groupCount()
        for (i in (1..groupCount)) {
            val group = matcher.group(i)
            if (group.isNull()) {
                continue
            }
            val start = matcher.start(i) - offset
            var end = matcher.end(i) - offset

            fun hasNext(): Boolean {
                return groupCount >= i + 1
            }

            when (i) {
                1 -> {// (MM-dd)
                    if (!hasFlag(flags, FLAG_DATE)) {
                        if (hasNext()) {
                            end = matcher.start(i + 1) - offset// remove ' '
                        }
                        offset += delete(builder, start, end)
                    }
                }
                2 -> {// (HH:mm:ss.SSS)
                    if (!hasFlag(flags, FLAG_TIME)) {
                        if (hasNext()) {
                            end = matcher.start(i + 1) - offset// remove ' '
                        }
                        offset += delete(builder, start, end)
                    }
                }
                3 -> {// (PID)
                    if (!hasFlag(flags, FLAG_PID)) {
                        if (hasNext()) {
                            end = matcher.start(i + 1) - offset// remove ' '
                        }
                        offset += delete(builder, start, end)
                    }
                }
                4 -> {// (TID)
                    if (!hasFlag(flags, FLAG_TID)) {
                        if (hasNext()) {
                            end = matcher.start(i + 1) - offset// remove ' '
                        }
                        offset += delete(builder, start, end)
                    }
                }
                5 -> {// (LEVEL)
                    if (!hasFlag(flags, FLAG_LEVEL)) {
                        if (hasNext()) {
                            end = matcher.start(i + 1) - offset// remove ' '
                        }
                        offset += delete(builder, start, end)
                    }
                    decorateLevel(builder, group!!, start, end)
                }
                6 -> {// (TAG)
                    if (!hasFlag(flags, FLAG_TAG)) {
                        if (hasNext()) {
                            end = matcher.start(i + 1) - offset// remove ' '
                        }
                        offset += delete(builder, start, end)
                    }
                }
                7 -> {// (LOG_MSG)
                    if (!hasFlag(flags, FLAG_CONTENT)) {
                        offset += delete(builder, start, end)
                    }
                }
            }
        }

        return builder.trim()
    }

    private fun delete(span: SpannableStringBuilder, start: Int, end: Int): Int {
        require(start <= end) { "delete error, start: $start, end: $end" }

        span.delete(start, end)
        return end - start
    }

    private fun hasFlag(flags: Int, flag: Int): Boolean {
        return flags and flag == flag
    }

    private fun decorateLevel(log: Spannable, level: String, start: Int, end: Int) {
        val color = when (level) {
            "D" -> 0xFF726FCD
            "V" -> 0xFFBBBBBB
            "I" -> 0xFF00B600
            "W" -> 0xFFDDD600
            "E" -> 0xFFFF4D4A
            "A" -> 0xFFFF4D4A
            else -> 0xFFFFFFFF
        }
        log.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        log.setSpan(
            ForegroundColorSpan(color.toInt()),
            0,
            log.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}