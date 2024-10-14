package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_FRONT_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.task.Task2;
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

public class FrontendCompile implements Task1<SModule, List<FullPath>> {
  private final TaskExecutor taskExecutor;

  @Inject
  public FrontendCompile(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  @Override
  public Output<SModule> execute(List<FullPath> modules) {
    var module = taskExecutor.submit(LoadInternalModuleMembers.class);
    for (var fullPath : modules) {
      module = taskExecutor.submit(ScheduleModuleCompilation.class, module, promise(fullPath));
    }
    var label = COMPILE_FRONT_LABEL.append("schedule");
    var report = report(label, new Trace(), EXECUTION, list());
    return schedulingOutput(module, report);
  }

  public static class ScheduleModuleCompilation implements Task2<SModule, SModule, FullPath> {
    private final TaskExecutor taskExecutor;

    @Inject
    public ScheduleModuleCompilation(TaskExecutor taskExecutor) {
      this.taskExecutor = taskExecutor;
    }

    @Override
    public Output<SModule> execute(SModule importedModule, FullPath fullPath) {
      var environment = promise(importedModule.membersAndImported());
      var path = promise(fullPath);
      var fileContent = taskExecutor.submit(ReadFileContent.class, path);
      var moduleContext = taskExecutor.submit(Parse.class, fileContent, path);
      var moduleP = taskExecutor.submit(TranslateAp.class, moduleContext, path);
      var withSyntaxCheck = taskExecutor.submit(FindSyntaxErrors.class, moduleP);
      var withDecodedLiterals = taskExecutor.submit(DecodeLiterals.class, withSyntaxCheck);
      var withInitializedScopes = taskExecutor.submit(InitializeScopes.class, withDecodedLiterals);
      var withUndefinedDetected =
          taskExecutor.submit(DetectUndefined.class, withInitializedScopes, environment);
      var withInjected =
          taskExecutor.submit(InjectDefaultArguments.class, withUndefinedDetected, environment);
      var sorted = taskExecutor.submit(SortModuleMembersByDependency.class, withInjected);
      var typesInferred = taskExecutor.submit(InferTypes.class, sorted, environment);
      var moduleS = taskExecutor.submit(ConvertPs.class, typesInferred, environment);
      var label = COMPILE_FRONT_LABEL.append("schedule").append("module");
      var report = report(label, new Trace(), EXECUTION, list());
      return schedulingOutput(moduleS, report);
    }
  }
}
