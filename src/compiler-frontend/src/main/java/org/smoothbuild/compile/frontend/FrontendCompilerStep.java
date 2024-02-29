package org.smoothbuild.compile.frontend;

import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.step.Step.constStep;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.common.step.Step.tryStep;
import static org.smoothbuild.compile.frontend.lang.define.ScopeS.scopeS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.space.FilePath;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
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

public class FrontendCompilerStep {

  public static Step<Tuple0, ScopeS> createFrontendCompilerStep(List<FilePath> modules) {
    var step = tryStep(LoadInternalModuleMembers.class);
    for (var filePath : modules) {
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
          .then(tryStep(ReadFileContent.class))
          .append(filePath)
          .then(tryStep(Parse.class))
          .append(filePath)
          .then(tryStep(TranslateAp.class))
          .then(tryStep(FindSyntaxErrors.class))
          .then(tryStep(DecodeLiterals.class))
          .then(tryStep(InitializeScopes.class))
          .append(scopeS)
          .then(tryStep(DetectUndefined.class))
          .append(scopeS)
          .then(tryStep(InjectDefaultArguments.class))
          .then(tryStep(SortModuleMembersByDependency.class))
          .append(scopeS)
          .then(tryStep(InferTypes.class))
          .append(scopeS)
          .then(tryStep(ConvertPs.class))
          .then(tryStep((ModuleS m) -> success(scopeS(scopeS, m.members()))));
    }
  }
}
