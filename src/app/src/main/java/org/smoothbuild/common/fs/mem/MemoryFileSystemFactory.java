package org.smoothbuild.common.fs.mem;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.FileSystemFactory;
import org.smoothbuild.common.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.space.Space;

public class MemoryFileSystemFactory implements FileSystemFactory {
  @Override
  public FileSystem create(Space space) {
    return new SynchronizedFileSystem(new MemoryFileSystem());
  }
}
