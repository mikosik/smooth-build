package org.smoothbuild.common.filesystem.wiring;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Space;

public interface FileSystemFactory {
  public FileSystem create(Space space);
}
