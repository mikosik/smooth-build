package org.smoothbuild.run;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.base.type.Types.BASIC_TYPES;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;
import static org.smoothbuild.util.Lists.concat;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.install.ProjectPaths;
import org.smoothbuild.lang.base.ModuleInfo;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.Definitions;

import com.google.common.collect.ImmutableMap;

import okio.BufferedSource;

public class RuntimeController {
  private final InstallationPaths installationPaths;
  private final ProjectPaths projectPaths;
  private final Reporter reporter;

  @Inject
  public RuntimeController(InstallationPaths installationPaths, ProjectPaths projectPaths,
      Reporter reporter) {
    this.installationPaths = installationPaths;
    this.projectPaths = projectPaths;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<Definitions> runner) {
    reporter.startNewPhase("Parsing");

    Definitions definitions = basicTypeDefinitions();
    for (ModuleInfo module : modules()) {
      try (LoggerImpl logger = new LoggerImpl(module.smooth().shorted(), reporter)) {
        definitions = Definitions.union(definitions, load(module, definitions, logger));
      }
      if (reporter.isProblemReported()) {
        reporter.printSummary();
        return EXIT_CODE_ERROR;
      }
    }
    runner.accept(definitions);
    reporter.printSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Definitions basicTypeDefinitions() {
    return new Definitions(
        BASIC_TYPES.stream().collect(toImmutableMap(Type::name, t -> t)),
        ImmutableMap.of());
  }

  private List<ModuleInfo> modules() {
    return concat(installationPaths.slibModules(), projectPaths.userModule());
  }

  private Definitions load(ModuleInfo info, Definitions imports, LoggerImpl logger) {
    String sourceCode = readFileContent(info.smooth().path(), logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    } else {
      return loadModule(imports, sourceCode, info, logger);
    }
  }

  private static String readFileContent(Path filePath, Logger logger) {
    try {
      return readFileContent(filePath);
    } catch (NoSuchFileException e) {
      logger.error("'" + filePath + "' doesn't exist.");
      return null;
    } catch (IOException e) {
      logger.error("Cannot read build script file '" + filePath + "'.");
      return null;
    }
  }

  private static String readFileContent(Path filePath) throws IOException {
    try (BufferedSource source = buffer(source(filePath))) {
      return source.readString(SmoothConstants.CHARSET);
    }
  }
}
