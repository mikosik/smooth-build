package org.smoothbuild.run;

import static org.smoothbuild.compile.frontend.FrontendCompilerStep.frontendCompilerStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.run.eval.SaveArtifacts;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;

public class BuildStepFactory implements StepFactory<List<String>, String> {
  @Override
  public Step<Tuple0, String> create(List<String> names) {
    return step(RemoveArtifacts.class)
        .then(frontendCompilerStep())
        .append(names)
        .then(stepFactory(new EvaluateStepFactory()))
        .then(step(SaveArtifacts.class).named("Saving artifact(s)"));
  }
}
