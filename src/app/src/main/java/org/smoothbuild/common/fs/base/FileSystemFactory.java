package org.smoothbuild.common.fs.base;

import org.smoothbuild.fs.space.Space;

public interface FileSystemFactory {
  public FileSystem create(Space space);
}
