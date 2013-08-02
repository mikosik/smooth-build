package org.smoothbuild.lang.type;

public interface FilesRo {
  public FileRo fileRo(Path path);

  public Iterable<FileRo> asIterable();
}
