package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CallsPreprocessor.preprocessCalls;
import static org.smoothbuild.compile.ps.DecodeLiterals.decodeLiterals;
import static org.smoothbuild.compile.ps.DetectUndefinedRefablesAndTypes.detectUndefinedRefablesAndTypes;
import static org.smoothbuild.compile.ps.FindSyntaxErrors.findSyntaxErrors;
import static org.smoothbuild.compile.ps.ModuleCreator.createModuleS;
import static org.smoothbuild.compile.ps.ParseModule.parseModule;
import static org.smoothbuild.compile.ps.ScopesInitializer.initializeScopes;
import static org.smoothbuild.compile.ps.ast.ModuleDependenciesSorter.sortByDependencies;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.compile.ap.ApTranslator;
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;

public class LoadModule {
  public static Maybe<ModuleS> loadModule(
      ModFiles modFiles, String sourceCode, DefinitionsS imported) {
    var logBuffer = new LogBuffer();
    var filePath = modFiles.smoothFile();
    Maybe<ModContext> moduleContext = parseModule(filePath, sourceCode);
    logBuffer.logAll(moduleContext.logs());
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    var maybeModule = ApTranslator.translate(filePath, moduleContext.value());
    logBuffer.logAll(maybeModule.logs());
    if (maybeModule.logs().containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }
    var moduleP = maybeModule.value();
    logBuffer.logAll(findSyntaxErrors(moduleP));
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    logBuffer.logAll(decodeLiterals(moduleP));
    logBuffer.logAll(initializeScopes(moduleP));
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    var maybeSortedModuleP = sortByDependencies(moduleP);
    logBuffer.logAll(maybeSortedModuleP.logs());
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }
    var sortedModuleP = maybeSortedModuleP.value();

    logBuffer.logAll(detectUndefinedRefablesAndTypes(sortedModuleP, imported));
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    logBuffer.logAll(preprocessCalls(sortedModuleP, imported));
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    var mod = createModuleS(modFiles, sortedModuleP, imported);
    logBuffer.logAll(mod.logs());
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    return maybe(mod.value(), logBuffer);
  }
}
