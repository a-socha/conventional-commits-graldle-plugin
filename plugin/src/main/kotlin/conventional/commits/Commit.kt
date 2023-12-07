package conventional.commits

data class Commit(
        val hash: String,
        val summary: String,
        val body: String
) {
}