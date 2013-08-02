package org.smoothbuild.lang.type;


public interface FilesRw extends FilesRo {
  public FileRw createFileRw(Path path);
}
