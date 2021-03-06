package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.evalTos.IPredictor;
import fr.inria.astor.approaches.tos.core.evalTos.MultiMetaEvalTOSApproach;
import fr.inria.astor.approaches.tos.core.evalTos.Prediction;
import fr.inria.astor.approaches.tos.core.evalTos.PredictionElement;
import fr.inria.astor.approaches.tos.core.evalTos.PredictionResult;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MultiMetEngineSimpleProgramsTest {

	@Test
	public void test_doomy_Multi_with_target() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testMulti1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-mr1a");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 24
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java")))
				.findAny().get();
		assertNotNull(mp24);

		Prediction prediction = new Prediction();

		approach.setPredictor(new IPredictor() {

			@Override
			public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {
				// No prediction
				return null;
			}
		});

		CtElement binary = mp24.getCodeElement().getElements(e -> e.toString().equals("i2 > i1")).get(0);
		assertNotNull(binary);

		CtReturn rnt = (CtReturn) mp24.getCodeElement().getElements(e -> e.toString().equals("return i1")).get(0);
		assertNotNull(rnt);

		prediction.add(new PredictionElement(1, binary), new OperatorReplacementOp());
		prediction.add(new PredictionElement(2, rnt.getReturnedExpression()), new VarReplacementByAnotherVarOp());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();

		List<ProgramVariant> solutions = approach.getSolutions();

		assertTrue(solutions.size() > 0);
		ProgramVariant solutions0 = solutions.get(0);
		assertEquals(2, solutions0.getAllOperations().size());

	}

	@Test
	public void test_doomy_Multi_without_target() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testMulti1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-mr1b");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 24
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java")))
				.findAny().get();
		assertNotNull(mp24);

		Prediction prediction = new Prediction();

		CtElement binary = mp24.getCodeElement().getElements(e -> e.toString().equals("i2 > i1")).get(0);
		assertNotNull(binary);

		CtReturn rnt = (CtReturn) mp24.getCodeElement().getElements(e -> e.toString().equals("return i1")).get(0);
		assertNotNull(rnt);

		prediction.add(new PredictionElement(1, binary), new OperatorReplacementOp());
		prediction.add(new PredictionElement(2, rnt.getReturnedExpression()), new VarReplacementByAnotherVarOp());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();
	}

}
