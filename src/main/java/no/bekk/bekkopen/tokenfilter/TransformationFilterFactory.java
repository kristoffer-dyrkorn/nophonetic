/*

TransformationFilterFactory.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.bekk.bekkopen.transformator.GenericTransformator;
import no.bekk.bekkopen.transformator.TransformationRule;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;


public class TransformationFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {

	private String ruleFile = "phonetic.dat";
	private List<TransformationRule> rules = new ArrayList<TransformationRule>();
	
	@Override
	public void init(Map<String, String> args) {
		super.init(args);
		final String cfgRuleFile = args.get("ruleFile");
		if (cfgRuleFile != null) {
			ruleFile = cfgRuleFile;
		}
	}

	public void inform(ResourceLoader loader) throws IOException {
		BufferedReader ruleReader = new BufferedReader(new InputStreamReader(loader.openResource(ruleFile)));
		rules = TransformationRule.buildRules(ruleReader);
	}
	
	public TransformationFilter create(TokenStream input) {
		GenericTransformator trans = new GenericTransformator(rules);
		return new TransformationFilter(input, trans);
	}
}