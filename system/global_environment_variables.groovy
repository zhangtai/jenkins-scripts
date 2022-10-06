import hudson.slaves.EnvironmentVariablesNodeProperty.Entry
import hudson.slaves.EnvironmentVariablesNodeProperty


// Print exist envVars
Jenkins.instance.globalNodeProperties[0].envVars.each { e ->
  println e
}


// Create new envVars
def evnp = new EnvironmentVariablesNodeProperty([
  new Entry("CONTROLLER", "ADMIN")
])

evnp.envVars.each { e ->
  println e
}

null
