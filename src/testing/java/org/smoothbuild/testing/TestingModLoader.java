package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.io.fs.base.TestingFilePath.BUILD_FILE_PATH;
import static org.smoothbuild.lang.base.define.TestingModFiles.importedModFiles;
import static org.smoothbuild.lang.base.define.TestingModFiles.modFiles;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.ModFiles;
import org.smoothbuild.lang.base.define.ModPath;
import org.smoothbuild.lang.base.define.ModS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.parse.ModLoader;
import org.smoothbuild.lang.parse.TopEvalLoader;
import org.smoothbuild.lang.parse.TypeInferrer;

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

  public TestingModLoader loadsSuccessfully() {
    modS = load();
    assertWithMessage(messageWithSourceCode())
        .that(modS.logs().toList())
        .isEmpty();
    return this;
  }

  public void containsEval(TopEvalS expected) {
    String name = expected.name();
    TopEvalS actual = assertContainsEval(name);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsEvalWithType(String name, Type expectedType) {
    TopEvalS referencable = assertContainsEval(name);
    assertThat(referencable.type())
        .isEqualTo(expectedType);
  }

  private TopEvalS assertContainsEval(String name) {
    var referencables = modS.value().referencables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(referencables.containsName(name))
        .isTrue();
    return referencables.get(name);
  }

  public void containsType(Type expected) {
    var name = expected.name();
    var types = modS.value().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.containsName(name))
        .isTrue();
    Type actual = types.get(name).type();
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
    TypingS typing = testingContext.typingS();
    TypeFactoryS factory = testingContext.typeFactoryS();
    ModLoader modLoader = new ModLoader(
        new TypeInferrer(factory, typing), new TopEvalLoader(factory), factory);
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