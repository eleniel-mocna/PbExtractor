import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Represents a problem.
 *
 * @property examples List of examples for the problem.
 * @constructor Creates a new instance of Problem with the given examples.
 *
 * Should be created and then filled with examples by calling addExample.
 *
 * Then, when the problem is filled with examples, it can be serialized to JSON by calling toJson.
 */
@Serializable
data class Problem(val examples: MutableList<Example>) {
    constructor() : this(mutableListOf())

    private val synthesizableFromFirst = mutableListOf<Boolean>()
    private val distanceFromFirst = mutableListOf<Double>()

    /**
     * Adds an example to the list of examples.
     *
     * @param example The example to be added.
     */
    fun addExample(example: Example) {
        if (example.input.text.trim() == example.output.text.trim() || !example.isSelfSynthesizable()) {
            return
        }
        examples.add(example)
        synthesizableFromFirst.add(isSynthesizableFromFirst(example))
        distanceFromFirst.add(distanceFromFirst(example))
    }

    private fun distanceFromFirst(example: Example): Double {
        return if (examples.isNotEmpty()) {
            examples.first().distance(example)
        } else 0.0
    }

    private fun isSynthesizableFromFirst(other: Example): Boolean {
        return if (examples.isNotEmpty()) {
            other.isSynthesizable(examples.first())
        } else true
    }

    companion object {
        private val prettyJson = Json {
            prettyPrint = true
        }
    }

    fun toJson(): String {
        return prettyJson.encodeToString(this)
    }
}
