<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 2rem;
}

.card {
	max-width: 420px;
	padding: 1.5rem;
	border: 1px solid #ddd;
	border-radius: 12px;
}

.row {
	margin-bottom: 0.75rem;
	display: flex;
	flex-direction: column;
}

input {
	padding: 0.5rem;
	font-size: 1rem;
}

button {
	padding: 0.6rem 1rem;
	font-size: 1rem;
	cursor: pointer;
}

.err {
	color: #b00020;
	margin-top: 0.5rem;
}

.hint {
	color: #555;
	font-size: 0.9rem;
	margin-top: 0.75rem;
}

.ssl {
	margin-top: 1rem;
	font-size: 0.85rem;
	color: #333;
}
</style>
</head>
<body>
	<div class="card">
		<h2>Login</h2>
		<form method="post" action="login">
			<div class="row">
				<label>Username</label> <input type="text" name="username"
					placeholder="e.g. hostedftp" required />
			</div>
			<div class="row">
				<label>Password</label> <input type="password" name="password"
					placeholder="Password" required />
			</div>
			<button type="submit">Sign in</button>
			<% String err = (String) request.getAttribute("error");
           if (err != null) { %>
			<div class="err"><%= err %></div>
			<% } %>
		</form>
		<div class="ssl">This demo expects HTTPS (Tomcat SSL enabled).</div>
	</div>
</body>
</html>