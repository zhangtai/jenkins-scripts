// System.setProperty("http.proxyHost", "proxy.example.com")
// System.setProperty("http.proxyPort", "3128")

def command = [
  "ls",
  "-ltr"
]
def proc = command.execute()
proc.waitFor()
println proc.in.text

null