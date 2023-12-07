package conventional.commits

import conventional.commits.VersionIncrement.MAJOR
import conventional.commits.VersionIncrement.NONE

class NewVersionCalculator(
        private val mapping: TypeToVersionMapping
) {
    fun calculateIncrement(commits: List<Commit>): VersionIncrement = commits
            .mapNotNull { ConventionalCommit.from(it) }
            .let { conventionalCommits ->
                if (conventionalCommits.any { it.breaking }) MAJOR
                else highestChange(conventionalCommits)
            }

    private fun highestChange(conventionalCommits: List<ConventionalCommit>) =
            conventionalCommits.minOfOrNull { mapping.getIncrementFor(it.type) } ?: NONE

}

class TypeToVersionMapping(
        mappings: Map<List<String>, VersionIncrement>
) {
    private val efficientMappings = mappings
            .flatMap { (key, value) -> key.map { it to value } }
            .toMap()

    fun getIncrementFor(type: String): VersionIncrement = efficientMappings[type] ?: NONE
}