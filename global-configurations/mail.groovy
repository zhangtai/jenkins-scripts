import hudson.plugins.emailext.ExtendedEmailPublisherDescriptor
import hudson.ExtensionList
import hudson.tasks.Mailer

def mailExtDescriptor = ExtensionList.lookupSingleton(ExtendedEmailPublisherDescriptor.class)
def mailerDescriptor = Mailer.descriptor()
def account = mailExtDescriptor.getMailAccount()
account.setCredentialsId(null)
mailerDescriptor.setAuthentication(null)
Jenkins.get().save()
