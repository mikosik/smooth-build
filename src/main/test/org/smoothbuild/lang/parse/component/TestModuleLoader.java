package org.smoothbuild.lang.parse.component;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.lang.base.Definitions.basicTypeDefinitions;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.ModuleInfo;
import org.smoothbuild.lang.base.ShortablePath;
import org.smoothbuild.lang.base.Space;
import org.smoothbuild.lang.parse.LoadModule;

public class TestModuleLoader {
  private static final String BUILD_FILE = "myBuild.smooth";
  private static final String NATIVE_JAR = "myBuild.jar";
  public static final ModuleInfo MODULE_INFO = moduleInfo(BUILD_FILE);
  public static final ModuleInfo IMPORTED_INFO = moduleInfo("imported.smooth");

  private final String sourceCode;
  private ModuleInfo moduleInfo;
  private Definitions imports;
  private ValueWithLogs<Definitions> module;

  public static TestModuleLoader module(String sourceCode) {
    return new TestModuleLoader(sourceCode, MODULE_INFO, basicTypeDefinitions());
  }

  public TestModuleLoader(String sourceCode, ModuleInfo moduleInfo, Definitions imports) {
    this.sourceCode = sourceCode;
    this.moduleInfo = moduleInfo;
    this.imports = imports;
  }

  public TestModuleLoader withImportedModuleInfo() {
    withModuleInfo(IMPORTED_INFO);
    return this;
  }

  public TestModuleLoader withModuleInfo(ModuleInfo moduleInfo) {
    this.moduleInfo = moduleInfo;
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
    return LoadModule.loadModule(imports, moduleInfo, sourceCode);
  }

  public static ModuleInfo moduleInfo(String buildFile) {
    ShortablePath smooth = new ShortablePath(Path.of("long/path/myBuild.smooth"), buildFile);
    ShortablePath nativ = new ShortablePath(Path.of("long/path/myBuild.jar"), NATIVE_JAR);
    return new ModuleInfo(Space.USER, smooth, nativ);
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE + ":" + line + ": " + message);
  }
}
