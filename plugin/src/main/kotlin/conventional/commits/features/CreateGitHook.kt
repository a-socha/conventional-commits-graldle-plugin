package conventional.commits.features

import conventional.commits.ConventionalCommitConfig
import conventional.commits.GitHookScript
import org.gradle.api.Project
import java.io.File

class CreateGitHook(
    private val project: Project,
    conventionalCommitConfig: ConventionalCommitConfig,
    wrongCommitMessage: String
) {
    private val gitHookScript = GitHookScript(conventionalCommitConfig, wrongCommitMessage)

    private val gitDirectory = ".git"
    private val gitHookDirectory = "$gitDirectory/hooks"
    private val commitHook = "commit-msg"

    fun createGitGook() {
        generateSequence(project.projectDir) { it.parentFile }.find { it.isGitFolder() }?.let {
            val scriptContent = gitHookScript.hook
            val hooksFolder = it.resolve(gitHookDirectory)
            if (!hooksFolder.exists()) hooksFolder.mkdir()
            it.resolve("$gitHookDirectory/$commitHook").writeScript(scriptContent)
        } ?: run {
            project.logger.warn("[ConventionalCommits] '$gitDirectory' folder not found. Git hook not created")
        }
    }

    private fun File.isGitFolder(): Boolean =
        listFiles()?.any { folder -> folder.isDirectory && folder.name == gitDirectory } ?: false

    private fun File.writeScript(content: String) {
        writeText(content)
        setExecutable(true)
    }
}

