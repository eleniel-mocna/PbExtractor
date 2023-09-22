import kotlinx.serialization.Serializable

@Serializable
sealed interface TextEdit {
    val index: Int
    fun apply(input: List<String>, index:Int): List<String>
}

@Serializable
data class Insert(override val index: Int, val text: List<String>) : TextEdit {
    override fun toString(): String {
        return "I($text @ $index)"
    }
    override fun apply(input: List<String>, index: Int): List<String> {
        return input.subList(0, index) + text + input.subList(index, input.size)
    }

}

@Serializable
data class Delete(override val index: Int, val text: List<String>) : TextEdit {
    override fun toString(): String {
        return "D($text @ $index)"
    }
    override fun apply(input: List<String>, index: Int): List<String> {
        return input.subList(0, index) + input.subList(index + text.size, input.size)
    }

}

@Serializable
data class Replace(override val index: Int, val oldText: List<String>, val newText: List<String>) : TextEdit {
    override fun toString(): String {
        return "R($oldText -> $newText @ $index)"
    }
    override fun apply(input: List<String>, index: Int): List<String> {
        return input.subList(0, index) + newText + input.subList(index + oldText.size, input.size)
    }

}

/**
 * Retrieves the list of edits required to transform one list of strings into another.
 *
 * Here edits are understood in the levenshtein distance sense, i.e. insertions, deletions and replacements.
 *
 * @param s1 The original list of strings.
 * @param s2 The target list of strings.
 * @return The list of edits, in the order they need to be applied, to transform s1 into s2.
 */
fun getEdits(s1: List<String>, s2: List<String>): List<TextEdit> {
    val len1 = s1.size
    val len2 = s2.size
    val dp = Array(len1 + 1) { IntArray(len2 + 1) }
    for (i in 0..len1) {
        dp[i][0] = i
    }
    for (j in 0..len2) {
        dp[0][j] = j
    }
    for (i in 1..len1) {
        for (j in 1..len2) {
            val left = dp[i - 1][j] + 1
            val down = dp[i][j - 1] + 1
            val leftDown = dp[i - 1][j - 1]
            dp[i][j] = minOf(left, down, leftDown + if (s1[i - 1] == s2[j - 1]) 0 else 1)
        }
    }
    val edits = mutableListOf<TextEdit>()
    var i = len1
    var j = len2
    while (i > 0 || j > 0) {
        if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
            edits.add(Delete(i - 1, listOf(s1[i - 1])))
            i--
        } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
            edits.add(Insert(i, listOf(s2[j - 1])))
            j--
        } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
            edits.add(Replace(i - 1, listOf(s1[i - 1]), listOf(s2[j - 1])))
            i--
            j--
        } else {
            i--
            j--
        }
    }
    return edits.reversed()

}

fun levenshteinDistance(s1: List<String>, s2: List<String>): Int {
    return getEdits(s1, s2).size
}