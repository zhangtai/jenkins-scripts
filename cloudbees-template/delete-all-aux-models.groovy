import com.cloudbees.hudson.plugins.modeling.impl.auxiliary.AuxModel
Jenkins.get().with {
  allItems(AuxModel).each { it.delete() }
  save()
  reload()
}
