package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.inject.Stage.PRODUCTION;
import static org.awaitility.Awaitility.await;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.testing.TestingFilesystem.createFile;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import java.io.IOException;
import org.smoothbuild.common.filesystem.base.Filesystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.SynchronizedBucket;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.testing.TestReporter;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class FrontendCompileTester extends FrontendCompilerTestContext {
  public Api module(String sourceCode) {
    return new Api(sourceCode);
  }

  public class Api {
    private final String sourceCode;
    private String importedSourceCode;
    private Try<SModule> moduleS;

    private Api(String sourceCode) {
      this.sourceCode = sourceCode;
    }

    public Api withImported(String imported) {
      this.importedSourceCode = imported;
      return this;
    }

    public Api loadsWithSuccess() {
      moduleS = loadModule();
      assertWithMessage(messageWithSourceCode()).that(moduleS.logs()).isEmpty();
      return this;
    }

    public void containsEvaluable(SNamedEvaluable expected) {
      String name = expected.name();
      var actual = assertContainsEvaluable(name);
      assertThat(actual).isEqualTo(expected);
    }

    public void containsEvaluableWithSchema(String name, SchemaS expectedT) {
      var referenceable = assertContainsEvaluable(name);
      assertThat(referenceable.schema()).isEqualTo(expectedT);
    }

    private SNamedEvaluable assertContainsEvaluable(String name) {
      var evaluables = moduleS.get().members().evaluables();
      assertWithMessage("Module doesn't contain '" + name + "'.")
          .that(evaluables.contains(name))
          .isTrue();
      return evaluables.get(name);
    }

    public void containsType(SType expected) {
      var name = expected.name();
      var types = moduleS.get().members().types();
      assertWithMessage("Module doesn't contain value with '" + name + "' type.")
          .that(types.contains(name))
          .isTrue();
      SType actual = types.get(name).type();
      assertWithMessage("Module contains type '" + name + "', but")
          .that(actual)
          .isEqualTo(expected);
    }

    public SModule getLoadedModule() {
      return moduleS.get();
    }

    public void loadsWithProblems() {
      var module = loadModule();
      assertWithMessage(messageWithSourceCode())
          .that(containsFailure(module.logs()))
          .isTrue();
    }

    public void loadsWithError(int line, String message) {
      loadsWith(err(line, message));
    }

    public void loadsWithError(String message) {
      loadsWith(error(message));
    }

    public void loadsWith(Log... logs) {
      var module = loadModule();
      assertWithMessage(messageWithSourceCode())
          .that(module.logs())
          .containsExactlyElementsIn(logs);
    }

    private String messageWithSourceCode() {
      return "For source code = "
          + "\n====================\n"
          + sourceCode
          + "\n====================\n";
    }

    private Try<SModule> loadModule() {
      var projectBucket = new SynchronizedBucket(new MemoryBucket());
      var filesystem = new Filesystem(map(PROJECT, projectBucket));
      var testReporter = new TestReporter();

      var injector = Guice.createInjector(PRODUCTION, new AbstractModule() {
        @Override
        protected void configure() {
          bind(Reporter.class).toInstance(testReporter);
        }

        @Provides
        public Filesystem provideFilesystem() {
          return filesystem;
        }
      });
      writeModuleFiles(filesystem);
      var scheduler = injector.getInstance(Scheduler.class);
      var paths = list(standardLibraryModulePath(), moduleFullPath());
      var module = scheduler.submit(FrontendCompile.class, argument(paths));
      await().until(() -> module.toMaybe().isSome());
      return Try.of(module.get().getOr(null), testReporter.logs());
    }

    private void writeModuleFiles(Filesystem buckets) {
      writeModuleFile(
          buckets,
          standardLibraryModulePath(),
          importedSourceCode == null ? "" : importedSourceCode);
      writeModuleFile(buckets, moduleFullPath(), sourceCode);
    }

    private static void writeModuleFile(Filesystem filesystem, FullPath fullPath, String content) {
      try {
        createFile(filesystem, fullPath, content);
      } catch (IOException e) {
        throw new RuntimeException("Can't happen for MemoryBucket.", e);
      }
    }

    private FullPath standardLibraryModulePath() {
      return PROJECT.append("std_lib.smooth");
    }
  }
}
