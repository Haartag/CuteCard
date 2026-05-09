package site.llinsoft.cutecard

import androidx.compose.runtime.Immutable

/**
 * Holds all data for a single flashcard.
 *
 * @param word The original word to learn.
 * @param translation The translated word or phrase.
 * @param phonetics Optional phonetic transcription e.g. "[ˈje.ro]".
 * Null hides the phonetics row entirely.
 * @param wordClass Optional grammatical class e.g. "noun", "verb".
 * Null hides the word class pill entirely.
 * @param audioUrl Optional URL for pronunciation audio.
 * Null hides the audio button entirely.
 * @param sourceLanguage Short language code for the word's language e.g. "EN", "RU".
 * Shown as a small pill in the top-left corner of the word face.
 * Null hides the pill entirely.
 * @param targetLanguage Short language code for the translation's language e.g. "ES", "SR".
 * Shown as a small pill in the top-left corner of the translation face.
 * Null hides the pill entirely.
 */
@Immutable
data class CuteCardContent(
    val word: String,
    val translation: String,
    val phonetics: String? = null,
    val wordClass: String? = null,
    val audioUrl: String? = null,
    val sourceLanguage: String? = null,
    val targetLanguage: String? = null
)
