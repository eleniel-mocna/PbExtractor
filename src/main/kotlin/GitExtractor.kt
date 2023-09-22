import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * A class that extracts diffs from a Git repository and saves them to a file.
 *
 * This is the main class for running the extraction.
 *
 * @property directory The directory of the Git repository.
 * @property resultFile The file to save the extracted problems.
 */
class GitExtractor(private val directory: File, private val resultFile: File) {

    /**
     * Extracts problems from Git commits and saves them to a file.
     *
     * This method prepares a result file, retrieves the list of commits from Git,
     * and then iterates over the commits in parallel. For each commit, it retrieves
     * the diff and saves the problems to the result file. Finally, it finishes the
     * result file.
     */
    fun extractFromGitToFile(limit: Int = Int.MAX_VALUE) {
        prepareResultFile()
        val commitList = getCommits().take(limit).toList()
        val progress = AtomicInteger()
        commitList.parallelStream().forEach {
            val progressCount = progress.incrementAndGet()
            if (progressCount % 100 == 0) {
                println("$progressCount / ${commitList.size}")
            }
            saveProblems(getDiff(it))
        }
        finishResultFile()
    }

    private fun getCommits(): Sequence<String> {
        val process = Runtime.getRuntime().exec("git log --all", null, directory)
        val processOutputReader = process.inputStream.bufferedReader()
        return processOutputReader.lineSequence()
            .filter { it.startsWith("commit") }
            .map { it.split(" ")[1] }
    }

    private fun getDiff(commit: String): Sequence<String> {
        val process = Runtime.getRuntime().exec("git diff $commit~ $commit", null, directory)
        val processOutputReader = process.inputStream.bufferedReader()
        return processOutputReader.lineSequence()
    }

    private val outputFileMutex: Mutex = Mutex()

    private suspend fun saveProblemToFile(problem: Problem) {
        outputFileMutex.lock()
        if (problem.examples.size > 1) {
            this.resultFile.appendText(problem.toJson() + ",\n")
        }
        outputFileMutex.unlock()
    }

    private fun saveProblems(diff: Sequence<String>) {
        var currentProblem = Problem()
        var currentInput: Sample? = null
        diff.forEach { line ->
            if (line.startsWith("- ")) {
                currentInput = Sample(line.substring(2).trim())
            } else if (line.startsWith("+ ") && currentInput != null) {
                val output = Sample(line.substring(2).trim())
                if (currentInput!!.isSimilar(output)) {
                    currentProblem.addExample(Example(currentInput!!, output))
                } else {
                    runBlocking {
                        saveProblemToFile(currentProblem)
                    }
                    currentProblem = Problem()
                }
                currentInput = null
            }
        }
    }

    private fun prepareResultFile() {
        this.resultFile.writeText("[\n")
    }


    private fun finishResultFile() {
        this.resultFile.appendText("{}\n]")
    }

}

fun main(args: Array<String>) {
    val repoPath: String
    val outputFile: String
    val limit: Int = if (args.size==3) args[2].toInt() else 0
    if (args.size >= 2) {
        repoPath = args[0]
        outputFile = args[1]
    } else {
        repoPath = "."
        outputFile = "problems.json"
    }
    println("Extracting from $repoPath to $outputFile (limit: $limit commits)")
    val gitExtractor = GitExtractor(File(repoPath), File(outputFile))
    gitExtractor.extractFromGitToFile(limit)
}