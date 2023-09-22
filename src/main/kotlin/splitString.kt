/**
 * Splits the given input string based on various delimiters and returns a list of strings.
 *
 * This splits the input string based on the following delimiters:
 * - whitespace
 * - punctuation
 * - capital letters
 * - special characters
 *
 * So this splits tokens by whitespaces as expected, but also splits tokens like "HelloWorld" into "Hello" and "World".
 * This is done to make the program synthesis algorithm more robust to camelCase and snake_case.
 *
 *
 * @param inputString the string to be split
 * @return a list of strings after splitting the input string
 */
fun splitString(inputString: String): List<String> {
    return inputString
        .split("([_.,;~ˇ^˘°˛`˙˝¨\"\'\\s])|(?=\\p{Lu})|(?=\\p{Punct})|(?<=\\p{Punct})".toRegex())
        .filter { it.isNotBlank() }
        .map { it.lowercase() }
}
