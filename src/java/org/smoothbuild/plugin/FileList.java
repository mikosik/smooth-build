package org.smoothbuild.plugin;

public interface FileList {
  public File file(Path path);

  public Iterable<File> asIterable();

  public File createFile(Path path);
}
