import com.cloudbees.hudson.plugins.folder.Folder
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.GitSCM
import hudson.plugins.git.BranchSpec
import hudson.plugins.git.UserRemoteConfig
import hudson.model.ParametersDefinitionProperty
import hudson.model.StringParameterDefinition
import hudson.triggers.TimerTrigger


String folderName = "Folder/SubFolder"
String jobName = "JobName"
String jobFullName = "${folderName}/${jobName}"
String timerTrigger = "@daily"
String repoUrl = "https://git.example.com/user/repo.git"
String branchName = "main"
String credsId = "credsId"
String gitToolName = "gitDefault"
String jenkinsfileName = "Jenkinsfile"
ArrayList params = [
    [
      name: "LIB_VERSION",
      defaultValue: "main",
      desc: "",
      trim: true
    ]
]


def jenkins = Jenkins.get()
def gitPlugin = jenkins.pluginManager.plugins.find { it.shortName == "git" }
def folder = jenkins.getAllItems(Folder).find { it.fullName == folderName }
def job = jenkins.getAllItems(Job).find { it.fullName == jobFullName }


def gitScm = (gitPlugin.version > "4.4.5") ?
    new GitSCM(
        [new UserRemoteConfig(repoUrl, null, null, credsId)],
        [new BranchSpec(branchName)],
        null,
        gitToolName,
        []
    ) :
    new GitSCM(
        [new UserRemoteConfig(repoUrl, null, null, credsId)],
        [new BranchSpec(branchName)],
        false, [],
        null,
        gitToolName,
        []
    )


def jobDefination = new CpsScmFlowDefinition(gitScm, jenkinsfileName)
jobDefination.setLightweight(true)

if (!job) {
    println "Creating new job"
    job = new WorkflowJob(folder, jobName)
}

println "Updating job"
job.setDefinition(jobDefination)
job.triggers = [new TimerTrigger(timeTrigger)]
job.removeProperty(ParametersDefinitionProperty)
params.each { paramDef ->
    job.addProperty(new ParametersDefinitionProperty(
        new StringParameterDefinition(
            paramDef.name, paramDef.defaultValue, paramDef.desc, paramDef.trim)
        )
    )
}

job.save()
job.doReload()
folder.doReload()
println "Job saved and reloaded"

null
