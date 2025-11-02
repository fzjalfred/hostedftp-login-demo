<%@ page import="java.sql.*" %>
<%@ page import="com.hostedftp.auth.DB" %>
<pre>
<%
try {
  Class.forName("com.mysql.cj.jdbc.Driver");
  out.println("driver ok: " + Thread.currentThread().getContextClassLoader().getResource("com/mysql/cj/jdbc/Driver.class"));
  try (Connection c = DB.conn();
       Statement s = c.createStatement();
       ResultSet rs = s.executeQuery("SELECT @@hostname, @@version")) {
    out.println("OK: JDBC connected");
    if (rs.next()) out.println("host=" + rs.getString(1) + ", ver=" + rs.getString(2));
  }
} catch (Throwable t) {
  t.printStackTrace(new java.io.PrintWriter(out));
}
%>
</pre>
