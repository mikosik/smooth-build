package org.smoothbuild.common.filesystem.mem;

import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.filesystem.base.AbstractFileSystemTest;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;

public class MemoryFileSystemTest extends AbstractFileSystemTest {
  @Override
  protected FileSystem<FullPath> fileSystem(Set<Alias> aliases) {
    return new MemoryFileSystem(aliases);
  }
}
