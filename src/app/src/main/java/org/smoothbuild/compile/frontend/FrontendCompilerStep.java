package org.smoothbuild.compile.frontend;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.compile.frontend.lang.define.ScopeS.scopeS;
import static org.smoothbuild.filesystem.install.InstallationLayout.STANDARD_LIBRARY_MODULES;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.DEFAULT_MODULE_FILE_PATH;
import static org.smoothbuild.out.log.Try.success;
import static org.smoothbuild.run.step.Step.constStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.frontend.compile.ConvertPs;
import org.smoothbuild.compile.frontend.compile.DecodeLiterals;
import org.smoothbuild.compile.frontend.compile.DetectUndefined;
import org.smoothbuild.compile.frontend.compile.InitializeScopes;
import org.smoothbuild.compile.frontend.compile.InjectDefaultArguments;
import org.smoothbuild.compile.frontend.compile.LoadInternalModuleMembers;
import org.smoothbuild.compile.frontend.compile.ast.SortModuleMembersByDependency;
import org.smoothbuild.compile.frontend.compile.infer.InferTypes;
import org.smoothbuild.compile.frontend.lang.define.ModuleS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.compile.frontend.parse.FindSyntaxErrors;
import org.smoothbuild.compile.frontend.parse.Parse;
import org.smoothbuild.compile.frontend.parse.TranslateAp;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;

public class FrontendCompilerStep {
  private static final List<FilePath> MODULES =
      listOfAll(STANDARD_LIBRARY_MODULES).append(DEFAULT_MODULE_FILE_PATH);

  public static Step<Tuple0, ScopeS> frontendCompilerStep() {
    var step = step(LoadInternalModuleMembers.class);
    for (var filePath : MODULES) {
      step = step.append(filePath)
          .then(stepFactory(new FrontendCompilerStepFactory()).named(filePath.toString()));
    }
    return step.named("Parsing");
  }

  public static class FrontendCompilerStepFactory
      implements StepFactory<Tuple2<ScopeS, FilePath>, ScopeS> {
    @Override
    public Step<Tuple0, ScopeS> create(Tuple2<ScopeS, FilePath> argument) {
      var scopeS = argument.element1();
      var filePath = argument.element2();
      return constStep(filePath)
          .then(step(ReadFileContent.class))
          .append(filePath)
          .then(step(Parse.class))
          .append(filePath)
          .then(step(TranslateAp.class))
          .then(step(FindSyntaxErrors.class))
          .then(step(DecodeLiterals.class))
          .then(step(InitializeScopes.class))
          .append(scopeS)
          .then(step(DetectUndefined.class))
          .append(scopeS)
          .then(step(InjectDefaultArguments.class))
          .then(step(SortModuleMembersByDependency.class))
          .append(scopeS)
          .then(step(InferTypes.class))
          .append(scopeS)
          .then(step(ConvertPs.class))
          .then(step((ModuleS m) -> success(scopeS(scopeS, m.members()))));
    }
  }
}
