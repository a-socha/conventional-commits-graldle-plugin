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

}
