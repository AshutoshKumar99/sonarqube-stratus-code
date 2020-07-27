<%@ page contentType="text/html;charset=UTF-8" language="java" import="com.pb.stratus.controller.configuration.TenantProfileManagerImpl" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/thirdPartyAPIInserter.tld" prefix="scriptInserter" %>
<%@page import="com.pb.stratus.controller.i18n.LocaleResolver"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>


<html>
    <head>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/connect.css"></link>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/dojox/grid/resources/Grid.css"></link>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/dojox/grid/resources/tundraGrid.css"></link>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/${tenantName}/controller/theme/css/custom.css"></link>
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
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/dojo/dojo.js" data-dojo-config="isDebug: true, async: true, parseOnLoad: true" ></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/static/dijit/dijit-custom.js"></script>
        <c:set var="tenantName" value="${tenant}"/>
        <script type="text/javascript" src="${pageContext.request.contextPath}/${tenantName}/ria.js"></script>
        <script type="text/javascript">
        contextPath = "${pageContext.request.contextPath}";
        </script>
        <script type="text/javascript">	 	     
     function loadResource() {
            require(["dojo/ready", "dojo/dom", "dojo/i18n", "dijit/layout/ContentPane", "dojo/i18n!connect/nls/resources"],					
                    function(ready, dom, i18n){
						insufficientResources: null,
                        ready(function()
                        {
                            insufficientResources = i18n.getLocalization("connect", "resources");
							dom.byId("title").innerHTML= insufficientResources.InsufficientPrivilege;
							dom.byId("mapUnavailable").innerHTML= insufficientResources.MapUnavailable;
							dom.byId("insufficientPrivilege").innerHTML= insufficientResources.InsufficientPrivilegeMessage;
							dom.byId("logout").innerHTML= insufficientResources.Logout;
                        });
                    });
     }
     </script>
		
        
    <title id="title">resources</title>
    </head>
    <body class="tundra" onload="loadResource()">
    <div id="companyLogo"><h1 class="hidden">Pitney Bowes Software</h1></div>
    <div id="productLogo"></div>
    <div style="background-color: #F6F6F6;height: 2.2em;margin-top: 83px;overflow: hidden;" data-dojo-type="dijit.layout.ContentPane">
        <div id="AuthControl"></div>
    </div>
    <div style="margin-top:5cm; text-align:center; color:#000000;" id="insufficientMsg">
         <h4 id="mapUnavailable"></h2>
         <h4 id="insufficientPrivilege"></h2>
         <a href="<%= TenantProfileManagerImpl.getRequestProfile(request).getConfiguration().getSloStartUrl() %>"> <h4 id="logout"></h2></a>
    </div>
    </body>
</html>