<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<c:set var="ctx" value="${pageContext.request.contextPath }" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导航</title>
</head>
<body>
    <a href="${ctx }/file/pre" target="_blank">上传文件</a>
    <a href="${ctx }/image/pre" target="_blank">上传图片</a>
    <a href="${ctx }/avatar/pre" target="_blank">上传头像</a>
    <a href="${ctx }/attachment/pre" target="_blank">上传附件</a>
    <a href="${ctx }/file/all" target="_blank">查看文件</a>
</body>
</html>