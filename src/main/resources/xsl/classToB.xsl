<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <Classes>
            <xsl:apply-templates select="classes/class"/>
        </Classes>
    </xsl:template>

    <xsl:template match="class">
        <class>
            <编号><xsl:value-of select="id"/></编号>
            <名称><xsl:value-of select="name"/></名称>
            <xsl:if test="normalize-space(time) != ''">
                <课时><xsl:value-of select="time"/></课时>
            </xsl:if>
            <学分><xsl:value-of select="score"/></学分>
            <老师><xsl:value-of select="teacher"/></老师>
            <地点><xsl:value-of select="location"/></地点>
            <xsl:if test="normalize-space(share) != ''">
                <共享><xsl:value-of select="share"/></共享>
            </xsl:if>
        </class>
    </xsl:template>
</xsl:stylesheet>
