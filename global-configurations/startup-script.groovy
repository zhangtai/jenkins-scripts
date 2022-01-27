import jenkins.model.Jenkins
import hudson.EnvVars
import hudson.slaves.EnvironmentVariablesNodeProperty
import jenkins.model.GlobalConfiguration
import com.splunk.splunkjenkins.SplunkJenkinsInstallation
import java.util.logging.Logger

Logger logger = Logger.getLogger("init.init_02_global_configuration.groovy")

Jenkins jenkins = Jenkins.get()
String controllerNameUpper = jenkins.rootUrl.split("/").last().toUpperCase()

// START: set environment vars
def masterEnvs = jenkins.getGlobalNodeProperties().get(EnvironmentVariablesNodeProperty).getEnvVars()
EnvVars newEv = new EnvVars("GROUP_NAME", controllerNameUpper)
masterEnvs.overrideExpandingAll(newEv)
logger.info("Set EnvVar 'GROUP_NAME' to ${controllerNameUpper}")
// END: set environment vars

// START: Splunk Hostname
def splunkConfig = GlobalConfiguration.all().get(SplunkJenkinsInstallation)
if (splunkConfig) {
    splunkConfig.metadataHost = controllerNameUpper
    logger.info("Set Splunk hostname to ${controllerNameUpper}")
} else {
    logger.info("No SplunkJenkinsInstallation found, ignore")
}
// END: Splunk Hostname

jenkins.save()
