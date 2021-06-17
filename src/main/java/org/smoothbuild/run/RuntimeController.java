package org.smoothbuild.run;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.install.InstallationPaths.STANDARD_LIBRARY_MODULES;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_FILE_NAME;
import static org.smoothbuild.lang.base.define.SModule.baseTypesModule;
import static org.smoothbuild.lang.base.define.Space.USER;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.install.ModuleFilesDetector;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.SModule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import okio.BufferedSource;

public class RuntimeController {
  private static final ImmutableList<FileLocation> MODULES =
      ImmutableList.<FileLocation>builder()
          .addAll(STANDARD_LIBRARY_MODULES)
          .add(new FileLocation(USER, Path.of(USER_MODULE_FILE_NAME)))
          .build();

  private final FullPathResolver fullPathResolver;
  private final ModuleFilesDetector moduleFilesDetector;
  private final Reporter reporter;

  @Inject
  public RuntimeController(FullPathResolver fullPathResolver,
      ModuleFilesDetector moduleFilesDetector, Reporter reporter) {
    this.fullPathResolver = fullPathResolver;
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
      reporter.report(moduleFiles.smoothFile().prefixedPath(), module.logs());
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
    var sourceCode = readFileContent(fullPathResolver.resolve(moduleFiles.smoothFile()));
    if (sourceCode.hasProblems()) {
      return  Maybe.withLogsFrom(sourceCode);
    } else {
      return loadModule(imported, path, moduleFiles, sourceCode.value());
    }
  }

  private static Maybe<String> readFileContent(Path filePath) {
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

  private static String readFileContentImpl(Path filePath) throws IOException {
    try (BufferedSource source = buffer(source(filePath))) {
      return source.readString(SmoothConstants.CHARSET);
    }
  }
}
