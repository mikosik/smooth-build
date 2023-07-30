package org.smoothbuild.fs.project;

import static org.smoothbuild.fs.space.Space.PROJECT;

import java.nio.file.Path;
import java.util.Objects;

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

public class ProjectFileSystemModule extends AbstractModule {
  private final Path projectDir;

  public ProjectFileSystemModule(Path projectDir) {
    this.projectDir = projectDir;
  }

  @Override
  protected void configure() {
    configureSpaceToFileSystemMap();
  }

  private void configureSpaceToFileSystemMap() {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, FileSystem.class);
    mapBinder.addBinding(PROJECT).to(Key.get(FileSystem.class, new ForSpaceImpl(PROJECT)));
  }

  @Provides
  @Singleton
  @ForSpace(PROJECT)
  public FileSystem provideProjectFileSystem() {
    return new SynchronizedFileSystem(new DiskFileSystem(Objects.requireNonNull(projectDir)));
  }
}
