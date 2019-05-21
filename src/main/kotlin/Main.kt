package me.eater.emo.aardvark

import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.launch
import java.security.Security

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // If the JRE doesn't include the JCE it breaks because most sites uses newer algorithms
            // So inject BouncyCastle which does provide the newer algorithms
            // This e.g. happens when creating a runtime image of Aardvark
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(BouncyCastleProvider())
            }

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