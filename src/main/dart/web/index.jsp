<%@ page import="org.apache.shiro.SecurityUtils" %><%
    String username = (String)SecurityUtils.getSubject().getPrincipal();
    Cookie cookie = new Cookie("username", username);
    //cookie.setPath(request.getContextPath());
    cookie.setPath("/"); // for debug
    response.addCookie(cookie);
%>
<%@include  file="index.html" %>