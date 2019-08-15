package mcdts.core.models;

import org.epochx.gr.model.GRModel;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.grammar.Grammar;

import mcdts.core.operators.MCTSInitializer;
import mcdts.utils.DistCalculator;

public class TestModel extends GRModel {

	private final String[] argNames;

	private final String targetProgram = "(((((!(d2))?d1:d0-((!(d1))?d3:d0)?d0:d2))?(!(d1))?d2:d0-!(d3)-!(d3):!(d3)-((d2+!(d0)+d2-d0)-((!(d3)+!(d1)))?(d2)?!(d2):!(d3):!(d1)-d2)?((!(d1)-((!(d3))?!(d2):!(d2))?(!(d3)-d1):(d0+!(d3))))?((!(d1)+!(d1)))?d3:d0:(d3-!(d1))-((d1-!(d0))+(d0-!(d0))):((!(d2)-!(d3))-(!(d3)+d0))?(d1+!(d2)+(!(d0)-d1)):((!(d3))?!(d2):!(d2))?(!(d3)-d1):(d0+!(d3))-(!(d1)-d3))?((d1-d1)?d2+d3:(d3+d1)-((d1)?d3:!(d1)-((d2-!(d3))-(d0+!(d0)))?!(d3):d3)):((!(d3)+d1)-(!(d3))?d2:!(d0))?(d0-!(d0))?(d0)?d2:d0:(d1-d1):(d2-!(d3))-(d0+!(d0)))?(((d0+d3)-d0+!(d3)+(!(d2))?!(d0):!(d0)-(!(d1))?d0:!(d3))+d1+!(d1)+(!(d2))?!(d3):!(d3)-((!(d1))?d0:!(d3)+(d3+!(d1)))):((!(d0)+d2)?(d3+d0):(((d2+!(d0)+d2-d0)-((!(d3)+!(d1)))?(d2)?!(d2):!(d3):!(d1)-d2)?((!(d1)-d2))?(!(d1))?d3:d0:(d3-!(d1))-((d1-!(d0))+(d0-!(d0))):((!(d2)-!(d3))-(!(d3)+d0))?(d1+!(d2)+(!(d0)-d1)):((!(d3))?!(d2):!(d2))?(!(d3)-d1):(d0+!(d3))-d1)+(d1-!(d1))-!(d3)-d2)?(((d0)?d0:d2-(!(d1)+!(d1)))+(!(d2)+d0+(!(d1))?d1:d2)):((d3)?d0:d3-!(d1)-d3+(!(d1)-!(d3))?(!(d0)+!(d3)):(!(d1)+d0))";

	private static final String GRAMMAR_FRAGMENT = "<prog> ::= <expr>\n" + "<expr> ::= <expr> <op> <expr> "
			+ "| ( <expr> <op> <expr> ) " + "| <var> " + "| <pre-op> ( <var> ) " + "| ( <expr> ) ? <expr> : <expr>\n"
			+ "<pre-op> ::= !\n" + "<op> ::= \"+\" | -\n" + "<var> ::= ";

	public TestModel(final int noInputBits) {

		// Determine the input argument names.
		argNames = new String[noInputBits];
		for (int i = 0; i < noInputBits; i++) {
			argNames[i] = "d" + i;
		}

		// Complete the grammar string and construct grammar instance.
		setGrammar(new Grammar(getGrammarString()));
		
		setInitialiser(new MCTSInitializer(this));
	}

	@Override
	public double getFitness(CandidateProgram program) {
		final GRCandidateProgram p_aux = (GRCandidateProgram) program;
		double fitness = 0;
		String word = p_aux.toString();
		fitness = DistCalculator.getLevenshteinDistance(word, this.targetProgram);
		return fitness;
	}

	/**
	 * Constructs and returns the full grammar string for the majority problem
	 * with the correct number of input bits.
	 * 
	 * @return the grammar string for the majority problem with the set number
	 *         of input bits
	 */
	public String getGrammarString() {
		final StringBuilder buffer = new StringBuilder(GRAMMAR_FRAGMENT);
		for (int i = 0; i < argNames.length; i++) {
			if (i > 0) {
				buffer.append(" | ");
			}
			buffer.append(argNames[i]);
		}
		buffer.append('\n');

		return buffer.toString();
	}

}
