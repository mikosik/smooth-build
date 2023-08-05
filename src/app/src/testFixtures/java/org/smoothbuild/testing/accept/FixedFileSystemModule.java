package org.smoothbuild.testing.accept;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.FileSystemFactory;

import com.google.inject.AbstractModule;

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
      case BINARY -> throw new IllegalArgumentException();
    });
  }
}
