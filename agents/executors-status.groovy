import hudson.model.Run
import hudson.model.Executor
import org.jenkinsci.plugins.workflow.support.steps.ExecutorStepExecution.PlaceholderTask
import org.jenkinsci.plugins.workflow.support.steps.input.InputAction
import org.jenkinsci.plugins.workflow.support.steps.input.ApproverAction

def jenkins = Jenkins.get()
cmOnlineNodes = Jenkins.get().nodes.findAll { it.name.startsWith("linux-") && !it.computer.isOffline() }

cmOnlineNodes.each { node ->
    println node.name
    node.computer.executors.each { executor ->
        Integer number
        Boolean isIdle
        Boolean hasInputAction = false
        Boolean hasApproverAction = false
        String buildUrl = ""
        String logLastLine = ""
        long elapsedTime

        number = executor.number
        isIdle = executor.isIdle()
        if (!executor.isIdle()) {
            def workUnit = executor.getCurrentWorkUnit()
            buildUrl = workUnit.work.url
            elapsedTime = executor.getElapsedTime() / 1000 / 60 / 60
            try {
                Run taskRun = (workUnit.work instanceof PlaceholderTask) ?
                        workUnit.work.context.get(Run.class) : workUnit.getExecutable() as Run

                if (taskRun.getActions(InputAction).size() > 0) hasInputAction = true
                if (taskRun.getActions(ApproverAction).size() > 0) hasApproverAction = true

                try {
                    String lastLogFull = taskRun.getLog(2)[1].replaceAll("\n", "")
                    String lastLogTrunc = lastLogFull.size() > 256 ? "${lastLogFull[0..255]}..." : lastLogFull
                    logLastLine = lastLogTrunc
                } catch (FileNotFoundException ignored) {
                    println "Failed to get log for executor(#${executor.number})"
                }
            } catch (IOException ignored) {
                println "Cannot find current thread(#${executor.number})"
            }
        }
        println """
    number: ${number}
    isIdle: ${isIdle}
    hasInputAction: ${hasInputAction}
    hasApproverAction: ${hasApproverAction}
    buildUrl: ${jenkins.rootUrl}${buildUrl}
    logLastLine: ${logLastLine}
    elapsedTime(hr): ${elapsedTime}
        """
    }
}

null
