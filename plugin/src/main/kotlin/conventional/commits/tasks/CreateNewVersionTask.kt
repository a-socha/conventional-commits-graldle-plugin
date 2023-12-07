package conventional.commits.tasks

import conventional.commits.ConventionalCommitSetting
import conventional.commits.Git
import conventional.commits.NewVersionCalculator
import conventional.commits.TypeToVersionMapping
import conventional.commits.features.CreateNewVersionFeature
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

open class CreateNewVersionTask : DefaultTask() {
    private val createNewVersionFeature: CreateNewVersionFeature = CreateNewVersionFeature(
            Git(gitDirectory = project.projectDir),
            NewVersionCalculator(TypeToVersionMapping(ConventionalCommitSetting.defaultIncrementMapping))
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