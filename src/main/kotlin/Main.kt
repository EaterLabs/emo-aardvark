package me.eater.emo.aardvark

import tornadofx.launch

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("prism.lcdtext", "false")
            System.setProperty("prism.text", "t2k")

            launch<AardvarkApp>()
        }
    }
}