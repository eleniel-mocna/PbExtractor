import kotlin.math.min

data class Example(val input: Sample, val output: Sample) {
    val size = min(input.size, output.size)
    val innerDistance = input.distance(output) / size
    fun distance(other: Example): Double {
        return (input.distance(other.input) + output.distance(other.output)) / (size + other.size)
    }

    fun isSimilar(other: Example): Boolean {
        return distance(other) < 0.5
    }

    val cleanInput = "- ${input.text.trim()}"
    val cleanOutput = "+ ${output.text.trim()}"
}