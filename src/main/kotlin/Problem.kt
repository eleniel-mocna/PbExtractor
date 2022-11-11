data class Problem(val examples: MutableList<Example>) {
    constructor() : this(mutableListOf())

    fun addExample(example: Example) {
        examples.add(example)
    }

    fun isSimilar(other: Example): Boolean {
        return if (examples.isNotEmpty()) {
            examples.first().isSimilar(other)
        } else true

    }
}
