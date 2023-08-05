package org.smoothbuild.common.fs.disk;

import java.nio.file.Path;
import java.util.Map;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.FileSystemFactory;
import org.smoothbuild.common.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.space.Space;

import jakarta.inject.Inject;

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
