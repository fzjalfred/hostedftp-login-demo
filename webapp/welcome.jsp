<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="javax.servlet.http.*, javax.servlet.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 2rem;
}

.card {
	max-width: 560px;
	padding: 1.5rem;
	border: 1px solid #ddd;
	border-radius: 12px;
}
</style>
</head>
<body>
	<%
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    String username = (String) session.getAttribute("username");
    String fullName = (String) session.getAttribute("full_name");
%>
	<div class="card">
		<h2>
			Welcome,
			<%= (fullName != null && !fullName.isEmpty()) ? fullName : username %>!
		</h2>
		<p>You have successfully logged in via HTTPS. Your profile data
			was fetched from MySQL.</p>
		<p>
			<b>Username:</b>
			<%= username %></p>
		<p>
			<b>Full name:</b>
			<%= (fullName != null) ? fullName : "(none set)" %></p>
		<p>
			<a href="<%= request.getContextPath() %>/index.jsp">Logout</a> (just
			go back to the login page for this demo)
		</p>
	</div>
</body>
</html>