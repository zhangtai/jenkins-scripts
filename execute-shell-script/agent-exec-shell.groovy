import hudson.util.RemotingDiagnostics

def nodes = Jenkins.get().nodes.findAll { it.name.startsWith("cm-linux") && !it.computer.isOffline() }

String shellCommand = 'free -h; nproc'

printStatsScript = """
def shellProc = ['sh', '-c', '${shellCommand}'].execute()
def sout = new StringBuilder(), serr = new StringBuilder()
shellProc.consumeProcessOutput(sout, serr)
shellProc.waitFor()
println "\$sout\$serr"
"""

nodes.each { node ->
  nodeResult = RemotingDiagnostics.executeGroovy(printStatsScript, node.getChannel())
  println node.name + '$>' + shellCommand
  println nodeResult
}

null