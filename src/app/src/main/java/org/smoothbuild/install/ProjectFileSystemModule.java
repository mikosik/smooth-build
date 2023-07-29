package org.smoothbuild.install;

import static org.smoothbuild.fs.space.Space.PRJ;

import java.nio.file.Path;
import java.util.Objects;

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

public class ProjectFileSystemModule extends AbstractModule {
  private final Path projectDir;

  public ProjectFileSystemModule(Path projectDir) {
    this.projectDir = projectDir;
  }

  @Override
  protected void configure() {
    configureSpaceToPathMap();
    configureSpaceToFileSystemMap();
  }

  private void configureSpaceToPathMap() {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, Path.class);
    mapBinder.addBinding(PRJ).toInstance(projectDir);
  }

  private void configureSpaceToFileSystemMap() {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, FileSystem.class);
    mapBinder.addBinding(PRJ).to(Key.get(FileSystem.class, new ForSpaceImpl(PRJ)));
  }

  @Provides
  @Singleton
  @ForSpace(PRJ)
  public FileSystem provideProjectFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(Objects.requireNonNull(projectDir)));
  }
}
