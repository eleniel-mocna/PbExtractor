import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * Represents a PbE input/output text.
 *
 * The class provides functionality to calculate the Levenshtein distance
 * between two `Sample` objects and check if they are similar by comparing
 * the distance to a threshold.
 *
 * @param text The text of the sample.
 */
@Serializable
data class Sample(val text: String) {
    val splitText = splitString(text)
    val size = splitText.size

    /**
     * Calculates the levenshtein distance between this sample and another one using
     *
     * @param other The other Sample object to calculate the distance with.
     * @return The distance between the two Sample objects as a Double value.
     */
    fun distance(other: Sample): Double {
        return levenshteinDistance(splitText, other.splitText) / min(size, other.size).toDouble()
    }

    /**
     * Determines whether the current Sample object is similar enough to the provided Sample object.
     * This method is used in order to prune Samples that are too different from the current one.
     *
     * @param other The Sample object to compare with.
     * @return true if the more expensive steps should be performed on the Sample object to see if this meets PbE criteria.
     */
    fun isSimilar(other: Sample): Boolean {
        return distance(other) < Config.instance.maxSampleDistance
    }
}
