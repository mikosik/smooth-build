package org.smoothbuild.io.fs;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.disk.DiskFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class FileSystemModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @ProjectDir
  public FileSystem provideProjectFileSystem() {
    return new DiskFileSystem(Path.root());
  }

  @Provides
  @SmoothDir
  public FileSystem provideSmoothFileSystem() {
    return new DiskFileSystem(SMOOTH_DIR);
  }
}
