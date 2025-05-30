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
  private final ReadFileContent readFileContent;

  @Inject
  public CompileModule(Scheduler scheduler, ReadFileContent readFileContent) {
    this.scheduler = scheduler;
    this.readFileContent = readFileContent;
  }

  @Override
  public Output<SModule> execute(SModule importedModule, FullPath fullPath) {
    var importedScope = argument(importedModule.scope());
    var path = argument(fullPath);
    var fileContent = scheduler.submit(readFileContent, path);
    var moduleContext = scheduler.submit(new Parse(), fileContent, path);
    var pModule = scheduler.submit(new TranslateAp(), moduleContext, path);
    var withVerifiedIds = scheduler.submit(new GenerateFqns(), pModule);
    var withGeneratedDefaults = scheduler.submit(new GenerateDefaultValues(), withVerifiedIds);
    var withSyntaxCheck = scheduler.submit(new FindSyntaxErrors(), withGeneratedDefaults);
    var withDecodedLiterals = scheduler.submit(new DecodeLiterals(), withSyntaxCheck);
    var withGeneratedConstructors =
        scheduler.submit(new GenerateConstructors(), withDecodedLiterals);
    var withInitializedScopes =
        scheduler.submit(new GenerateScopes(), importedScope, withGeneratedConstructors);
    var withResolvedReferences = scheduler.submit(new ResolveReferences(), withInitializedScopes);
    var withInjected = scheduler.submit(new InjectDefaultArguments(), withResolvedReferences);
    var sorted = scheduler.submit(new SortModuleMembersByDependency(), withInjected);
    var typesInferred = scheduler.submit(new InferTypes(), sorted);
    var sModule = scheduler.submit(new TranslatePs(), typesInferred, importedScope);
    var report = report(COMPILER_FRONT_LABEL.append(":schedule:module"), list());
    return schedulingOutput(sModule, report);
  }
}
