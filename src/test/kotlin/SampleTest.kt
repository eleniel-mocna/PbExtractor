import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SampleTest{

    @Test
    fun zeroDistanceTest(){
        val sample1 = Sample("isVariableValid=true")
        assertEquals(0.0, sample1.distance(sample1))
    }
    @Test
    fun simpleSimilarityTest(){
        val sample1 = Sample("isVariableValid=true")
        val sample2 = Sample("isThisVariableValid=true")
        assertTrue(sample1.isSimilar(sample2))
    }
    @Test
    fun simpleUnsimilarityTest(){
        val sample1 = Sample("isVariableValid = true")
        val sample2 = Sample("isCompletelyDifferentThing(thing) = thing.isVariableValid")
        assertFalse(sample1.isSimilar(sample2))
    }
    @Test
    fun simpleAdditionTest(){
        val sample1 = Sample("isVariableValid=true")
        val sample2 = Sample("isVariableValid=true + false")
        assertTrue(sample1.isSimilar(sample2))
    }
    @Test
    fun simpleDeletionTest(){
        val sample1 = Sample("isVariableValid=true + false")
        val sample2 = Sample("isVariableValid=true")
        assertTrue(sample1.isSimilar(sample2))
    }
}