import com.cloudbees.opscenter.server.model.ManagedMaster

// operations-center-server.jar plugin

def jenkins  = Jenkins.get()
jenkins.allItems(ManagedMaster).each { c ->
  // c.configuration is from master-provisioning-kubernetes.jar
  //     com.cloudbees.masterprovisioning.kubernetes.KubernetesMasterProvisioning
  def cc = c.configuration
  println "${c.name}: ${cc.cpus}/${cc.memory}/${cc.disk}"

  // .properties[0].properties.bundle  // Check bundle config
  // .configuration.yaml
  // .stopAction(false)
  // .provisionAndStartAction()
  // .persistedState
  // .resource
  // .resource.isStale()

  // Update all controller image
  c.configuration.setImage('CloudBees CI - Managed Controller - 2.361.4.1')
      // Stop all stale controllers
  if (c.resource?.isStale() && c.state.name() == "APPROVED") c.stopAction(false)
      // Start all stopped controllers
  if (c.state.name() == "CREATED") println(c.provisionAndStartAction())
  c.save()
}
