package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;

import java.io.IOException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;

public class FrontendCompileTester extends FrontendCompilerTestContext {
  public Api module(String sourceCode) {
    return new Api(sourceCode);
  }

  public class Api {
    private final String sourceCode;
    private String importedSourceCode;
    private Try<SModule> sModule;

    private Api(String sourceCode) {
      this.sourceCode = sourceCode;
    }

    public Api withImported(String imported) {
      this.importedSourceCode = imported;
      return this;
    }

    public Api loadsWithSuccess() {
      assertWithMessage(messageWithSourceCode()).that(loadModule().logs()).isEmpty();
      return this;
    }

    public void containsEvaluable(SPolyEvaluable expected) {
      var actual = assertContainsEvaluable(expected.fqn());
      assertThat(actual).isEqualTo(expected);
    }

    private SPolyEvaluable assertContainsEvaluable(Fqn fqn) {
      var evaluables = sModule.get().evaluables();
      var sNamedEvaluable = evaluables.get(fqn.parts().last());
      assertWithMessage("Module doesn't contain " + fqn.q() + ".")
          .that(sNamedEvaluable)
          .isNotNull();
      assertWithMessage("Module doesn't contain " + fqn.q() + ".")
          .that(sNamedEvaluable.fqn())
          .isEqualTo(fqn);
      return sNamedEvaluable;
    }

    public void containsType(SStructType expected) {
      var fqn = expected.fqn();
      var typeDefinition = sModule.get().types().get(fqn.parts().last());
      assertWithMessage("Module doesn't contain type '" + fqn + "'.")
          .that(typeDefinition)
          .isNotNull();
      assertWithMessage("Module doesn't contain type '" + fqn + "'.")
          .that(typeDefinition.fqn())
          .isEqualTo(fqn);
      assertWithMessage("Module contains type '" + fqn + "', but")
          .that(typeDefinition.type())
          .isEqualTo(expected);
    }

    public SModule getLoadedModule() {
      return sModule.get();
    }

    public void loadsWithProblems() {
      assertWithMessage(messageWithSourceCode())
          .that(containsFailure(loadModule().logs()))
          .isTrue();
    }

    public void loadsWithError(int line, String message) {
      loadsWith(err(line, message));
    }

    public void loadsWithError(String message) {
      loadsWith(error(message));
    }

    public void loadsWith(Log... logs) {
      assertWithMessage(messageWithSourceCode())
          .that(loadModule().logs())
          .containsExactlyElementsIn(logs);
    }

    private String messageWithSourceCode() {
      return "For source code = "
          + "\n====================\n"
          + sourceCode
          + "\n====================\n";
    }

    public Try<SModule> loadModule() {
      createModuleFiles(provide().fileSystem());
      var paths = list(standardLibraryModulePath(), moduleFullPath());
      var module = provide().scheduler().submit(provide().frontendCompile(), argument(paths));
      await().until(() -> module.toMaybe().isSome());
      sModule = Try.of(module.get().getOr(null), provide().reporter().logs());
      return sModule;
    }

    private void createModuleFiles(FileSystem<FullPath> fileSystem) {
      writeModuleFile(
          fileSystem,
          standardLibraryModulePath(),
          importedSourceCode == null ? "" : importedSourceCode);
      writeModuleFile(fileSystem, moduleFullPath(), sourceCode);
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
  }
}
