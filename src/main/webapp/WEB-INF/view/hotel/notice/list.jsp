<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	String cp=request.getContextPath();
%>


<h1 id="page_tit">숙박관리</h1>
<!-- 현재 페이지 정보 -->
<div class="page_info">
	<h2>숙박관리</h2>
	<p><strong>공지사항</strong></p>
</div>

<div class="hotel-body" style="width: 100%;">
		<table class="table td_bor_no" style="width: 100%;">
			<thead>
				<tr>
					<th width="100">번호</th>
					<th width="50">제목</th>
					<th width="70">작성자</th>
					<th width="100" colspan="2">날짜</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		<div class="btn_wrap view_btn">
				<button class="button btn" type="button" 
					onclick="location.href='<%=cp%>/hotel/notice/insertNotice';">공지사항 등록</button>
			</div>
	</div>