package me.eater.emo.aardvark.styles

import javafx.geometry.Pos
import javafx.scene.Cursor.OPEN_HAND
import javafx.scene.paint.Color
import tornadofx.*

class MainStyle : Stylesheet() {
    companion object {
        val mainLabel by cssclass()
        val mainButtonLabel by cssclass()
        val mainWindow by cssclass()

        val labelButton by cssclass()

        val modpacksTab by cssid()
        val accountsTab by cssid()
        val profilesTab by cssid()

        val modpacksView by cssid()
        val accountsView by cssid()
        val profilesView by cssid()
    }

    init {
        mainWindow {
            child("HBox") {
                alignment = Pos.BOTTOM_LEFT
            }

            backgroundColor = multi(Colors.dark)

            text {
                fill = Colors.light
            }
        }

        mainLabel {
            font = Fonts.minecraftia
            padding = box(10.px)
            fontSize = 25.px
        }

        mainButtonLabel {
            font = Fonts.minecraftia
            alignment = Pos.BOTTOM_LEFT
            cursor = OPEN_HAND

            label {
                padding = box(10.px)
                fontSize = 20.px
            }
        }



        modpacksTab {
            and(hover) {
                backgroundColor = multi(Colors.modpacks)
            }

            and(".current") {
                backgroundColor = multi(Colors.modpacks)
            }
        }

        profilesTab {
            and(hover) {
                backgroundColor = multi(Colors.profiles)
            }

            and(".current") {
                backgroundColor = multi(Colors.profiles)
            }
        }

        accountsTab {
            and(hover) {
                backgroundColor = multi(Colors.accounts)
            }

            and(".current") {
                backgroundColor = multi(Colors.accounts)
            }
        }

        labelButton {
            padding = box(5.px)
            cursor = OPEN_HAND
        }

        withStyle {
            labelButton {
                backgroundColor = multi(it.color)

                and(hover) {
                    backgroundColor = multi(it.color.darker())
                }
            }

            s(".actionbar") {
                backgroundColor = multi(it.color)
            }
        }
    }

    fun withStyle(block: CssSelectionBlock.(Style) -> Unit) {
        profilesView {
            block(Style(Colors.profiles, "profiles"))
        }

        accountsView {
            block(Style(Colors.accounts, "accounts"))
        }
        modpacksView {
            block(Style(Colors.modpacks, "modpacks"))
        }
    }

    data class Style(val color: Color, val name: String)
}