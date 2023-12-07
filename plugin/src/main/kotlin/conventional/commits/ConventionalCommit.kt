package conventional.commits

import java.util.regex.Pattern

data class ConventionalCommit(
        val hash: String,
        val type: String,
        val scope: String?,
        val summary: String,
        val body: String?,
        val breaking: Boolean
) {

    companion object {
        fun from(commit: Commit): ConventionalCommit? = parseSummary(commit.summary)
                ?.let { (type, scope, summary, breaking) ->
                    ConventionalCommit(commit.hash, type, scope, summary, commit.body, breaking)
                }
    }
}

private val possibleTypes = ConventionalCommitSetting.defaultIncrementMapping.keys.flatten()
private val typesRegexp = possibleTypes.joinToString("|")
private val scopeRegexp = "[^!]*"
private val summaryRegexp = ".*"
private val breakingMarkerRegexp = "!"
private val summaryPattern: Pattern = Pattern.compile("(?<type>$typesRegexp)(?<scope>$scopeRegexp)?(?<breaking>$breakingMarkerRegexp)?: (?<summary>$summaryRegexp)")

private fun parseSummary(summary: String): ParsedSummary? {
    val matcher = summaryPattern.matcher(summary)
    return if (matcher.find()) ParsedSummary(
            matcher.group("type"),
            matcher.group("scope")?.removePrefix("(")?.removeSuffix(")")?.takeIf { it.isNotBlank() },
            matcher.group("summary"),
            matcher.group("breaking") != null,
    )
    else null
}

private data class ParsedSummary(val type: String, val scope: String?, val summary: String, val breaking: Boolean) {
}