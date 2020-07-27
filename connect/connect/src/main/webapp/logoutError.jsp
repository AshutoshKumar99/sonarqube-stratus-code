<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/thirdPartyAPIInserter.tld" prefix="scriptInserter" %>
<%@page import="com.pb.stratus.controller.i18n.LocaleResolver"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>
<html>
<head>
    <script type="text/javascript">
        dojoConfig = 
        {
            <c:if test="${param['debug'] != null && param['debug'] == true}">
                isDebug: true,
            </c:if>
            locale: "<%=StringEscapeUtils.escapeJavaScript(LocaleResolver.getLocale().toString().replaceAll("_", "-").toLowerCase())%>",
            useCommentedJson: true
        }
     </script>
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/connect.css"></link>
     <script type="text/javascript" dojoConfig="parseOnLoad:true" src="${pageContext.request.contextPath}/static/dojo/dojo.js"></script> 
     <script type="text/javascript" djConfig="parseOnLoad:true" src="${pageContext.request.contextPath}/static/ria.js"></script>	 
     <script type="text/javascript">	 	     
     function loadResource() {
            require(["dojo/ready", "dojo/dom", "dojo/i18n", "dojo/i18n!connect/nls/resources"],					
                    function(ready, dom, i18n){
					    logOutResources: null,
                        ready(function()
                        {
                            logOutResources = i18n.getLocalization("connect", "resources");
							dom.byId("title").innerHTML= logOutResources.Logout;
							dom.byId("errorMsg").innerHTML= logOutResources.LogoutErrorMsg;
							dom.byId("tryAgain").innerHTML= logOutResources.LogoutTryAgain;
                        });
                    });
     }
     </script>
     <title id="title"></title>
</head>
<body onload="loadResource()">
<div id="companyLogo">
<h1 class="hidden">Pitney Bowes Software</h1>
</div>
<div id="productLogo"></div>
<div style="margin-top:5cm; text-align:center; color:#000000; font-size:18px;" id="insufficientMsg">
    <h3 id="errorMsg"> </h2>
    <a href="${pageContext.request.contextPath}/${tenant}"><h3 id="tryAgain"></h2></a>
</div>
</body>
</html>