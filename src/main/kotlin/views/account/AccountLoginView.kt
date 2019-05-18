package me.eater.emo.aardvark.views.account

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.input.KeyCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.controllers.EmoController
import tornadofx.*

class AccountLoginView : View() {
    private val emoController: EmoController by inject()

    private var hasError by property(false)
    private var errorLabel: Label by singleAssign()
    private var passwordField: PasswordField by singleAssign()

    private var username by property("")
    private var password by property("")

    private val accountListView: AccountListView by inject()

    override fun onDock() {
        password = ""
        username = ""
        hasError = false
    }

    override fun onUndock() {
        password = ""
        username = ""
        hasError = false
    }

    override val root = vbox {
        addClass("login-form-container")

        form {
            addClass("login-form")

            fieldset("Login") {
                hbox {
                    visibleWhen(this@AccountLoginView.getProperty(AccountLoginView::hasError))
                    managedWhen(this@AccountLoginView.getProperty(AccountLoginView::hasError))

                    addClass("error-box")

                    alignment = Pos.CENTER

                    label("[ERROR MESSAGE]") {
                        errorLabel = this
                    }
                }

                field("Username") {
                    textfield(this@AccountLoginView.getProperty(AccountLoginView::username)) {
                        onKeyPressed = EventHandler {
                            if (it.code == KeyCode.ENTER) {
                                runLogin()
                            }
                        }
                    }
                }

                field("Password") {
                    passwordfield(this@AccountLoginView.getProperty(AccountLoginView::password)) {
                        passwordField = this

                        onKeyPressed = EventHandler {
                            if (it.code == KeyCode.ENTER) {
                                runLogin()
                            }
                        }
                    }
                }
            }

            buttonbar {
                button("Login") {
                    action {
                        runLogin()
                    }
                }

                button("Cancel") {
                    action {
                        username = ""
                        password = ""
                        replaceWith(accountListView)
                    }
                }
            }
        }
    }

    fun runLogin() {
        val password = password
        this@AccountLoginView.password = ""

        GlobalScope.launch {
            val result = runCatching {
                emoController.logIn(username, password)
            }

            launch(Dispatchers.JavaFx) ui@{
                if (result.isFailure) {
                    errorLabel.text = result.exceptionOrNull()!!.message
                    hasError = true
                    passwordField.requestFocus()

                    return@ui
                }

                replaceWith(accountListView)
            }
        }
    }
}
