package org.smoothbuild.run;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;
import static org.smoothbuild.util.Lists.concat;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.install.ProjectPaths;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.ModuleInfo;

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

    Definitions allDefinitions = Definitions.basicTypeDefinitions();
    for (ModuleInfo module : modules()) {
      ValueWithLogs<Definitions> definitions = load(module, allDefinitions);
      reporter.report(module.smooth().shorted(), definitions.logs());
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

  private List<ModuleInfo> modules() {
    return concat(installationPaths.slibModules(), projectPaths.userModule());
  }

  private ValueWithLogs<Definitions> load(ModuleInfo info, Definitions imports) {
    var sourceCode = readFileContent(info.smooth().path());
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
