def nowDate = new Date()
def now = nowDate.getTime()
def q = Jenkins.get().queue

// q.clear()

def assignees = [:]
q.items.each { 
  assignees[it.assignedLabel] = assignees[it.assignedLabel] ? assignees[it.assignedLabel] + 1 : 1
}
println "Queue size: ${items.size()}\n"
println "Group by nodes"
assignees.each { k, v -> println "    ${k}: ${v}" }


def waitingCount = 0
q.items.each {
  def waitingHours = (now - it.inQueueSince) / 1000 / 60 / 60
  if (waitingHours > 24) {
    waitingCount += 1
    q.cancel(it.task)
  }
}
println "${waitingCount} queue items over 24 hours cleared"

// .inQueueSince
return
