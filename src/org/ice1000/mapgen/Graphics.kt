@file:JvmName("DSL")
@file:JvmMultifileClass

package org.ice1000.mapgen

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * Created by ice1000 on 17-6-6.
 *
 * @author ice1000
 */

private val random = Random(System.currentTimeMillis())

fun rand() = random.nextInt()
fun rand(i: Int) = random.nextInt(i)

fun fileImage(name: String) = ImageIO.read(File(name))!!

fun urlImage(name: String) = ImageIO.read(URL(name))!!

inline fun image(name: String, width: Int, height: Int, block: BufferedImage.() -> Unit) {
	ImageIO.write(BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply(block), "png", File(name).apply {
		if (!exists()) createNewFile()
	})
}

inline fun image(width: Int, height: Int, block: BufferedImage.() -> Unit) {
	BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).run(block)
}

fun BufferedImage.color(p: Pair<Int, Int>, i: Int) = color(p.first, p.second, i)

val BLUE = Color.BLUE.rgb
val LIGHT_GREEN = 0x26D308
val MIDDLE_GREEN = 0x529D52
val SAND = 0xEDDE78
val M_LIGHT_GREEN = 0x259D17
val BROWN = 0xBE9970
val WHITE = Color.WHITE.rgb

val BufferedImage.color: BufferedImage.(Int, Int, Int) -> Unit
	inline get() = BufferedImage::setRGB

fun BufferedImage.colorOf(p: Pair<Int, Int>): Int = colorOf(p.first, p.second)

val BufferedImage.colorOf: BufferedImage.(Int, Int) -> Int
	inline get() = BufferedImage::getRGB

fun BufferedImage.show() {
	JFrame("show").let {
		it.setSize(width, height)
		it.defaultCloseOperation = 3
		it.add(object : JPanel() {
			override fun paintComponent(p0: Graphics?) {
				p0?.drawImage(this@show, 0, 0, it)
				super.paintComponent(p0)
			}
		})
		it.isVisible = true
	}
}

fun BufferedImage.write(name: String) {
	ImageIO.write(this, "png", File(name))
}
