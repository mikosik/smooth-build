package org.smoothbuild.common.filesystem.mem;

import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.filesystem.base.AbstractFullFileSystemTest;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;

public class MemoryFullFileSystemTest extends AbstractFullFileSystemTest {
  @Override
  protected FileSystem<FullPath> fileSystem(Set<Alias> aliases) {
    return new MemoryFullFileSystem(aliases);
  }
}
