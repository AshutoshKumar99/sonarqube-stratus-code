<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<html>
			<head>
				<title>Stratus Connect Acceptance Test Results</title>
				<style type="text/css">
                    body
                    {
                        font-family: Arial, sans-serif;
                    }
                    .error
                    {
                        background-color: #FF9693;
                    }
                    
                    .success
                    {
                        background-color: #B8FFAD;
                    }

                    .echo
                    {
                        background-color: #FFFFAD;
                    }
                    
                    .errorText
                    {
                       color: #cc0000;
                    }
                    
                    .successText
                    {
                       color: #004400;
                    }                    
                    
                    thead
                    {
                        text-align: left;
                        font-weight: normal;
                    }
                    
                    table
                    {
                        border-collapse:collapse;
                        width: 100%;
                        font-size: 10pt;
                    }
                    
                    tr
                    {
                        border-width: 1px 0 1px 0;
                        border-style: solid;
                        border-color: grey;
                    }
                    
                   .message
                    {	
						text-align:right;
                    }
                    .testCaseSection
                    {
                    
                    }
                    li
                    {
                    font-weight:bold;
                    }
                </style>
			</head>
			<body>
				<h1>Test Results</h1>
				<p>Subversion revision: <xsl:apply-templates select="/results/svn-revision-number"/>
				</p>
				<p>Test Number: <xsl:apply-templates select="/results/test-build-number"/>
				</p>
				<p>Connect Host: <xsl:value-of select="//host[1]/@hostName"/>
				</p>
				<h2>Summary</h2>
				<table border="1">
					<th>Suite Name</th>
					<th>Browser</th>
					<th>Test Case Name</th>
					<xsl:apply-templates select="/results/suite" mode="summary"/>
				</table>
				<h2>Detailed Results</h2>
				<xsl:apply-templates select="/results/suite" mode="detailed"/>
			</body>
		</html>
	</xsl:template>
	<!-- Summary Information -->
	<xsl:template match="suite" mode="summary">
		<xsl:if test="@pass!= 'true'">
			<tr>
				<xsl:attribute name="class">error</xsl:attribute>
				<td>
                    <xsl:apply-templates select="@suiteName"/>
				</td>
				<td> </td>
				<td> </td>
			</tr>
			<xsl:apply-templates select="host/browser" mode="summary"/>
		</xsl:if>
		<xsl:if test="@pass = 'true'">
			<tr>
				<xsl:attribute name="class">success</xsl:attribute>
				<td colspan="3">
                    <xsl:apply-templates select="@suiteName"/>
				</td>
			
			</tr>
		</xsl:if>
	</xsl:template>
	
    <!-- 
        Renders a short form of the fully-qualified suite name
    -->
	<xsl:template match="//suite/@suiteName">
        <xsl:value-of select="substring(., string-length(/results/base-dir) + 2)"/>
	</xsl:template>
	
	<!--<xsl:template match="//browser" mode="summary">
		<xsl:if test="@pass!= 'true'">
			<tr>
				<td> </td>
				<td>
					<xsl:attribute name="class">errorText</xsl:attribute>
					<xsl:value-of select="@browserName"/> (<xsl:value-of select="@fail-count"/> Failed Test cases)
				</td>
				<td> </td>
			</tr>
			<xsl:apply-templates select="testcase" mode="summary"/>
		</xsl:if>
	</xsl:template>-->
	<xsl:template match="//testcase" mode="summary">
		<xsl:if test="@pass!= 'true'">
			<tr>
				<xsl:attribute name="class">error</xsl:attribute>
				<td></td>				
				<td>
					<xsl:value-of select="./ancestor::browser/@browserName"/>
				</td>
				<td>
					<xsl:variable name="testname" select="substring-before(substring-after(@testName,'('),')')"/>
					<a >
						<xsl:attribute name="href">#<xsl:value-of select="$testname"/>_<xsl:value-of select="./ancestor::browser/@browserName"/></xsl:attribute>
						<xsl:value-of select="$testname"/>
					</a>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	<!-- Detailed Information -->
	<xsl:template match="suite" mode="detailed">
		<h2>
			<xsl:if test="@pass!= 'true'">
				<xsl:value-of select="substring(@suiteName, string-length(/results/base-dir) + 2)"/>
				<xsl:apply-templates select="host/browser/testcase" mode="detailed"/>
			</xsl:if>
		</h2>
	</xsl:template>
	<xsl:template match="//testcase" mode="detailed">
		<p class="testCaseSection">
		<xsl:if test="@pass!= 'true'">
			<h4>
				<xsl:variable name="testname" select="substring-before(substring-after(@testName,'('),')')"/>
				<a >
					<xsl:attribute name="name"><xsl:value-of select="$testname"/>_<xsl:value-of select="./ancestor::browser/@browserName"/></xsl:attribute>
				</a>
				<xsl:value-of select="$testname"/> - <xsl:value-of select="./ancestor::browser/@browserName"/>
			</h4>
			<table>
				<thead>
					<tr>
						<th>Command</th>
						<th class="message">Message</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="command" mode="detailed"/>
				</tbody>
			</table>
			</xsl:if>
		</p>
	</xsl:template>
	<xsl:template match="command" mode="detailed">
		<tr>
			<xsl:if test="./success != 'true'">
				<xsl:attribute name="class">error</xsl:attribute>
			</xsl:if>
			<xsl:if test="./success = 'true'">
				<xsl:attribute name="class">success</xsl:attribute>
			</xsl:if>
			<xsl:if test="starts-with(./signature,'echo ')">
				<xsl:attribute name="class">echo</xsl:attribute>
			</xsl:if>
			<td>
				<xsl:value-of select="./signature"/>
			</td>
			<td class="message">
			    <xsl:if test="starts-with(./signature,'echo ')">
				    <xsl:attribute name="style">font-weight:bold</xsl:attribute>
                </xsl:if>
				<xsl:value-of select="./message"/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>

