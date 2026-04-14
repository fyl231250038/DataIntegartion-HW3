<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <Choices>
            <xsl:apply-templates select="choices/choice"/>
        </Choices>
    </xsl:template>

    <xsl:template match="choice">
        <choice>
            <Cno><xsl:value-of select="cid"/></Cno>
            <Sno><xsl:value-of select="sid"/></Sno>
            <xsl:if test="normalize-space(score) != ''">
                <Grd><xsl:value-of select="score"/></Grd>
            </xsl:if>
        </choice>
    </xsl:template>
</xsl:stylesheet>
