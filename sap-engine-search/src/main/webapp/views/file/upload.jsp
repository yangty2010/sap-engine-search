<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<c:set var="ctx" value="${pageContext.request.contextPath }" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件上传</title>
</head>
<body>

    <h4>文件上传</h4>
    <form action="${ctx }/image/upload" method="post" enctype="multipart/form-data">
                        请选择文件：<br> 
        <input name="image" type="file"><br>
        <input name="thumbnails" type="hidden" value="100,200"/>
        <input name="scaleStrategy" type="hidden" value="rect"/>
        <input type="submit" value="上传文件">
    </form>

</body>
</html>