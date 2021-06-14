package org.smoothbuild.install;

import java.nio.file.Path;

import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.Space;

import com.google.common.collect.ImmutableMap;

public class FullPathResolver {
  private final ImmutableMap<Space, Path> resolvers;

  public FullPathResolver(ImmutableMap<Space, Path> resolvers) {
    this.resolvers = resolvers;
  }

  public Path resolve(FileLocation fileLocation) {
    Path path = resolvers.get(fileLocation.space());
    if (path == null) {
      throw new RuntimeException("Cannot resolve full path for " + fileLocation);
    }
    return path.resolve(fileLocation.path());
  }
}
