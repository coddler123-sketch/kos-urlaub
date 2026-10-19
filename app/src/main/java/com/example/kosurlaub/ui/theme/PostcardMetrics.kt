package com.example.kosurlaub.ui.theme

/** Reine Zahlenwerte des Postkarten-Designsystems; ohne Android-Abhängigkeit, damit sie unit-testbar sind. */
object PostcardMetrics {
    const val MIN_TEXT_SP = 11f
    const val MIN_PADDING_DP = 8f

    const val TITLE_SP = 26f
    const val HEADING_SP = 18f
    const val QUOTE_SP = 14f
    const val BODY_SP = 14f
    const val LABEL_SP = 11f
    const val NUMERAL_SP = 28f

    const val PANEL_PADDING_DP = 12f
    const val CHIP_H_PADDING_DP = 8f
    const val CHIP_V_PADDING_DP = 8f
    const val PHOTO_TEXT_PADDING_DP = 10f

    val textSizesSp = mapOf(
        "Title" to TITLE_SP, "Heading" to HEADING_SP, "Quote" to QUOTE_SP,
        "Body" to BODY_SP, "Label" to LABEL_SP, "Numeral" to NUMERAL_SP
    )

    val innerPaddingsDp = mapOf(
        "Panel" to PANEL_PADDING_DP, "ChipH" to CHIP_H_PADDING_DP,
        "ChipV" to CHIP_V_PADDING_DP, "PhotoText" to PHOTO_TEXT_PADDING_DP
    )
}
