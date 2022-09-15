import hudson.model.JDK

def jdkImpl = new JDK.DescriptorImpl()
println(jdkImpl.getDisplayName())
println(jdkImpl.getInstallations())
jdkImpl.setInstallations(
    new JDK('linux/jdk11:latest', '/usr/lib/jdk/11.0.16'),
    new JDK('linux/jdk11:11.0.16', '/usr/lib/jdk/11.0.16')
)
