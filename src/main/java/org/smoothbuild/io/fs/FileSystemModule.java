package org.smoothbuild.io.fs;

import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.io.fs.space.Space.SDK;
import static org.smoothbuild.util.Maps.mapValues;

import java.nio.file.Path;

import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.disk.DiskFileSystem;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.io.fs.space.Space;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class FileSystemModule extends AbstractModule {
  private final Path projectDir;

  public FileSystemModule() {
    this(null);
  }

  public FileSystemModule(Path projectDir) {
    this.projectDir = projectDir;
  }

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ImmutableMap<Space, FileSystem> provideFileSystems(ImmutableMap<Space, Path> spacePaths) {
    return mapValues(spacePaths, FileSystemModule::newFileSystem);
  }

  private static SynchronizedFileSystem newFileSystem(Path path) {
    return new SynchronizedFileSystem(new DiskFileSystem(path));
  }

  @Provides
  @Singleton
  @ForSpace(PRJ)
  public FileSystem providePrjFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
    return fileSystems.get(PRJ);
  }

  @Provides
  @Singleton
  @ForSpace(SDK)
  public FileSystem provideSdkFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
    return fileSystems.get(SDK);
  }

  @Provides
  @Singleton
  public ImmutableMap<Space, Path> provideSpaceToPathMap(InstallationPaths installationPaths) {
    Path sdkApiDir = installationPaths.standardLibraryDir();
    if (projectDir == null) {
      return ImmutableMap.of(SDK, sdkApiDir);
    } else {
      return ImmutableMap.of(SDK, sdkApiDir, PRJ, projectDir);
    }
  }
}
