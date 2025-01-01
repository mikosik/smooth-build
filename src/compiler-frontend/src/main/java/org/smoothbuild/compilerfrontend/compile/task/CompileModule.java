package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.infer.InferTypes;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class CompileModule implements Task2<SModule, FullPath, SModule> {
  private final Scheduler scheduler;

  @Inject
  public CompileModule(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<SModule> execute(SModule importedModule, FullPath fullPath) {
    var importedScope = argument(importedModule.fullScope());
    var path = argument(fullPath);
    var fileContent = scheduler.submit(ReadFileContent.class, path);
    var moduleContext = scheduler.submit(Parse.class, fileContent, path);
    var pModule = scheduler.submit(TranslateAp.class, moduleContext, path);
    var withVerifiedIds = scheduler.submit(GenerateIds.class, pModule);
    var withGeneratedDefaults = scheduler.submit(GenerateDefaultValues.class, withVerifiedIds);
    var withSyntaxCheck = scheduler.submit(FindSyntaxErrors.class, withGeneratedDefaults);
    var withDecodedLiterals = scheduler.submit(DecodeLiterals.class, withSyntaxCheck);
    var withGeneratedConstructors =
        scheduler.submit(GenerateConstructors.class, withDecodedLiterals);
    var withInitializedScopes =
        scheduler.submit(GenerateScopes.class, importedScope, withGeneratedConstructors);
    var withUndefinedDetected = scheduler.submit(DetectUndefined.class, withInitializedScopes);
    var withInjected = scheduler.submit(InjectDefaultArguments.class, withUndefinedDetected);
    var sorted = scheduler.submit(SortModuleMembersByDependency.class, withInjected);
    var typesInferred = scheduler.submit(InferTypes.class, sorted);
    var sModule = scheduler.submit(TranslatePs.class, typesInferred, importedScope);
    var report = report(COMPILER_FRONT_LABEL.append(":schedule:module"), list());
    return schedulingOutput(sModule, report);
  }
}
