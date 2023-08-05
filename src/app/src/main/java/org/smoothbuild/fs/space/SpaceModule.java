package org.smoothbuild.fs.space;

import java.nio.file.Path;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class SpaceModule extends AbstractModule {
  private final ImmutableMap<Space, Path> spaceToPath;

  public SpaceModule(ImmutableMap<Space, Path> spaceToPath) {
    this.spaceToPath = spaceToPath;
  }

  @Override
  protected void configure() {
    var mapBinder = MapBinder.newMapBinder(binder(), Space.class, Path.class);
    spaceToPath.forEach((space, path) -> mapBinder.addBinding(space).toInstance(path));
  }
}
