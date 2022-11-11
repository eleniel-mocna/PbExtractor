import GitExtractor.Companion.file
import java.io.File

class GitExtractor(private val directory: File) {
    companion object {
        val file = File("gitExtractor.diff")
    }

    fun getCommits(): Sequence<String> {
        val process = Runtime.getRuntime().exec("git log --all", null, directory)
        val processOutputReader = process.inputStream.bufferedReader()
        return processOutputReader.lineSequence()
            .filter { it.startsWith("commit") }
            .map { it.split(" ")[1] }
    }

    fun getDiff(commit: String): Sequence<String> {
        val process = Runtime.getRuntime().exec("git diff $commit~ $commit", null, directory)
        val processOutputReader = process.inputStream.bufferedReader()
        return processOutputReader.lineSequence()
    }

    fun saveProblemToFile(problem: Problem) {
        if (problem.examples.size > 1) {
            problem.examples.forEach {
                file.appendText("${it.cleanInput}\n")
                file.appendText("${it.cleanOutput}\n")
            }
            file.appendText("\n")
        }
    }

    fun saveProblems(diff: Sequence<String>) {
        var currentProblem = Problem()
        var currentInput: Sample? = null
        diff.forEach { line ->
            if (line.startsWith("- ")) {
                currentInput = Sample(line.substring(2))
            } else if (line.startsWith("+ ") && currentInput != null) {
                val output = Sample(line.substring(2))
                if (currentInput!!.isSimilar(output)) {
                    currentProblem.addExample(Example(currentInput!!, output))
                } else {
                    saveProblemToFile(currentProblem)
                    currentProblem = Problem()
                }
                currentInput = null

            }

        }
    }

    fun extractFromGitToFile() {
        file.delete()
        getCommits().forEach {
            saveProblems(getDiff(it))
        }
    }

}

fun main() {

    val gitExtractor = GitExtractor(File("../TypeScript"))
    gitExtractor.extractFromGitToFile()
}