package org.smoothbuild.lang;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.io.fs.base.TestingFilePath.BUILD_FILE_PATH;
import static org.smoothbuild.lang.base.define.SModule.baseTypesModule;
import static org.smoothbuild.lang.base.define.TestingModuleFiles.importedModuleFiles;
import static org.smoothbuild.lang.base.define.TestingModuleFiles.moduleFiles;

import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.SModule;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.LoadModule;

import com.google.common.collect.ImmutableMap;

public class TestModuleLoader {
  private final String sourceCode;
  private ModuleFiles moduleFiles;
  private Definitions imported;
  private Maybe<SModule> module;

  public static TestModuleLoader module(String sourceCode) {
    return new TestModuleLoader(
        sourceCode, moduleFiles(), Definitions.empty().withModule(baseTypesModule()));
  }

  public TestModuleLoader(String sourceCode, ModuleFiles moduleFiles, Definitions imported) {
    this.sourceCode = sourceCode;
    this.moduleFiles = moduleFiles;
    this.imported = imported;
  }

  public TestModuleLoader withImportedModuleFiles() {
    this.moduleFiles = importedModuleFiles();
    return this;
  }

  public TestModuleLoader withImported(Definitions imported) {
    this.imported = imported;
    return this;
  }

  public TestModuleLoader loadsSuccessfully() {
    module = load();
    assertWithMessage(messageWithSourceCode())
        .that(module.logs())
        .isEmpty();
    return this;
  }

  public void containsReferencable(Referencable expected) {
    String name = expected.name();
    Referencable actual = assertContainsReferencable(name);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsReferencableWithType(String name, Type expectedType) {
    Referencable referencable = assertContainsReferencable(name);
    assertThat(referencable.type())
        .isEqualTo(expectedType);
  }

  private Referencable assertContainsReferencable(String name) {
    ImmutableMap<String, ? extends Referencable> referencables = module.value().referencables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(referencables)
        .containsKey(name);
    Referencable actual = referencables.get(name);
    return actual;
  }

  public void containsType(Type expected) {
    String name = expected.name();
    ImmutableMap<String, ? extends Defined> types = module.value().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types)
        .containsKey(name);
    Type actual = types.get(name).type();
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public Definitions getModuleAsDefinitions() {
    return Definitions.empty().withModule(module.value());
  }

  public void loadsWithProblems() {
    var module = load();
    assertWithMessage(messageWithSourceCode())
        .that(module.hasProblems())
        .isTrue();
  }

  public void loadsWithError(int line, String message) {
    loadsWithErrors(List.of(err(line, message)));
  }

  public void loadsWithError(String message) {
    loadsWithErrors(List.of(error(message)));
  }

  public void loadsWithErrors(List<Log> errors) {
    var module = load();
    assertWithMessage(messageWithSourceCode())
        .that(module.logs())
        .containsExactlyElementsIn(errors);
  }

  private String messageWithSourceCode() {
    String message = "For source code = "
        + "\n====================\n"
        + sourceCode
        + "\n====================\n";
    return message;
  }

  private Maybe<SModule> load() {
    return LoadModule.loadModule(
        imported, ModulePath.of(moduleFiles.smoothFile()), moduleFiles, sourceCode);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH.toString() + ":" + line + ": " + message);
  }
}
