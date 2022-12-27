package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.parser.SmoothParser.parse;
import static org.smoothbuild.compile.ps.CallsPreprocessor.preprocessCalls;
import static org.smoothbuild.compile.ps.DecodeLiterals.decodeLiterals;
import static org.smoothbuild.compile.ps.DetectUndefinedRefablesAndTypes.detectUndefinedRefablesAndTypes;
import static org.smoothbuild.compile.parser.FindSyntaxErrors.findSyntaxErrors;
import static org.smoothbuild.compile.ps.ModuleCreator.createModuleS;
import static org.smoothbuild.compile.ps.ScopesInitializer.initializeScopes;
import static org.smoothbuild.compile.ps.ast.ModuleDependenciesSorter.sortByDependencies;

import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

public class ModuleLoader {
  public static Maybe<ModuleS> loadModule(
      ModFiles modFiles, String sourceCode, DefinitionsS imported) {
    return new Loader(modFiles, sourceCode, imported)
        .process();
  }

  private static class Loader extends MaybeProcessor<ModuleS> {
    private final ModFiles modFiles;
    private final String sourceCode;
    private final DefinitionsS imported;

    private Loader(ModFiles modFiles, String sourceCode, DefinitionsS imported) {
      this.modFiles = modFiles;
      this.sourceCode = sourceCode;
      this.imported = imported;
    }

    @Override
    protected ModuleS processImpl() throws FailedException {
      var moduleP = addLogsAndGetValue(parse(modFiles, sourceCode));
      addLogs(decodeLiterals(moduleP));
      addLogs(initializeScopes(moduleP));
      addLogs(detectUndefinedRefablesAndTypes(moduleP, imported));
      addLogs(preprocessCalls(moduleP, imported));
      var sortedModuleP = addLogsAndGetValue(sortByDependencies(moduleP));
      return addLogsAndGetValue(createModuleS(modFiles, sortedModuleP, imported));
    }
  }
}
