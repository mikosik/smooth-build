package org.smoothbuild.common.filesystem.space;

import org.smoothbuild.common.filesystem.base.FileSystem;

public interface FileSystemFactory {
  public FileSystem create(Space space);
}