package org.smoothbuild.filesystem.project;

import static org.smoothbuild.filesystem.space.Space.PROJECT;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FileSystemFactory;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

@Singleton
public class ProjectFileSystemProvider implements Provider<FileSystem> {
  private final FileSystem fileSystem;

  @Inject
  public ProjectFileSystemProvider(FileSystemFactory fileSystemFactory) {
    this.fileSystem = fileSystemFactory.create(PROJECT);
  }

  @Override
  public FileSystem get() {
    return fileSystem;
  }
}
