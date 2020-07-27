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
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/static/images/favicon.ico"></link>
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
            require(["dojo/ready", "dojo/dom","stratus/configuration/BrowserFingerPrint", "dojo/i18n", "dojo/i18n!connect/nls/resources"
                        ],
                    function(ready, dom,BrowserFingerPrint, i18n){
                        logOutResources: null,
                                ready(function()
                                {
                                    var browserFingerPrint = new BrowserFingerPrint();
                                    var uniqueBrowerIdentifier = browserFingerPrint._getUniqueBrowserIdentifier();
                                    uniqueBrowserFingerPrint
                                    document.getElementById("uniqueBrowserFingerPrint").value = uniqueBrowerIdentifier;
                                    console.log(document.getElementById("uniqueBrowserFingerPrint").value);
                                });
                    });
        }

        function registerAppLinkingUser() {
            require(["dojo/ready", "dojo/dom","stratus/configuration/BrowserFingerPrint",
                        "dojo/i18n", "stratus/RiaHub","stratus/async/SingleDeferred",
                        "dojo/_base/lang",
                        "dojo/i18n!connect/nls/resources"
            ],
            function(ready, dom,BrowserFingerPrint, i18n,RiaHub,SingleDeferred,lang)
            {
                ready(function()
                {

                    var controllerAction = "${pageContext.request.contextPath}/${tenantName}/registerApplicationLinkingUser";
                    var params = {
                        timeout:5000,
//                        action:controllerAction,
                        url:"${pageContext.request.contextPath}/analyst/controller/registerApplicationLinkingUser",
                        method:"POST"
                    };
                    var browserFingerPrint = new BrowserFingerPrint();
                    var uniqueBrowerIdentifier = browserFingerPrint._getUniqueBrowserIdentifier();

                    var parameters = {"uniqueBrowserFingerPrint":uniqueBrowerIdentifier};

                    params["parameters"] = parameters;
                    var deferred = new SingleDeferred();
                    params.successCallback =  registrationInfo;
                    params.errorCallback = lang.hitch(this,
                            "deferredErrorCallback", deferred);
                    RiaHub.getInstance().invokeControllerAction(params) });
                var deferredErrorCallback = function(deferred, error) {
                    if (error.xhr)
                    {
                        deferred.fail(error.xhr.statusText);
                    }
                    else
                    {
                        deferred.fail(error);
                    }
                };
                var registrationInfo = function(result){
                    document.getElementById("registrationResultContainer").
                            style.display="block";
                    console.log(result);
                    if(document.getElementById("registrationResultDynamic"))
                    {
                        document.getElementById("registrationResult").
                                removeChild(document.getElementById("registrationResultDynamic"));
                    }
                    var container = document.createElement('div');
                    container.setAttribute("id","registrationResultDynamic");
                    var table = document.createElement('table');
                    console.log(document.getElementById('appLinkingConfigInfoTable'));
                    var table = '<table id="appLinkingConfigInfoTable">' +
                            '<tr><td style="align:left width:\\"60%\\"">AppLinkingEnabled</td><td style="align:right">' +
                            result.appLinkingEnabled +
                            '</td></tr>' +
                            '<tr><td style="align:left">AppLinkingHostIP</td><td style="align:right">' +
                            result.appLinkingHostIP +
                            '</td></tr>' +
                            '<tr><td style="align:left">AppLinkingHostPort</td><td style="align:right">' +
                            result.appLinkingHostPort +
                            '</td></tr>' +
                            '<tr><td style="align:left">RegistrationId</td><td style="align:right">' +
                            result.userRegistrationId +
                            '</td></tr>'+
                            '</table>'
                    container.innerHTML = table;

                    document.getElementById("registrationResult").appendChild(container);

                }
            }


            );
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
        <span id="logo">Spectrum<sup style="font-family: sans-serif;font-size: 10px;"> TM </sup>Spatial Analyst</span>
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
                    <form id="appLinkingRegistrationForm" action="controller/registerApplicationLinkingUser" method="post">

                        <div class="form-group">
                            <input id="uniqueBrowserFingerPrint" class="form-control" name="uniqueBrowserFingerPrint" type="hidden" value="abcd"
                                   onchange="hideErrorMsg();"/>
                        </div>
                        <input id="submitId" type="button" onclick="registerAppLinkingUser()" class="btn btn-primary" value="Click to Register"/>
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
    <div id="registrationResultContainer" class="row-fluid" style="display:none">
        <div class="col-xs-6 col-xs-offset-3">
            <div class="panel panel-default login">
                <div id="registrationResult" class="panel-body">
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