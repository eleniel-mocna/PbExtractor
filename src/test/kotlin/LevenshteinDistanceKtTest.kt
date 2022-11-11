import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LevenshteinDistanceKtTest {
    @Test
    fun zeroDistance() {
        assertEquals(0, levenshteinDistance(listOf("abc"), listOf("abc")))
    }

    @Test
    fun simpleInsert() {
        assertEquals(1, levenshteinDistance(listOf("a"), listOf("a", "b")))
    }

    @Test
    fun simpleDelete() {
        assertEquals(1, levenshteinDistance(listOf("a", "b"), listOf("a")))
    }

    @Test
    fun simpleReplace() {
        assertEquals(1, levenshteinDistance(listOf("a"), listOf("b")))
    }

    @Test
    fun simpleReplace2() {
        assertEquals(1, levenshteinDistance(listOf("a", "b"), listOf("a", "c")))
    }

    @Test
    fun simpleReplace3() {
        assertEquals(1, levenshteinDistance(listOf("a", "b"), listOf("c", "b")))
    }

    @Test
    fun simpleReplace4() {
        assertEquals(2, levenshteinDistance(listOf("a", "b"), listOf("c", "d")))
    }

    @Test
    fun simpleReplace5() {
        assertEquals(3, levenshteinDistance(listOf("a", "b", "c"), listOf("c", "d", "e")))
    }
}