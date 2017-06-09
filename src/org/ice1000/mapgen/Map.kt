@file:JvmName(CLASS_NAME)
@file:JvmMultifileClass

package org.ice1000.mapgen

import java.io.PrintStream
import java.lang.Math.max
import java.lang.Math.min

/**
 * Created by ice1000 on 17-6-6.
 * @author ice1000
 */

class GameMap(private var map: List<MutableList<Int>>) {
	operator fun set(x: Int, y: Int, v: Int) {
		map[x][y] = v
	}

	operator fun get(x: Int, y: Int) = map[x][y]

	val width = map.size
	val height = map.first().size
	val internalMap: List<MutableList<Int>>
		get() = map

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

	val Point.neighborsAndMe: List<Point>
		get () = neighbors
				.toMutableList()
				.apply { add(this@neighborsAndMe) }
				.toList()

	val traverse = map::traverse
	val forEach = map::forEach

	fun map(block: GameMap.(Triple<Int, Int, Int>) -> Int) =
			map.mapIndexed { i, o -> o.mapIndexed { j, q -> block(Triple(i, j, q)) }.toMutableList() }

	fun averagify() = this {
		map = map { (x, y, _) -> Pair(x, y).neighborsAndMe.run { sumBy { (x, y) -> this@map[x, y] } / size } }
	}

	fun doublify() = GameMap(map.map(MutableList<Int>::doublify).doublify())
	fun triplify() = GameMap(map.map(MutableList<Int>::triplify).triplify())
}

fun gameMapOf(width: Int, height: Int) =
		GameMap((0..width - 1).map { (0..height - 1).map { rand(300) }.toMutableList() })

inline infix fun List<MutableList<Int>>.traverse(block: (Triple<Int, Int, Int>) -> Unit) =
		forEachIndexed { x, ls -> ls.forEachIndexed { y, i -> block(Triple(x, y, i)) } }

fun <T> List<T>.doublify() = flatMap { listOf(it, it) }

@JvmName("mDoublify")
fun <T> MutableList<T>.doublify() = flatMap { listOf(it, it) }.toMutableList()

fun <T> List<T>.triplify() = flatMap { listOf(it, it, it) }

@JvmName("mTriplify")
fun <T> MutableList<T>.triplify() = flatMap { listOf(it, it, it) }.toMutableList()

fun printf(s: String, vararg a: Any?): PrintStream? = System.out.printf(s, *a)

infix inline fun <T, R> List<T>.eachTwo(block: (T, T) -> R) =
		forEachIndexed { i, a -> forEachIndexed { j, b -> if (i != j) block(a, b) } }

@JvmName("mEachTwo")
infix inline fun <T, R> MutableList<T>.eachTwo(block: (T, T) -> R) =
		forEachIndexed { i, a -> forEachIndexed { j, b -> if (i != j) block(a, b) } }

inline fun forceRun(block: () -> Unit) = try {
	block()
} catch (e: Throwable) {
}

/**
 * @author ice1000
 * Created by ice1000 on 2016/8/8.
 *
 */
class Line(one: Point, two: Point) {

	private val a = two.second - one.second
	private val b = one.first - two.first
	private val c = two.first * one.second - one.first * two.second
	val allPoints = HashSet<Point>()

	init {
		(min(one.first, two.first)..max(one.first, two.first))
				.forEach { x -> allPoints.add(Point(x, x2y(x))) }
		(min(one.second, two.second)..max(one.second, two.second))
				.forEach { y -> allPoints.add(Point(y2x(y), y)) }
	}

	fun x2y(x: Int) = if (0 == b) c / a else -(a * x + c) / b
	fun y2x(y: Int) = if (0 == a) c / b else -(b * y + c) / a

	override operator fun equals(other: Any?): Boolean {
		if (other == null || other !is Line) return false
		return a / other.a == b / other.b && b / other.b == c / other.c
	}

	override fun hashCode(): Int {
		var result = a.hashCode()
		result = 31 * result + b.hashCode()
		result = 31 * result + c.hashCode()
		return result
	}
}
