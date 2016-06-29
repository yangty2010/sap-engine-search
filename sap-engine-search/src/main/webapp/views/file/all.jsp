<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>所有上传的文件</title>
</head>
<body>
    <table>
        <thead>
            <tr>
                <td>文件名称</td>
                <td>大小(Bytes)</td>
                <td>上传时间</td>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="file" items="${files}">
                <tr>
                    <td><a href="${ctx }/file/view/${file.id}" target="_blank">${file.name }</a></td>
                    <td>${file.sizeInBytes }</td>
                    <td>${file.uploadTime }</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>