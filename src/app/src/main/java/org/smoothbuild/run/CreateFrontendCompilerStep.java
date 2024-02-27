package org.smoothbuild.run;

import static org.smoothbuild.compile.frontend.FrontendCompilerStep.createFrontendCompilerStep;

import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.layout.Layout;

public class CreateFrontendCompilerStep {
  public static Step<Tuple0, ScopeS> frontendCompilerStep() {
    return createFrontendCompilerStep(Layout.MODULES);
  }
}
