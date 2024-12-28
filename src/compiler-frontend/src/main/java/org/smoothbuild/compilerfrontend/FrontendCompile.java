package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.DecodeLiterals;
import org.smoothbuild.compilerfrontend.compile.DetectUndefined;
import org.smoothbuild.compilerfrontend.compile.FindSyntaxErrors;
import org.smoothbuild.compilerfrontend.compile.GenerateConstructors;
import org.smoothbuild.compilerfrontend.compile.GenerateDefaultValues;
import org.smoothbuild.compilerfrontend.compile.GenerateIds;
import org.smoothbuild.compilerfrontend.compile.GenerateScopes;
import org.smoothbuild.compilerfrontend.compile.InjectDefaultArguments;
import org.smoothbuild.compilerfrontend.compile.LoadInternalModuleMembers;
import org.smoothbuild.compilerfrontend.compile.Parse;
import org.smoothbuild.compilerfrontend.compile.ReadFileContent;
import org.smoothbuild.compilerfrontend.compile.SortModuleMembersByDependency;
import org.smoothbuild.compilerfrontend.compile.TranslateAp;
import org.smoothbuild.compilerfrontend.compile.TranslatePs;
import org.smoothbuild.compilerfrontend.compile.infer.InferTypes;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class FrontendCompile implements Task1<List<FullPath>, SModule> {
  private final Scheduler scheduler;

  @Inject
  public FrontendCompile(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<SModule> execute(List<FullPath> modules) {
    var module = scheduler.submit(LoadInternalModuleMembers.class);
    for (var fullPath : modules) {
      module = scheduler.submit(ScheduleModuleCompilation.class, module, argument(fullPath));
    }
    var report = report(COMPILER_FRONT_LABEL.append(":schedule"), list());
    return schedulingOutput(module, report);
  }

  public static class ScheduleModuleCompilation implements Task2<SModule, FullPath, SModule> {
    private final Scheduler scheduler;

    @Inject
    public ScheduleModuleCompilation(Scheduler scheduler) {
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
      var typesInferred = scheduler.submit(InferTypes.class, sorted, importedScope);
      var sModule = scheduler.submit(TranslatePs.class, typesInferred, importedScope);
      var report = report(COMPILER_FRONT_LABEL.append(":schedule:module"), list());
      return schedulingOutput(sModule, report);
    }
  }
}
