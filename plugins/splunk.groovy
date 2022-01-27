import jenkins.model.GlobalConfiguration
import com.splunk.splunkjenkins.SplunkJenkinsInstallation

def splunkConfig = GlobalConfiguration.all().get(SplunkJenkinsInstallation)
if (splunkConfig) {
    println("Splunk hostname is: ${splunkConfig.metadataHost}")
} else {
    println("No SplunkJenkinsInstallation found, ignore")
}
