<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <Students>
            <xsl:apply-templates select="students/student"/>
        </Students>
    </xsl:template>

    <xsl:template match="student">
        <student>
            <Sno><xsl:value-of select="id"/></Sno>
            <Snm><xsl:value-of select="name"/></Snm>
            <xsl:if test="normalize-space(sex) != ''">
                <Sex><xsl:value-of select="sex"/></Sex>
            </xsl:if>
            <xsl:if test="normalize-space(major) != ''">
                <Sde><xsl:value-of select="major"/></Sde>
            </xsl:if>
        </student>
    </xsl:template>
</xsl:stylesheet>
