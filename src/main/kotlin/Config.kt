import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.io.File

@Serializable
class Config(
    val maxEditSize: Int,
    val maxExampleDistance: Double,
    val maxSampleDistance: Double,
    val maxProgramDistance: Double
) {
    companion object {
        val instance = loadConfig()
        private fun loadConfig(): Config {
            val configFile = File("src/main/resources/config.json")
            return decodeFromString(configFile.readText())
        }
    }
}