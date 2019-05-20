package me.eater.emo.aardvark.styles

import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

object Icons {
    val aardvark: String = javaClass.getResource("/icon.svg")!!.readText()
    val aardvarkUri = javaClass.getResource("/icon.svg")!!.toURI()

    val aardvarkPath: String by lazy {
        val doc = DocumentBuilderFactory.newDefaultInstance()
            .newDocumentBuilder()
            .parse(ByteArrayInputStream(aardvark.toByteArray()))
        doc.getElementsByTagName("path")
            .item(0)
            .attributes
            .getNamedItem("d")
            .nodeValue
    }
}