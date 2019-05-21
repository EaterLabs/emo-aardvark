package me.eater.emo.aardvark.views

import javafx.geometry.Pos
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.labelButton
import tornadofx.*

class ConfirmationView : Fragment("Confirmation") {
    private val description: TextFlow by param()
    private val callback: (Boolean) -> Unit by param()

    override val root = vbox {
        addClass("confirmation-view")

        add(description)

        hbox {
            addClass("confirmation-buttons")
            alignment = Pos.BASELINE_RIGHT

            labelButton {
                label("Yes")

                click {
                    callback(true)
                    close()
                }
            }

            labelButton {
                label("No")

                click {
                    callback(false)
                    close()
                }
            }
        }
    }
}