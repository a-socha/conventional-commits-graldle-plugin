package conventional.commits

import conventional.commits.VersionIncrement.MAJOR
import conventional.commits.VersionIncrement.NONE

class NewVersionCalculator(
    private val config: ConventionalCommitConfig,
) {

    private val factory = ConventionalCommitFactory(config)

    fun calculateIncrement(commits: List<Commit>): VersionIncrement = commits
        .mapNotNull { factory.create(it) }
        .let { conventionalCommits ->
            if (conventionalCommits.any { it.breaking }) MAJOR
            else highestChange(conventionalCommits)
        }

    private fun highestChange(conventionalCommits: List<ConventionalCommit>) =
        conventionalCommits.minOfOrNull { config.getIncrementFor(it.type) } ?: NONE

}

data class ConventionalCommitConfig(
    private val incrementToTypes: Map<VersionIncrement, List<String>>
) {
    private val efficientMapping = incrementToTypes
        .flatMap { (key, values) -> values.map { it to key } }
        .toMap()

    fun getIncrementFor(type: String): VersionIncrement = efficientMapping[type] ?: NONE

    fun allPossibleTypes() = incrementToTypes.values.flatten()
}
