package org.ice1000.mapgen

/**
 * Created by ice1000 on 17-6-5.
 *
 * @author ice1000
 */

class GameMap(val map: List<MutableList<Int>>) {
	operator fun set(x: Int, y: Int, v: Int) {
		map[x][y] = v
	}

	operator fun get(x: Int, y: Int) = map[x][y]

	infix inline operator fun <R> invoke(block: GameMap.() -> R) = block()

	val Point.neighbors: List<Point>
		get () {
			val ls = mutableListOf<Point>()
			if (0 < first) ls.add(Point(first - 1, second))
			if (0 < second) ls.add(Point(first, second - 1))
			if (map.size - 1 > second) ls.add(Point(first, second + 1))
			if (map.first().size - 1 > first) ls.add(Point(first + 1, second))
			return ls.toList()
		}

	val traverse = map::traverse
	val forEach = map::forEach
}

fun gameMapOf(width: Int, height: Int) = GameMap((0..width - 1).map { (0..height - 1).map { 0 }.toMutableList() })

fun List<MutableList<Int>>.traverse(block: (Triple<Int, Int, Int>) -> Unit) =
		forEachIndexed { x, ls -> ls.forEachIndexed { y, i -> block(Triple(x, y, i)) } }

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
	val map = gameMapOf(50, 50)
	(0..4).forEach {
		val x = rand(50)
		val y = rand(50)
		val v = rand(200) + 1000 + it * 200
		map {
			Pair(x, y).neighbors.forEach { p ->
				set(p.first, p.second, v + rand(100) - 50)
				p.neighbors.forEach { (x, y) ->
					set(x, y, v + rand(100) - 50)
				}
			}
			set(x, y, v)
		}
	}
	(0..100).forEach { map[rand(48) + 1, rand(48) + 1] = rand(500) + 300 }

	map.forEach {
		it.forEach {
			System.out.printf("%5d", it)
		}
		println()
	}
	image(50, 50) {
		map.traverse { (x, y, i) -> color(x, y, i) }
		write("out.png")
	}
}
