package org.smoothbuild.install;

import java.nio.file.Path;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FilePath;
import org.smoothbuild.io.fs.base.Space;

import com.google.common.collect.ImmutableMap;

public class JPathResolver {
  private final ImmutableMap<Space, Path> spacePaths;

  @Inject
  public JPathResolver(ImmutableMap<Space, Path> spacePaths) {
    this.spacePaths = spacePaths;
  }

  public Path resolve(FilePath filePath) {
    Path path = spacePaths.get(filePath.space());
    if (path == null) {
      throw new RuntimeException("Cannot resolve full path for " + filePath);
    }
    return path.resolve(filePath.path().toString());
  }
}
