package conventional.commits

import java.util.regex.Pattern

class ConventionalCommitRegexpProvider(
    config: ConventionalCommitConfig
) {
    private val possibleTypes = config.allPossibleTypes()
    private val typesRegexp = possibleTypes.joinToString("|")
    private val scopeRegexp = "[^!]*"
    private val summaryRegexp = ".*"
    private val breakingMarkerRegexp = "!"
    val commitPattern: Pattern
        get() = Pattern.compile("(?<type>$typesRegexp)(?<scope>$scopeRegexp)?(?<breaking>$breakingMarkerRegexp)?:\\ (?<summary>$summaryRegexp)")


    val commitPatternWithoutGroups: Pattern
        get() = Pattern.compile("($typesRegexp)($scopeRegexp)?($breakingMarkerRegexp)?:\\ ($summaryRegexp)")

}