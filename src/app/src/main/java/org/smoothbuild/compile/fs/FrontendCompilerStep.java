package org.smoothbuild.compile.fs;

import static org.smoothbuild.compile.fs.lang.define.ScopeS.scopeS;
import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_MODS;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.DEFAULT_MODULE_FILE_PATH;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.run.step.Step.constStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;

import org.smoothbuild.compile.fs.fp.FindSyntaxErrors;
import org.smoothbuild.compile.fs.fp.Parse;
import org.smoothbuild.compile.fs.fp.TranslateAp;
import org.smoothbuild.compile.fs.lang.define.ModuleS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.ps.ConvertPs;
import org.smoothbuild.compile.fs.ps.DecodeLiterals;
import org.smoothbuild.compile.fs.ps.DetectUndefined;
import org.smoothbuild.compile.fs.ps.InitializeScopes;
import org.smoothbuild.compile.fs.ps.InjectDefaultArguments;
import org.smoothbuild.compile.fs.ps.LoadInternalModuleMembers;
import org.smoothbuild.compile.fs.ps.ast.SortModuleMembersByDependency;
import org.smoothbuild.compile.fs.ps.infer.InferTypes;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;

import com.google.common.collect.ImmutableList;

import io.vavr.Tuple0;
import io.vavr.Tuple2;

public class FrontendCompilerStep {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(STD_LIB_MODS)
          .add(DEFAULT_MODULE_FILE_PATH)
          .build();

  public static Step<Tuple0, ScopeS> frontendCompilerStep() {
    var step = step(LoadInternalModuleMembers.class);
    for (var filePath : MODULES) {
      step = step
          .append(filePath)
          .then(stepFactory(new FrontendCompilerStepFactory())
              .named(filePath.toString()));
    }
    return step.named("Parsing");
  }

  public static class FrontendCompilerStepFactory
      implements StepFactory<Tuple2<ScopeS, FilePath>, ScopeS> {
    @Override
    public Step<Tuple0, ScopeS> create(Tuple2<ScopeS, FilePath> argument) {
      var scopeS = argument._1();
      var filePath = argument._2();
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
          .then(step((ModuleS m) -> maybe(scopeS(scopeS, m.members()))));
    }
  }
}
