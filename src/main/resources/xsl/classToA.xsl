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
            <课程编号><xsl:value-of select="id"/></课程编号>
            <课程名称><xsl:value-of select="name"/></课程名称>
            <学分><xsl:value-of select="score"/></学分>
            <授课老师><xsl:value-of select="teacher"/></授课老师>
            <授课地点><xsl:value-of select="location"/></授课地点>
            <xsl:if test="normalize-space(share) != ''">
                <共享><xsl:value-of select="share"/></共享>
            </xsl:if>
        </class>
    </xsl:template>
</xsl:stylesheet>
