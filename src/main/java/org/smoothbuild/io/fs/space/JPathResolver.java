package org.smoothbuild.io.fs.space;

import java.nio.file.Path;

import javax.inject.Inject;

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
      throw new IllegalArgumentException("Unknown space = " + filePath.space() + ".");
    }
    return path.resolve(filePath.path().toString());
  }
}
