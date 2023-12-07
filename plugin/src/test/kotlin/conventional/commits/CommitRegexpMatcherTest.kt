package conventional.commits

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class CommitRegexpMatcherTest {
    private val commitRegexpMatcher = CommitRegexpMatcher()

    @ParameterizedTest
    @MethodSource("commits")
    fun `should parse commit in format hash subject body`(
            commitAsString: String, expectedCommit: Commit
    ) {
        // when
        val result = commitRegexpMatcher.fromCommitString(commitAsString)

        // then
        assertThat(result).isEqualTo(expectedCommit)
    }

    companion object {
        @JvmStatic
        fun commits(): Stream<Arguments> = Stream.of(
                arguments(
                        "[385bf5e] [build: some short summary] []",
                        Commit(
                                hash = "385bf5e",
                                summary = "build: some short summary",
                                body = ""
                        )
                ),
                arguments(
                        """[7d8ff59] [docs: commit summary] [long commit message
                           |that cannot fits in single line
                           |]""".trimMargin(),
                        Commit(
                                hash = "7d8ff59",
                                summary = "docs: commit summary",
                                body = """long commit message
                                        |that cannot fits in single line
                                        |""".trimMargin()
                        )
                ),
                arguments(
                        """[7d8ff59] [docs(scope): summary with scope] [long message, with commas (and other sings);
                           |reviewed-by: anonymous@developer.com
                           |]""".trimMargin(),
                        Commit(
                                hash = "7d8ff59",
                                summary = "docs(scope): summary with scope",
                                body = """long message, with commas (and other sings);
                                          |reviewed-by: anonymous@developer.com
                                          |""".trimMargin()
                        )
                )
        )
    }
}