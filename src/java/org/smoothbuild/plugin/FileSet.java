package org.smoothbuild.plugin;

public interface FileSet extends Iterable<File> {
  public boolean contains(Path path);

  public File file(Path path);

}
