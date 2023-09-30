package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.inject.Guice.createInjector;
import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.filesystem.space.SpaceUtils.forSpace;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.TestContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestContext.importedModuleResources;
import static org.smoothbuild.testing.TestContext.moduleResources;
import static org.smoothbuild.testing.TestContext.writeFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.compile.fs.FsTranslator;
import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.filesystem.project.ProjectSpaceModule;
import org.smoothbuild.filesystem.space.MemoryFileSystemModule;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;

import com.google.inject.Injector;
import com.google.inject.Key;

public class TestingModuleLoader {
  private final String sourceCode;
  private String importedSourceCode;
  private Maybe<ScopeS> definitions;

  TestingModuleLoader(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public TestingModuleLoader withImported(String imported) {
    this.importedSourceCode = imported;
    return this;
  }

  public TestingModuleLoader loadsWithSuccess() {
    definitions = load();
    assertWithMessage(messageWithSourceCode())
        .that(definitions.logs().toList())
        .isEmpty();
    return this;
  }

  public void containsEvaluable(NamedEvaluableS expected) {
    String name = expected.name();
    var actual = assertContainsEvaluable(name);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsEvaluableWithSchema(String name, SchemaS expectedT) {
    var referenceable = assertContainsEvaluable(name);
    assertThat(referenceable.schema())
        .isEqualTo(expectedT);
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
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
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

  private Maybe<ScopeS> load() {
    var injector =
        createInjector(PRODUCTION, new ProjectSpaceModule(), new MemoryFileSystemModule());
    var modules = createModules(injector);
    return injector.getInstance(FsTranslator.class).translateFs(modules);
  }

  private List<ModuleResources> createModules(Injector injector) {
    var modules = new ArrayList<ModuleResources>();
    if (importedSourceCode != null) {
      createModule(injector, modules, importedModuleResources(), importedSourceCode);
    }
    createModule(injector, modules, moduleResources(), sourceCode);
    return modules;
  }

  private static void createModule(
      Injector injector, List<ModuleResources> modules, ModuleResources module, String content) {
    modules.add(module);
    writeModuleFile(injector, module, content);
  }

  private static void writeModuleFile(
      Injector injector, ModuleResources moduleResources, String content) {
    try {
      var filePath = moduleResources.smoothFile();
      var space = filePath.space();
      var fileSystem = injector.getInstance(Key.get(FileSystem.class, forSpace(space)));
      writeFile(fileSystem, filePath.path(), content);
    } catch (IOException e) {
      throw new RuntimeException("Can't happen for in memory filesystem.", e);
    }
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
