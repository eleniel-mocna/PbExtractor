import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Problem(val examples: MutableList<Example>) {
    constructor() : this(mutableListOf())

    private val synthesizableFromFirst = mutableListOf<Boolean>()
    private val distanceFromFirst = mutableListOf<Double>()

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
            prettyPrint = true;
        }
    }

    fun toJson(): String {
        return prettyJson.encodeToString(this)
    }
}
