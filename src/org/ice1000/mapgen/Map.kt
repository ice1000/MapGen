@file:JvmName("DSL")
@file:JvmMultifileClass

package org.ice1000.mapgen

import java.io.PrintStream

/**
 * Created by ice1000 on 17-6-6.
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

fun printf(s: String, vararg a: Any?): PrintStream? = System.out.printf(s, *a)
