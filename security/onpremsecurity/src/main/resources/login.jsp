<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/thirdPartyAPIInserter.tld" prefix="scriptInserter" %>
<%@page import="com.pb.stratus.controller.i18n.LocaleResolver"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>
<html>
<!DOCTYPE html>
<html class="no-js">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bootstrap.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
<link rel="shortcut icon" href="${pageContext.request.contextPath}/static/images/favicon.ico"/>
<!-- GOOGLE FONT -->
<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,700italic,300,400,700">
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
<script type="text/javascript">
     function loadResource() {
            require(["dojo/ready", "dojo/dom", "dojo/i18n", "dojo/i18n!connect/nls/resources"],
                    function(ready, dom, i18n){
                        logOutResources: null,
                        ready(function()
                        {
                            logOutResources = i18n.getLocalization("connect", "resources");
                            if (dom.byId("wrongUsernamePasswordMsg"))
                            { 
                                dom.byId("wrongUsernamePasswordMsg").innerHTML = logOutResources.wrongUsernamePasswordMsg; 
                            }
                            
                            dom.byId("username").innerHTML = logOutResources.username; 
                            dom.byId("password").innerHTML = logOutResources.password;
                            dom.byId("login").innerHTML = logOutResources.login;
                            dom.byId("enterPasswordId").innerHTML = logOutResources.enterPassword;
                            dom.byId("enterUserNameId").innerHTML = logOutResources.enterUserName;
                            dom.byId("submitId").value = logOutResources.submit;
                        });
                    });
     }
     function validateRequired(field) {
        with (field) {
            if (value == null || value == "") {
                return false;
            }
            else {
                return true;
            }
        }
    }

    function validateLoginForm(thisform) {
        with (thisform) {
            if (validateRequired(j_username) == false)
            {
                j_username.focus();
                if (document.getElementById("errorMsg1"))
                {
                    document.getElementById("errorMsg1").innerHTML = "";
                }
                document.getElementById("errorMsg").innerHTML = document.getElementById("enterUserNameId").innerHTML;
                document.getElementById("errorMsg").style.display = "block";
                return false;
            }

            if (validateRequired(j_password) == false)
            {
                j_password.focus();
                if (document.getElementById("errorMsg1"))
                {
                    document.getElementById("errorMsg1").innerHTML = "";
                }
                document.getElementById("errorMsg").innerHTML = document.getElementById("enterPasswordId").innerHTML;
                document.getElementById("errorMsg").style.display = "block";
                return false;
            }
            return true;
        }
    }

</script>

</head>
<body onload="loadResource()">
<% String lastUserName = (String) session.getAttribute("SPRING_SECURITY_LAST_USERNAME");%>
<div id="header">
    <div id="logo-group">
        <span id="logo">Spectrum Spatial<sup style="font-family: sans-serif;font-size: 10px;"> TM </sup> Analyst</span>
    </div>
    <div class="pb-corporate-logo">Pitney Bowes</div>
</div>


<!-- MAIN PANEL -->
<div class="container">
    <div class="row-fluid">
        <div class="col-xs-6 col-xs-offset-3">
            <div class="panel panel-default login">
                <div id="login" class="panel-heading"></div>
                <div class="panel-body">
                    <form id="onPremLogin" action="j_spring_security_check" method="post" onsubmit="return validateLoginForm(this)">
                        <div class="form-group">
                            <label id="username" for="j_username"></label>
                            <input id="j_username" value="<%= (lastUserName == null ? "": lastUserName) %>"
                                   class="form-control" name="j_username" type="text" onchange="hideErrorMsg();"/>
                        </div>
                        <div class="form-group">
                            <label id="password" for="j_password"></label>
                            <input id="j_password" class="form-control" name="j_password" type="password" value=""
                                   onchange="hideErrorMsg();"/>
                        </div>
                        <input id="submitId" type="submit" class="btn btn-primary" value=""/>
                        <br/><br/>
                        <div id="errorMsg" style="display:none;color:red;font-weight:bold;font-family:Open Sans,Arial,Helvetica,Sans-Serif !important"></div>
                        <div id="errorMsg1"><c:if test="${param['login_error'] == 1}">
                            <div id="wrongUsernamePasswordMsg" style="color:#FF1C19;font-weight:bold;font-family:Open Sans,Arial,Helvetica,Sans-Serif !important"></div>
                        </c:if></div>
                        <div id="enterPasswordId" style="display:none;"></div>
                        <div id="enterUserNameId" style="display:none;"></div>
                    </form>
                </div>
            </div>
        </div>
    </div>


</div>

<!-- END MAIN PANEL -->

<div class="pb-footer pb-footer-fixed">
    <span class="pb-footer-logo">&copy; Pitney Bowes Inc. All rights reserved.</span>
</div>
</body></html>