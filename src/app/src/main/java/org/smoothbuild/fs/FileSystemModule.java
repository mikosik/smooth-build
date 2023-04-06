package org.smoothbuild.fs;

import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.fs.space.Space.STD_LIB;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.nio.file.Path;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.disk.DiskFileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.fs.space.Space;
import org.smoothbuild.install.InstallationPaths;

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
  @ForSpace(STD_LIB)
  public FileSystem provideSlibFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
    return fileSystems.get(STD_LIB);
  }

  @Provides
  @Singleton
  public ImmutableMap<Space, Path> provideSpaceToPathMap(InstallationPaths installationPaths) {
    Path slibDir = installationPaths.standardLibraryDir();
    if (projectDir == null) {
      return ImmutableMap.of(STD_LIB, slibDir);
    } else {
      return ImmutableMap.of(STD_LIB, slibDir, PRJ, projectDir);
    }
  }
}
