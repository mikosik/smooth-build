package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ap.ApTranslator.translateAp;
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

import java.util.function.Supplier;

import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.out.log.Maybe;

public class LoadModule {
  public static Maybe<ModuleS> loadModule(
      ModFiles modFiles, String sourceCode, DefinitionsS imported) {
    return new Loader(modFiles, sourceCode, imported)
        .load();
  }

  private static class Loader {
    private final ModFiles modFiles;
    private final String sourceCode;
    private final DefinitionsS imported;
    private final LogBuffer logBuffer = new LogBuffer();

    private Loader(ModFiles modFiles, String sourceCode, DefinitionsS imported) {
      this.modFiles = modFiles;
      this.sourceCode = sourceCode;
      this.imported = imported;
    }

    public Maybe<ModuleS> load() {
      try {
        return maybe(loadImpl(), logBuffer);
      } catch (FailedException e) {
        return maybeLogs(logBuffer);
      }
    }

    private ModuleS loadImpl() throws FailedException {
      var modContext = runAndHandleMaybe(() -> parseModule(modFiles.smoothFile(), sourceCode));
      var moduleP = runAndHandleMaybe(() -> translateAp(modFiles.smoothFile(), modContext));
      runAndHandleLogs(() -> findSyntaxErrors(moduleP));
      runAndHandleLogs(() -> decodeLiterals(moduleP));
      runAndHandleLogs(() -> initializeScopes(moduleP));
      runAndHandleLogs(() -> detectUndefinedRefablesAndTypes(moduleP, imported));
      runAndHandleLogs(() -> preprocessCalls(moduleP, imported));
      var sortedModuleP = runAndHandleMaybe(() -> sortByDependencies(moduleP));
      return runAndHandleMaybe(() -> createModuleS(modFiles, sortedModuleP, imported));
    }

    private <T> T runAndHandleMaybe(Supplier<Maybe<T>> supplier) throws FailedException {
      Maybe<T> maybe = supplier.get();
      runAndHandleLogs(maybe::logs);
      return maybe.value();
    }

    private void runAndHandleLogs(Supplier<Logs> supplier) throws FailedException {
      logBuffer.logAll(supplier.get());
      if (logBuffer.containsAtLeast(ERROR)) {
        throw new FailedException();
      }
    }
  }

  private static class FailedException extends Exception {}
}
