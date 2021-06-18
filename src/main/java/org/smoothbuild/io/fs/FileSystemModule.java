package org.smoothbuild.io.fs;

import static org.smoothbuild.io.fs.base.Space.PRJ;

import java.nio.file.Path;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.ForSpace;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.disk.DiskFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class FileSystemModule extends AbstractModule {
  private final Path projectDir;

  public FileSystemModule(Path projectDir) {
    this.projectDir = projectDir;
  }

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @ForSpace(PRJ)
  public FileSystem provideFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(projectDir));
  }
}
