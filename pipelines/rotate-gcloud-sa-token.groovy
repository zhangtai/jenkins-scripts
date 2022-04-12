import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.hudson.plugins.folder.Folder
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.Domain;
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl;
import java.nio.file.*;

def recreateServiceAccount(serviceAccountShortName, projectName) {
    String serviceAccountFullName = "${serviceAccountShortName}@${projectName}.iam.gserviceaccount.com"
    sh """
        echo "Deleting Existing Keys for SA: ${serviceAccountShortName}"
        gcloud iam service-accounts keys list --iam-account=${serviceAccountFullName} --managed-by=user
        gcloud iam service-accounts keys list --iam-account=${serviceAccountFullName} --managed-by=user| awk '{print \$1}' | grep -v KEY_ID | xargs -IkeyId gcloud iam service-accounts keys delete keyId --iam-account=${serviceAccountFullName}
        echo "Creating New Key for SA: ${serviceAccountFullName}"
        gcloud iam service-accounts keys create --key-file-type=json "${serviceAccountShortName}.json" --iam-account=${serviceAccountFullName}
    """
}

def serviceAccounts = [
    "terraform",
    "gcs-admin"
]

def mailTemplate = "Hi,\n\n"

timeout(30){
    node("cm-linux") {
        withCredentials([file(credentialsId: 'CBCI_GCP_SA_TOKEN', variable: 'sa')]) {
            stage("SA Key Rotation"){
                sh "gcloud auth activate-service-account --key-file=${sa}"
                serviceAccounts.each { serviceAccount ->
                    recreateServiceAccount(serviceAccount, PROJ_ENV)
                    jsonContent = readFile "${serviceAccount}.json"
                    mailTemplate += "New key has been provisioned for Service Account: ${serviceAccount}.\n\nBelow the key for your reference:\n\n${jsonContent}\n\n"
                }
                mail body: mailTemplate, subject: "${projectName}-${PROJ_ENV} - SA - Keys", to: 'admin@example.com'
            }
        }

        stage("Jenkins Credentials Update"){
            jsonContent = readFile 'terraform.json'
            println "jsonContent = ${jsonContent}"
            def secretBytes = SecretBytes.fromBytes(jsonContent.getBytes())
            def credentials = new FileCredentialsImpl(CredentialsScope.GLOBAL, 'CBCI_GCP_SA_TOKEN', "ALMJENKINS ${PROJ_ENV} SA", 'terraform.json', secretBytes)
            SystemCredentialsProvider.instance.store.removeCredentials(Domain.global(), credentials)
            SystemCredentialsProvider.instance.store.addCredentials(Domain.global(), credentials)
        }

        withCredentials([file(credentialsId: 'CBCI_GCP_SA_TOKEN', variable: 'sa')]) {
            stage("GCS Update"){
                sh """
                gcloud auth activate-service-account --key-file=${sa}
                echo "Pushing key to Bucket: ${projectName}-${PROJ_ENV}/sa/keys/"
                """
                serviceAccounts.each { serviceAccount ->
                    sh "gsutil cp ${serviceAccount}.json gs://${projectName}/sa/keys/"
                }
            }
        }

        stage("Update CJOC creds") {
            jsonContent = sh(returnStdout: true, script: "jq -r tostring terraform.json|sed 's/\\\\n/\\\\\\\\n/g'").trim()
            echo jsonContent
            scriptText = """
import org.apache.commons.codec.binary.StringUtils
import com.cloudbees.plugins.credentials.SecretBytes
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotCredentialsModule
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotPrivateKeyCredentials
import com.google.jenkins.plugins.credentials.oauth.JsonServiceAccountConfig
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain

def clearStr = '''${jsonContent}'''
SecretBytes prev = new SecretBytes(false, StringUtils.getBytesUtf8(clearStr))

JsonServiceAccountConfig jsonServiceAccountConfig = new JsonServiceAccountConfig();
jsonServiceAccountConfig.setFilename("terraform.json");
jsonServiceAccountConfig.setSecretJsonKey(prev);

GoogleRobotPrivateKeyCredentials credentials = new GoogleRobotPrivateKeyCredentials(
  "${projectName}-${PROJ_ENV}", jsonServiceAccountConfig, new GoogleRobotCredentialsModule()
);

SystemCredentialsProvider.getInstance().getStore().getCredentials(Domain.global()).each { cred ->
  if(cred.username == "terraform@${projectName}-${PROJ_ENV}.iam.gserviceaccount.com") {
    SystemCredentialsProvider.instance.store.removeCredentials(Domain.global(), cred)
  }
}

SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), credentials)
"""
            echo scriptText
            httpRequest authentication: 'CJOC_ADM_TOKEN',
                consoleLogResponseBody: true,
                httpMode: 'POST',
                formData: [[body: scriptText, contentType: 'application/x-www-form-urlencoded', fileName: '', name: 'script', uploadFile: '']],
                responseHandle: 'NONE',
                url: "https://cjoc.example.com/cjoc/scriptText",
                // httpProxy: "http://${EGRESS_PROXY_HOST}:${EGRESS_PROXY_PORT}",
                wrapAsMultipart: false
        }
    }
}
