package org.smoothbuild.install;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FilePath;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;

import com.google.common.collect.ImmutableMap;

public class ModuleFilesDetector {
  private final FullPathResolver fullPathResolver;

  @Inject
  public ModuleFilesDetector(FullPathResolver fullPathResolver) {
    this.fullPathResolver = fullPathResolver;
  }

  public ImmutableMap<ModulePath, ModuleFiles> detect(List<FilePath> smoothFiles) {
    var builder = ImmutableMap.<ModulePath, ModuleFiles>builder();
    for (FilePath file : smoothFiles) {
      builder.put(ModulePath.of(file), new ModuleFiles(file, nativeFileFor(file)));
    }
    return builder.build();
  }

  private Optional<FilePath> nativeFileFor(FilePath file) {
    FilePath nativeFilePath = file.withExtension("jar");
    Path resolved = fullPathResolver.resolve(nativeFilePath);
    if (Files.exists(resolved)) {
      return Optional.of(nativeFilePath);
    } else {
      return Optional.empty();
    }
  }
}
