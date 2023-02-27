import hudson.triggers.TimerTrigger
import hudson.matrix.MatrixProject
import hudson.maven.MavenModuleSet
import hudson.model.FreeStyleProject
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def TIMER_TRIGGER_DESCRIPTOR = Hudson.instance.getDescriptorOrDie(TimerTrigger.class)
def validJobClasses = [MatrixProject, MavenModuleSet, FreeStyleProject, WorkflowJob]
def jobs = Jenkins.get().getAllItems(Job).findAll { validJobClasses.contains(it.class) }

jobs.each { item ->
  def timertrigger = item.getTriggers().get(TIMER_TRIGGER_DESCRIPTOR)
  if (timertrigger && !item.isDisabled()) {
    if (timertrigger.spec.startsWith("* ")) {
      println item.fullName + ": " + timertrigger.spec
    }
  }
}

null
