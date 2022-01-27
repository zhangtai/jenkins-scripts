import hudson.tools.ToolInstallation
ToolInstallation.all().each { tool ->
  tool.installations.each { install ->
    println "${install.name}: ${install.home}"
  }
}
null