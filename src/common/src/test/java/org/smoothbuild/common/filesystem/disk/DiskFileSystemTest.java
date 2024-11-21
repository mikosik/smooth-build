package org.smoothbuild.common.filesystem.disk;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.filesystem.base.AbstractFileSystemTest;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;

public class DiskFileSystemTest extends AbstractFileSystemTest {
  @TempDir
  java.nio.file.Path path;

  @Override
  protected FileSystem<FullPath> fileSystem(Set<Alias> aliases) {
    var aliasToPath = aliases.toMap(a -> path.resolve(a.name()));
    try {
      aliasToPath.mapValues(Files::createDirectories);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new DiskFileSystem(aliasToPath);
  }
}
