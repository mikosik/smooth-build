package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.ps.DecodeLiterals.decodeLiterals;
import static org.smoothbuild.compile.fs.ps.DefaultArgumentInjector.injectDefaultArguments;
import static org.smoothbuild.compile.fs.ps.DetectUndefinedRefablesAndTypes.detectUndefinedRefablesAndTypes;
import static org.smoothbuild.compile.fs.ps.PsConverter.convertPs;
import static org.smoothbuild.compile.fs.ps.ScopesInitializer.initializeScopes;
import static org.smoothbuild.compile.fs.ps.ast.ModuleDependenciesSorter.sortByDependencies;
import static org.smoothbuild.compile.fs.ps.infer.TypeInferrer.inferTypes;

import org.smoothbuild.compile.fs.lang.define.ModuleS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

public class PsTranslator {
  public static Maybe<ModuleS> translatePs(ModuleP moduleP, ScopeS imported) {
    return new Translator(moduleP, imported)
        .process();
  }

  private static class Translator extends MaybeProcessor<ModuleS> {
    private final ScopeS imported;
    private final ModuleP moduleP;

    private Translator(ModuleP moduleP, ScopeS imported) {
      this.moduleP = moduleP;
      this.imported = imported;
    }

    @Override
    protected ModuleS processImpl() throws FailedException {
      addLogs(decodeLiterals(moduleP));
      addLogs(initializeScopes(moduleP));
      addLogs(detectUndefinedRefablesAndTypes(moduleP, imported));
      addLogs(injectDefaultArguments(moduleP, imported));
      var sortedModuleP = addLogsAndGetValue(sortByDependencies(moduleP));
      addLogs(inferTypes(sortedModuleP, imported));
      return convertPs(moduleP, imported);
    }
  }
}
