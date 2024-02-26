package org.smoothbuild.common.filesystem.space;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;

public class MemoryFileSystemFactory implements FileSystemFactory {
  @Override
  public FileSystem create(Space space) {
    return new SynchronizedFileSystem(new MemoryFileSystem());
  }
}
