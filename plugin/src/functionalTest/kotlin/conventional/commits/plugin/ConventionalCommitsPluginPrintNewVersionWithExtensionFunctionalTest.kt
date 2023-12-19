package conventional.commits.plugin

import conventional.commits.PluginFunctionalTestWithGitSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ConventionalCommitsPluginPrintNewVersionWithExtensionFunctionalTest : PluginFunctionalTestWithGitSupport() {
    private val v0_0_0 = "v0.0.0"

    override val buildFileContent: String
        get() = """
            plugins {
                id('io.github.asocha.conventional.commits')
            }
            
            conventionalCommitsConfig {
                major = ["docs"]
                minor = ["fix"]
                patch = ["custom"]
                none = ["feat"]
            }
        """.trimIndent()

    @Test
    fun `should print the same version if version do not need to change`() {
        // given
        gitCommit("feat: Initial commit")
        gitTag(v0_0_0)
        gitCommit("feat: Nothing special, should not upgrade")

        // when
        val result = runGradleTask("printNewVersion")

        // then
        assertThat(result.output).contains("v0.0.0")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "docs   ,   v1.0.0",
            "fix   ,   v0.1.0",
            "feat!  ,   v1.0.0",
            "custom    ,   v0.0.1"
        ]
    )
    fun `should print new version depends on commits`(
        type: String, expectedTag: String
    ) {
        // given
        gitCommit("chore: Initial commit")
        gitTag(v0_0_0)
        gitCommit("$type: Some message")

        // when
        val result = runGradleTask("printNewVersion")

        // then
        assertThat(result.output).contains(expectedTag)
        assertThat(listGitTag()).doesNotContain(expectedTag)
    }
}