import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ExampleTest{

    @Test
    fun zeroDistanceTest(){
        val example1 = Example(Sample("isVariableValid=true"), Sample("isThisVariableValid=true"))
        assertEquals(0.0, example1.distance(example1))
    }
    @Test
    fun simpleSimilarityTest(){
        val example1 = Example(Sample("isVariableValid=true"), Sample("isThisVariableValid=true"))
        val example2 = Example(Sample("isSomethingValid=true"), Sample("isThisSomethingValid=true"))
        assertTrue(example1.isSimilar(example2))
    }
    @Test
    fun longSimilarityTest(){
        val example1 = Example(Sample("isVariableValid = true"), Sample("isCompletelyDifferentThing(thing) = thing.isVariableValid"))
        val example2 = Example(Sample("isVariableValid = true"), Sample("isCompletelyDifferentThing(thing) = thing.isVariableValid"))
        assertTrue(example1.isSimilar(example2))
    }
    @Test
    fun shortSimilarityTest(){
        val example1 = Example(Sample("isValid"), Sample("isThisValid"))
        val example2 = Example(Sample("isVariable"), Sample("isThisVariable"))
        assertTrue(example1.isSimilar(example2))
    }
    @Test
    fun shortChangeTest(){
        val example1 = Example(Sample("isValid"), Sample("isntValid"))
        val example2 = Example(Sample("isVariable"), Sample("isntVariable"))
        assertTrue(example1.isSimilar(example2))
    }
    @Test
    fun UnsimilarTest(){
        val example1 = Example(Sample("isValid"), Sample("isntValid"))
        val example2 = Example(Sample("SomethingAbsolutelyDifferent"), Sample("SomethingAbsolutelyDifferent+10"))
        assertFalse(example1.isSimilar(example2))
    }
    @Test
    fun UnsimilarChangeTest(){
        val example1 = Example(Sample("isValid"), Sample("isntValid"))
        val example2 = Example(Sample("isSimilar"), Sample("isSimilar+10"))
        assertFalse(example1.isSimilar(example2))
    }
}