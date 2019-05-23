package me.eater.emo.aardvark.views.modpack

import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.httpGet
import javafx.scene.Node
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import me.eater.emo.RepositoryCache
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.fragments.RepositoryFragment
import me.eater.emo.aardvark.utils.not
import me.eater.emo.emo.RepositoryDefinition
import me.eater.emo.emo.RepositoryType
import me.eater.emo.emo.dto.repository.Repository
import tornadofx.*
import java.util.concurrent.locks.ReentrantLock

class AddRepositoryView : View() {
    val emoController: EmoController by inject()

    private var url: String by property()
    private var status: String by property("No repository found")
    private var isOkay: Boolean by property(false)
    private var repository: Repository? by property()
    private var downloadLock = ReentrantLock()

    private lateinit var repositorySlot: Node

    override val root = vbox {
        addClass("form-container")

        form {
            fieldset("Add repository") {
                field("Url") {
                    textfield(this@AddRepositoryView.getProperty(AddRepositoryView::url))
                }

                field("Repository") {
                    label(this@AddRepositoryView.getProperty(AddRepositoryView::status)) {
                        toggleClass(
                            CssRule.c("italic"),
                            this@AddRepositoryView.getProperty(AddRepositoryView::isOkay).not()
                        )
                    }
                }
            }

            repositorySlot = vbox()

            buttonbar {
                button("Add") {
                    enableWhen(this@AddRepositoryView.getProperty(AddRepositoryView::isOkay))

                    click {
                        emoController.addRemoteRepository(url)

                        replaceWith<RepositoryListingView>()
                    }
                }

                button("Cancel") {
                    click {
                        replaceWith<RepositoryListingView>()
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
                else -> "${it.modpacks.size} modpack${if (it.modpacks.size == 1) "" else "s"}"
            }

            val newSlot = when (it) {
                null -> vbox()
                else -> find(
                    RepositoryFragment::class,
                    "repositoryCache" to RepositoryCache.fromRepository(
                        RepositoryDefinition(RepositoryType.Remote, url),
                        it
                    ),
                    "showRemove" to false
                ).root
            }

            repositorySlot.replaceWith(newSlot)
            repositorySlot = newSlot
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

            var repo: Repository?

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

