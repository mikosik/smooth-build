package org.smoothbuild.common.filesystem.mem;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FileSystemFactory;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.filesystem.space.Space;

public class MemoryFileSystemFactory implements FileSystemFactory {
  @Override
  public FileSystem create(Space space) {
    return new SynchronizedFileSystem(new MemoryFileSystem());
  }
}
