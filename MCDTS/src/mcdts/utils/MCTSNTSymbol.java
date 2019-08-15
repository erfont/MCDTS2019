package mcdts.utils;

import org.epochx.tools.grammar.GrammarRule;
import org.epochx.tools.grammar.NonTerminalSymbol;

public class MCTSNTSymbol extends NonTerminalSymbol {
	
	static double epsilon = 1e-6;
	double nVisits, totValue;

	public MCTSNTSymbol(GrammarRule grammarRule) {
		super(grammarRule);
	}
	
	//IMPLEMENT MCTS LOGIC HERE NOW

}
