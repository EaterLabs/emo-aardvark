package me.eater.emo.aardvark.views

import me.eater.emo.EmoEnvironment
import me.eater.emo.aardvark.controllers.AardvarkController
import me.eater.emo.aardvark.settings.JavaStyle
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.utils.fxprop
import me.eater.emo.aardvark.utils.map
import me.eater.emo.aardvark.utils.prop
import tornadofx.*

class SettingsView : View() {
    private val env = EmoEnvironment()
    private val aardvarkController: AardvarkController by inject()
    private var location: String by fxprop("")
    private var implementation: JavaStyle.AdoptOpenJDK.Implementation by fxprop(JavaStyle.AdoptOpenJDK.Implementation.HotSpot)
    private var javaStyle: String by fxprop()

    override val root = borderpane {
        id = "settings-view"

        top = hbox {
            addClass("actionbar")
            label("OS: ${env.osName}")
            label("Arch: ${env.osArch}")
        }

        center = vbox {
            addClass("form-container")

            form {
                label("Settings") {
                    addClass("form-title")
                }

                fieldset("Java") {
                    field("Java style") {
                        combobox(::javaStyle.prop()) {
                            items = JavaStyle.getOptions().asObservable()

                            cellFormat {
                                text = when (it) {
                                    "minecraftjdk" -> "Minecraft JDK"
                                    "adoptopenjdk" -> "AdoptOpenJDK"
                                    "system" -> "System"
                                    else -> "Broken"
                                }
                            }
                        }
                    }

                    field("Location") {
                        val shouldBeEnabled = ::javaStyle.prop().map { it == "system" }
                        managedWhen(shouldBeEnabled)
                        visibleWhen(shouldBeEnabled)

                        textfield(::location.prop())

                        button("Select...") {
                            click {

                            }
                        }
                    }

                    field("Implementation") {
                        val shouldBeEnabled = ::javaStyle.prop().map { it == "adoptopenjdk" }
                        managedWhen(shouldBeEnabled)
                        visibleWhen(shouldBeEnabled)

                        combobox(::implementation.prop()) {
                            items = listOf(
                                JavaStyle.AdoptOpenJDK.Implementation.HotSpot,
                                JavaStyle.AdoptOpenJDK.Implementation.OpenJ9
                            ).asObservable()
                        }
                    }
                }

                buttonbar {
                    button("Cancel") {
                        click {
                            updateFromSettings()
                        }
                    }

                    button("Save") {
                        click {
                            aardvarkController.useSettings {
                                it.javaStyle = when (javaStyle) {
                                    "minecraftjdk" -> JavaStyle.MinecraftJDK
                                    "adoptopenjdk" -> JavaStyle.AdoptOpenJDK(implementation)
                                    "system" -> JavaStyle.System(location)
                                    else -> JavaStyle.default()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateFromSettings() {
        val style = aardvarkController.settingsObservable.value.javaStyle
        javaStyle = style.style
        location = when (style) {
            is JavaStyle.System -> style.location
            else -> System.getenv("JAVA_HOME")
        }
        implementation = when (style) {
            is JavaStyle.AdoptOpenJDK -> style.implementation
            else -> JavaStyle.AdoptOpenJDK.Implementation.HotSpot
        }
    }


    init {
        aardvarkController.settingsObservable.onChange {
            updateFromSettings()
        }

        updateFromSettings()
    }
}