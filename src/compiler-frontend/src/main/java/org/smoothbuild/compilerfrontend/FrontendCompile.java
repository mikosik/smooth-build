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
    var label = COMPILER_FRONT_LABEL.append(":schedule");
    var report = report(label, list());
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
      var environment = argument(importedModule.membersAndImported());
      var path = argument(fullPath);
      var fileContent = scheduler.submit(ReadFileContent.class, path);
      var moduleContext = scheduler.submit(Parse.class, fileContent, path);
      var moduleP = scheduler.submit(TranslateAp.class, moduleContext, path);
      var withSyntaxCheck = scheduler.submit(FindSyntaxErrors.class, moduleP);
      var withDecodedLiterals = scheduler.submit(DecodeLiterals.class, withSyntaxCheck);
      var withInitializedScopes = scheduler.submit(InitializeScopes.class, withDecodedLiterals);
      var withUndefinedDetected =
          scheduler.submit(DetectUndefined.class, withInitializedScopes, environment);
      var withInjected =
          scheduler.submit(InjectDefaultArguments.class, withUndefinedDetected, environment);
      var sorted = scheduler.submit(SortModuleMembersByDependency.class, withInjected);
      var typesInferred = scheduler.submit(InferTypes.class, sorted, environment);
      var moduleS = scheduler.submit(ConvertPs.class, typesInferred, environment);
      var label = COMPILER_FRONT_LABEL.append(":schedule").append(":module");
      var report = report(label, list());
      return schedulingOutput(moduleS, report);
    }
  }
}
