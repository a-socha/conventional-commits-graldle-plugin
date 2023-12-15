package conventional.commits.plugin

import conventional.commits.ConventionalCommitConfig
import conventional.commits.VersionIncrement

open class ConventionalCommitConfigExtension {
    var major: List<String> = emptyList()
    var minor: List<String> = listOf("feat")
    var patch: List<String> = listOf("fix", "chore", "style", "refactor", "perf", "test")
    var none: List<String> = listOf("build", "ci", "docs")


    internal fun toConfig() = ConventionalCommitConfig(
        mapOf(
            VersionIncrement.MAJOR to major.toList(),
            VersionIncrement.MINOR to minor.toList(),
            VersionIncrement.PATCH to patch.toList(),
            VersionIncrement.NONE to none.toList()
        )
    )
}

