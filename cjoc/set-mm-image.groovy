import com.cloudbees.opscenter.server.model.ManagedMaster

def jenkins  = Jenkins.get()
jenkins.allItems(ManagedMaster).each { controller ->
  controller.configuration.setImage('CloudBees CI - Managed Controller - 2.361.4.1')
  // controller.configuration.yaml
  controller.save()
}
