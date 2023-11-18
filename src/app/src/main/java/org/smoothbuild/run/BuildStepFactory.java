package org.smoothbuild.run;

import static org.smoothbuild.compile.frontend.FrontendCompilerStep.frontendCompilerStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;

import org.smoothbuild.run.eval.SaveArtifacts;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;

import io.vavr.Tuple0;
import io.vavr.collection.Array;

public class BuildStepFactory implements StepFactory<Array<String>, String> {
  @Override
  public Step<Tuple0, String> create(Array<String> names) {
    return step(RemoveArtifacts.class)
        .then(frontendCompilerStep())
        .append(names)
        .then(stepFactory(new EvaluateStepFactory()))
        .then(step(SaveArtifacts.class).named("Saving artifact(s)"));
  }
}
