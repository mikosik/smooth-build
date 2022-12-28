package org.smoothbuild.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.when;
import static org.smoothbuild.compile.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.compile.ps.PsTranslator.translatePs;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.testing.TestContext.BUILD_FILE_PATH;
import static org.smoothbuild.testing.TestContext.importedModFiles;
import static org.smoothbuild.testing.TestContext.modFiles;

import java.io.IOException;

import org.mockito.Mockito;
import org.smoothbuild.compile.fp.FpTranslator;
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;

public class TestingModLoader {
  private final String sourceCode;
  private ModFiles modFiles;
  private DefinitionsS imported;
  private Maybe<ModuleS> moduleS;

  TestingModLoader(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public TestingModLoader withImportedModFiles() {
    this.modFiles = importedModFiles();
    return this;
  }

  public TestingModLoader withImported(DefinitionsS imported) {
    this.imported = imported;
    return this;
  }

  public TestingModLoader loadsWithSuccess() {
    moduleS = load();
    assertWithMessage(messageWithSourceCode())
        .that(moduleS.logs().toList())
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
    var evaluables = moduleS.value().evaluables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(evaluables.contains(name))
        .isTrue();
    return evaluables.get(name);
  }

  public void containsType(TypeS expected) {
    var name = expected.name();
    var types = moduleS.value().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.contains(name))
        .isTrue();
    TypeS actual = types.get(name).type();
    assertWithMessage(
        "Module contains type '" + name + "', but")
        .that(actual)
        .isEqualTo(expected);
  }

  public DefinitionsS getModuleAsDefinitions() {
    return DefinitionsS.empty()
        .withModule(loadInternalModule())
        .withModule(moduleS.value());
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
    var importedSane = imported != null
        ? imported
        : DefinitionsS.empty().withModule(loadInternalModule());
    var modFilesSane = this.modFiles != null ? modFiles : modFiles();
    var fileResolver = createFileResolver(modFilesSane);
    var moduleP = new FpTranslator(fileResolver)
        .translateFp(modFilesSane);
    if (moduleP.containsProblem()) {
      return maybeLogs(moduleP.logs());
    } else {
      return translatePs(moduleP.value(), importedSane);
    }
  }

  private FileResolver createFileResolver(ModFiles modFilesSane) {
    var fileResolver = Mockito.mock(FileResolver.class);
    try {
      when(fileResolver.readFileContentAndCacheHash(modFilesSane.smoothFile()))
          .thenReturn(sourceCode);
    } catch (IOException e) {
      throw new RuntimeException("cannot happen", e);
    }
    return fileResolver;
  }

  public static Log err(int line, String message) {
    return error(BUILD_FILE_PATH + ":" + line + ": " + message);
  }
}
