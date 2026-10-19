package com.example.kosurlaub

import com.example.kosurlaub.ui.theme.PostcardMetrics
import org.junit.Assert.assertTrue
import org.junit.Test

class PostcardMetricsTest {

    @Test
    fun noTextStyleIsBelowMinimum() {
        PostcardMetrics.textSizesSp.forEach { (name, size) ->
            assertTrue("$name: $size sp < ${PostcardMetrics.MIN_TEXT_SP} sp", size >= PostcardMetrics.MIN_TEXT_SP)
        }
    }

    @Test
    fun noInnerPaddingIsBelowMinimum() {
        PostcardMetrics.innerPaddingsDp.forEach { (name, size) ->
            assertTrue("$name: $size dp < ${PostcardMetrics.MIN_PADDING_DP} dp", size >= PostcardMetrics.MIN_PADDING_DP)
        }
    }
}
