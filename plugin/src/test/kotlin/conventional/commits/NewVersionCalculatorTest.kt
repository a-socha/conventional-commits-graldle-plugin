package conventional.commits

import conventional.commits.VersionIncrement.*
import conventional.commits.plugin.ConventionalCommitConfigExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class NewVersionCalculatorTest {

    private val newVersionCalculator = NewVersionCalculator(
            ConventionalCommitConfigExtension().toConfig()
    )

    @ParameterizedTest
    @MethodSource("commitsToExpectedIncrement")
    fun `should return MAJOR if one commit is with breaking change`(
            commits: List<Commit>, expectedIncrement: VersionIncrement
    ) {
        // when
        val increment = newVersionCalculator.calculateIncrement(commits)

        // then
        assertThat(increment).isEqualTo(expectedIncrement)
    }

    companion object {
        @JvmStatic
        fun commitsToExpectedIncrement(): Stream<Arguments> = Stream.of(
                arguments(
                        named("Empty commit list", listOf<Commit>()), NONE
                ),
                arguments(
                        named("Commit contains breaking without scope", listOf(
                                commitWithSummary("feat: not a breaking change"),
                                commitWithSummary("chore: not a breaking change"),
                                commitWithSummary("docs!: surprise, breaking change"))
                        ),
                        MAJOR
                ),
                arguments(
                        named("Commit contains breaking with scope", listOf(
                                commitWithSummary("feat: not a breaking change"),
                                commitWithSummary("chore: not a breaking change"),
                                commitWithSummary("docs(some)!: surprise, breaking change"))
                        ),
                        MAJOR
                ),
                arguments(
                        named("Commit without breaking change and higher is feature", listOf(
                                commitWithSummary("feat: not a breaking change"),
                                commitWithSummary("chore: not a breaking change"),
                                commitWithSummary("docs(some): not a breaking change"))
                        ),
                        MINOR
                ),
                arguments(
                        named("Only patch changes", listOf(
                                commitWithSummary("fix: not a breaking change"),
                                commitWithSummary("style: not a breaking change"),
                                commitWithSummary("chore: not a breaking change"),
                                commitWithSummary("refactor(some): not a breaking change"),
                                commitWithSummary("test(some): not a breaking change"),
                                commitWithSummary("perf(some): not a breaking change"))
                        ),
                        PATCH
                ),
                arguments(
                        named("No build needed changes", listOf(
                                commitWithSummary("build: not a breaking change"),
                                commitWithSummary("ci(some): not a breaking change"),
                                commitWithSummary("docs(some): not a breaking change"))
                        ),
                        NONE
                )
        )
    }
}