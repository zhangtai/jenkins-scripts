import hudson.tools.ToolInstallation

ToolInstallation.all().each{tool ->
  println(tool)
}
null