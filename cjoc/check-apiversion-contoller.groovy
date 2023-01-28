// https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/cloudbees-ci-on-modern-cloud-platforms/the-apiversion-on-a-resource-being-deserialized-is-required-after-upgrading-kubernetes-client-6-x
def check = { yaml ->
  def result = []
  if (!yaml?.trim()) {
    result += 'OK (blank)'
  }

  def fragments = yaml.split('---')
  result << fragments.size() + ' fragment' + (fragments.size() > 1 ? 's' : '')
  fragments.each { f ->
      if (yaml.contains('apiVersion:')) {
        result << 'OK'
      } else {
        result << 'KO (missing apiVersion)'
      }
  }
  return result.join(', ')
}

def verbose = false

println 'Global configuration'
println '--------------------'
def d = ExtensionList.lookupSingleton(com.cloudbees.masterprovisioning.kubernetes.KubernetesMasterProvisioning.DescriptorImpl.class)
println check(d.yaml)
if (verbose) {
  println d.yaml
}
println ''

println 'Controllers configuration'
println '-------------------------'

Jenkins.instance.allItems.each { it ->
  if (it instanceof com.cloudbees.opscenter.server.model.ManagedMaster) {
    if (it.configuration.hasProperty("yaml")){
      def yaml = it.configuration.yaml
      println '* ' + it.fullName + ' --> ' + check(yaml)
      if (verbose) {
        println yaml
        println ''
      }
    }
  }
}
return

