import jenkins.*
import jenkins.model.*

Jenkins.instance.getExtensionList('org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval')[0].get().preapproveAll()
