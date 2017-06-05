@file:JvmName("DSL")
@file:JvmMultifileClass

package org.ice1000.mapgen

import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

/**
 * Created by ice1000 on 17-6-6.
 *
 * @author ice1000
 */

fun fileImage(name: String) = ImageIO.read(File(name))!!

fun urlImage(name: String) = ImageIO.read(URL(name))!!

inline fun image(name: String, width: Int, height: Int, block: BufferedImage.() -> Unit) {
	ImageIO.write(BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply(block), "png", File(name).apply {
		if (!exists()) createNewFile()
	})
}

val BufferedImage.color: BufferedImage.(Int, Int, Int) -> Unit
	inline get() = BufferedImage::setRGB

val BufferedImage.colorOf: BufferedImage.(Int, Int) -> Int
	inline get() = BufferedImage::getRGB



