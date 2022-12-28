package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.TestContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestContext.importedModFiles;
import static org.smoothbuild.testing.TestContext.modFiles;

import java.io.IOException;
import java.util.ArrayList;

import org.smoothbuild.compile.fs.FsTranslator;
import org.smoothbuild.compile.fs.fp.FpTranslator;
import org.smoothbuild.compile.fs.lang.define.DefinitionsS;
import org.smoothbuild.compile.fs.lang.define.ModFiles;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;

public class TestingModuleLoader {
  private final String sourceCode;
  private String importedSourceCode;
  private ModFiles modFiles;
  private Maybe<DefinitionsS> definitions;

  TestingModuleLoader(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public TestingModuleLoader withImportedModFiles() {
    this.modFiles = importedModFiles();
    return this;
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
    var refable = assertContainsEvaluable(name);
    assertThat(refable.schema())
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

  public DefinitionsS getLoadedDefinitions() {
    return definitions.value();
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

  private Maybe<DefinitionsS> load() {
    var modFilesSane = this.modFiles != null ? modFiles : modFiles();
    var fileResolver = mock(FileResolver.class);
    var moduleFiles = new ArrayList<ModFiles>();
    if (importedSourceCode != null) {
      var importedModuleFiles = importedModFiles();
      moduleFiles.add(importedModuleFiles);
      mockFileContent(fileResolver, importedModuleFiles, importedSourceCode);
    }
    mockFileContent(fileResolver, modFilesSane, sourceCode);
    moduleFiles.add(modFilesSane);
    return new FsTranslator(new FpTranslator(fileResolver))
        .translateFs(moduleFiles);
  }

  private void mockFileContent(FileResolver fileResolver, ModFiles moduleFiles, String code) {
    try {
      when(fileResolver.readFileContentAndCacheHash(moduleFiles.smoothFile()))
          .thenReturn(code);
    } catch (IOException e) {
      throw new RuntimeException("cannot happen", e);
    }
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
