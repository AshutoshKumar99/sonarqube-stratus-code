<%@ page contentType="text/html;charset=UTF-8" language="java" import="com.pb.stratus.controller.configuration.TenantProfileManagerImpl" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/thirdPartyAPIInserter.tld" prefix="scriptInserter" %>
<%@page import="com.pb.stratus.controller.i18n.LocaleResolver"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/static/images/favicon.ico"></link>
    <script type="text/javascript">
        djConfig = 
        {
            <c:if test="${param['debug'] != null && param['debug'] == true}">
                isDebug: true,
            </c:if>
            locale: "<%=StringEscapeUtils.escapeJavaScript(LocaleResolver.getLocale().toString().replaceAll("_", "-").toLowerCase())%>",
            useCommentedJson: true
        }
    </script>
    <script type="text/javascript" djConfig="parseOnLoad:true" src="${pageContext.request.contextPath}/static/dojo/dojo.js"></script>
    <script type="text/javascript" djConfig="parseOnLoad:true" src="${pageContext.request.contextPath}/static/ria.js"></script>
    <script type="text/javascript">
    function loadResource()
    {
        require(["dojo/ready", "dojo/dom", "dojo/i18n", "dojo/i18n!connect/nls/resources"],
            function(ready, dom, i18n)
            {
                insufficientResources: null,
                ready(function()
                {
                    insufficientResources = i18n.getLocalization("connect", "resources");
                    dom.byId("StratusHomePage").innerHTML = insufficientResources.Logout;
                    dom.byId("mapUnavailable").innerHTML= insufficientResources.MapUnavailable;
                    dom.byId("insufficientPrivilege").innerHTML= insufficientResources.InsufficientPrivilegeMessage;
                });
            }
            );
     }
     </script>
     <title id="title"></title>
     </head>
     <body onload="loadResource()">
        <div id="header">
                <div id="logo-group">
                    <span id="logo">Spectrum&#153;Spatial Analyst</span>
                </div>
                <div class="pb-corporate-logo">Pitney Bowes</div>
        </div>
        <div style="margin-top:5cm; text-align:center; color:#000000;font-size:14px;">
            <h4 id="mapUnavailable"></h2>
            <h4 id="insufficientPrivilege"></h2>
            <a id="StratusHomePage" href="<%= TenantProfileManagerImpl.getRequestProfile(request).getConfiguration().getSloStartUrl() %>"></a>
        </div>
</body>
</html>