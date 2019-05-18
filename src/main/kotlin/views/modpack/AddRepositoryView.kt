package me.eater.emo.aardvark.views.modpack

import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.httpGet
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.text.FontPosture
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import me.eater.emo.aardvark.click
import me.eater.emo.emo.dto.repository.Repository
import tornadofx.*
import java.util.concurrent.locks.ReentrantLock

class AddRepositoryView : View() {
    var url: String by property()
    var status: String by property("No repository found")
    var isOkay: Boolean by property(false)
    var repository: Repository? by property()

    var downloadLock = ReentrantLock()

    override val root = vbox {
        addClass("form-container")

        form {
            fieldset("Add repository") {
                field("Url") {
                    textfield(this@AddRepositoryView.getProperty(AddRepositoryView::url))
                }

                field("Modpack") {
                    label(this@AddRepositoryView.getProperty(AddRepositoryView::status)) {
                        toggleClass(CssRule.c("italic"), this@AddRepositoryView.getProperty(AddRepositoryView::isOkay).not())
                    }
                }
            }

            buttonbar {
                button("Add") {
                    enableWhen(this@AddRepositoryView.getProperty(AddRepositoryView::isOkay))
                }

                button("Cancel") {
                    click {

                    }
                }
            }
        }
    }

    init {
        getProperty(AddRepositoryView::url).onChange {
            checkRepository()
        }

        getProperty(AddRepositoryView::repository).onChange {
            status = when (it) {
                null -> "No repository found"
                else -> "${it.name} (${it.modpacks.size} modpack${if (it.modpacks.size == 1) "" else "s"})"
            }
        }
    }

    fun checkRepository() {
        GlobalScope.launch {
            val oldUrl = url
            delay(300)

            if (url != oldUrl || downloadLock.isLocked) {
                return@launch
            }

            GlobalScope.async(Dispatchers.Main) {
                downloadLock.lock()
            }.await()

            var repo: Repository? = null

            try {
                repo = Repository.fromJson(
                    oldUrl
                        .httpGet()
                        .awaitString()
                )
            } catch (t: Throwable) {
                repo = null
            } finally {
                GlobalScope.async(Dispatchers.Main) {
                    downloadLock.unlock()
                }.await()
            }

            GlobalScope.launch(Dispatchers.JavaFx) {
                repository = repo
                isOkay = repository != null
            }
        }
    }
}

private fun ObjectProperty<Boolean>.not(): ObservableBooleanValue {
    val simple = SimpleBooleanProperty(!this.get())
    this.onChange { simple.set(!this.get()) }
    return simple
}
