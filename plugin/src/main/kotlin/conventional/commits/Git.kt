package conventional.commits

import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeUnit

class Git(
        private val waitTime: Duration = Duration.ofSeconds(5L),
        private val gitDirectory: File? = null
) {
    private val commitMatcher: RawGitLogToCommitsMapper = RawGitLogToCommitsMapper()
    fun allCommits(): List<Commit> = runCommand("git", "log", "--pretty=[%h] [%s] [%b]")
            .let { commitMatcher.fromRawGitLog(it) }

    fun commitsBetween(tagFrom: String, tagEnd: String? = null): List<Commit> =
            runCommand("git", "log", "--pretty=[%h] [%s] [%b]", "$tagFrom..${tagEnd ?: ""}")
                    .let { commitMatcher.fromRawGitLog(it) }

    fun getLatestVersionTag(): String? {
        return runCommand("git", "tag", "--sort=-version:refname", "-l", "v*.*.*").firstOrNull()
    }

    fun tagLatestCommit(tag: String) {
        runCommand("git", "tag", tag)
    }
    fun tagCommit(commit: Commit, tag: String) {
        runCommand("git", "tag", tag, commit.hash)
    }

    fun pushTag(tag: String) {
        runCommand("git", "push", "origin", ":refs/tags/$tag")
    }

    private fun runCommand(vararg command: String): List<String> {
        val process = startProcess(command)
        val errorLines = process.errorStream.reader().readLines()
        val outputLines = process.inputStream.reader().readLines()
        val completed = process.waitFor(waitTime.seconds, TimeUnit.SECONDS)
        val exitCode = process.exitValue()

        if (!completed) {
            throw IOException("Command '${command.joinToString(" ")}' did not complete within ${waitTime.seconds} seconds")
        }

        if (exitCode != 0) {
            throw IOException("Command '${command.joinToString(" ")}' failed to run: " + errorLines.joinToString("\n"))
        }
        return outputLines
    }

    private fun startProcess(command: Array<out String>): Process =
            ProcessBuilder(command.toList()).apply {
                if (gitDirectory != null) directory(gitDirectory)
            }.start()


}
