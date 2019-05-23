package me.eater.emo.aardvark.utils

import tornadofx.CssBox
import tornadofx.Dimension
import tornadofx.px

fun <T> box(
    all: T,
    vertical: T = all,
    horizontal: T = all,
    top: T = vertical,
    right: T = horizontal,
    bottom: T = vertical,
    left: T = horizontal
) = CssBox(top, right, bottom, left)

@Suppress("UNCHECKED_CAST")
fun <T : Dimension<Dimension.LinearUnits>> box(
    all: T = 0.px as T,
    vertical: T = all,
    horizontal: T = all,
    top: T = vertical,
    right: T = horizontal,
    bottom: T = vertical,
    left: T = horizontal
) = CssBox(top, right, bottom, left)