<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/thirdPartyAPIInserter.tld" prefix="scriptInserter" %>
<%@page import="com.pb.stratus.controller.i18n.LocaleResolver"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>
<html>
<head>
    <style type="text/css">
        .hidden
        {
            left: -9999px;
            margin: 0;
            position: absolute;
        }

        #companyLogo,
        #productLogo
        {
            background-repeat: no-repeat;
        }

        #companyLogo
        {
            background-image: url(../theme/images/banner/PitneyBowesBlue.gif);
            width:209px;
            height:51px;
            float:left;
            position:absolute;
            top:16px;
            left:15px;
        }

        #productLogo
        {
            background-image: url(../theme/images/banner/productLogo.gif);
            width:260px;
            height:76px;
            position:absolute;
            top:4px;
            right:15px;
        }
    </style>
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
        function loadResource() {
            require(["dojo/ready", "dojo/dom", "dojo/i18n", "dojo/i18n!connect/nls/resources"],
                    function(ready, dom, i18n){
                        logOutResources: null,
                                ready(function()
                                {
                                    logOutResources = i18n.getLocalization("connect", "resources");

                                    dom.byId("successfullySignedOut").innerHTML = logOutResources.successfullySignedOut;
                                    dom.byId("StratusHomePage").innerHTML = logOutResources.StratusHomePage;
                                    dom.byId("SignIn").innerHTML = logOutResources.SignIn;
                                });
                    });
        }

        function appendQueryString(){
            var stratusHomePage = dojo.byId('StratusHomePage').href;
            dojo.byId('StratusHomePage').href =
                    stratusHomePage.concat(location.search);
        }

        function onLoad(){
            loadResource();
            appendQueryString();
        }
    </script>

    <title id="SignIn"></title>

</head>
<body onload="onLoad()">
<div id="companyLogo"><h1 class="hidden">Pitney Bowes Software</h1></div>
<div id="productLogo"></div>
<div style="margin-top:5cm; text-align:center; color:#000000; font-size:18px;" id="insufficientMsg">
    <h1  id="successfullySignedOut" style='margin-top:5cm; text-align:center; color:#000000; font-size:18px;'></h1>
    <a id="StratusHomePage" href="./"></a>
</div>
</body>
</html>