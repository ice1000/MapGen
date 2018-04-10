/**
 * Created by ice1000 on 17-6-5.
 *
 * @author ice1000
 */
@file:JvmName(CLASS_NAME)
@file:JvmMultifileClass

package org.ice1000.mapgen

import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.*

const val CLASS_NAME = "MapGen"

typealias Point = Pair<Int, Int>

const val MAGIC_NUM_1 = 1600

fun main(vararg args: String) {
	var image = run().generateImage()
	args.firstOrNull()?.let {
		image.write(it)
	} ?: JFrame().apply frame@{
		title = "MapGen by ice1000"
		layout = BorderLayout()
		setSize(image.width, image.height + 20)
		add(object : JComponent() {
			override fun paintComponent(g: Graphics) {
				super.paintComponent(g)
				g.drawImage(image, 0, 0, null)
			}
		}, BorderLayout.CENTER)
		add(JButton("Regenerate").apply {
			addActionListener {
				this@frame.repaint()
				image = run().generateImage()
			}
		}, BorderLayout.SOUTH)
		defaultCloseOperation = 3
		isVisible = true
	}
}

fun run(): GameMap {
	/// the first map
	val map1 = gameMapOf(60, 60)
	val ls = map1.genRandPts(11)
	/// initial points
	map1 {
		ls.forEach { (x, y, i) ->
			val v = rand(200) + 1000 + i * (MAGIC_NUM_1 / ls.size)
			Pair(x, y).pnd.forEach { p ->
				set(p.first, p.second, v + rand(100) - 50)
				p.pnd.forEach { (x, y) -> set(x, y, v + rand(100) - 50) }
			}
			set(x, y, v)
		}
	}
	ls eachTwo { (x1, y1, _), (x2, y2, _) ->
		if (rand(5) >= 2) {
			map1 {
				val k = (map1[x1, y1] + map1[x2, y2]) shr 1
				Line(Point(x1, y1), Point(x2, y2)).allPoints.forEach { (x, y) ->
					70 % { map1[x, y] = k + rand(200) - 100 }
					Point(x, y).pnd.forEach { 70 % { map1[it] = k + rand(200) - 100 } }
				}
			}
		}
	}
	repeat(200) {
		val x = rand(map1.width - 2) + 1
		val y = rand(map1.height - 2) + 1
		map1[x, y] = rand(500) + 300
	}
	map1.averagify8().averagify().averagify8()
	/// expand the size, the second map
	val map2 = map1.doublify()
	/// traverse and add random points
	map2.traverse { (x, y, i) -> map2[x, y] = rand(300) - 150 + i }
	/// random points, as islands
	val ls2 = map2.genRandPts(20)
	map2 {
		ls2.forEach { (x, y, i) ->
			val v = rand(300) + 500 + i * (MAGIC_NUM_1 / ls2.size)
			Pair(x, y).pnd.forEach { p ->
				val (x2, y2) = p
				set(x2, y2, v + rand(100) - 50)
				p.pnd.forEach { p2 ->
					val (x3, y3) = p2
					set(x3, y3, v + rand(100) - 50)
					p2.pnd.forEach { (x, y) ->
						set(x, y, v + rand(100) - 50)
					}
				}
			}
			set(x, y, v)
		}
	}
	/// expand the map size, the final map
	map2.averagify8().averagify8().averagify()
	val map3 = map2.triplify()
	map3.averagify8().averagify8().averagify()
	/// now the map is ready
	/// rivers(based on A* algorithm)
	repeat(rand(6, 9)) { map3.rivers += map3.river }
	return map3
}

fun GameMap.generateImage() = image(width, height) {
	traverse { (x, y, i) ->
		color(x, y, when (i) {
			in -10000..300 -> DEEP_BLUE
			in 0..500 -> BLUE
			in 0..900 -> SHALLOW_BLUE
//				in 0..900 -> SAND
			in 0..1200 -> MIDDLE_GREEN
			in 0..1400 -> L_LIGHT_GREEN
			in 0..1600 -> LIGHT_GREEN
			in 0..1700 -> DARK_GREEN
			in 0..1900 -> M_DARK_GREEN
			in 0..2070 -> if (1 == rand(72)) GRAY else BROWN
			else -> if (1 == rand(24)) GRAY else WHITE
		})
	}
	val rg = 801..960
	fun bfs(p: Point, block: (Point) -> List<Point>) {
		val q: Queue<Point> = LinkedList<Point>()
		q.offer(p)
		var i = 0
		while (q.isNotEmpty() && ++i < 50000) {
			if (contains(q.peek()) && colorOf(q.peek()) != SAND && get(q.peek()) in rg) {
				block(q.peek()).filter { it !in q && get(it) in rg }.forEach {
					q.offer(it)
					color(it, SAND)
				}
			}
			q.poll()
		}
	}

	val offset = 10
	val i = rand(offset, width - offset)
	var j = 0
	while (j < height && get(i, j) !in rg) ++j
	bfs(Point(i, j), { it.pndL })
	bfs(Point(i, j), { it.pndR })
	j = height - 1
	while (j > 0 && get(i, j) !in rg) --j
	bfs(Point(i, j), { it.pndL })
	bfs(Point(i, j), { it.pndR })
	/// pumpkins
	repeat(6) {
		genRandPtSatisfying { get(it) in 1151..1450 }.pnd9
				.forEach { color(it, ORANGE) }
	}
	rivers.forEach { it.flatMap { it.pnd5 }.forEach { color(it, SHALLOW_BLUE) } }
}
