package conventional.commits.tasks

import conventional.commits.*
import conventional.commits.features.CreateNewVersionFeature
import conventional.commits.plugin.ConventionalCommitConfigExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class PrintNewVersionTask @Inject constructor(
    conventionalCommitConfig: ConventionalCommitConfigExtension
) : DefaultTask() {
    private val createNewVersionFeature: CreateNewVersionFeature = CreateNewVersionFeature(
        Git(gitDirectory = project.projectDir),
        NewVersionCalculator(conventionalCommitConfig.toConfig())
    )


    @TaskAction
    fun printNewVersion() {
        val version = createNewVersionFeature.calculateNewVersion()
        print(version.toTag())
    }
}