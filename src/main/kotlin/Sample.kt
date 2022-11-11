import kotlin.math.max
import kotlin.math.min

data class Sample(val text: String) {
    val splitText = splitString(text)
    val size = splitText.size
    fun distance(other: Sample): Double {
        return levenshteinDistance(splitText, other.splitText)/min(size, other.size).toDouble()
    }
    fun isSimilar(other: Sample): Boolean {
        return distance(other) < 0.5
    }
}