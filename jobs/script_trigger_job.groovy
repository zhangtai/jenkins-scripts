def myJob = Jenkins.get().getAllItems(Job).find { it.fullName == "My_Job" }
if (myJob) {
    myJob.scheduleBuild()
}
