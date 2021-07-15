package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.console.ImmutableLogs.logs;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValue;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.install.InstallationPaths.SDK_MODULES;
import static org.smoothbuild.install.ProjectPaths.PRJ_MODULE_FILE_PATH;
import static org.smoothbuild.lang.base.define.SModule.baseTypesModule;
import static org.smoothbuild.lang.base.define.SModule.calculateModuleHash;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.hashed.Hash;
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
      reporter.report(moduleFiles.smoothFile().toString(), module.logs().toList());
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
    if (sourceCode.containsProblem()) {
      return maybeLogs(sourceCode.logs());
    } else {
      Maybe<Hash> hash = moduleHash(path, moduleFiles, imported.modules().values().asList());
      if (hash.containsProblem()) {
        return maybeLogs(hash.logs());
      } else {
        return loadModule(path, hash.value(), moduleFiles, sourceCode.value(), imported);
      }
    }
  }

  private Maybe<String> readFileContent(FilePath filePath) {
    try {
      return maybeValue(readFileContentImpl(filePath));
    } catch (NoSuchFileException e) {
      return maybeLogs(logs(error("'" + filePath + "' doesn't exist.")));
    } catch (IOException e) {
      return maybeLogs(logs(error("Cannot read build script file '" + filePath + "'.")));
    }
  }

  private String readFileContentImpl(FilePath filePath) throws IOException {
    try (BufferedSource source = fileResolver.source(filePath)) {
      return source.readString(SmoothConstants.CHARSET);
    }
  }

  private Maybe<Hash> moduleHash(
      ModulePath path, ModuleFiles moduleFiles, ImmutableList<SModule> modules) {
    Maybe<Hash> moduleFilesHash = hashOfModuleFiles(moduleFiles);
    if (moduleFilesHash.containsProblem()) {
      return maybeLogs(moduleFilesHash.logs());
    }
    Hash filesHash = moduleFilesHash.value();
    Hash hash = calculateModuleHash(path, filesHash, modules);
    return maybeValue(hash);
  }

  private Maybe<Hash> hashOfModuleFiles(ModuleFiles moduleFiles) {
    var logger = new LogBuffer();
    List<Hash> hashes = new ArrayList<>();
    for (FilePath filePath : moduleFiles.asList()) {
      try {
        hashes.add(Hash.of(fileResolver.source(filePath)));
      } catch (NoSuchFileException e) {
        logger.error("'" + filePath + "' doesn't exist.");
      } catch (IOException e) {
        logger.error("Cannot read file '" + filePath + "'.");
      }
    }
    Hash hash = Hash.of(hashes);
    return maybeValueAndLogs(hash, logger);
  }
}
