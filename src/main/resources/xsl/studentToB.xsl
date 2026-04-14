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
            <学号><xsl:value-of select="id"/></学号>
            <姓名><xsl:value-of select="name"/></姓名>
            <xsl:if test="normalize-space(sex) != ''">
                <性别><xsl:value-of select="sex"/></性别>
            </xsl:if>
            <xsl:if test="normalize-space(major) != ''">
                <专业><xsl:value-of select="major"/></专业>
            </xsl:if>
        </student>
    </xsl:template>
</xsl:stylesheet>
