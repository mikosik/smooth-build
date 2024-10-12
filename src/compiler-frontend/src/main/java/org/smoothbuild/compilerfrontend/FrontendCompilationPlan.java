package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.plan.Plan.apply1;
import static org.smoothbuild.common.plan.Plan.apply2;
import static org.smoothbuild.common.plan.Plan.evaluate;
import static org.smoothbuild.common.plan.Plan.value;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import jakarta.inject.Inject;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.Plan;
import org.smoothbuild.common.plan.TryFunction2;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.compilerfrontend.compile.ConvertPs;
import org.smoothbuild.compilerfrontend.compile.DecodeLiterals;
import org.smoothbuild.compilerfrontend.compile.DetectUndefined;
import org.smoothbuild.compilerfrontend.compile.FindSyntaxErrors;
import org.smoothbuild.compilerfrontend.compile.InitializeScopes;
import org.smoothbuild.compilerfrontend.compile.InjectDefaultArguments;
import org.smoothbuild.compilerfrontend.compile.LoadInternalModuleMembers;
import org.smoothbuild.compilerfrontend.compile.Parse;
import org.smoothbuild.compilerfrontend.compile.ReadFileContent;
import org.smoothbuild.compilerfrontend.compile.SortModuleMembersByDependency;
import org.smoothbuild.compilerfrontend.compile.TranslateAp;
import org.smoothbuild.compilerfrontend.compile.infer.InferTypes;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class FrontendCompilationPlan {
  public static Plan<SModule> frontendCompilationPlan(List<FullPath> modules) {
    var loadingPlan = Plan.task0(LoadInternalModuleMembers.class);
    for (var fullPath : modules) {
      loadingPlan = evaluate(apply2(InflatePlan.class, loadingPlan, value(fullPath)));
    }
    return loadingPlan;
  }

  public static class InflatePlan implements TryFunction2<SModule, FullPath, Plan<SModule>> {
    private final TaskExecutor taskExecutor;

    @Inject
    public InflatePlan(TaskExecutor taskExecutor) {
      this.taskExecutor = taskExecutor;
    }

    @Override
    public Label label() {
      return Label.label(COMPILE_PREFIX, "inflateFrontendCompilationPlan");
    }

    @Override
    public Try<Plan<SModule>> apply(SModule importedModule, FullPath fullPath) {
      var environmentLegacy = value(importedModule.membersAndImported());
      var environment = promise(importedModule.membersAndImported());
      var path = promise(fullPath);
      var fileContent = taskExecutor.submit(ReadFileContent.class, path);
      var moduleContext = taskExecutor.submit(Parse.class, fileContent, path);
      var moduleP = taskExecutor.submit(TranslateAp.class, moduleContext, path);
      var withSyntaxCheck = taskExecutor.submit(FindSyntaxErrors.class, moduleP);
      var withDecodedLiterals = taskExecutor.submit(DecodeLiterals.class, withSyntaxCheck);
      var withInitializedScopes = taskExecutor.submit(InitializeScopes.class, withDecodedLiterals);
      var withUndefinedDetected =
          Plan.task2(DetectUndefined.class, withInitializedScopes, environment);
      var withInjected =
          apply2(InjectDefaultArguments.class, withUndefinedDetected, environmentLegacy);
      var sorted = apply1(SortModuleMembersByDependency.class, withInjected);
      var typesInferred = apply2(InferTypes.class, sorted, environmentLegacy);
      var moduleS = apply2(ConvertPs.class, typesInferred, environmentLegacy);
      return success(moduleS);
    }
  }
}
