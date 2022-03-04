import com.cloudbees.opscenter.server.model.SharedCloud
import com.cloudbees.opscenter.server.jnlp.cloud.JnlpCloud

def cmLinuxGce = Jenkins.get().allItems(SharedCloud).find {it.name == "linux-vm"}

cmLinuxGce.cloud.with {
  println "getAvailablePoolSize: ${getAvailablePoolSize()}"
  println "countConnectedSlaves: ${countConnectedSlaves()}"
  println "countAvailableSlaves: ${countAvailableSlaves()}"
  println "getChannelStates: ${getChannelStates()}"
}

null
