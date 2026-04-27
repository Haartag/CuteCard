package com.llinsoft.demo

import com.llinsoft.cutecard.api.CuteCardContent

object SampleDeck {
    val cards = listOf(
        CuteCardContent(
            word = "water",
            translation = "agua",
            phonetics = "[ˈa.ɣwa]",
            wordClass = "noun"
        ),
        CuteCardContent(
            word = "to run",
            translation = "correr",
            phonetics = "[ko.ˈrer]",
            wordClass = "verb"
        ),
        CuteCardContent(
            word = "beautiful",
            translation = "bonito",
            phonetics = "[bo.ˈni.to]",
            wordClass = "adjective"
        ),
        CuteCardContent(
            word = "always",
            translation = "siempre",
            phonetics = "[ˈsjem.pre]",
            wordClass = "adverb"
        ),
        CuteCardContent(
            word = "iron",
            translation = "hierro",
            phonetics = "[ˈje.ro]",
            wordClass = "noun"
        ),
        CuteCardContent(
            word = "light",
            translation = "luz",
            phonetics = "[luθ]",
            wordClass = "noun"
        )
    )
}
