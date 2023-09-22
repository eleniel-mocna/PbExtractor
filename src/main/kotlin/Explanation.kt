import kotlinx.serialization.Serializable

/**
 * The Condition interface represents a condition that can be checked against an example (list of strings).
 *
 * Conditions implementing this interface must provide an implementation for the check function,
 * which takes a List of strings and an index as parameters, and returns a Boolean indicating
 * whether the condition is met.
 *
 * Condition is always checked against the original list of tokens.
 */
@Serializable
sealed interface Condition {
    fun check(input: List<String>, index: Int): Boolean
}

/**
 * Check if we are on the given index.
 */
@Serializable
class OnIndex(private val index: Int) : Condition {
    override fun check(input: List<String>, index: Int): Boolean {
        return this.index == index
    }

    override fun toString(): String {
        return "@($index)"
    }
}

/**
 * Check if the previous token is the given token.
 */
@Serializable
class PreviousToken(private val token: String) : Condition {
    override fun check(input: List<String>, index: Int): Boolean {
        return index > 0 && input[index - 1] == token
    }

    override fun toString(): String {
        return "P($token)"
    }
}

/**
 * Check if the next token is the given token.
 */
@Serializable
class NextToken(private val token: String) : Condition {
    override fun check(input: List<String>, index: Int): Boolean {
        return index < input.size - 1 && input[index + 1] == token
    }

    override fun toString(): String {
        return "N($token)"
    }
}

/**
 * Check if the current token is the given token.
 */
@Serializable
class ThisToken(private val token: String) : Condition {
    override fun check(input: List<String>, index: Int): Boolean {
        return input[index] == token
    }

    override fun toString(): String {
        return "T($token)"
    }
}

/**
 * Represents an explanation object that contains a condition and an edit.
 * Provides a method to apply the edit to a list of input strings.
 *
 * @property condition The condition to check against the input strings.
 * @property edit The edit to apply to the input strings.
 */
@Serializable
class Explanation(private val condition: Condition, private val edit: TextEdit) {
    fun apply(input: List<String>): List<String>? {
        for (i in input.indices) {
            if (condition.check(input, i)) {
                return edit.apply(input, i)
            }
        }
        return null
    }

    override fun toString(): String {
        return "$condition => \"$edit\""
    }
}

/**
 * This class represents a whole explanation which consists of a list of simple explanations.
 *
 * This class is mainly just a wrapper around List<Explanation> that provides a method to apply the whole explanation
 * to generate the predicted output for a given second input in the apply method.
 *
 * @property explanations The list of explanations.
 */
@Serializable
class WholeExplanation(val explanations: List<Explanation>) {
    /**
     * Applies a list of explanations to the given second input and returns the result.
     *
     * @param secondInput The second input to apply the explanations to.
     * @return The result after applying all explanations, or null if any explanation returns null.
     */
    fun apply(secondInput: List<String>): List<String>? {
        var result = secondInput
        for (explanation in explanations) {
            result = explanation.apply(result) ?: return null
        }
        return result
    }

    override fun toString(): String {
        return explanations.joinToString("\n")
    }
}

/**
 * Returns a sequence of whole explanations for transforming the input list of strings to the output list of strings.
 *
 * @param input The input list of strings.
 * @param output The output list of strings.
 * @return A sequence of whole explanations for transforming the input list of strings to the output list of strings.
 */
fun getExplanations(input: List<String>, output: List<String>): Sequence<WholeExplanation> {
    val edits = getEdits(input, output)
    if (edits.size > 10) {
        return emptySequence()
    }
    return getExplanationsRecursive(input, edits)

}

private fun getExplanationsRecursive(input: List<String>, edits: List<TextEdit>): Sequence<WholeExplanation> {
    val lastEdit = edits.lastOrNull() ?: return sequenceOf(WholeExplanation(emptyList()))
    val explanations = mutableListOf<Explanation>()
    explanations.add(Explanation(OnIndex(lastEdit.index), lastEdit))
    if (lastEdit !is Insert) {
        explanations.add(Explanation(ThisToken(input[lastEdit.index]), lastEdit))
    }
    if (lastEdit.index > 0) {
        explanations.add(Explanation(PreviousToken(input[lastEdit.index - 1]), lastEdit))
    }
    if (lastEdit.index < input.size - 1) {
        explanations.add(Explanation(NextToken(input[lastEdit.index + 1]), lastEdit))
    }
    val remainingExplanations = getExplanationsRecursive(input, edits.subList(0, edits.size - 1))
    return remainingExplanations.flatMap { remainingExplanation ->
        explanations.map { explanation ->
            WholeExplanation(remainingExplanation.explanations + explanation)
        }
    }
}


