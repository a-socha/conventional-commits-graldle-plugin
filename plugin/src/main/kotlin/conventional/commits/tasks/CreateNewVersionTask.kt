package conventional.commits.tasks

import conventional.commits.*
import conventional.commits.features.CreateNewVersionFeature
import conventional.commits.plugin.ConventionalCommitConfigExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import javax.inject.Inject

open class CreateNewVersionTask @Inject constructor(
    conventionalCommitConfig: ConventionalCommitConfigExtension
) : DefaultTask() {
    private val createNewVersionFeature: CreateNewVersionFeature = CreateNewVersionFeature(
        Git(gitDirectory = project.projectDir),
        NewVersionCalculator(conventionalCommitConfig.toConfig())
    )


    @get:Input
    @set:Option(option = "no-push", description = "Disable pushing to repo")
    open var noPush: Boolean = false

    @TaskAction
    fun createNewVersion() {
        val version = createNewVersionFeature.createNewVersion(!noPush)
        print(version.toTag())
    }
}