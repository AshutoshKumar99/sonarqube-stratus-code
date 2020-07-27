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
                        font-family: Arial sans-serif;
                    }
                    .error
                    {
                        background-color: #FF9693;
                    }
                    
                    .success
                    {
                        background-color: #B8FFAD;
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
                        font-size: 80%;
                    }
                    
                    tr
                    {
                        border-width: 1px 0 1px 0;
                        border-style: solid;
                        border-color: grey;
                    }
                </style>
            </head>
            <body>
                <h1>Test Results</h1>
                <p>Subversion revision: <xsl:apply-templates select="/results/svn-revision-number"/></p>
                <p>Test Number: <xsl:apply-templates select="/results/test-build-number"/></p>
                <table>
                    <thead>
                        <tr>
                            <th>Suite</th>
                            <th>Host</th>
                            <th>Browser</th>
                            <th>Test Case</th>
                            <th>Command</th>
                            <th>Status</th>
                            <th>Message</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="/results/suite"/>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="//command">
        <tr>
            <xsl:if test="./success != 'true'">
                <xsl:attribute name="class">error</xsl:attribute>
            </xsl:if>
            <xsl:if test="./success = 'true'">
                <xsl:attribute name="class">success</xsl:attribute>
            </xsl:if>
            <td><xsl:value-of select="./ancestor::suite/@suiteName"/></td>
            <td><xsl:value-of select="./ancestor::host/@hostName"/></td>
            <td><xsl:value-of select="./ancestor::browser/@browserName"/></td>
            <td><xsl:value-of select="./ancestor::testcase/@testName"/></td>
            <td><xsl:value-of select="./signature"/></td>
            <td>
                <xsl:if test="./success = 'true'">
                    Success
                </xsl:if>
                <xsl:if test="./success != 'true'">
                    Failure
                </xsl:if>
            </td>
            <td><xsl:value-of select="./message"/></td>
        </tr>
    </xsl:template>
    
</xsl:stylesheet>

