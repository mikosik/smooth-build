package org.smoothbuild.io.util;

import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.define.ModuleLocation;

public class JarFile {
  private final ModuleLocation moduleLocation;
  private final Path resolvedPath;
  private final Hash hash;

  public JarFile(ModuleLocation moduleLocation, Path resolvedPath, Hash hash) {
    this.moduleLocation = moduleLocation;
    this.resolvedPath = resolvedPath;
    this.hash = hash;
  }

  public Hash hash() {
    return hash;
  }

  public ModuleLocation location() {
    return moduleLocation;
  }

  public Path path() {
    return resolvedPath;
  }
}
