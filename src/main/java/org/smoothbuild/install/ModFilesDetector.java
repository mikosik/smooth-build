package org.smoothbuild.install;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.lang.base.define.ModFiles;
import org.smoothbuild.lang.base.define.ModPath;

import com.google.common.collect.ImmutableMap;

public class ModFilesDetector {
  private final FileResolver fileResolver;

  @Inject
  public ModFilesDetector(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  public ImmutableMap<ModPath, ModFiles> detect(List<FilePath> smoothFiles) {
    var builder = ImmutableMap.<ModPath, ModFiles>builder();
    for (FilePath file : smoothFiles) {
      builder.put(ModPath.of(file), new ModFiles(file, nativeFileFor(file)));
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