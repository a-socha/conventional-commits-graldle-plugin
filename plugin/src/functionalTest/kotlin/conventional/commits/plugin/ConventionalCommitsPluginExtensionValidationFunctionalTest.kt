package conventional.commits.plugin

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ConventionalCommitsPluginExtensionValidationFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

    @Test
    fun `should print validation error if some type occurs more than one`() {
        // given
        registerPluginWithExtension(
            """
            conventionalCommitsConfig {
                major = ["docs"]
                minor = ["fix", "docs"]
                patch = ["custom"]
                none = ["feat"]
            }
            """.trimIndent()
        )

        // when
        val results = startGradle()

        // then
        assertThat(results.output).contains("Following types occur more than one in configuration: docs")
    }

    @Test
    fun `should print validation error if some type has other characters than letters`() {
        // given
        registerPluginWithExtension(
            """
            conventionalCommitsConfig {
                major = ["docs"]
                minor = ["fix!"]
                patch = ["custom1"]
                none = ["feat@"]
            }
            """.trimIndent()
        )

        // when
        val results = startGradle()

        // then
        assertThat(results.output).contains("Following types contains other character than letters: fix!, custom1, feat@")
    }

    fun registerPluginWithExtension(extension: String) {
        settingsFile.writeText("")
        buildFile.writeText(
            """
                plugins {
                    id('conventional.commits')
                }
                
                $extension
            """.trimIndent()
        )
    }

    private fun startGradle(): BuildResult {
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withProjectDir(projectDir)
        return runner.build()
    }
}