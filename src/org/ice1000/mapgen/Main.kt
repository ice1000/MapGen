/**
 * Created by ice1000 on 17-6-5.
 *
 * @author ice1000
 */
@file:JvmName("DSL")
@file:JvmMultifileClass

package org.ice1000.mapgen

/**
 * document
 *
 * 8 个高地
 *
 * 0 Gay Rock
 * 1 - 800 海
 * 801 - 1200 平原
 * 1201 - 1500 高原
 * 1501 - 2000 山的偏高处
 * 2000 + 雪
 */
fun main(vararg args: String) {
	val map = gameMapOf(50, 50)
	(0..4).forEach {
		val x = rand(50)
		val y = rand(50)
		val v = rand(200) + 1000 + it * 200
		map {
			Pair(x, y).neighbors.forEach { p ->
				set(p.first, p.second, v + rand(100) - 50)
				p.neighbors.forEach { (x, y) -> set(x, y, v + rand(100) - 50) }
			}
			set(x, y, v)
		}
	}
	(0..100).forEach { map[rand(48) + 1, rand(48) + 1] = rand(500) + 300 }

	map.forEach {
		it.forEach { printf("%5d", it) }
		println()
	}
	image(50, 50) {
		map.traverse { (x, y, i) ->
			when (i) {
				in 0..800 -> color(x, y, BLUE)
				in 801..1200 -> color(x, y, GREEN)
			}
		}
		write("out.png")
	}
}
