package org.smoothbuild.run;

import static org.smoothbuild.common.step.Step.step;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.compile.frontend.FrontendCompilerStep.frontendCompilerStep;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.run.eval.SaveArtifacts;

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
