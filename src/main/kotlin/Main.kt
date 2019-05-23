package me.eater.emo.aardvark

import me.eater.emo.EmoEnvironment
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.misc.Unsafe
import sun.net.www.protocol.css.Handler
import tornadofx.launch
import java.net.URL
import java.security.Security

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            disableWarning()
            if (!Security.getAlgorithms("KeyPairGenerator").contains("EC")) {
                println("Loading BouncyCastle since default Java doesn't include any, may show some warnings.")
                // If the JRE doesn't include the JCE it breaks because most sites uses newer algorithms
                // So inject BouncyCastle which does provide the newer algorithms
                // This e.g. happens when creating a runtime image of Aardvark
                if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                    Security.addProvider(BouncyCastleProvider())
                }
            }

            // Register css protocol already for TornadoFX so it will shut up
            URL.setURLStreamHandlerFactory(Handler.HandlerFactory())

            // If a tiling WM is used and the default gtk version is used (3)
            // All popup menu offsets will be wrong. so force it to 2
            System.setProperty("jdk.gtk.version", "2")

            if (EmoEnvironment().osName == "linux") {
                // To make text look crispy
                System.setProperty("prism.lcdtext", "false")
                System.setProperty("prism.text", "t2k")
            }

            launch<AardvarkApp>()
        }

        /**
         * We're using BouncyCastle as JRE polyfill, since it's been built for Java 1.8
         * it does some reflect voodoo magic that isn't "allowed" anymore.
         * This gives unnecessary warnings that I don't want, so this hack fixes it
         *
         * Source: https://stackoverflow.com/a/46458447
         */
        private fun disableWarning() {
            try {
                val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
                theUnsafe.isAccessible = true
                val u = theUnsafe.get(null) as Unsafe

                val cls = Class.forName("jdk.internal.module.IllegalAccessLogger")
                val logger = cls.getDeclaredField("logger")
                u.putObjectVolatile(cls, u.staticFieldOffset(logger), null)
            } catch (e: Exception) {
                System.err.println("Couldn't silence warning: ${e.message}")
                // ignore
            }

        }
    }
}