package conventional.commits

class ConventionalCommitFactory(
    config: ConventionalCommitConfig
) {

    private val regexpProvider = ConventionalCommitRegexpProvider(config)
    fun create(commit: Commit): ConventionalCommit? = parseSummary(commit.summary)
        ?.let { (type, scope, summary, breaking) ->
            ConventionalCommit(commit.hash, type, scope, summary, commit.body, breaking)
        }

    private fun parseSummary(summary: String): ParsedSummary? {
        val matcher = regexpProvider.commitPattern.matcher(summary)
        return if (matcher.find()) ParsedSummary(
            matcher.group("type"),
            matcher.group("scope")?.removePrefix("(")?.removeSuffix(")")?.takeIf { it.isNotBlank() },
            matcher.group("summary"),
            matcher.group("breaking") != null,
        )
        else null
    }

}

private data class ParsedSummary(val type: String, val scope: String?, val summary: String, val breaking: Boolean)