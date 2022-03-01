import com.cloudbees.opscenter.server.model.ManagedMaster

def jenkins  = Jenkins.get()
jenkins.allItems(ManagedMaster).each { controller ->
  controller.configuration.setImage('CloudBees CI - Managed Master - 2.319.3.3')
  controller.save()
}

jenkins.save()
jenkins.reload()

null
