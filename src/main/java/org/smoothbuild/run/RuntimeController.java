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
import static org.smoothbuild.lang.base.define.ModuleS.calculateModuleHash;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.install.ModuleFilesDetector;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.InternalModuleLoader;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.ModuleS;
import org.smoothbuild.lang.parse.ModuleLoader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class RuntimeController {
  private static final ImmutableList<FilePath> MODULES =
      ImmutableList.<FilePath>builder()
          .addAll(SDK_MODULES)
          .add(PRJ_MODULE_FILE_PATH)
          .build();


  private final FileResolver fileResolver;
  private final ModuleFilesDetector moduleFilesDetector;
  private final ModuleLoader moduleLoader;
  private final InternalModuleLoader internalModuleLoader;
  private final Reporter reporter;

  @Inject
  public RuntimeController(FileResolver fileResolver, ModuleFilesDetector moduleFilesDetector,
      ModuleLoader moduleLoader, InternalModuleLoader internalModuleLoader, Reporter reporter) {
    this.fileResolver = fileResolver;
    this.moduleFilesDetector = moduleFilesDetector;
    this.moduleLoader = moduleLoader;
    this.internalModuleLoader = internalModuleLoader;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<DefinitionsS> runner) {
    reporter.startNewPhase("Parsing");

    ModuleS internalModule = internalModuleLoader.loadModule();
    DefinitionsS allDefinitions = DefinitionsS.empty().withModule(internalModule);
    ImmutableMap<ModulePath, ModuleFiles> files = moduleFilesDetector.detect(MODULES);
    for (Entry<ModulePath, ModuleFiles> entry : files.entrySet()) {
      ModuleFiles moduleFiles = entry.getValue();
      Maybe<ModuleS> module = load(allDefinitions, entry.getKey(), moduleFiles);
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

  private Maybe<ModuleS> load(DefinitionsS imported, ModulePath path, ModuleFiles moduleFiles) {
    var sourceCode = readFileContent(moduleFiles.smoothFile());
    if (sourceCode.containsProblem()) {
      return maybeLogs(sourceCode.logs());
    } else {
      Maybe<Hash> hash = moduleHash(path, moduleFiles, imported.modules().values().asList());
      if (hash.containsProblem()) {
        return maybeLogs(hash.logs());
      } else {
        return moduleLoader.loadModule(
            path, hash.value(), moduleFiles, sourceCode.value(), imported);
      }
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

  private Maybe<Hash> moduleHash(
      ModulePath path, ModuleFiles moduleFiles, ImmutableList<ModuleS> modules) {
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
        hashes.add(fileResolver.hashOf(filePath));
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
