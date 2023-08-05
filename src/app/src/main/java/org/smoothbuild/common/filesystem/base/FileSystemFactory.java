package org.smoothbuild.common.filesystem.base;

import org.smoothbuild.filesystem.space.Space;

public interface FileSystemFactory {
  public FileSystem create(Space space);
}
