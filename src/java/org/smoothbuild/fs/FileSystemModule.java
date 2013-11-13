package org.smoothbuild.fs;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.disk.DiskFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class FileSystemModule extends AbstractModule {

  @Override
  protected void configure() {}

  @Provides
  @ProjectDir
  public FileSystem provideProjectFileSystem() {
    return new DiskFileSystem(".");
  }
}
