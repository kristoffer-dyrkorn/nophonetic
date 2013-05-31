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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransformationRule {

	public static final String[] IGNORED_KEYWORDS = {"version", "followup", "collapse_result"};
	public static final char STARTMULTI = '(';
	public static final char ENDMULTI = ')';
	public static final String REPLACEVOID = "_";
	public static final String DIGITCODE = "0";

	public String replace;
	private char[] match;
	public int takeOut, matchLength;
	public boolean start, end;

	public TransformationRule(String match, String replace, int takeout, int matchLength, boolean start, boolean end) {
		this.match = match.toCharArray();
		this.replace = replace;
		this.takeOut = takeout;
		this.matchLength = matchLength;
		this.start = start;
		this.end = end;
	}

	public static List<TransformationRule> buildRules(BufferedReader ruleReader) throws IOException {
		List<TransformationRule> rules = new ArrayList<TransformationRule>();
		String read = null;
		while ((read = ruleReader.readLine()) != null) {
			TransformationRule r = buildRule(read);
			if (r != null) {
				rules.add(r);
			}
		}
		return rules;
	}
	
	private static TransformationRule buildRule(String str) {
		str = trimComments(str);
		
		if (str.length() < 1) {
			return null;
		}
		for (int i = 0; i < IGNORED_KEYWORDS.length; i++) {
			if (str.startsWith(IGNORED_KEYWORDS[i]))
				return null;
		}

		StringBuffer matchExp = new StringBuffer();
		StringBuffer replaceExp = new StringBuffer();
		boolean start = false, end = false;
		int takeOutPart = 0, matchLength = 0;
		boolean match = true, inMulti = false;

		for (int i = 0; i < str.length(); i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				match = false;
			} else {
				if (match) {
					if (!isReservedChar(str.charAt(i))) {
						matchExp.append(str.charAt(i));
						if (!inMulti) {
							takeOutPart++;
							matchLength++;
						}
						if (str.charAt(i) == STARTMULTI || str.charAt(i) == ENDMULTI)
							inMulti = !inMulti;
					}
					if (str.charAt(i) == '-')
						takeOutPart--;
					if (str.charAt(i) == '^')
						start = true;
					if (str.charAt(i) == '$')
						end = true;
				} else {
					replaceExp.append(str.charAt(i));
				}
			}
		}
		if (replaceExp.toString().equals(REPLACEVOID)) {
			replaceExp = new StringBuffer("");
		}
		return new TransformationRule(matchExp.toString(), replaceExp.toString(), takeOutPart, matchLength, start, end);
	}
	
	private static String trimComments(String row) {
		int pos = row.indexOf('#');
		if (pos != -1) {
			row = row.substring(0, pos);
		}
		return row.trim();
	}

	private static boolean isReservedChar(char ch) {
		if (ch == '<' || ch == '>' || ch == '^' || ch == '$' || ch == '-' || Character.isDigit(ch)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isMatching(StringBuffer word, int wordPos) {
		boolean matching = true, inMulti = false, multiMatch = false;
		char matchCh;

		for (int matchPos = 0; matchPos < match.length; matchPos++) {
			matchCh = match[matchPos];
			if (matchCh == STARTMULTI || matchCh == ENDMULTI) {
				inMulti = !inMulti;
				if (!inMulti)
					matching = matching & multiMatch;
				else
					multiMatch = false;
			} else {
				if (matchCh != word.charAt(wordPos)) {
					if (inMulti)
						multiMatch = multiMatch | false;
					else
						matching = false;
				} else {
					if (inMulti)
						multiMatch = multiMatch | true;
					else
						matching = true;
				}
				if (!inMulti)
					wordPos++;
				if (!matching)
					break;
			}
		}
		if (end && wordPos != word.length()) {
			matching = false;
		}
		return matching;
	}
}
