import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SplitStringKtTest {
    @Test
    fun simple() {
        assertEquals(
            splitString("thisIsASample = get_someSample(a:b,d:x)"),
            listOf("this", "Is", "A", "Sample", "=", "get", "some", "Sample", "(", "a", ":", "b", "d", ":", "x", ")")
        )
    }

    @Test
    fun arithmetic() {
        assertEquals(
            splitString("a /=1+2 *(3/ 4) -5"),
            listOf("a", "/", "=", "1", "+", "2", "*", "(", "3", "/", "4", ")", "-", "5")
        )
    }



}