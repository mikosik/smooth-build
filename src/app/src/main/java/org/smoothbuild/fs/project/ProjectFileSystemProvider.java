package org.smoothbuild.fs.project;

import static org.smoothbuild.fs.space.Space.PROJECT;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.FileSystemFactory;

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
