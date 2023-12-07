package conventional.commits

import java.util.regex.Pattern

class RawGitLogToCommitsMapper {
    private val commitRegexpMatcher = CommitRegexpMatcher()
    fun fromRawGitLog(logLines: List<String>): List<Commit> =
            mergeLinesFromSameCommit(logLines).map { commitRegexpMatcher.fromCommitString(it) }

    private fun mergeLinesFromSameCommit(logLines: List<String>): List<String> {
        var singleLineCommit = ""
        val singleLineCommits = mutableListOf<String>()
        logLines.forEach {
            if (it.startsWith("[")) {
                singleLineCommits.add(singleLineCommit)
                singleLineCommit = it
            } else {
                singleLineCommit += "\n$it"
            }
        }
        singleLineCommits.add(singleLineCommit)
        return singleLineCommits.toList().drop(1)
    }
}

class CommitRegexpMatcher {
    private val pattern = Pattern.compile("\\[(.*)] \\[(.*)] \\[((.|\\n)*)]")
    fun fromCommitString(multiLineCommit: String): Commit {
        val matcher = pattern.matcher(multiLineCommit)
        matcher.find()
        return Commit(matcher.group(1), matcher.group(2), matcher.group(3))
    }
}