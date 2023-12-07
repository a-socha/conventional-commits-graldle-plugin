package conventional.commits.features

import conventional.commits.Commit
import conventional.commits.Git
import conventional.commits.NewVersionCalculator
import conventional.commits.SemVer

class CreateNewVersionFeature(
        private val git: Git,
        private val newVersionCalculator: NewVersionCalculator
) {

    fun createNewVersion(push: Boolean): SemVer {
        val (currentVersion, newVersion) = currentAndNewVersion()
        if (newVersion != currentVersion) tagVersion(newVersion, push)
        return newVersion
    }

    fun calculateNewVersion(): SemVer {
        return currentAndNewVersion().second
    }

    private fun currentAndNewVersion(): Pair<SemVer?, SemVer> {
        val currentVersionTag = git.getLatestVersionTag()
        val commits = commitsFromLastVersion(currentVersionTag)
        val currentVersion = currentVersionTag?.let { SemVer.from(it) }
        val increment = newVersionCalculator.calculateIncrement(commits)
        val newVersion = currentVersion?.increment(increment) ?: SemVer.ZERO
        return currentVersion to newVersion

    }

    private fun commitsFromLastVersion(currentVersion: String?): List<Commit> =
            currentVersion?.let { git.commitsBetween(currentVersion) }
                    ?: git.allCommits()

    private fun tagVersion(newVersion: SemVer, push: Boolean) {
        git.tagLatestCommit(newVersion.toTag())
        if (push) git.pushTag(newVersion.toTag())
    }
}