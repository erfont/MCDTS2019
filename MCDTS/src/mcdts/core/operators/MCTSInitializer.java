package mcdts.core.operators;

import java.util.ArrayList;
import java.util.List;

import org.epochx.gr.model.GRModel;
import org.epochx.gr.op.init.FullInitialiser;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.grammar.Grammar;
import org.epochx.tools.grammar.GrammarLiteral;
import org.epochx.tools.grammar.GrammarNode;
import org.epochx.tools.grammar.GrammarProduction;
import org.epochx.tools.grammar.GrammarRule;
import org.epochx.tools.grammar.NonTerminalSymbol;
import org.epochx.tools.grammar.TerminalSymbol;
import org.epochx.tools.random.RandomNumberGenerator;

import mcdts.utils.MCTSNTSymbol;

public class MCTSInitializer extends FullInitialiser {

	public MCTSInitializer(GRModel model, boolean acceptDuplicates) {
		super(model, acceptDuplicates);
	}

	public MCTSInitializer(RandomNumberGenerator rng, Grammar grammar, int popSize, int depth,
			boolean acceptDuplicates) {
		super(rng, grammar, popSize, depth, acceptDuplicates);
		// TODO Auto-generated constructor stub
	}

	public MCTSInitializer(GRModel model) {
		super(model);
		System.out.println("MCTS Initializer created!");

	}

//	@Override
//	public List<CandidateProgram> getInitialPopulation() {
//		System.out.println("MCTS Initializer running!");
//		return super.getInitialPopulation();
//	}
	
	/**
	 * Generates a population of new <code>CandidatePrograms</code> constructed
	 * from the <code>Grammar</code> attribute. The size of the population will
	 * be equal to the population size attribute. All programs in the population
	 * are only guarenteed to be unique (as defined by the <code>equals</code>
	 * method on <code>GRCandidateProgram</code>) if the
	 * <code>isDuplicatesEnabled</code> method returns <code>true</code>. Each
	 * program will have a full parse tree with a depth equal to the depth
	 * attribute.
	 * 
	 * @return A <code>List</code> of newly generated
	 *         <code>GRCandidateProgram</code> instances with full parse trees.
	 */
	@Override
	public List<CandidateProgram> getInitialPopulation() {
		System.out.println("MCTS Initializer running!");
		if (getPopSize() < 1) {
			throw new IllegalStateException("Population size must be 1 or greater");
		}

		// Create population list to be populated.
		final List<CandidateProgram> firstGen = new ArrayList<CandidateProgram>(getPopSize());

		// Create and add new programs to the population.
		for (int i = 0; i < getPopSize(); i++) {
			GRCandidateProgram candidate;
			do {
				// Create a new program at the models initial max depth.
				candidate = getInitialProgram();
			} while (!isDuplicatesEnabled() && firstGen.contains(candidate));

			// Add to the new population.
			firstGen.add(candidate);
		}

		return firstGen;
	}

	/**
	 * Constructs and returns a new <code>GRCandidateProgram</code> with a full
	 * parse tree with the given depth.
	 * 
	 * @return The root node of a randomly generated full parse tree of the
	 *         requested depth.
	 */
	public GRCandidateProgram getInitialProgram() {
		if (getRNG() == null) {
			throw new IllegalStateException("No random number generator has been set");
		} else if (getGrammar() == null) {
			throw new IllegalStateException("No grammar has been set");
		}

		// Get the root of the grammar.
		final GrammarRule startRule = getGrammar().getStartRule();

		// Determine the minimum depth possible for a valid program.
		final int minDepth = startRule.getMinDepth();
		if (minDepth > getDepth()) {
			throw new IllegalStateException("No possible programs within given max depth parameter for this grammar.");
		}

		// Construct the root of the parse tree.
		final MCTSNTSymbol parseTree = new MCTSNTSymbol(startRule);

		// Build a tree below the root.
		buildDerivationTree(parseTree, startRule, 0, getDepth());

		// Construct and return the program.
		return new GRCandidateProgram(parseTree, getModel());
	}

	/*
	 * Builds a full parse tree from the given non-terminal symbol using the
	 * grammar rule.
	 */
	private void buildDerivationTree(final MCTSNTSymbol parseTree, final GrammarRule rule, final int currentDepth,
			final int maxDepth) {
		// Check if theres more than one production.
		int productionIndex = 0;
		final int noProductions = rule.getNoProductions();
		
		if (noProductions > 1) { // HERE'S WHERE MCTS LOGIC APPLIES. MORE THAN ONE AVAILABLE PRODUCTION, THEN SELECT BASED ON MCTS LOGIC
			final List<Integer> validProductions = getValidProductionIndexes(rule.getProductions(), maxDepth
					- currentDepth
					- 1);

			// APPLY LOGIC ONLY ON THE PRODUCTIONS RETURNED AS VALID PRODUCTIONS (DEPTH DEPENDANT) 
			// MCTS SELECT
			final int chosenProduction = getRNG().nextInt(validProductions.size());
			productionIndex = validProductions.get(chosenProduction);
		}

		// Drop down the tree at this production.
		final GrammarProduction p = rule.getProduction(productionIndex);

		final List<GrammarNode> grammarNodes = p.getGrammarNodes();
		for (final GrammarNode node: grammarNodes) {
			if (node instanceof GrammarRule) {
				final GrammarRule r = (GrammarRule) node;

				final MCTSNTSymbol nt = new MCTSNTSymbol((GrammarRule) node);

				buildDerivationTree(nt, r, currentDepth + 1, maxDepth);

				parseTree.addChild(nt);
			} else {
				// Must be a grammar literal.
				parseTree.addChild(new TerminalSymbol((GrammarLiteral) node));
			}
		}
	}

	/*
	 * Gets a List of indexes to those productions from the List of productions
	 * given that can be used with the specified maximum depth constraint.
	 */
	private List<Integer> getValidProductionIndexes(final List<GrammarProduction> grammarProductions, final int maxDepth) {
		final List<Integer> validRecursive = new ArrayList<Integer>();
		final List<Integer> validAll = new ArrayList<Integer>();

		for (int i = 0; i < grammarProductions.size(); i++) {
			final GrammarProduction p = grammarProductions.get(i);

			if (p.getMinDepth() <= maxDepth) {
				validAll.add(i);

				if (p.isRecursive()) {
					validRecursive.add(i);
				}
			}
		}

		// If there were any valid recursive productions, return them, otherwise
		// use the others.
		return validRecursive.isEmpty() ? validAll : validRecursive;
	}

}
