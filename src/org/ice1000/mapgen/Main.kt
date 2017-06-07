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
	val gameMap = gameMapOf(50, 50)
	(0..4)
			.map { Point(rand(gameMap.width), rand(gameMap.height)) }
			.forEach { (x, y) ->
				val v = rand(200) + 1000 + it * 200
				gameMap {
					Pair(x, y).neighbors.forEach { p ->
						set(p.first, p.second, v + rand(100) - 50)
						p.neighbors.forEach { (x, y) -> set(x, y, v + rand(100) - 50) }
					}
					set(x, y, v)
				}
			}
	(0..100).forEach {
		gameMap[rand(gameMap.width - 2) + 1, rand(gameMap.height - 2) + 1] = rand(500) + 300
	}
	gameMap {
		gameMap.traverse { (x, y, i) ->
			gameMap[x, y] = Point(x, y).neighbors.run {
				sumBy { (x, y) -> gameMap[x, y] } / size + rand(100)
			}
		}
	}

//	gameMap.forEach {
//		it.forEach { printf("%5d", it) }
//		println()
//	}
	image(50, 50) {
		gameMap.traverse { (x, y, i) ->
			when (i) {
				in 0..800 -> color(x, y, BLUE)
				in 801..1200 -> color(x, y, MIDDLE_GREEN)
				in 1201..1500 -> color(x, y, LIGHT_GREEN)
				in 1501..2000 -> color(x, y, BROWN)
				else -> color(x, y, WHITE)
			}
		}
		write("out.png")
	}
}
