package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.compile.lang.define.LoadInternalMod.loadInternalMod;
import static org.smoothbuild.compile.ps.LoadMod.loadModule;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.TestContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestContext.importedModFiles;
import static org.smoothbuild.testing.TestContext.modFiles;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModPath;
import org.smoothbuild.compile.lang.define.ModS;
import org.smoothbuild.compile.lang.define.PolyRefableS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.TypelikeS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;

public class TestingModLoader {
  private final TestContext testContext;
  private final String sourceCode;
  private ModFiles modFiles;
  private DefsS imported;
  private Maybe<ModS> modS;

  TestingModLoader(TestContext testContext, String sourceCode) {
    this.testContext = testContext;
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
    modS = load();
    assertWithMessage(messageWithSourceCode())
        .that(modS.logs().toList())
        .isEmpty();
    return this;
  }

  public void containsRefable(RefableS expected) {
    String name = expected.name();
    var actual = assertContainsRefable(name);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsRefableWithType(String name, TypelikeS expectedT) {
    var refable = assertContainsRefable(name);
    assertThat(refable.typelike())
        .isEqualTo(expectedT);
  }

  private PolyRefableS assertContainsRefable(String name) {
    var refables = modS.value().refables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(refables.contains(name))
        .isTrue();
    return refables.get(name);
  }

  public void containsType(TypeS expected) {
    var name = expected.name();
    var types = modS.value().tDefs();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.contains(name))
        .isTrue();
    TypeS actual = types.get(name).type();
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public DefsS getModAsDefinitions() {
    return DefsS.empty()
        .withModule(loadInternalMod())
        .withModule(modS.value());
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

  private Maybe<ModS> load() {
    DefsS importedSane = imported != null ? imported : DefsS.empty().withModule(loadInternalMod());
    ModFiles modFilesSane = this.modFiles != null ? modFiles : modFiles();
    return loadModule(
        ModPath.of(modFilesSane.smoothFile()), modFilesSane, sourceCode, importedSane);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
