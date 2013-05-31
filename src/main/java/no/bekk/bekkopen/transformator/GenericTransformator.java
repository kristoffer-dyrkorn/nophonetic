/*

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

@author Robert Gustavsson (robert@lindesign.se)

*/

package no.bekk.bekkopen.transformator;

import java.util.ArrayList;
import java.util.List;


public class GenericTransformator {

	private List<TransformationRule> rules = new ArrayList<TransformationRule>();

	public GenericTransformator(List<TransformationRule> rules) {
		this.rules = rules;
	}

	public String transform(String word) {
		if (rules == null) {
			return "";
		}
		
		StringBuffer str = new StringBuffer(word.toUpperCase());
		int strLength = str.length();
		int startPos = 0, add = 1;

		while (startPos < strLength) {
			add = 1;
			if (Character.isDigit(str.charAt(startPos))) {
				replace(str, startPos, startPos + TransformationRule.DIGITCODE.length(), TransformationRule.DIGITCODE);
				startPos += add;
				continue;
			}

			for (TransformationRule rule : rules) {
				if (rule.start && startPos > 0) {
					continue;
				}
				if (startPos + rule.matchLength > strLength) {
					continue;
				}
				if (rule.isMatching(str, startPos)) {
					String replaceExp = rule.replace;
					add = replaceExp.length();
					replace(str, startPos, startPos + rule.takeOut, replaceExp);
					strLength -= rule.takeOut;
					strLength += add;
					break;
				}
			}
			startPos += add;
		}
		return str.toString();
	}

	public static StringBuffer replace(StringBuffer buf, int start, int end, String text) {
		int len = text.length();
		char[] ch = new char[buf.length() + len - (end - start)];
		buf.getChars(0, start, ch, 0);
		text.getChars(0, len, ch, start);
		buf.getChars(end, buf.length(), ch, start + len);
		buf.setLength(0);
		buf.append(ch);
		return buf;
	}
}
