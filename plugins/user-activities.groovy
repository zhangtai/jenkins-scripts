import java.sql.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import groovy.sql.Sql
import org.h2.jdbcx.JdbcConnectionPool
import org.jenkinsci.plugins.useractivity.dao.H2UserActivityDao

ResultSet getCountResult() {
    def rootDir = Jenkins.get().getRootDir()
    def userActivityMonitoringPlugin = Jenkins.get().pluginManager.plugins.find { it.shortName == "user-activity-monitoring" }
    String startOfTodayUtc = Instant.now().truncatedTo(ChronoUnit.DAYS)
    def h2dao
    String sql

    if (userActivityMonitoringPlugin.version < "1.4") {
        h2dao = new H2UserActivityDao(rootDir)
        sql = "select count(1) from USERS where LAST_SEEN > '${startOfTodayUtc}'"
    } else {
        JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:${rootDir}/user-activity/user-activity", "sa", "sa");
        h2dao = new H2UserActivityDao(cp)
        sql = "select count(1) from USER_ACTIVITY where LAST_SEEN > '${startOfTodayUtc}'"
    }
    Connection cnn = h2dao.jdbcConnectionPool.getConnection()
    Statement stmt = cnn.createStatement()
    ResultSet rst = stmt.executeQuery(sql)
    return rst
}

ResultSet rst = getCountResult()
int a = 0
while (rst.next()) {
    a = rst.getObject(1)
}
return a
