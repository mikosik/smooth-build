package org.smoothbuild.install;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FilePath;
import org.smoothbuild.io.fs.base.FileResolver;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;

import com.google.common.collect.ImmutableMap;

public class ModuleFilesDetector {
  private final FileResolver fileResolver;

  @Inject
  public ModuleFilesDetector(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
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
    return switch (fileResolver.pathState(nativeFilePath)) {
      case FILE -> Optional.of(nativeFilePath);
      case DIR, NOTHING -> Optional.empty();
    };
  }
}
