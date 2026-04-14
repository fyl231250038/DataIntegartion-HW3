<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <classes>
            <xsl:apply-templates select="Classes/class | classes/class"/>
        </classes>
    </xsl:template>

    <xsl:template match="class">
        <class>
            <id><xsl:value-of select="课程编号 | 编号 | Cno | id"/></id>
            <name><xsl:value-of select="课程名称 | 名称 | Cnm | name"/></name>
            <xsl:if test="normalize-space(课时 | Ctm | time) != ''">
                <time><xsl:value-of select="课时 | Ctm | time"/></time>
            </xsl:if>
            <score><xsl:value-of select="学分 | Cpt | score"/></score>
            <teacher><xsl:value-of select="授课老师 | 老师 | Tec | teacher"/></teacher>
            <location><xsl:value-of select="授课地点 | 地点 | Pla | location"/></location>
            <xsl:if test="normalize-space(共享 | Share | share) != ''">
                <share><xsl:value-of select="共享 | Share | share"/></share>
            </xsl:if>
        </class>
    </xsl:template>
</xsl:stylesheet>
