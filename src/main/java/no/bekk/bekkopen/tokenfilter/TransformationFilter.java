/*

TransformationFilter.java
Copyright (C) 2013 Kristoffer Dyrkorn

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

package no.bekk.bekkopen.tokenfilter;

import java.io.IOException;

import no.bekk.bekkopen.transformator.GenericTransformator;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class TransformationFilter extends TokenFilter {

	private GenericTransformator trans = null;
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	public TransformationFilter(TokenStream in, GenericTransformator trans) {
		super(in);
		this.trans = trans;
	}
	
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String term = termAtt.toString();
			String encoding = trans.transform(term);
			final char[] encodingBuffer = encoding.toCharArray();
			final int encodingLength = encodingBuffer.length;
			termAtt.copyBuffer(encodingBuffer, 0, encodingLength);
			return true;
		} else {
			return false;
		}
	}
}