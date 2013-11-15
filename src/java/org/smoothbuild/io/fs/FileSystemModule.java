package org.smoothbuild.io.fs;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.disk.DiskFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class FileSystemModule extends AbstractModule {
  public static final Path SMOOTH_DIR = path(".smooth");

  @Override
  protected void configure() {}

  @Provides
  @ProjectDir
  public FileSystem provideProjectFileSystem() {
    return new DiskFileSystem(".");
  }

  @Provides
  @SmoothDir
  public FileSystem provideSmoothFileSystem() {
    return new DiskFileSystem(SMOOTH_DIR.value());
  }

}
