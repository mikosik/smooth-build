package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.compile.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.compile.ps.LoadModule.loadModule;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.TestContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestContext.importedModFiles;
import static org.smoothbuild.testing.TestContext.modFiles;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;

public class TestingModLoader {
  private final String sourceCode;
  private ModFiles modFiles;
  private DefsS imported;
  private Maybe<ModuleS> moduleS;

  TestingModLoader(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public TestingModLoader withImportedModFiles() {
    this.modFiles = importedModFiles();
    return this;
  }

  public TestingModLoader withImported(DefsS imported) {
    this.imported = imported;
    return this;
  }

  public TestingModLoader loadsWithSuccess() {
    moduleS = load();
    assertWithMessage(messageWithSourceCode())
        .that(moduleS.logs().toList())
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
    var refable = assertContainsEvaluable(name);
    assertThat(refable.schema())
        .isEqualTo(expectedT);
  }

  private NamedEvaluableS assertContainsEvaluable(String name) {
    var evaluables = moduleS.value().evaluables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(evaluables.contains(name))
        .isTrue();
    return evaluables.get(name);
  }

  public void containsType(TypeS expected) {
    var name = expected.name();
    var types = moduleS.value().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.contains(name))
        .isTrue();
    TypeS actual = types.get(name).type();
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public DefsS getModuleAsDefinitions() {
    return DefsS.empty()
        .withModule(loadInternalModule())
        .withModule(moduleS.value());
  }

  public void loadsWithProblems() {
    var module = load();
    assertWithMessage(messageWithSourceCode())
        .that(module.containsProblem())
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

  private Maybe<ModuleS> load() {
    DefsS importedSane = imported != null ? imported : DefsS.empty().withModule(loadInternalModule());
    ModFiles modFilesSane = this.modFiles != null ? modFiles : modFiles();
    return loadModule(modFilesSane, sourceCode, importedSane);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
