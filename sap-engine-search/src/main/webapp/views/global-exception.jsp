<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.lang.Exception"%>
<%
	Exception e = (Exception) request.getAttribute("exception");
	String errMsg = e.getMessage();
	e.printStackTrace();
%>
<div class="KS-wrap">
	<div class="row show-grid">
		<div class="span8 offset4">
			<section>
				<div style="border:solid 1px #DDD;margin:50px;padding:30px;">
					<h3>糟糕！程序出现异常了</h3>
					<hr/>
					
					<span>我很忙，</span><a href="javascript:history.back(-1);">返回上页</a>
					<br/><br/>
					<span>帮你们一下，mail至</span><a href="mailto:ohwyaa@neusoft.com?subject=report exception&body=<%=errMsg%>">ohwyaa@neusoft.com</a>
				</div>
			</section>
		</div>
	</div>
</div>
