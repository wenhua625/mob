<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>跳转中...</title>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<link rel="stylesheet" type="text/css" href="css/public.css" />
</head>

<body>
	<div class="box">
		<h1>跳转中...</h1>
		<form id="subForm" action="<%=request.getAttribute("reqUrl") %>" method="post">
			<%
			Map reqParams = (HashMap) request.getAttribute("reqParams");
			for (Iterator iter = reqParams.keySet().iterator(); iter.hasNext();) {
				String paramName = (String) iter.next();
				String paramValue = (String) reqParams.get(paramName);
			%>
			<input type="hidden" name="<%=paramName %>" value="<%=URLEncoder.encode(paramValue, "GBK") %>" />
			<%
			}
			%>
		</form>
	</div>
</body>
<script type="text/javascript">
	document.getElementById("subForm").submit();
</script>