import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials

def creds = CredentialsProvider.lookupCredentials(
StandardCredentials.class,
    Jenkins.instance,
    null,
    null
);

creds.each { cred ->
  println "\n========== Credential ${cred.id} Start =========="
  cred.properties.each { println it }
  println "========== Credential ${cred.id} End   ==========\n"
}

null
