package org.smoothbuild.type.api;


public interface MutableFileSet extends FileSet {
  public MutableFile createFile(Path path);
}
