package conventional.commits

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class ConventionalCommitTest {

    private val commitHash = "a-hash"
    private val summaryMessage = "some message"
    private val body = "some body"

    @ParameterizedTest
    @ValueSource(strings = [
        "feat",
        "fix",
        "build",
        "chore",
        "ci",
        "docs",
        "style",
        "refactor",
        "perf",
        "test"
    ])
    fun `should map commit to conventional commit for not breaking commits without a scope`(type: String) {
        // given
        val commit = Commit(commitHash, "$type: $summaryMessage", body)

        // when
        val conventionalCommit = ConventionalCommit.from(commit)

        // then
        assertThat(conventionalCommit).isEqualTo(
                ConventionalCommit(
                        commitHash,
                        type,
                        null,
                        summaryMessage,
                        body,
                        false
                )
        )
    }

    @ParameterizedTest
    @CsvSource(value = [
//      commit_prefix, type,    scope,   breaking
        "feat(order) , feat,    order,   false",
        "feat(order)!, feat,    order,   true",
        "feat!       , feat,         ,   true",
        "feat        , feat,         ,   false",
        "docs(order) , docs,    order,   false",
        "docs(order)!, docs,    order,   true",
        "docs!       , docs,         ,   true",
        "docs        , docs,         ,   false",
        "ci(order)   , ci,      order,   false",
        "ci(order)!  , ci,      order,   true",
        "ci!         , ci,           ,   true",
        "ci          , ci,           ,   false",
    ])
    fun `should map commit to conventional commit`(
            prefix: String,
            type: String,
            scope: String?,
            breaking: Boolean) {
        // given
        val commit = Commit(commitHash, "$prefix: $summaryMessage", body)

        // when
        val conventionalCommit = ConventionalCommit.from(commit)

        // then
        assertThat(conventionalCommit).isEqualTo(
                ConventionalCommit(
                        commitHash,
                        type,
                        scope,
                        summaryMessage,
                        body,
                        breaking
                )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "feat!(almost): I am almost conventional",
        "I'm not conventional at all",
        "feat(almost)! : so close",
        "feat(almost)!:no matter how far",
        "(first)scope: than message",
        "!ci(order)!!: cannot be conventional because too breaking",
        "unknown: I will be conventional but I have unknown type",
    ])
    fun `should skip commit if does not follow conventional commits`(summary: String) {
        // given
        val commit = Commit(commitHash, summary, body)

        // when
        val conventionalCommit = ConventionalCommit.from(commit)

        // then
        assertThat(conventionalCommit).isNull()
    }

}