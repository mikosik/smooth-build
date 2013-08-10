package org.smoothbuild.lang.type;

public interface Files {
  public File file(Path path);

  public Iterable<File> asIterable();

  public File createFile(Path path);
}
