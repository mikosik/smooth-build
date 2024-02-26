package org.smoothbuild.testing.accept;

import static org.smoothbuild.filesystem.space.SmoothSpace.PROJECT;
import static org.smoothbuild.filesystem.space.SmoothSpace.STANDARD_LIBRARY;

import com.google.inject.AbstractModule;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.space.FileSystemFactory;

public class FixedFileSystemModule extends AbstractModule {
  private final FileSystem prjFileSystem;
  private final FileSystem stdLibFileSystem;

  public FixedFileSystemModule(FileSystem prjFileSystem, FileSystem stdLibFileSystem) {
    this.prjFileSystem = prjFileSystem;
    this.stdLibFileSystem = stdLibFileSystem;
  }

  @Override
  protected void configure() {
    bind(FileSystemFactory.class).toInstance(space -> switch (space) {
      case PROJECT -> prjFileSystem;
      case STANDARD_LIBRARY -> stdLibFileSystem;
      default -> throw new IllegalArgumentException();
    });
  }
}
