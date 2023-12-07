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
            subject: String,
            body: String? = null
    ) {
        val message = body?.let { "$subject\n\n$it" } ?: subject
        runBashCommand("git", "commit", "-m", message, "--allow-empty")
    }

    protected fun gitTag(tagName: String) {
        runBashCommand("git", "tag", tagName)
    }

    protected fun listGitTag(): List<String> {
        return runBashCommand("git", "tag")
    }

    protected fun hashOfTaggedCommit(tag: String): String =
            runBashCommand("git", "rev-list", "-n", "1", tag).singleOrNull() ?: ""

    protected fun runGradleTask(vararg arguments: String): BuildResult {
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments(*arguments, "--stacktrace")
        runner.withProjectDir(projectDir)
        return runner.build()
    }

    private fun runBashCommand(vararg command: String): List<String> {
        val output = ProcessBuilder(command.toList())
                .directory(projectDir)
                .start().inputStream.reader().readLines()
        println(output.joinToString("\n"))
        return output
    }


}