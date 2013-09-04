package org.smoothbuild.plugin;

public interface FileSet extends Iterable<File> {
  public File file(Path path);
}
