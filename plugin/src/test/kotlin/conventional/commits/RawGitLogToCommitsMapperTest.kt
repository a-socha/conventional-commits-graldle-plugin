package conventional.commits

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RawGitLogToCommitsMapperTest {
    private val rawGitLog: List<String> = listOf(
            "[7d8ff59] [docs: summary of long commit] [some long body that cannot fit in single line",
            "second line",
            "]",
            "[385bf5e] [build(orders): some message(scoped for order)] []",
            "[0810cb2] [refactor: refactoring message] []",
            "[b48308a] [test: testing message] []",
            "[e3975b6] [feat: feature message] []",
            "[0d0c325] [feat!: breaking feature message] []",
            "[d7cc085] [feat(orders)!: breaking feature (in orders scope)] []",
            "[c256d67] [docs: some docs message] []",
            "[e761ea0] [style: style improved] []",
            "[e74af5e] [perf: performance improved] []",
            "[d7567c8] [docs: added documentation] []",
            "[76d8220] [ci: commit about continuous integration] []",
    )

    private val mapper = RawGitLogToCommitsMapper()

    @Test
    fun `should parse raw git log to commits`() {
        // when
        val results = mapper.fromRawGitLog(rawGitLog)

        // then
        assertThat(results).containsExactly(
                Commit("7d8ff59", "docs: summary of long commit", """some long body that cannot fit in single line
                        |second line
                        |""".trimMargin()
                ),
                Commit("385bf5e", "build(orders): some message(scoped for order)", ""),
                Commit("0810cb2", "refactor: refactoring message", ""),
                Commit("b48308a", "test: testing message", ""),
                Commit("e3975b6", "feat: feature message", ""),
                Commit("0d0c325", "feat!: breaking feature message", ""),
                Commit("d7cc085", "feat(orders)!: breaking feature (in orders scope)", ""),
                Commit("c256d67", "docs: some docs message", ""),
                Commit("e761ea0", "style: style improved", ""),
                Commit("e74af5e", "perf: performance improved", ""),
                Commit("d7567c8", "docs: added documentation", ""),
                Commit("76d8220", "ci: commit about continuous integration", ""),
        )

    }
}