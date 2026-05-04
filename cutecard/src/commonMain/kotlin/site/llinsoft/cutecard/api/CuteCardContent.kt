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
 */
@Immutable
data class CuteCardContent(
    val word: String,
    val translation: String,
    val phonetics: String? = null,
    val wordClass: String? = null,
    val audioUrl: String? = null
)
