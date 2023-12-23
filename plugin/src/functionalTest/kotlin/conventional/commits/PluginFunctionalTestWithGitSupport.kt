package conventional.commits

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File

abstract class PluginFunctionalTestWithGitSupport {

    abstract val buildFileContent: String

    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }


    @BeforeEach
    fun initTest() {
        gitInit()
        registerPlugin()
    }

    private fun gitInit() {
        runBashCommand("git", "init")
    }

    private fun registerPlugin() {
        settingsFile.writeText("")
        buildFile.writeText(buildFileContent)
    }

    protected fun gitCommit(
        message: String
    ): ProcessResult {
        return runBashCommand("git", "commit", "-m", message, "--allow-empty")
    }

    protected fun gitTag(tagName: String) {
        runBashCommand("git", "tag", tagName)
    }

    protected fun listGitTag(): List<String> {
        return runBashCommand("git", "tag").successOutput
    }

    protected fun hashOfTaggedCommit(tag: String): String =
        runBashCommand("git", "rev-list", "-n", "1", tag).successOutput.singleOrNull() ?: ""

    protected fun runGradleTask(vararg arguments: String): BuildResult {
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments(*arguments, "--stacktrace")
        runner.withProjectDir(projectDir)
        return runner.build()
    }

    protected fun startGradle(): BuildResult {
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withProjectDir(projectDir)
        return runner.build()
    }

    private fun runBashCommand(vararg command: String): ProcessResult {
        val result = ProcessBuilder(command.toList())
            .directory(projectDir)
            .start().result()
        println(result.successOutput)
        return result
    }

}

@Suppress("ControlFlowWithEmptyBody")
fun Process.result(): ProcessResult = let {
    while (isAlive) {
    }
    ProcessResult(
        status = it.exitValue(),
        successOutput = it.inputStream.reader().readLines(),
        errorOutput = it.errorStream.reader().readLines()
    )
}

data class ProcessResult(
    val status: Int,
    val successOutput: List<String>,
    val errorOutput: List<String>
)
