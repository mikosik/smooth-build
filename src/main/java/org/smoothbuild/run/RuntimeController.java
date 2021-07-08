package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.install.InstallationPaths.SDK_MODULES;
import static org.smoothbuild.install.ProjectPaths.PRJ_MODULE_FILE_PATH;
import static org.smoothbuild.lang.base.define.SModule.baseTypesModule;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.install.ModuleFilesDetector;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.SModule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import okio.BufferedSource;

public class RuntimeController {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SDK_MODULES)
          .add(PRJ_MODULE_FILE_PATH)
          .build();

  private final FileResolver fileResolver;
  private final ModuleFilesDetector moduleFilesDetector;
  private final Reporter reporter;

  @Inject
  public RuntimeController(FileResolver fileResolver, ModuleFilesDetector moduleFilesDetector,
      Reporter reporter) {
    this.fileResolver = fileResolver;
    this.moduleFilesDetector = moduleFilesDetector;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<Definitions> runner) {
    reporter.startNewPhase("Parsing");

    Definitions allDefinitions = Definitions.empty().withModule(baseTypesModule());
    ImmutableMap<ModulePath, ModuleFiles> files = moduleFilesDetector.detect(MODULES);
    for (Entry<ModulePath, ModuleFiles> entry : files.entrySet()) {
      ModuleFiles moduleFiles = entry.getValue();
      Maybe<SModule> module = load(allDefinitions, entry.getKey(), moduleFiles);
      reporter.report(moduleFiles.smoothFile().toString(), module.logs());
      if (reporter.isProblemReported()) {
        reporter.printSummary();
        return EXIT_CODE_ERROR;
      } else {
        allDefinitions = allDefinitions.withModule(module.value());
      }
    }
    runner.accept(allDefinitions);
    reporter.printSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Maybe<SModule> load(Definitions imported, ModulePath path, ModuleFiles moduleFiles) {
    var sourceCode = readFileContent(moduleFiles.smoothFile());
    if (sourceCode.hasProblems()) {
      return Maybe.withLogsFrom(sourceCode);
    } else {
      return loadModule(imported, path, moduleFiles, sourceCode.value());
    }
  }

  private Maybe<String> readFileContent(FilePath filePath) {
    var result = new Maybe<String>();
    try {
      result.setValue(readFileContentImpl(filePath));
    } catch (NoSuchFileException e) {
      result.error("'" + filePath + "' doesn't exist.");
    } catch (IOException e) {
      result.error("Cannot read build script file '" + filePath + "'.");
    }
    return result;
  }

  private String readFileContentImpl(FilePath filePath) throws IOException {
    try (BufferedSource source = fileResolver.source(filePath)) {
      return source.readString(SmoothConstants.CHARSET);
    }
  }
}
