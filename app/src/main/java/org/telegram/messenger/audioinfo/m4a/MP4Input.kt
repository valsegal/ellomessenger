/*
 * Copyright 2013-2014 Odysseus Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.telegram.messenger.audioinfo.m4a

import org.telegram.messenger.audioinfo.util.PositionInputStream
import java.io.IOException
import java.io.InputStream

class MP4Input(delegate: InputStream?) : MP4Box<PositionInputStream>(PositionInputStream(delegate), null, "") {
	@Throws(IOException::class)
	fun nextChildUpTo(expectedTypeExpression: String): MP4Atom {
		while (true) {
			val atom = nextChild()

			if (atom.type.matches(expectedTypeExpression.toRegex())) {
				return atom
			}
		}
	}

	override fun toString(): String {
		return "mp4[pos=$position]"
	}
}
