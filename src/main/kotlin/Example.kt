import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * This class represents an PbE example - an input and output string.
 *
 * @property input The input sample.
 * @property output The output sample.
 * @property edits The string representation of the edits made to the input to transform it into the output
 *      needed for serialization
 * @property explanation The last found explanation
 * @property size The size of the example, which is the minimum size between the input and output.
 * @property innerDistance Levenshtein distance between the input and output text
 */
@Serializable
data class Example(
    val input: Sample, val output: Sample,
) {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    val edits: String = getEdits(input.splitText, output.splitText).joinToString(separator = ",") { it.toString() }
    private var explanation: WholeExplanation? = null
    private val size = min(input.size, output.size)
    private val innerDistance = input.distance(output) / size

    /**
     * Calculates a distance similar to levenshtein distance between 2 examples
     *
     * @param other The other Example object to calculate the distance with.
     * @return The distance between the two Example objects as a Double value.
     */
    fun distance(other: Example): Double {
        return (input.distance(other.input) + output.distance(other.output)) / (size + other.size)
    }

    /**
     * Determines whether the current Example object is similar enough to the provided Example object to be classified
     * as a PbE example.
     *
     * @param other The Example object to compare with.
     * @return true if the more expensive steps should be performed on the Example object to see if this meets PbE criteria.
     */
    fun isSimilar(other: Example): Boolean {
        return distance(other) < 0.5
    }

    /**
     * Returns whether the object is synthesizable from itself.
     *
     * This method is used to generate an explanation for each Example object and to prune Examples that the simple
     * program synthesis algorithm cannot synthesize (e.g. when more tokens influence each other).
     *
     * @return true if the object is self synthesizable, false otherwise.
     */
    fun isSelfSynthesizable(): Boolean {
        return isSynthesizable(this)
    }

    /**
     * Determines whether the current Example object is synthesizable from the provided Example object.
     *
     * @param other The Example object to compare with.
     * @return true if the current Example object can be synthesized from the provided Example object.
     */
    fun isSynthesizable(other: Example): Boolean {
        val explanations = getExplanations(input.splitText, output.splitText)
        return explanations.firstOrNull { areListsEqual(it.apply(other.input.splitText), other.output.splitText) }
            ?.let {
                explanation = it
                true
            } ?: false
    }

    private fun areListsEqual(first: List<String>?, second: List<String>?): Boolean {
        first ?: return false
        second ?: return false
        if (first.size != second.size) {
            return false
        }
        return first.mapIndexed { index, s -> s == second[index] }.all { it }
    }
}