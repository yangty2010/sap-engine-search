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
    <form action="${ctx }/avatar/upload" method="post" enctype="multipart/form-data">
                        请选择文件：<br> 
        <input name="image" type="file"><br>
        filekey:
        <input name="fileKey"  type="text">
        <input name="thumbnails" type="hidden" value="100,200"/>
        <input name="scaleStrategy" type="hidden" value="rect"/>
        <input type="submit" value="上传文件">
    </form>
    <form action="${ctx }/avatar/uploadandrect" method="post" enctype="multipart/form-data">
                        请选择文件：<br> 
        <input name="image" type="file"><br>
        filekey:
        <input name="fileKey"  type="text">
        <input name="thumbnails" type="hidden" value="100,200"/>
        <input name="scaleStrategy" type="hidden" value="rect"/>
        <input type="submit" value="上传并切割文件">
    </form>
    <h3>切割图片</h3>
     <form action="${ctx }/avatar/rect" method="get" >
        avatarId: <input name="avatarId"  type="text"></br>
        x: <input name="x"  type="text"></br>
        y: <input name="y"  type="text"></br>
        width: <input name="width"  type="text"></br>
        height: <input name="height"  type="text"></br>
        <input type="submit" value="切割文件">
    </form>
</body>
</html>