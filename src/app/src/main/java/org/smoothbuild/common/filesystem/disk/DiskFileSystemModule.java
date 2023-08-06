package org.smoothbuild.common.filesystem.disk;

import java.nio.file.Path;

import org.smoothbuild.common.filesystem.base.FileSystemFactory;
import org.smoothbuild.filesystem.space.Space;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class DiskFileSystemModule extends AbstractModule {
  private final ImmutableMap<Space, Path> spaceToPath;

  public DiskFileSystemModule(ImmutableMap<Space, Path> spaceToPath) {
    this.spaceToPath = spaceToPath;
  }

  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(DiskFileSystemFactory.class);
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, Path.class);
    spaceToPath.forEach((space, path) -> mapBinder.addBinding(space).toInstance(path));
  }
}
