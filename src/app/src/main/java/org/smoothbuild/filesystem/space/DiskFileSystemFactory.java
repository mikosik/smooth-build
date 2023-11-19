package org.smoothbuild.filesystem.space;

import jakarta.inject.Inject;
import java.nio.file.Path;
import java.util.Map;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.disk.DiskFileSystem;

public class DiskFileSystemFactory implements FileSystemFactory {
  private final Map<Space, Path> spacePathMap;

  @Inject
  public DiskFileSystemFactory(Map<Space, Path> spacePathMap) {
    this.spacePathMap = spacePathMap;
  }

  @Override
  public FileSystem create(Space space) {
    return new SynchronizedFileSystem(new DiskFileSystem(spacePathMap.get(space)));
  }
}
