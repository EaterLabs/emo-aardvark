package me.eater.emo.aardvark.styles

import javafx.geometry.Pos
import javafx.scene.Cursor.OPEN_HAND
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.TextBoundsType
import me.eater.emo.aardvark.box
import tornadofx.*

class MainStyle(private val theme: Colors = Colors()) : Stylesheet() {
    companion object {
        val mainLabel by cssclass()
        val mainButtonLabel by cssclass()
        val mainWindow by cssclass()

        val labelButton by cssclass()

        val bigAccount by cssclass()
        val actionbar by cssclass()

        val modpacksTab by cssid()
        val accountsTab by cssid()
        val profilesTab by cssid()

        val modpacksView by cssid()
        val accountsView by cssid()
        val profilesView by cssid()

        val loginForm by cssclass()


        val minecraftia by cssclass()

        val vbox by csselement("VBox")
        val hbox by csselement("HBox")
    }

    init {
        mainWindow {
            withStyle {
                s(actionbar, bigAccount, labelButton, textField) {
                    backgroundColor = it.color.m
                }

                textField {
                    borderColor = box(it.color.darker()).m
                }

                labelButton {
                    and(hover) {
                        backgroundColor = it.color.darker().m
                    }
                }

                button {
                    backgroundColor = it.color.darker().m

                    and(hover) {
                        backgroundColor = it.color.darker().darker().m
                    }
                }

                ".form" {
                    backgroundColor = it.color.m
                    maxWidth = 600.px
                }
            }

            backgroundColor = theme.background.m

            ".text, Text" {
                fill = theme.foreground

            }

            ".italic" {
                fontStyle = FontPosture.ITALIC
            }


            ".scroll-pane" {
                padding = box(10.px)

                ".scroll-bar" {
                    padding = box(0.px)

                    ".decrement-button, .increment-button" {
                        backgroundColor = theme.background.darker().m
                        backgroundInsets = box(0.px).m

                        and(hover) {
                            backgroundColor = theme.background.darker().m
                        }
                    }
                }

                "StackPane, .viewport" {
                    backgroundColor = theme.background.m
                }

                ".listing-filled" {
                    spacing = 10.px
                }

                ".track" {
                    backgroundColor = theme.background.darker().m
                    backgroundRadius = box(0.px).m
                }

                ".thumb" {
                    borderWidth = box(0.px).m
                    backgroundColor = multi(theme.background.darker(), theme.background.brighter())
                    backgroundInsets = multi(box(0.px), box(vertical = 10.px))
                    backgroundRadius = box(0.px).m
                }

                backgroundColor = theme.background.m
            }

            ".tab-bar" {
                padding = box(top = (-10).px)

                s(vbox) {
                    alignment = Pos.BASELINE_LEFT
                }
            }

            ".main-logo-container" {
                ".main-logo" {
                    fill = theme.foreground
                }
            }

            ".main-label-container" {
                alignment = Pos.BASELINE_LEFT
                padding = box(horizontal = 10.px, bottom = 5.px)

                mainLabel {
                    alignment = Pos.BASELINE_LEFT
                    font = Fonts.minecraftia
                    fontSize = 25.px
                }
            }

            minecraftia {
                font = Fonts.minecraftia
            }

            mainButtonLabel {
                font = Fonts.minecraftia
                alignment = Pos.BASELINE_LEFT
                cursor = OPEN_HAND
                padding = box(all = 10.px, bottom = 5.px)

                label {
                    alignment = Pos.BASELINE_LEFT

                    fontSize = 20.px
                }
            }

            ".listing-empty" {
                hbox {
                    alignment = Pos.CENTER
                }
            }

            ".form-container" {
                alignment = Pos.TOP_CENTER
                padding = box(10.px)
            }

            modpacksTab {
                and(hover) {
                    backgroundColor = theme.modpacks.m
                }

                and(".current") {
                    backgroundColor = theme.modpacks.m
                }
            }

            profilesTab {
                and(hover) {
                    backgroundColor = theme.profiles.m
                }

                and(".current") {
                    backgroundColor = theme.profiles.m
                }
            }

            accountsTab {
                and(hover) {
                    backgroundColor = theme.accounts.m
                }

                and(".current") {
                    backgroundColor = theme.accounts.m
                }
            }

            labelButton {
                padding = box(5.px)
                cursor = OPEN_HAND
            }

            accountsView {
                child(vbox) {
                    alignment = Pos.TOP_CENTER
                    padding = box(10.px)
                }

                ".big-account" {
                    padding = box(10.px)
                    alignment = Pos.CENTER_LEFT

                    ".username" {
                        s(".text") {
                            unsafe("-fx-bounds-type", TextBoundsType.VISUAL.name)
                        }
                        fontSize = 20.px
                    }

                }

                ".login-form-container" {
                    alignment = Pos.TOP_CENTER

                    ".login-form" {
                        backgroundColor = theme.accounts.m
                        maxWidth = 600.px
                    }
                }
            }

            buttonBar {
                padding = box(vertical = 5.px)
            }

            button {
                borderRadius = multi(box(0.px))
                padding = box(10.px)
                textFill = theme.light
                cursor = OPEN_HAND
            }

            textField {
                borderRadius = multi(box(0.px))
                padding = box(10.px)
                textFill = theme.light
                borderStyle = BorderStrokeStyle.SOLID.m
                borderWidth = box(2.px).m
            }

            ".error-box" {
                backgroundColor = theme.error.m
                padding = box(5.px)
            }

            ".repository-listing, .modpack-listing" {
                alignment = Pos.TOP_CENTER

                ".repository-fragment, .modpack-fragment" {
                    maxWidth = 800.px
                }
            }

            ".repository-fragment, .modpack-fragment" {
                padding = box(5.px)
                backgroundColor = theme.modpacks.darker().m

                ".repository-description, .modpack-description" {
                    fontStyle = FontPosture.ITALIC
                    fontSize = 12.px
                }
            }

            ".modpack-fragment" {
                ".combo-box" {
                    backgroundRadius = box(0.px).m
                    backgroundColor = theme.modpacks.m
                    backgroundInsets = box(0.px).m
                    padding = box(0.px)

                    ".virtual-flow, .clipped-container, .list-cell, .list-view, .arrow-button" {
                        backgroundRadius = box(0.px).m
                        backgroundColor = theme.modpacks.m
                        backgroundInsets = box(0.px).m
                        cursor = OPEN_HAND
                    }

                    ".list-view" {
                        padding = box(0.px)
                    }

                    ".list-cell:hover:filled" {
                        backgroundColor = theme.modpacks.darker().darker().m
                    }

                    ".arrow-button" {
                        padding = box(5.px)

                        ".arrow" {
                            backgroundColor = theme.foreground.m
                        }
                    }

                    ".new-profile-button" {
                        fontSize = 15.px
                    }
                }
            }

            "#modpacks-view" {
                ".modpack-fragment" {
                    labelButton {
                        and(hover) {
                            backgroundColor = theme.modpacks.darker().darker().m
                        }
                    }
                }
            }
        }
    }

    private fun withStyle(block: CssSelectionBlock.(Style) -> Unit) {
        profilesView {
            block(Style(theme.profiles, "profiles"))
        }

        accountsView {
            block(Style(theme.accounts, "accounts"))
        }
        modpacksView {
            block(Style(theme.modpacks, "modpacks"))
        }
    }

    data class Style(val color: Color, val name: String)

    private val <T> T.m: MultiValue<T>
        get() = multi(this)
}