import com.cloudbees.opscenter.server.model.*
import com.cloudbees.opscenter.server.clusterops.steps.*

def retour = '\n'
String script = """
print GroovySystem.version
"""

Jenkins.instance.getAllItems(ConnectedMaster.class).each{ master ->
  if (master.channel) {
    def stream = new ByteArrayOutputStream();
    def listener = new StreamBuildListener(stream);
    master.channel.call(new MasterGroovyClusterOpStep.Script(
      script,
      listener,
      "script.groovy",
      [:]
    ))
    retour = retour << "${master.displayName}: ${stream.toString()}\n"
  }
}

println retour

null
