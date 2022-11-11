fun splitString(inputString: String): List<String> {
    return inputString
        .split("([_.,;~ˇ^˘°˛`˙˝¨\"\'\\s])|(?=\\p{Lu})|(?=\\p{Punct})|(?<=\\p{Punct})".toRegex())
        .filter { it.isNotBlank() }
}
