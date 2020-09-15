package org.smoothbuild.lang.parse.component;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.lang.base.Definitions.basicTypeDefinitions;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.ModuleLocation;
import org.smoothbuild.lang.base.Space;
import org.smoothbuild.lang.parse.LoadModule;

public class TestModuleLoader {
  private static final String BUILD_FILE = "myBuild.smooth";
  public static final ModuleLocation MODULE_INFO = moduleLocation(BUILD_FILE);
  public static final ModuleLocation IMPORTED_INFO = moduleLocation("imported.smooth");

  private final String sourceCode;
  private ModuleLocation moduleLocation;
  private Definitions imports;
  private ValueWithLogs<Definitions> module;

  public static TestModuleLoader module(String sourceCode) {
    return new TestModuleLoader(sourceCode, MODULE_INFO, basicTypeDefinitions());
  }

  public TestModuleLoader(String sourceCode, ModuleLocation moduleLocation, Definitions imports) {
    this.sourceCode = sourceCode;
    this.moduleLocation = moduleLocation;
    this.imports = imports;
  }

  public TestModuleLoader withImportedModuleLocation() {
    withModuleLocation(IMPORTED_INFO);
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

  private ValueWithLogs<Definitions> load() {
    return LoadModule.loadModule(imports, moduleLocation, sourceCode);
  }

  public static ModuleLocation moduleLocation(String buildFile) {
    return new ModuleLocation(Space.USER, Path.of(buildFile));
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE + ":" + line + ": " + message);
  }
}
