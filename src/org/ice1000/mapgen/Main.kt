/**
 * Created by ice1000 on 17-6-5.
 *
 * @author ice1000
 */
@file:JvmName("DSL")
@file:JvmMultifileClass

package org.ice1000.mapgen

typealias Point = Pair<Int, Int>

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
	val ls = (0..8).map { Triple(rand(gameMap.width), rand(gameMap.height), it) }
	ls.forEach { (x, y, i) ->
		val v = rand(200) + 1000 + i * 200
		gameMap {
			Pair(x, y).neighbors.forEach { p ->
				set(p.first, p.second, v + rand(100) - 50)
				p.neighbors.forEach { (x, y) -> set(x, y, v + rand(100) - 50) }
			}
			set(x, y, v)
		}
	}
	ls.forEachIndexed { i, (x1, y1, _) ->
		ls.forEachIndexed { j, (x2, y2, _) ->
			if (i != j) {
				val h1 = gameMap[x1, y1]
				val h2 = gameMap[x2, y2]
//				println("h1 = $h1, h2 = $h2")
				gameMap {
					val k = (h1 + h2) / 2
					Line(Point(x1, y1), Point(x2, y2))
							.allPoints
//						.apply { forEach(::println) }
							.forEach { (x, y) ->
								try {
									if (rand(100) >= 30) gameMap[x, y] = k + rand(200) - 100
									Point(x, y).neighbors.forEach { (x, y) ->
										if (rand(100) >= 30) gameMap[x, y] = k + rand(200) - 100
									}
								} catch (e: Throwable) {
									// why
								}
							}
				}
			}
		}
	}
	(0..100).forEach {
		gameMap[rand(gameMap.width - 2) + 1, rand(gameMap.height - 2) + 1] = rand(500) + 300
	}
	(0..3).forEach {
		gameMap {
			gameMap.traverse { (x, y, _) ->
				gameMap[x, y] = Point(x, y).neighbors.run {
					sumBy { (x, y) -> gameMap[x, y] } / size //+ rand(50)
				}
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
