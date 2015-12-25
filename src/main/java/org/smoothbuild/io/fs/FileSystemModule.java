package org.smoothbuild.io.fs;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.disk.DiskFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class FileSystemModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  public FileSystem provideProjectFileSystem() {
    return new DiskFileSystem(Path.root());
  }
}
