package conventional.commits

object ConventionalCommitSetting {
    val defaultIncrementMapping = mapOf(
            emptyList<String>() to VersionIncrement.MAJOR,
            listOf("feat") to VersionIncrement.MINOR,
            listOf("fix", "chore", "style", "refactor", "perf", "test") to VersionIncrement.PATCH,
            listOf("build", "ci", "docs") to VersionIncrement.NONE,
    )
}