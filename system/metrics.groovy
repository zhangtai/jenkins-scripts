import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.ZoneOffset
import groovy.transform.Field
import groovy.json.JsonOutput
import jenkins.metrics.api.Metrics
import org.jenkinsci.plugins.useractivity.UserActivityPlugin
import org.jenkinsci.plugins.useractivity.UserActivityReport
import org.jenkinsci.plugins.useractivity.dao.UserActivityDao
import org.jenkinsci.plugins.workflow.support.steps.ExecutorStepExecution.PlaceholderTask
import org.jenkinsci.plugins.workflow.support.steps.input.InputAction
import org.jenkinsci.plugins.workflow.support.steps.input.ApproverAction
import com.cloudbees.hudson.plugins.folder.Folder
import com.cloudbees.hudson.plugins.modeling.impl.jobTemplate.JobTemplate
import net.bull.javamelody.*
import net.bull.javamelody.internal.model.*
import net.bull.javamelody.internal.common.*

@Field def jenkins = Jenkins.get()
@Field def java = new JavaInformations(Parameters.getServletContext(), true)
@Field def metricRegistry = Metrics.metricRegistry()
@Field def jenseKeyMapping = [
    "Delivery_Pipeline_Job": "dp_count",
    "Automation_Tests_Template": "att_count",
    "org.jenkinsci.plugins.workflow.job.WorkflowJob": "workflow_count",
    "hudson.model.FreeStyleProject": "freestyle_count",
    "hudson.maven.MavenModuleSet": "maven_count",
    "hudson.matrix.MatrixProject": "matrix_count",
    "com.tikal.jenkins.plugins.multijob.MultiJobProject": "multijob_count"
]

def getTodayLoginCount() {
    def count = 0
    try {
        UserActivityDao userActivityDao = ((UserActivityPlugin)ExtensionList.lookupSingleton(UserActivityPlugin.class)).getUserActivityDao()
        UserActivityReport report = (new UserActivityReport()).withUserActivitySummary(userActivityDao.generateUserActivitySummary('1', 'D')).withAuthAccessReport(userActivityDao.generateAuthAccessReport()).withScmAccessReport(userActivityDao.generateScmAccessReport());
        count = report.summary.users.findAll{ it.lastSeenInstant > OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toInstant() }.size()
    } catch (groovy.lang.MissingMethodException err) {}
    return count
}

def getQueueSizeByLabel() {
    def items = jenkins.queue.items
    def assignees = [:]
    items.each {
        label = it.assignedLabel ?: "NULL"
        assignees[label] = assignees[it.assignedLabel] ? assignees[it.assignedLabel] + 1 : 1
    }
    return assignees
}

def getJobCounts() {
    def jobsByClass = [:]
    def jobsByTemplate = [:]
    def allJobs = jenkins.allItems(Job).findAll { !(it.class.simpleName == "MavenModule") }
    allJobs.each { job ->
      String jenseMappedField = jenseKeyMapping.get(job.class.name, 'other_count')
      def c = jobsByClass[jenseMappedField]
      jobsByClass[jenseMappedField] = (c == null) ? 0 : c + 1
    }
    jobTemplateNames = ["Delivery_Pipeline_Job", "Automation_Tests_Template"]
    def jobTemplates = jenkins.allItems(JobTemplate).findAll { it.name in jobTemplateNames }
    jobTemplates.each { temp ->
      jobsByTemplate[jenseKeyMapping.get(temp.name)] = temp.listInstances().size()
    }
    def metricRegistryJobs = [
        "count": metricRegistry.getGauges().get("jenkins.job.count.value").value,
        "enabled_count": metricRegistry.getGauges().get("jenkins.project.enabled.count.value").value,
        "disabled_count": metricRegistry.getGauges().get("jenkins.project.disabled.count.value").value
    ]
    return metricRegistryJobs + jobsByClass + jobsByTemplate
}

def validCmNodes(nodeName) {
  return nodeName.startsWith("cm-") && !nodeName.startsWith("cm-linux-SMAP")
}

def getAgentExecutors() {
    def agents = []
    def cmNodes = jenkins.nodes.findAll {node ->
        validCmNodes(node.nodeName) && node.toComputer()
    }

    for (node in cmNodes) {
        def nodeOutput = [
            name: node.name,
            os: node.computer.isUnix() ? "LIN" : "WIN",
            executors: []
        ]
        node.toComputer().getExecutors().each {executor ->
            if (!executor.isIdle()) {
                def workUnit = executor.getCurrentWorkUnit()
                executorOutput = [
                    number: executor.number,
                    is_idle: executor.isIdle(),
                    build: [
                        has_input_action: false,
                        has_approver_action: false,
                        log_last_line: ""
                    ]
                ]
                try {
                    Run taskRun = (workUnit.work instanceof PlaceholderTask) ?
                            workUnit.work.context.get(Run.class) : workUnit.getExecutable() as Run

                    executorOutput.build.start_time = taskRun.getStartTimeInMillis() / 1000
                    executorOutput.build.build_url_path = workUnit.work.url

                    if (taskRun.getActions(InputAction).size() > 0) executorOutput.build.has_input_action = true
                    if (taskRun.getActions(ApproverAction).size() > 0) executorOutput.build.has_approver_action = true

                    try {
                        String lastLogFull = taskRun.getLog(2)[1].replaceAll("\n", "")
                        String lastLogTrunc = lastLogFull.size() > 256 ? "${lastLogFull[0..255]}..." : lastLogFull
                        executorOutput.build.log_last_line = lastLogTrunc
                    } catch (FileNotFoundException ignored) {
                        print ""
                    }
                    nodeOutput.executors << executorOutput
                } catch (IOException ignored) {
                    print ""
                }
            }
        }
        agents << nodeOutput
    }
    return agents
}

print JsonOutput.toJson([
    "version": jenkins.version.toString(),
    "java_version": java.javaVersion,
    "java_session_count": java.sessionCount,
    "java_active_thread_count":  java.activeThreadCount,
    "java_system_cpu_load": java.systemCpuLoad,
    "java_system_load_average": java.systemLoadAverage,
    "started": jenkins.toComputer().getConnectTime(),
    "disk_gb_left": hudson.node_monitors.DiskSpaceMonitor.DESCRIPTOR.get(jenkins.computers[0]).gbLeft,
    "user_folder_count": jenkins.items.findAll {it.class == Folder && it.name.startsWith("project-")}.size,
    "login_today_count": getTodayLoginCount(),
    "run_since_start_count": metricRegistry.getMeters().get("jenkins.runs.total").count,
    "agent_count": metricRegistry.getGauges().get("jenkins.node.count.value").value,
    "agent_executors": getAgentExecutors(),
    "user_count": User.getAll().size(),
    "queue_by_label": getQueueSizeByLabel(),
    "jobs": getJobCounts(),
])
