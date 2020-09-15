package org.smoothbuild.install;

import java.nio.file.Path;

import org.smoothbuild.lang.base.ModuleLocation;
import org.smoothbuild.lang.base.Space;

import com.google.common.collect.ImmutableMap;

public class FullPathResolver {
  private final ImmutableMap<Space, Path> resolvers;

  public FullPathResolver(ImmutableMap<Space, Path> resolvers) {
    this.resolvers = resolvers;
  }

  public Path resolve(ModuleLocation moduleLocation) {
    Path path = resolvers.get(moduleLocation.space());
    if (path == null) {
      throw new RuntimeException("Cannot resolve full path for " + moduleLocation);
    }
    return path.resolve(moduleLocation.path());
  }
}
