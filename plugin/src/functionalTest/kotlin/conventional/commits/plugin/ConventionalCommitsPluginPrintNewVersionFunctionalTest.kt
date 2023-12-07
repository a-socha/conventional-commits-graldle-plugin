package conventional.commits.plugin

import conventional.commits.PluginFunctionalTestWithGitSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ConventionalCommitsPluginPrintNewVersionFunctionalTest : PluginFunctionalTestWithGitSupport() {
    private val v0_0_0 = "v0.0.0"

    @Test
    fun `should print the same version if version do not need to change`() {
        // given
        gitCommit("chore: Initial commit")
        gitTag(v0_0_0)
        gitCommit("docs: Nothing special, should not upgrade")

        // when
        val result = runGradleTask("printNewVersion")

        // then
        assertThat(result.output).contains("v0.0.0")
    }

    @ParameterizedTest
    @CsvSource(value = [
        "feat   ,   v0.1.0",
        "feat!  ,   v1.0.0",
        "fix    ,   v0.0.1"
    ])
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

    override val buildFileContent: String
        get() = """
            plugins {
                id('conventional.commits')
            }
        """.trimIndent()
}