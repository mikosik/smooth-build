package org.smoothbuild.install;

import static org.smoothbuild.fs.space.Space.STD_LIB;
import static org.smoothbuild.install.InstallationPaths.STD_LIB_DIR_NAME;

import java.nio.file.Path;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.disk.DiskFileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.fs.space.ForSpaceImpl;
import org.smoothbuild.fs.space.Space;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

public class InstallationFileSystemModule extends AbstractModule {
  private final Path installationDir;

  public InstallationFileSystemModule(Path installationDir) {
    this.installationDir = installationDir;
  }

  @Override
  protected void configure() {
    bind(InstallationPaths.class).toInstance(new InstallationPaths(installationDir));
    configureSpaceToPathMap();
    configureSpaceToFileSystemMap();
  }

  private void configureSpaceToPathMap() {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, Path.class);
    mapBinder.addBinding(STD_LIB).toInstance(stdLibDir());
  }

  private void configureSpaceToFileSystemMap() {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, FileSystem.class);
    mapBinder.addBinding(STD_LIB).to(Key.get(FileSystem.class, new ForSpaceImpl(STD_LIB)));
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
}
