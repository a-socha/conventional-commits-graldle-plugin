package conventional.commits.plugin

import conventional.commits.PluginFunctionalTestWithGitSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ConventionalCommitsPluginValidationHookFunctionalTest : PluginFunctionalTestWithGitSupport() {

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

    @ParameterizedTest
    @ValueSource(
        strings = [
            "test: I would be correct, but I'm not in config list",
            "!docs!: too major",
            "feat!(almost): I am almost conventional",
            "I'm not conventional at all",
            "feat(almost)! : so close",
            "feat(almost)!:no matter how far",
            "(first)scope: than message",
            "!ci(order)!!: cannot be conventional because too breaking"
        ]
    )
    fun `should not be able to commit messages that does not meet convention`(message: String) {
        // given
        startGradle()

        // when
        val result = gitCommit(message)

        assertThat(result.status).isNotEqualTo(0)
        assertThat(result.errorOutput).isEqualTo(
            listOf(
                "The commit message is not compliant with Conventional Commit standard",
                "See: https://www.conventionalcommits.org/en/v1.0.0/#summary"
            )
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "docs: correct without scope and major",
            "docs!: correct without scope",
            "fix(scope): correct with scope but without major",
            "fix(scope)!: correct with scope",
            "docs: I'm not conventional\nBecause there is on line break after subject",
            "docs: Multi line are ok\n\neven if a lot of spaces\n\nI'm still here",
        ]
    )
    fun `should be able to commit messages that meet convention`(message: String) {
        // given
        startGradle()

        // when
        val result = gitCommit(message)

        assertThat(result.status).isEqualTo(0)
    }

}