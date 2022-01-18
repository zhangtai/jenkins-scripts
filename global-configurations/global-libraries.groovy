import jenkins.model.GlobalConfiguration
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries

def glib = GlobalConfiguration.all().get(GlobalLibraries)
def jenseLib = glib.libraries.find { it.name == "jense" }

jenseLib.properties.each {println it}

null
