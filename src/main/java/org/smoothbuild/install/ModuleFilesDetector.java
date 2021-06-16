package org.smoothbuild.install;

import static org.smoothbuild.util.io.Paths.removeExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;

import com.google.common.collect.ImmutableList;

public class ModuleFilesDetector {
  private final FullPathResolver fullPathResolver;

  @Inject
  public ModuleFilesDetector(FullPathResolver fullPathResolver) {
    this.fullPathResolver = fullPathResolver;
  }

  public ImmutableList<ModuleFiles> detect(List<FileLocation> smoothFiles) {
    var builder = ImmutableList.<ModuleFiles>builder();
    for (FileLocation file : smoothFiles) {
      ModulePath name = new ModulePath(removeExtension(file.prefixedPath()));
      builder.add(new ModuleFiles(name, file.space(), file, nativeFileFor(file)));
    }
    return builder.build();
  }

  private Optional<FileLocation> nativeFileFor(FileLocation file) {
    FileLocation nativeFileLocation = file.withExtension("jar");
    Path resolved = fullPathResolver.resolve(nativeFileLocation);
    if (Files.exists(resolved)) {
      return Optional.of(nativeFileLocation);
    } else {
      return Optional.empty();
    }
  }
}
