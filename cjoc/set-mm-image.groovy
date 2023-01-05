import com.cloudbees.opscenter.server.model.ManagedMaster

// operations-center-server.jar plugin

def jenkins  = Jenkins.get()
jenkins.allItems(ManagedMaster).each { c ->
  
  // Set controller image
  // c.configuration is from master-provisioning-kubernetes.jar
  //     com.cloudbees.masterprovisioning.kubernetes.KubernetesMasterProvisioning
  c.configuration.setImage('CloudBees CI - Managed Controller - 2.361.4.1')
  // .c.properties[0].properties.bundle  // Check bundle config
  // .configuration.yaml
  // .stopAction(false)
  // .provisionAndStartAction()
  // .persistedState
  // .resource
  // .resource.isStale()

  // Stop all stale controllers
  // if (c.resource?.isStale() && c.state.name() == "APPROVED") c.stopAction(false)

  // Start all stopped controllers
  // if (c.state.name() == "CREATED") println(c.provisionAndStartAction())
  c.save()
}
