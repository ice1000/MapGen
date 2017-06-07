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
	}
}
