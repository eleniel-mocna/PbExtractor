// get levenstein distance between two strings
fun <T> levenshteinDistance(s1: List<T>, s2: List<T>): Int {
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
    return dp[len1][len2]
}