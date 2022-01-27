import com.cloudbees.hudson.plugins.folder.Folder
import com.cloudbees.opscenter.server.model.ManagedMaster

Jenkins.get().allItems(Folder).each {folder ->
  println folder.name
  folder.allItems(ManagedMaster).each {mm ->
    println "  " + mm.name
  }
}

null
