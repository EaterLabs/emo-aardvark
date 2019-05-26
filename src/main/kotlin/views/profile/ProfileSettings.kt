package me.eater.emo.aardvark.views.profile

import com.beust.klaxon.Klaxon
import me.eater.emo.aardvark.AardvarkProfile
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.utils.fxprop
import me.eater.emo.aardvark.utils.prop
import me.eater.emo.emo.dto.LaunchOptions
import tornadofx.*
import java.io.File

class ProfileSettings : Fragment() {
    val profile: AardvarkProfile by param()

    private val settingsFile: String
        get() = "${profile.location}/.emo/launch.json"

    private val launchOptions: LaunchOptions
        get() = Klaxon().parse<LaunchOptions>(File(settingsFile)) ?: LaunchOptions()

    private var jvmArgs: String by fxprop()

    override val root = vbox {
        addClass("form-container", "wide")

        form {
            fieldset("Launcher settings") {
                field("JVM Arguments") {
                    textfield(::jvmArgs.prop())
                }
            }

            buttonbar {
                button("Cancel") {
                    click {
                        replaceWith<ProfileListingView>()
                    }
                }

                button("Save") {
                    click {
                        val lo = launchOptions.java
                        val launchOptions = LaunchOptions(
                            lo,
                            jvmArgs
                        )

                        File(settingsFile).writeText(Klaxon().toJsonString(launchOptions))
                        replaceWith<ProfileListingView>()
                    }
                }
            }
        }
    }

    override fun onDock() {
        jvmArgs = launchOptions.jvmArgs ?: ""
    }
}