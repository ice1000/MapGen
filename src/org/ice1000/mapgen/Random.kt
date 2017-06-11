package org.ice1000.mapgen

import java.util.*

/**
 * Created by ice1000 on 17-6-11.
 *
 * @author ice1000
 */

private val random = Random(System.currentTimeMillis())

fun rand() = random.nextInt()
fun rand(i: Int) = random.nextInt(i)
fun rand(from: Int, to: Int) = rand(to - from) + from
fun randPt(i: Int) = Pair(random.nextInt(i), random.nextInt(i))
fun randPt(i: Int, j: Int) = Pair(random.nextInt(i), random.nextInt(j))

infix inline operator fun Int.rem(block: () -> Unit) {
	if (rand(100) < this) block()
}
