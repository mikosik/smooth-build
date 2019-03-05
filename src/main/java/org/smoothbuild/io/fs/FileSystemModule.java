package org.smoothbuild.io.fs;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.disk.DiskFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class FileSystemModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public FileSystem provideFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(Path.root()));
  }
}
