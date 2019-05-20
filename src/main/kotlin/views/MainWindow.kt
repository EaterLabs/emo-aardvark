package me.eater.emo.aardvark.views

import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.FillRule
import javafx.scene.shape.Rectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.map
import me.eater.emo.aardvark.styles.Icons
import tornadofx.*
import java.nio.file.*

class MainWindow : View("emo — Eater's Mod Manager") {
    private val emoController: EmoController by inject()

    private val accountsView: AccountsView by inject()
    private val profilesView: ProfilesView by inject()
    private val modpacksView: ModpacksView by inject()

    override fun onBeforeShow() {
        primaryStage.minWidth = 800.0
        primaryStage.minHeight = 480.0
    }

    override val root = borderpane {
        prefWidth = 680.0
        prefHeight = 400.0

        addClass("main-window", "contrast-light")

        center = if (emoController.accounts.count() == 0) accountsView.root else profilesView.root

        top = vbox {
            addClass("tab-bar")

            clip = Rectangle(this.width, this.height).apply {
                heightProperty().bind(this@vbox.heightProperty())
                widthProperty().bind(this@vbox.widthProperty())
            }


            hbox {
                fun menuButton(id: String, text: String, view: Node, block: VBox.() -> Unit = {}) = vbox {
                    this.id = id

                    addClass("main-button-label")

                    if (view === center) {
                        addClass("current")
                    }

                    label(text)

                    click {
                        this@borderpane.selectAll<Node>(CssRule.c("main-button-label"))
                            .forEach { node: Node -> node.removeClass("current") }

                        addClass("current")
                        center.replaceWith(view)
                    }

                    block(this)
                }

                vbox logoContainer@{
                    addClass("main-logo-container")

                    svgpath(Icons.aardvarkPath, FillRule.EVEN_ODD) {
                        addClass("main-logo")

                        vgrow = Priority.NEVER
                        isManaged = false

                        this@logoContainer.prefWidthProperty()
                            .bind(heightProperty().map { 10 + (it.toDouble() - 20) * (this.layoutBounds.width / this.layoutBounds.height) })

                        val scale = this@logoContainer.heightProperty()
                            .map { (it.toDouble() - 20.0) / this.layoutBounds.height }
                        scaleXProperty().bind(scale)
                        scaleYProperty().bind(scale)
                        translateXProperty()
                            .bind(scale.map { -((this.layoutBounds.width - (this.layoutBounds.width * it)) / 2) + 5 })
                        translateYProperty()
                            .bind(scale.map { -((this.layoutBounds.height - (this.layoutBounds.height * it)) / 2) + 15 })
                    }

                    clip = Rectangle(this.width, this.height).apply {
                        heightProperty().bind(this@logoContainer.heightProperty())
                        widthProperty().bind(this@logoContainer.widthProperty())
                    }

                    vgrow = Priority.NEVER
                }

                vbox {
                    addClass("main-label-container")

                    label("Aardvark") {
                        addClass("main-label")
                    }
                }

                menuButton("profiles-tab", "Profiles", profilesView.root)
                menuButton("modpacks-tab", "Modpacks", modpacksView.root)
                menuButton("accounts-tab", "Accounts", accountsView.root)
            }
        }
    }

    init {
        val liveCss = Paths.get("cheats")
        val watchService = FileSystems.getDefault().newWatchService()
        val key = liveCss.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        )

        fun removeCheatCSS(path: String) {
            primaryStage.scene.stylesheets.removeIf {
                it == path
            }
        }

        fun updateCheatCSS(path: String) {
            removeCheatCSS(path)
            primaryStage.scene.stylesheets.add(path)
        }

        primaryStage.sceneProperty().onChangeOnce {
            if (Files.exists(Paths.get(liveCss.toString(), "live.css"))) {
                updateCheatCSS("file:" + Paths.get(liveCss.toString(), "live.css").toAbsolutePath().toString())
            }
        }

        GlobalScope.launch {
            while (true) {
                loop@ for (event in key.pollEvents()) {
                    when  {
                        event.kind() == StandardWatchEventKinds.OVERFLOW -> continue@loop
                        event.context() is Path -> {
                            val path: Path = event.context() as Path
                            if (path.endsWith("live.css")) {
                                val absolute = "file:" + liveCss.resolve(path).toAbsolutePath().toString()

                                launch(Dispatchers.JavaFx) {
                                    when (event.kind()) {
                                        StandardWatchEventKinds.ENTRY_DELETE -> removeCheatCSS(absolute)
                                        else -> updateCheatCSS(absolute)
                                    }
                                }
                            }
                        }
                    }
                }

                delay(200)
            }
        }
    }
}