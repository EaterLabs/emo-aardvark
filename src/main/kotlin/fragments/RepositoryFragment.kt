package me.eater.emo.aardvark.fragments

import javafx.geometry.Pos
import me.eater.emo.RepositoryCache
import tornadofx.*

class RepositoryFragment : Fragment() {
    val repositoryCache: RepositoryCache by param()


    override val root = gridpane {

        constraintsForColumn(0).prefWidth = 100.0

        vgap = 5.0
        hgap = 10.0

        addClass("repository-fragment")

        hbox {
            alignment = Pos.CENTER

            minWidth = 100.0
            minHeight = 100.0

            imageview(repositoryCache.logo) {
                addClass("repository-icon")
                isPreserveRatio = true
                fitHeight = 100.0
                fitWidth = 100.0
            }

            gridpaneConstraints {
                rowSpan = 4
                rowIndex = 0
                columnIndex = 0
            }
        }

        label(repositoryCache.name) {
            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 1
            }
        }

        text(repositoryCache.description) {
            addClass("repository-description")

            gridpaneConstraints {
                rowIndex = 1
                columnIndex = 1
            }
        }
    }
}
