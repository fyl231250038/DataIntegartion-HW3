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
            <课程编号><xsl:value-of select="cid"/></课程编号>
            <学生编号><xsl:value-of select="sid"/></学生编号>
            <xsl:if test="normalize-space(score) != ''">
                <成绩><xsl:value-of select="score"/></成绩>
            </xsl:if>
        </choice>
    </xsl:template>
</xsl:stylesheet>
