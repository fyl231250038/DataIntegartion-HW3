<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <choices>
            <xsl:apply-templates select="Choices/choice | choices/choice"/>
        </choices>
    </xsl:template>

    <xsl:template match="choice">
        <choice>
            <sid><xsl:value-of select="学生编号 | 学号 | Sno | sid"/></sid>
            <cid><xsl:value-of select="课程编号 | Cno | cid"/></cid>
            <xsl:if test="normalize-space(成绩 | 得分 | Grd | score) != ''">
                <score><xsl:value-of select="成绩 | 得分 | Grd | score"/></score>
            </xsl:if>
        </choice>
    </xsl:template>
</xsl:stylesheet>
