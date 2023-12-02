package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.inject.Guice.createInjector;
import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.compile.frontend.FrontendCompilerStep.frontendCompilerStep;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.filesystem.space.SpaceUtils.forSpace;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Try.success;
import static org.smoothbuild.testing.TestContext.writeFile;

import com.google.inject.Injector;
import com.google.inject.Key;
import java.io.IOException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.filesystem.install.StandardLibrarySpaceModule;
import org.smoothbuild.filesystem.project.ProjectSpaceModule;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.MemoryFileSystemModule;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Try;
import org.smoothbuild.run.step.StepExecutor;
import org.smoothbuild.testing.accept.MemoryReporter;

public class TestingModuleLoader {
  private final String sourceCode;
  private String importedSourceCode;
  private Try<ScopeS> definitions;

  TestingModuleLoader(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public TestingModuleLoader withImported(String imported) {
    this.importedSourceCode = imported;
    return this;
  }

  public TestingModuleLoader loadsWithSuccess() {
    definitions = load();
    assertWithMessage(messageWithSourceCode()).that(definitions.logs().toList()).isEmpty();
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
        .that(module.logs().containsAtLeast(ERROR))
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
    assertWithMessage(messageWithSourceCode())
        .that(module.logs().toList())
        .containsExactlyElementsIn(logs);
  }

  private String messageWithSourceCode() {
    return "For source code = "
        + "\n====================\n"
        + sourceCode
        + "\n====================\n";
  }

  private Try<ScopeS> load() {
    var injector = createInjector(
        PRODUCTION,
        new StandardLibrarySpaceModule(),
        new ProjectSpaceModule(),
        new MemoryFileSystemModule());
    writeModuleFilesToFileSystems(injector);
    var steps = frontendCompilerStep();
    var memoryReporter = new MemoryReporter();
    var module = injector.getInstance(StepExecutor.class).execute(steps, null, memoryReporter);
    return success(module.getOr(null), memoryReporter.logs());
  }

  private void writeModuleFilesToFileSystems(Injector injector) {
    writeModuleFile(
        injector,
        new FilePath(STANDARD_LIBRARY, path("std_lib.smooth")),
        importedSourceCode == null ? "" : importedSourceCode);
    writeModuleFile(injector, new FilePath(PROJECT, path("build.smooth")), sourceCode);
  }

  private static void writeModuleFile(Injector injector, FilePath filePath, String content) {
    try {
      var space = filePath.space();
      var fileSystem = injector.getInstance(Key.get(FileSystem.class, forSpace(space)));
      writeFile(fileSystem, filePath.path(), content);
    } catch (IOException e) {
      throw new RuntimeException("Can't happen for in memory filesystem.", e);
    }
  }

  public static Log err(int line, String message) {
    return error("build.smooth:" + line + ": " + message);
  }
}
