package org.smoothbuild.install;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.FileResolver;

import com.google.common.collect.ImmutableList;

public class ModFilesDetector {
  private final FileResolver fileResolver;

  @Inject
  public ModFilesDetector(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  public ImmutableList<ModFiles> detect(List<FilePath> smoothFiles) {
    return map(smoothFiles, file -> new ModFiles(file, nativeFileFor(file)));
  }

  private Optional<FilePath> nativeFileFor(FilePath file) {
    FilePath nativeFilePath = file.withExtension("jar");
    return switch (fileResolver.pathState(nativeFilePath)) {
      case FILE -> Optional.of(nativeFilePath);
      case DIR, NOTHING -> Optional.empty();
    };
  }
}
