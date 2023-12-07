package conventional.commits.tasks

import conventional.commits.ConventionalCommitSetting
import conventional.commits.Git
import conventional.commits.NewVersionCalculator
import conventional.commits.TypeToVersionMapping
import conventional.commits.features.CreateNewVersionFeature
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class PrintNewVersionTask : DefaultTask() {
    private val createNewVersionFeature: CreateNewVersionFeature = CreateNewVersionFeature(
            Git(gitDirectory = project.projectDir),
            NewVersionCalculator(TypeToVersionMapping(ConventionalCommitSetting.defaultIncrementMapping))
    )


    @TaskAction
    fun printNewVersion() {
        val version = createNewVersionFeature.calculateNewVersion()
        print(version.toTag())
    }
}