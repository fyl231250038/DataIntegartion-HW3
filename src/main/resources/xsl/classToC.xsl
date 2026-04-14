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
            <Cno><xsl:value-of select="id"/></Cno>
            <Cnm><xsl:value-of select="name"/></Cnm>
            <xsl:if test="normalize-space(time) != ''">
                <Ctm><xsl:value-of select="time"/></Ctm>
            </xsl:if>
            <Cpt><xsl:value-of select="score"/></Cpt>
            <Tec><xsl:value-of select="teacher"/></Tec>
            <Pla><xsl:value-of select="location"/></Pla>
            <xsl:if test="normalize-space(share) != ''">
                <Share><xsl:value-of select="share"/></Share>
            </xsl:if>
        </class>
    </xsl:template>
</xsl:stylesheet>
