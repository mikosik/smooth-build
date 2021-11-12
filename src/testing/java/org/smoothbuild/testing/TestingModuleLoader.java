package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.io.fs.base.TestingFilePath.BUILD_FILE_PATH;
import static org.smoothbuild.lang.base.define.TestingModuleFiles.importedModuleFiles;
import static org.smoothbuild.lang.base.define.TestingModuleFiles.moduleFiles;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.ModuleS;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.parse.ModuleLoader;
import org.smoothbuild.lang.parse.ReferencableLoader;
import org.smoothbuild.lang.parse.TypeInferrer;

public class TestingModuleLoader {
  private final TestingContext testingContext;
  private final String sourceCode;
  private ModuleFiles moduleFiles;
  private Definitions imported;
  private Maybe<ModuleS> module;

  TestingModuleLoader(TestingContext testingContext, String sourceCode) {
    this.testingContext = testingContext;
    this.sourceCode = sourceCode;
  }

  public TestingModuleLoader withImportedModuleFiles() {
    this.moduleFiles = importedModuleFiles();
    return this;
  }

  public TestingModuleLoader withImported(Definitions imported) {
    this.imported = imported;
    return this;
  }

  public TestingModuleLoader loadsSuccessfully() {
    module = load();
    assertWithMessage(messageWithSourceCode())
        .that(module.logs().toList())
        .isEmpty();
    return this;
  }

  public void containsReferencable(GlobalReferencable expected) {
    String name = expected.name();
    GlobalReferencable actual = assertContainsReferencable(name);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsReferencableWithType(String name, Type expectedType) {
    GlobalReferencable referencable = assertContainsReferencable(name);
    assertThat(referencable.type())
        .isEqualTo(expectedType);
  }

  private GlobalReferencable assertContainsReferencable(String name) {
    var referencables = module.value().referencables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(referencables.containsWithName(name))
        .isTrue();
    return referencables.get(name);
  }

  public void containsType(Type expected) {
    var name = expected.name();
    var types = module.value().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.containsWithName(name))
        .isTrue();
    Type actual = types.get(name).type();
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public Definitions getModuleAsDefinitions() {
    return Definitions.empty()
        .withModule(testingContext.internalModule())
        .withModule(module.value());
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
    TypingS typing = testingContext.typingS();
    TypeFactoryS factory = testingContext.typeFactoryS();
    ModuleLoader moduleLoader = new ModuleLoader(
        new TypeInferrer(factory, typing), new ReferencableLoader(factory), factory);
    Definitions importedSane = imported != null ? imported
        : Definitions.empty().withModule(testingContext.internalModule());
    ModuleFiles moduleFilesSane = this.moduleFiles != null ? moduleFiles : moduleFiles();
    return moduleLoader.loadModule(ModulePath.of(moduleFilesSane.smoothFile()), Hash.of(13),
        moduleFilesSane, sourceCode, importedSane);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
