package org.smoothbuild.run;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.install.InstallationPaths.standardLibraryModuleLocations;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_FILE_NAME;
import static org.smoothbuild.lang.base.ModuleLocation.moduleLocation;
import static org.smoothbuild.lang.base.Space.USER;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.ModuleLocation;

import com.google.common.collect.ImmutableList;

import okio.BufferedSource;

public class RuntimeController {
  private static final ImmutableList<ModuleLocation> MODULES =
      ImmutableList.<ModuleLocation>builder()
          .addAll(standardLibraryModuleLocations())
          .add(moduleLocation(USER, Path.of(USER_MODULE_FILE_NAME)))
          .build();

  private final FullPathResolver fullPathResolver;
  private final Reporter reporter;

  @Inject
  public RuntimeController(FullPathResolver fullPathResolver, Reporter reporter) {
    this.fullPathResolver = fullPathResolver;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<Definitions> runner) {
    reporter.startNewPhase("Parsing");

    Definitions allDefinitions = Definitions.baseTypeDefinitions();
    for (ModuleLocation module : MODULES) {
      ValueWithLogs<Definitions> definitions = load(module, allDefinitions);
      reporter.report(module.path().toString(), definitions.logs());
      if (reporter.isProblemReported()) {
        reporter.printSummary();
        return EXIT_CODE_ERROR;
      } else {
        allDefinitions = Definitions.union(allDefinitions, definitions.value());
      }
    }
    runner.accept(allDefinitions);
    reporter.printSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private ValueWithLogs<Definitions> load(ModuleLocation info, Definitions imports) {
    var sourceCode = readFileContent(fullPathResolver.resolve(info));
    if (sourceCode.hasProblems()) {
      return new ValueWithLogs<>(sourceCode);
    } else {
      return loadModule(imports, info, sourceCode.value());
    }
  }

  private static ValueWithLogs<String> readFileContent(Path filePath) {
    var result = new ValueWithLogs<String>();
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
