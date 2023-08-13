package org.smoothbuild.filesystem.install;

import static org.smoothbuild.common.collect.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;

import com.google.common.collect.ImmutableList;

import jakarta.inject.Inject;

public class ModuleResourcesDetector {
  private final FileResolver fileResolver;

  @Inject
  public ModuleResourcesDetector(FileResolver fileResolver) {
    this.fileResolver = fileResolver;
  }

  public ImmutableList<ModuleResources> detect(List<FilePath> smoothFiles) {
    return map(smoothFiles, file -> new ModuleResources(file, nativeFileFor(file)));
  }

  private Optional<FilePath> nativeFileFor(FilePath file) {
    FilePath nativeFilePath = file.withExtension("jar");
    return switch (fileResolver.pathState(nativeFilePath)) {
      case FILE -> Optional.of(nativeFilePath);
      case DIR, NOTHING -> Optional.empty();
    };
  }
}