package org.smoothbuild.fs.install;

import static org.smoothbuild.fs.space.Space.BIN;
import static org.smoothbuild.fs.space.Space.STD_LIB;

import java.nio.file.Path;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.SynchronizedFileSystem;
import org.smoothbuild.common.fs.disk.DiskFileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.fs.space.ForSpaceImpl;
import org.smoothbuild.fs.space.Space;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

public class InstallationFileSystemModule extends AbstractModule {
  private static final String BIN_DIR_NAME = "bin";
  private static final String STD_LIB_DIR_NAME = "lib";

  private final Path installationDir;

  public InstallationFileSystemModule(Path installationDir) {
    this.installationDir = installationDir;
  }

  @Override
  protected void configure() {
    configureSpaceToFileSystemMap(STD_LIB);
    configureSpaceToFileSystemMap(BIN);
  }

  private void configureSpaceToFileSystemMap(Space space) {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, FileSystem.class);
    mapBinder.addBinding(space).to(Key.get(FileSystem.class, new ForSpaceImpl(space)));
  }

  @Provides
  @Singleton
  @ForSpace(STD_LIB)
  public FileSystem provideStdLibFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(stdLibDir()));
  }

  private Path stdLibDir() {
    return installationDir.resolve(STD_LIB_DIR_NAME);
  }

  @Provides
  @Singleton
  @ForSpace(BIN)
  public FileSystem provideBinFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(binDir()));
  }

  private Path binDir() {
    return installationDir.resolve(BIN_DIR_NAME);
  }
}
