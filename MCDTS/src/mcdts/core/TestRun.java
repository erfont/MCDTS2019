package mcdts.core;

import org.epochx.gr.model.GRModel;
import org.epochx.life.GenerationAdapter;
import org.epochx.life.Life;
import org.epochx.stats.StatField;
import org.epochx.stats.Stats;

import mcdts.core.models.TestModel;

public class TestRun {

	public static void main(String[] args) {

		GRModel m = new TestModel(4);
		
//		m.setNoGenerations(10);
//		m.setNoRuns(3);
//		m.setTerminationFitness(500);
		

		Life.get().addGenerationListener(new GenerationAdapter() {
			public void onGenerationEnd() {
				Stats.get().print(StatField.RUN_NUMBER, StatField.GEN_NUMBER, StatField.GEN_FITNESS_MIN, StatField.GEN_FITTEST_PROGRAM);
			}
		});
		m.run();

	}

}
