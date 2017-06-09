package org.ice1000.mapgen

import org.junit.Assert.assertEquals

/**
 * Created by ice1000 on 17-6-8.
 * @author ice1000
 */
class LineTest {
	@org.junit.Test
	fun getAllPoints() {
		assertEquals(setOf(Pair(1, 1), Pair(2, 2), Pair(3, 3)), Line(Pair(1, 1), Pair(3, 3)).allPoints)
		assertEquals(setOf(Pair(1, 1), Pair(2, 2)), Line(Pair(1, 1), Pair(2, 2)).allPoints)
		assertEquals(Line(Pair(3, 3), Pair(1, 1)).allPoints, Line(Pair(1, 1), Pair(3, 3)).allPoints)
	}

	@org.junit.Test
	fun getAllPoints2() {
		(0..1000).forEach {
			val x1 = rand(100)
			val y1 = rand(100)
			val x2 = rand(100)
			val y2 = rand(100)
			assertEquals(Line(Pair(x1, y1), Pair(x2, y2)).allPoints, Line(Pair(x2, y2), Pair(x1, y1)).allPoints)
		}
	}
}
