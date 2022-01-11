package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.console.ImmutableLogs.logs;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValue;
import static org.smoothbuild.install.InstallationPaths.SDK_MODULES;
import static org.smoothbuild.install.ProjectPaths.PRJ_MODULE_FILE_PATH;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.install.ModFilesDetector;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.InternalModLoader;
import org.smoothbuild.lang.base.define.ModFiles;
import org.smoothbuild.lang.base.define.ModPath;
import org.smoothbuild.lang.base.define.ModS;
import org.smoothbuild.lang.parse.ModLoader;

import com.google.common.collect.ImmutableList;

public class RuntimeController {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SDK_MODULES)
          .add(PRJ_MODULE_FILE_PATH)
          .build();

  private final FileResolver fileResolver;
  private final ModFilesDetector modFilesDetector;
  private final ModLoader modLoader;
  private final InternalModLoader internalModLoader;
  private final Reporter reporter;

  @Inject
  public RuntimeController(FileResolver fileResolver, ModFilesDetector modFilesDetector,
      ModLoader modLoader, InternalModLoader internalModLoader, Reporter reporter) {
    this.fileResolver = fileResolver;
    this.modFilesDetector = modFilesDetector;
    this.modLoader = modLoader;
    this.internalModLoader = internalModLoader;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<DefsS> runner) {
    reporter.startNewPhase("Parsing");

    var internalMod = internalModLoader.load();
    var allDefs = DefsS.empty().withModule(internalMod);
    var files = modFilesDetector.detect(MODULES);
    for (Entry<ModPath, ModFiles> entry : files.entrySet()) {
      ModFiles modFiles = entry.getValue();
      Maybe<ModS> module = load(allDefs, entry.getKey(), modFiles);
      reporter.report(modFiles.smoothFile().toString(), module.logs().toList());
      if (reporter.isProblemReported()) {
        reporter.printSummary();
        return EXIT_CODE_ERROR;
      } else {
        allDefs = allDefs.withModule(module.value());
      }
    }
    runner.accept(allDefs);
    reporter.printSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Maybe<ModS> load(DefsS imported, ModPath path, ModFiles modFiles) {
    var sourceCode = readFileContent(modFiles.smoothFile());
    if (sourceCode.containsProblem()) {
      return maybeLogs(sourceCode.logs());
    } else {
      return modLoader.loadModule(path, modFiles, sourceCode.value(), imported);
    }
  }

  private Maybe<String> readFileContent(FilePath filePath) {
    try {
      return maybeValue(fileResolver.readFileContentAndCacheHash(filePath));
    } catch (NoSuchFileException e) {
      return maybeLogs(logs(error("'" + filePath + "' doesn't exist.")));
    } catch (IOException e) {
      return maybeLogs(logs(error("Cannot read build script file '" + filePath + "'.")));
    }
  }
}
