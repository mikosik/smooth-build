package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.step.Step.constStep;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.common.step.Step.tryStep;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.compile.ConvertPs;
import org.smoothbuild.compilerfrontend.compile.DecodeLiterals;
import org.smoothbuild.compilerfrontend.compile.DetectUndefined;
import org.smoothbuild.compilerfrontend.compile.InitializeScopes;
import org.smoothbuild.compilerfrontend.compile.InjectDefaultArguments;
import org.smoothbuild.compilerfrontend.compile.LoadInternalModuleMembers;
import org.smoothbuild.compilerfrontend.compile.ast.SortModuleMembersByDependency;
import org.smoothbuild.compilerfrontend.compile.infer.InferTypes;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.parse.FindSyntaxErrors;
import org.smoothbuild.compilerfrontend.parse.Parse;
import org.smoothbuild.compilerfrontend.parse.TranslateAp;

public class FrontendCompilerStep {

  public static Step<Tuple0, ScopeS> createFrontendCompilerStep(List<FullPath> modules) {
    var step = Step.tryStep(LoadInternalModuleMembers.class);
    for (var fullPath : modules) {
      step = step.append(fullPath)
          .then(stepFactory(new FrontendCompilerStepFactory()).named(fullPath.toString()));
    }
    return step.named("Parsing");
  }

  public static class FrontendCompilerStepFactory
      implements StepFactory<Tuple2<ScopeS, FullPath>, ScopeS> {
    @Override
    public Step<Tuple0, ScopeS> create(Tuple2<ScopeS, FullPath> argument) {
      var scopeS = argument.element1();
      var fullPath = argument.element2();
      return constStep(fullPath)
          .then(tryStep(ReadFileContent.class))
          .append(fullPath)
          .then(tryStep(Parse.class))
          .append(fullPath)
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
          .then(Step.tryStep(ConvertPs.class))
          .then(tryStep((ModuleS m) -> success(ScopeS.scopeS(scopeS, m.members()))));
    }
  }
}
