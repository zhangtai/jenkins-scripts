import com.cloudbees.opscenter.server.model.ManagedMaster
import com.cloudbees.plugins.updatecenter.sources.LocalUpdateSource
import com.cloudbees.opscenter.plugins.updatecenter.ConnectedMasterUpdateCenterProperty

def jenkins  = Jenkins.get()
def cmucp = new ConnectedMasterUpdateCenterProperty(new LocalUpdateSource("update-center-new"))

jenkins.allItems(ManagedMaster).each { mm ->
    mm.properties.replace(cmucp)
    mm.save()
}
