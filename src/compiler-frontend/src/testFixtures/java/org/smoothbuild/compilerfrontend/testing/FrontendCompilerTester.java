package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Log.containsAnyFailure;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.testing.TestingFileSystem.writeFile;
import static org.smoothbuild.compilerfrontend.FrontendCompilerStep.createFrontendCompilerStep;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.DEFAULT_MODULE_FILE_PATH;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.PROJECT_SPACE;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.STANDARD_LIBRARY_MODULE_FILE_PATH;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.STANDARD_LIBRARY_SPACE;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import java.io.IOException;
import java.util.Map;
import org.smoothbuild.common.filesystem.base.FileResolver;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.StepExecutor;
import org.smoothbuild.common.testing.MemoryStepReporter;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

public class FrontendCompilerTester {
  private final String sourceCode;
  private String importedSourceCode;
  private Try<ScopeS> definitions;

  public static FrontendCompilerTester module(String sourceCode) {
    return new FrontendCompilerTester(sourceCode);
  }

  private FrontendCompilerTester(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public FrontendCompilerTester withImported(String imported) {
    this.importedSourceCode = imported;
    return this;
  }

  public FrontendCompilerTester loadsWithSuccess() {
    definitions = load();
    assertWithMessage(messageWithSourceCode()).that(definitions.logs()).isEmpty();
    return this;
  }

  public void containsEvaluable(NamedEvaluableS expected) {
    String name = expected.name();
    var actual = assertContainsEvaluable(name);
    assertThat(actual).isEqualTo(expected);
  }

  public void containsEvaluableWithSchema(String name, SchemaS expectedT) {
    var referenceable = assertContainsEvaluable(name);
    assertThat(referenceable.schema()).isEqualTo(expectedT);
  }

  private NamedEvaluableS assertContainsEvaluable(String name) {
    var evaluables = definitions.value().evaluables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(evaluables.contains(name))
        .isTrue();
    return evaluables.get(name);
  }

  public void containsType(TypeS expected) {
    var name = expected.name();
    var types = definitions.value().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.contains(name))
        .isTrue();
    TypeS actual = types.get(name).type();
    assertWithMessage("Module contains type '" + name + "', but").that(actual).isEqualTo(expected);
  }

  public ScopeS getLoadedDefinitions() {
    return definitions.value();
  }

  public void loadsWithProblems() {
    var module = load();
    assertWithMessage(messageWithSourceCode())
        .that(containsAnyFailure(module.logs()))
        .isTrue();
  }

  public void loadsWithError(int line, String message) {
    loadsWith(err(line, message));
  }

  public void loadsWithError(String message) {
    loadsWith(error(message));
  }

  public void loadsWith(Log... logs) {
    var module = load();
    assertWithMessage(messageWithSourceCode()).that(module.logs()).containsExactlyElementsIn(logs);
  }

  private String messageWithSourceCode() {
    return "For source code = "
        + "\n====================\n"
        + sourceCode
        + "\n====================\n";
  }

  private Try<ScopeS> load() {
    Map<Space, FileSystem> spaces = Map.of(
        PROJECT_SPACE, new MemoryFileSystem(), STANDARD_LIBRARY_SPACE, new MemoryFileSystem());
    var fileResolver = new FileResolver(spaces);
    var injector = Guice.createInjector(PRODUCTION, new AbstractModule() {
      @Override
      protected void configure() {}

      @Provides
      public FileResolver provideFileResolver() {
        return fileResolver;
      }
    });
    writeModuleFilesToFileSystems(spaces);
    var steps = createFrontendCompilerStep(
        list(STANDARD_LIBRARY_MODULE_FILE_PATH, DEFAULT_MODULE_FILE_PATH));
    var memoryReporter = new MemoryStepReporter();
    var module = injector.getInstance(StepExecutor.class).execute(steps, null, memoryReporter);
    return success(module.getOr(null), memoryReporter.logs());
  }

  private void writeModuleFilesToFileSystems(Map<Space, FileSystem> spaces) {
    writeModuleFile(
        spaces,
        STANDARD_LIBRARY_MODULE_FILE_PATH,
        importedSourceCode == null ? "" : importedSourceCode);
    writeModuleFile(spaces, DEFAULT_MODULE_FILE_PATH, sourceCode);
  }

  private static void writeModuleFile(
      Map<Space, FileSystem> spaces, FullPath fullPath, String content) {
    try {
      writeFile(spaces.get(fullPath.space()), fullPath.path(), content);
    } catch (IOException e) {
      throw new RuntimeException("Can't happen for in memory filesystem.", e);
    }
  }

  public static Log err(int line, String message) {
    return error("{prj}/build.smooth:" + line + ": " + message);
  }
}
