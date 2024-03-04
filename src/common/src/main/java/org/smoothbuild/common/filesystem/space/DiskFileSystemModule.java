package org.smoothbuild.common.filesystem.space;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import java.nio.file.Path;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Space;

public class DiskFileSystemModule extends AbstractModule {
  private final Map<Space, Path> spaceToPath;

  public DiskFileSystemModule(Map<Space, Path> spaceToPath) {
    this.spaceToPath = spaceToPath;
  }

  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(DiskFileSystemFactory.class);
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, Path.class);
    spaceToPath.forEach((space, path) -> mapBinder.addBinding(space).toInstance(path));
  }
}
