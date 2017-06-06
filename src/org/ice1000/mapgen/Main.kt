package org.ice1000.mapgen

/**
 * Created by ice1000 on 17-6-5.
 *
 * @author ice1000
 */

typealias Map = List<List<Int>>

fun main(vararg args: String) {
	image(50, 50) {
		write("out.png")
	}
}
