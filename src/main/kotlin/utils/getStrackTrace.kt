package me.eater.emo.aardvark.utils

import java.io.ByteArrayOutputStream
import java.io.PrintWriter

fun Throwable.getStrackTrace(): String = ByteArrayOutputStream().let {
    val printer = PrintWriter(it)
    this.printStackTrace(printer)
    printer.flush()
    it.toString().apply {
        printer.close()
    }
}