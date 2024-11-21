package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.common.collect.Set;

public class FullFileSystemTest extends AbstractFullFileSystemTest {
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
    return new FullFileSystem(aliasToPath);
  }
}
