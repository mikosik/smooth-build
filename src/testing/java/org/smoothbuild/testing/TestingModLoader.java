package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.TestingContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestingContext.importedModFiles;
import static org.smoothbuild.testing.TestingContext.modFiles;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ModLoader;
import org.smoothbuild.parse.TopObjLoader;
import org.smoothbuild.parse.TypeInferrer;

public class TestingModLoader {
  private final TestingContext testingContext;
  private final String sourceCode;
  private ModFiles modFiles;
  private DefsS imported;
  private Maybe<ModS> modS;

  TestingModLoader(TestingContext testingContext, String sourceCode) {
    this.testingContext = testingContext;
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

  public void containsTopRefable(TopRefableS expected) {
    String name = expected.name();
    TopRefableS actual = assertContainsTopRefable(name);
    boolean equals = actual.equals(expected);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsTopRefableWithType(String name, TypeS expectedT) {
    TopRefableS topRefable = assertContainsTopRefable(name);
    assertThat(topRefable.type())
        .isEqualTo(expectedT);
  }

  private TopRefableS assertContainsTopRefable(String name) {
    var topRefables = modS.value().topRefables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(topRefables.containsName(name))
        .isTrue();
    return topRefables.get(name);
  }

  public void containsType(MonoTS expected) {
    var name = expected.name();
    var types = modS.value().tDefs();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.containsName(name))
        .isTrue();
    MonoTS actual = types.get(name).type();
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public DefsS getModAsDefinitions() {
    return DefsS.empty()
        .withModule(testingContext.internalMod())
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
    var typing = testingContext.typingS();
    var factory = testingContext.typeFS();
    var typeInferrer = new TypeInferrer(factory, typing);
    var topRefableLoader = new TopObjLoader(factory);
    var modLoader = new ModLoader(typeInferrer, topRefableLoader, factory);
    DefsS importedSane = imported != null ? imported
        : DefsS.empty().withModule(testingContext.internalMod());
    ModFiles modFilesSane = this.modFiles != null ? modFiles : modFiles();
    return modLoader.loadModule(
        ModPath.of(modFilesSane.smoothFile()), modFilesSane, sourceCode, importedSane);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
