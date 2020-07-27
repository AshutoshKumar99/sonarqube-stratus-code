<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">
    <head>
        <title><decorator:title default="RIA Test Harness Page"/></title>
        <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
        <script type="text/javascript">djConfig = {parseOnLoad: true};</script>
        <script type="text/javascript" src="${contextPath}/ria.js"></script>
        <decorator:head/>
    </head>
    <body>
        <decorator:body/>
    </body>
</html>