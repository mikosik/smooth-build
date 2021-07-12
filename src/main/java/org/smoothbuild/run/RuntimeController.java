package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.install.InstallationPaths.SDK_MODULES;
import static org.smoothbuild.install.ProjectPaths.PRJ_MODULE_FILE_PATH;
import static org.smoothbuild.lang.base.define.SModule.baseTypesModule;
import static org.smoothbuild.lang.parse.LoadModule.loadModule;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
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
      Maybe<Hash> hash = moduleHash(path, moduleFiles, imported.modules());
      if (hash.hasProblems()) {
        return Maybe.withLogsFrom(hash);
      } else {
        return loadModule(path, hash.value(), moduleFiles, sourceCode.value(), imported);
      }
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

  private Maybe<Hash> moduleHash(
      ModulePath path, ModuleFiles moduleFiles, ImmutableList<SModule> modules) {
    Maybe<Hash> moduleFilesHash = hashOfModuleFiles(moduleFiles);
    if (moduleFilesHash.hasProblems()) {
      return Maybe.withLogsFrom(moduleFilesHash);
    }
    Hash filesHash = moduleFilesHash.value();
    Hash hash = SModule.moduleHash(path, filesHash, modules);
    return Maybe.of(hash);
  }

  private Maybe<Hash> hashOfModuleFiles(ModuleFiles moduleFiles) {
    Maybe<Hash> hash = new Maybe<>();
    List<Hash> hashes = new ArrayList<>();
    for (FilePath filePath : moduleFiles.asList()) {
      try {
        hashes.add(Hash.of(fileResolver.source(filePath)));
      } catch (NoSuchFileException e) {
        hash.error("'" + filePath + "' doesn't exist.");
      } catch (IOException e) {
        hash.error("Cannot read file '" + filePath + "'.");
      }
    }
    hash.setValue(Hash.of(hashes.toArray(new Hash[] {})));
    return hash;
  }
}
