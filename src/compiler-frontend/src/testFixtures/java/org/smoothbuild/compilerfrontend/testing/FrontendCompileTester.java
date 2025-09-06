package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;

import java.io.IOException;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class FrontendCompileTester extends FrontendCompilerTestContext {
  public ModuleCompilationOutput compileModule(String moduleText) {
    return compileModules(moduleText, none());
  }

  public ModuleCompilationOutput compileModules(
      String sourceCode, Maybe<String> standardLibraryCode) {
    FileSystem<FullPath> fileSystem = provide().fileSystem();
    writeModuleFile(fileSystem, standardLibraryModulePath(), standardLibraryCode.getOr(""));
    writeModuleFile(fileSystem, moduleFullPath(), sourceCode);
    var paths = list(standardLibraryModulePath(), moduleFullPath());
    var modulePromise = provide().scheduler().submit(provide().frontendCompile(), argument(paths));
    await().until(() -> modulePromise.toMaybe().isSome());
    var logs = provide().reporter().logs();
    @SuppressWarnings("NullAway")
    var module = modulePromise.get().getOr(null);
    return new ModuleCompilationOutput(Try.of(module, logs), sourceCode, standardLibraryCode);
  }

  private static void writeModuleFile(
      FileSystem<FullPath> fileSystem, FullPath fullPath, String content) {
    try {
      createFile(fileSystem, fullPath, content);
    } catch (IOException e) {
      throw new RuntimeException("Can't happen for MemoryFileSystem.", e);
    }
  }

  private FullPath standardLibraryModulePath() {
    return provide().projectPath().append("std_lib.smooth");
  }

  public static class ModuleCompilationOutput {
    private final Try<SModule> result;
    private final String sourceCode;
    private final Maybe<String> importedSourceCode;

    public ModuleCompilationOutput(
        Try<SModule> result, String sourceCode, Maybe<String> importedSourceCode) {
      this.result = result;
      this.sourceCode = sourceCode;
      this.importedSourceCode = importedSourceCode;
    }

    public Try<SModule> result() {
      return result;
    }

    public ModuleCompilationOutput assertLoadedWithSuccess() {
      assertWithMessage(messageWithSourceCode()).that(result.logs()).isEmpty();
      return this;
    }

    private String messageWithSourceCode() {
      return "For source code = "
          + "\n====================\n"
          + sourceCode
          + importedSourceCode.map(c -> "\n=== imported source code ===\n" + c).getOr("")
          + "\n====================\n";
    }
  }
}
