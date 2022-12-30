package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.ps.DecodeLiterals.decodeLiterals;
import static org.smoothbuild.compile.fs.ps.DefaultArgumentInjector.injectDefaultArguments;
import static org.smoothbuild.compile.fs.ps.DetectUndefinedRefablesAndTypes.detectUndefinedRefablesAndTypes;
import static org.smoothbuild.compile.fs.ps.ModuleCreator.createModuleS;
import static org.smoothbuild.compile.fs.ps.ScopesInitializer.initializeScopes;
import static org.smoothbuild.compile.fs.ps.ast.ModuleDependenciesSorter.sortByDependencies;

import org.smoothbuild.compile.fs.lang.define.DefinitionsS;
import org.smoothbuild.compile.fs.lang.define.ModuleS;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

public class PsTranslator {
  public static Maybe<ModuleS> translatePs(
      ModuleP moduleP, DefinitionsS imported) {
    return new Translator(moduleP, imported)
        .process();
  }

  private static class Translator extends MaybeProcessor<ModuleS> {
    private final DefinitionsS imported;
    private final ModuleP moduleP;

    private Translator(ModuleP moduleP, DefinitionsS imported) {
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
      return addLogsAndGetValue(createModuleS(sortedModuleP, imported));
    }
  }
}
