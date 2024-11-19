package org.smoothbuild.common.filesystem.base;

import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;

public class FullFileSystemTest extends AbstractFullFileSystemTest {
  @Override
  protected FileSystem<FullPath> fileSystem(Set<Alias> aliases) {
    return new FullFileSystem(aliases.toMap(a -> new MemoryBucket()));
  }
}
