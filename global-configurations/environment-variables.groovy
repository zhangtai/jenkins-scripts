import hudson.EnvVars
import hudson.slaves.EnvironmentVariablesNodeProperty

Jenkins jenkins = Jenkins.get()
def masterEnvs = jenkins.getGlobalNodeProperties().get(EnvironmentVariablesNodeProperty).getEnvVars()
String controllerNameUpper = jenkins.rootUrl.split("/").last().toUpperCase()
EnvVars newEv = new EnvVars("GROUP_NAME", controllerNameUpper)
masterEnvs.overrideExpandingAll(newEv)
jenkins.save()

null
