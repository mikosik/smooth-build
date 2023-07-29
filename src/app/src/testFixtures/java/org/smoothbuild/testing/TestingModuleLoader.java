package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.TestContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestContext.importedModuleResources;
import static org.smoothbuild.testing.TestContext.moduleResources;

import java.io.IOException;
import java.util.ArrayList;

import org.smoothbuild.compile.fs.FsTranslator;
import org.smoothbuild.compile.fs.fp.FpTranslator;
import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.fs.space.FileResolver;

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
    var fileResolver = mock(FileResolver.class);
    var modules = new ArrayList<ModuleResources>();
    if (importedSourceCode != null) {
      var importedModule = importedModuleResources();
      modules.add(importedModule);
      mockFileContent(fileResolver, importedModule, importedSourceCode);
    }
    var module = moduleResources();
    mockFileContent(fileResolver, module, sourceCode);
    modules.add(module);
    return new FsTranslator(new FpTranslator(fileResolver))
        .translateFs(modules);
  }

  private void mockFileContent(FileResolver fileResolver, ModuleResources module, String code) {
    try {
      when(fileResolver.readFileContentAndCacheHash(module.smoothFile()))
          .thenReturn(code);
    } catch (IOException e) {
      throw new RuntimeException("cannot happen", e);
    }
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
