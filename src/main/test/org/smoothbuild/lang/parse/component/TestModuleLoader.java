package org.smoothbuild.lang.parse.component;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.lang.base.Definitions.baseTypeDefinitions;
import static org.smoothbuild.lang.base.TestingModuleLocation.BUILD_FILE_PATH;
import static org.smoothbuild.lang.base.TestingModuleLocation.importedModuleLocation;
import static org.smoothbuild.lang.base.TestingModuleLocation.moduleLocation;

import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Declared;
import org.smoothbuild.lang.base.ModuleLocation;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.LoadModule;

import com.google.common.collect.ImmutableMap;

public class TestModuleLoader {
  private final String sourceCode;
  private ModuleLocation moduleLocation;
  private Definitions imports;
  private Maybe<Definitions> module;

  public static TestModuleLoader module(String sourceCode) {
    return new TestModuleLoader(sourceCode, moduleLocation(), baseTypeDefinitions());
  }

  public TestModuleLoader(String sourceCode, ModuleLocation moduleLocation, Definitions imports) {
    this.sourceCode = sourceCode;
    this.moduleLocation = moduleLocation;
    this.imports = imports;
  }

  public TestModuleLoader withImportedModuleLocation() {
    withModuleLocation(importedModuleLocation());
    return this;
  }

  public TestModuleLoader withModuleLocation(ModuleLocation moduleLocation) {
    this.moduleLocation = moduleLocation;
    return this;
  }

  public TestModuleLoader withImported(Definitions imports) {
    this.imports = imports;
    return this;
  }

  public TestModuleLoader loadsSuccessfully() {
    module = load();
    assertThat(module.logs())
        .isEmpty();
    return this;
  }

  public void containsEvaluable(Declared expected) {
    String name = expected.name();
    ImmutableMap<String, Declared> evaluables = module.value().evaluables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(evaluables)
        .containsKey(name);
    Declared actual = evaluables.get(name);
    assertThat(actual)
        .isEqualTo(expected);
  }

  public void containsType(Type expected) {
    String name = expected.name();
    ImmutableMap<String, Type> types = module.value().types();
    assertWithMessage("Module evaluables doesn't contain '" + name + "' type.")
        .that(types)
        .containsKey(name);
    Type actual = types.get(name);
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public Definitions getModule() {
    return module.value();
  }

  public void loadsWithProblems() {
    var module = load();
    assertThat(module.hasProblems())
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
    assertThat(module.logs())
        .containsExactlyElementsIn(errors);
  }

  private Maybe<Definitions> load() {
    return LoadModule.loadModule(imports, moduleLocation, sourceCode);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH.toString() + ":" + line + ": " + message);
  }
}
