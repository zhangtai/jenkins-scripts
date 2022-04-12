import org.apache.commons.codec.binary.StringUtils
import com.cloudbees.plugins.credentials.SecretBytes
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotCredentialsModule
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotPrivateKeyCredentials
import com.google.jenkins.plugins.credentials.oauth.JsonServiceAccountConfig
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain

// Replace "\n" to "\\n" in the "private_key" value
def clearStr = '{"type":"service_account","project_id":"test-project","private_key_id":"test-private-key-id","private_key":"test-private-key","client_email":"test-account@test-project.iam.gserviceaccount.com","client_id":"test-client-id","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_x509_cert_url":"https://www.googleapis.com/robot/v1/metadata/x509/test-account%40test-project.iam.gserviceaccount.com"}'
def secretBytes = new SecretBytes(false, StringUtils.getBytesUtf8(clearStr))

JsonServiceAccountConfig saConfig = new JsonServiceAccountConfig();
saConfig.setSecretJsonKey(SecretBytes.fromString(secretBytes.toString()));
GoogleRobotPrivateKeyCredentials credentials = new GoogleRobotPrivateKeyCredentials(
  "my-project-id", saConfig, new GoogleRobotCredentialsModule()
);

println saConfig.accountId

SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), credentials)
