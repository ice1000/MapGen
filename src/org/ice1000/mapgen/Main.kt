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
	val map1 = gameMapOf(60, 60)
	val ls = (0..8).map { Triple(rand(map1.width), rand(map1.height), it) }
	ls.forEach { (x, y, i) ->
		val v = rand(200) + 1000 + i * 200
		map1 {
			Pair(x, y).neighbors.forEach { p ->
				set(p.first, p.second, v + rand(100) - 50)
				p.neighbors.forEach { (x, y) -> set(x, y, v + rand(100) - 50) }
			}
			set(x, y, v)
		}
	}
	ls.forEachIndexed { i, (x1, y1, _) ->
		ls.forEachIndexed { j, (x2, y2, _) ->
			if (i != j && rand(50) >= 10) {
				val h1 = map1[x1, y1]
				val h2 = map1[x2, y2]
				map1 {
					val k = (h1 + h2) / 2
					Line(Point(x1, y1), Point(x2, y2))
							.allPoints
							.forEach { (x, y) ->
								try {
									if (rand(100) >= 30) map1[x, y] = k + rand(200) - 100
									Point(x, y).neighbors.forEach { (x, y) ->
										if (rand(100) >= 30) map1[x, y] = k + rand(200) - 100
									}
								} catch (e: Throwable) {
								}
							}
				}
			}
		}
	}
	(0..100).forEach {
		val x = rand(map1.width - 2) + 1
		val y = rand(map1.height - 2) + 1
		map1[x, y] = (rand(500) + 300)
	}
	(0..3).forEach { map1.averagify() }
	val map2 = map1.doublify()
	map2.traverse { (x, y, i) -> map2[x, y] = rand(300) - 150 + i }
	val ls2 = (0..8).map { Triple(rand(map2.width), rand(map2.height), it) }
	ls2.forEach { (x, y, i) ->
		val v = rand(200) + 1000 + i * 200
		map2 {
			Pair(x, y).neighbors.forEach { p ->
				set(p.first, p.second, v + rand(100) - 50)
				p.neighbors.forEach { (x, y) -> set(x, y, v + rand(100) - 50) }
			}
			set(x, y, v)
		}
	}
	(0..8).forEach { map2.averagify() }
	image(map2.width, map2.height) {
		map2.internalMap.traverse { (x, y, i) ->
			color(x, y, when (i) {
				in 0..300 -> DEEP_BLUE
				in 0..500 -> BLUE
				in 0..800 -> SHALLOW_BLUE
				in 0..900 -> SAND
				in 0..1200 -> MIDDLE_GREEN
				in 0..1400 -> L_LIGHT_GREEN
				in 0..1600 -> LIGHT_GREEN
				in 0..1700 -> DARK_GREEN
				in 0..1900 -> M_DARK_GREEN
				in 0..2050 -> BROWN
				else -> WHITE
			})
		}
		if (args.isNotEmpty()) write(args.first())
		else write("out.png")
	}
}
