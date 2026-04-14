<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <students>
            <xsl:apply-templates select="Students/student | students/student"/>
        </students>
    </xsl:template>

    <xsl:template match="student">
        <student>
            <id><xsl:value-of select="学号 | Sno | id"/></id>
            <name><xsl:value-of select="姓名 | Snm | name"/></name>
            <xsl:if test="normalize-space(性别 | Sex | sex) != ''">
                <sex><xsl:value-of select="性别 | Sex | sex"/></sex>
            </xsl:if>
            <xsl:if test="normalize-space(院系 | 专业 | Sde | major) != ''">
                <major><xsl:value-of select="院系 | 专业 | Sde | major"/></major>
            </xsl:if>
        </student>
    </xsl:template>
</xsl:stylesheet>
