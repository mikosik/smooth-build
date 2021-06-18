package org.smoothbuild.io.fs;

import static org.smoothbuild.install.InstallationPaths.LIB_DIR_NAME;
import static org.smoothbuild.io.fs.base.Space.PRJ;
import static org.smoothbuild.io.fs.base.Space.SDK;

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
  private final Path installationDir;

  public FileSystemModule(Path projectDir, Path installationDir) {
    this.projectDir = projectDir;
    this.installationDir = installationDir;
  }

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @ForSpace(PRJ)
  public FileSystem providePrjFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(projectDir));
  }

  @Provides
  @Singleton
  @ForSpace(SDK)
  public FileSystem provideSdkFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(installationDir.resolve(LIB_DIR_NAME)));
  }
}
