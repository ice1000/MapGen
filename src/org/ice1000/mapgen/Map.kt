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
	operator fun set(p: Pair<Int, Int>, v: Int) = set(p.first, p.second, v)
	operator fun set(x: Int, y: Int, v: Int) {
		if (x >= 0 && y >= 0) map[x][y] = v
	}

	operator fun get(x: Int, y: Int) = map[x][y]
	operator fun get(p: Pair<Int, Int>) = get(p.first, p.second)

	val width = map.size
	val height = map.first().size
	val internalMap get() = map

	val rivers = mutableListOf<List<Point>>()

	infix inline operator fun <R> invoke(block: GameMap.() -> R) = block()

	val Point.neighbors: List<Point>
		get () {
			val ls = mutableListOf<Point>()
			if (0 < first) ls.add(Point(first - 1, second))
			if (0 < second) ls.add(Point(first, second - 1))
			if (width - 1 > second) ls.add(Point(first, second + 1))
			if (height - 1 > first) ls.add(Point(first + 1, second))
			return ls.toList()
		}

	val Point.neighbors8: List<Point>
		get () {
			val ls = mutableListOf<Point>()
			val a = 0 < first
			val b = 0 < second
			if (a) ls.add(Point(first - 1, second))
			if (b) ls.add(Point(first, second - 1))
			if (a && b) ls.add(Point(first - 1, second - 1))
			val c = width - 1 > second
			val d = height - 1 > first
			if (c) ls.add(Point(first, second + 1))
			if (d) ls.add(Point(first + 1, second))
			if (c && d) ls.add(Point(first + 1, second + 1))
			if (a && c) ls.add(Point(first - 1, second + 1))
			if (d && b) ls.add(Point(first + 1, second - 1))
			return ls.toList()
		}

	val Point.neighborsAndMe: List<Point>
		get () = neighbors.toMutableList().apply { add(this@neighborsAndMe) }.toList()

	val traverse get() = map::traverse
	val forEach get() = map::forEach

	fun map(block: GameMap.(Triple<Int, Int, Int>) -> Int) =
			map.mapIndexed { i, o -> o.mapIndexed { j, q -> block(Triple(i, j, q)) }.toMutableList() }

	fun averagify() = this {
		map = map { (x, y, _) -> Pair(x, y).neighborsAndMe.run { sumBy { (x, y) -> this@map[x, y] } / size } }
		this
	}

	fun averagify8() = this {
		map = map { (x, y, _) -> Pair(x, y).neighbors8.run { sumBy { (x, y) -> this@map[x, y] } / size } }
		this
	}

	fun hardEncodeRivers(height: Int): GameMap {
		rivers.forEach { it.forEach { set(it, height) } }
		rivers.clear()
		return this
	}

	fun genRiver(): List<Point> {
		var pt: Point
		do {
			pt = randPt(width, height)
		} while (this[pt] !in 1201..1999)
		return this.genRiver(pt)
	}

	fun genRiver(begin: Point): List<Point> {
		val river = mutableListOf<Point>()
		var pt = begin
		this block@ {
			while (this[pt] in 601..1999) {
				val min = pt.neighbors8.minBy(this::get)
				if (null != min && this[min] < this[pt]) {
					pt = min
					river.add(min)
				} else {
					river.addAll(pt.neighbors8)
					return@block
				}
			}
		}
		return river
	}

	fun genRandPts(number: Int) = (0..number - 1).map { Triple(rand(width), rand(height), it) }

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
		if (a != 0 || b != 0) {
			(min(one.first, two.first)..max(one.first, two.first))
					.forEach { x -> allPoints.add(Point(x, x2y(x))) }
			(min(one.second, two.second)..max(one.second, two.second))
					.forEach { y -> allPoints.add(Point(y2x(y), y)) }
		}
	}

	fun x2y(x: Int) = if (0 == b) c / a else -(a * x + c) / b
	fun y2x(y: Int) = if (0 == a) c / b else -(b * y + c) / a
}
