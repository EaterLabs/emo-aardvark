package me.eater.emo.aardvark

import tornadofx.launch

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // If a tiling WM is used and the default gtk version is used (3)
            // All popup menu offsets will be wrong. so force it to 2
            System.setProperty("jdk.gtk.version", "2")

            // To make text look crispy
            System.setProperty("prism.lcdtext", "false")
            System.setProperty("prism.text", "t2k")

            launch<AardvarkApp>()
        }
    }
}