package org.smoothbuild.plugin.api;

public interface MutableFileSet extends FileSet {
  public MutableFile createFile(Path path);
}
