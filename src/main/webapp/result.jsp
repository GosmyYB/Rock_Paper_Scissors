<%@ page errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Game result</title>
</head>
<body>
<p>
${currentMatch}
</p>

<!-- Get the result from session -->
<p>
${records}
</p>
<a href="match.html">try again</a>
<form action="dbOperate" method="post">
	<input type="hidden" name="type" value="insert"/>
	<input type="submit" value="Exit and Save Records"/>
</form>
<form action="dbOperate" method="post">
	<input type="hidden" name="type" value="delete"/>
	<input type="submit" value="Delete Records"/>
</form>
</body>
</html>