package conventional.commits.plugin

import conventional.commits.PluginFunctionalTestWithGitSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ConventionalCommitsPluginNewVersionWithExtensionFunctionalTest : PluginFunctionalTestWithGitSupport() {
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
    fun `should not create new tag if version is not upgraded and there is a version tag in repo`() {
        // given
        gitCommit("feat: Initial commit")
        gitTag(v0_0_0)
        gitCommit("feat: Nothing special, should not upgrade")
        val hashOfTaggedCommit = hashOfTaggedCommit(v0_0_0)

        // when
        val result = runGradleTask("newVersion")

        // then
        assertThat(result.output).contains("v0.0.0")
        assertThat(hashOfTaggedCommit(v0_0_0)).isEqualTo(hashOfTaggedCommit)
    }

    @Test
    fun `should create new tag if version is not upgraded but there is no version tag in repo`() {
        // given
        gitCommit("feat: Initial commit")
        gitCommit("feat: Nothing special, should not upgrade")

        // when
        val result = runGradleTask("newVersion", "--no-push")

        // then
        assertThat(result.output).contains(v0_0_0)
        assertThat(listGitTag()).contains(v0_0_0)
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
    fun `should create new version in repo depends on commits`(
        type: String, expectedTag: String
    ) {
        // given
        gitCommit("chore: Initial commit")
        gitTag(v0_0_0)
        gitCommit("$type: Some message")

        // when
        val result = runGradleTask("newVersion", "--no-push")

        // then
        assertThat(result.output).contains(expectedTag)
        assertThat(listGitTag()).contains(expectedTag)
    }

}