package org.ice1000.mapgen

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by ice1000 on 17-6-9.
 * @author ice1000
 */
class GraphicsKtTest {
	@Test
	fun randTest() {
		(0..100).forEach {
			val limit = rand()
			assertTrue(rand(limit) < limit)
		}
	}

	@Test
	fun randTest1() {
		(0..100).forEach {
			val limit = rand()
			val limit2 = rand(limit)
			val test = rand(limit2, limit)
			assertTrue(test < limit)
			assertTrue(test > limit)
		}
	}
}