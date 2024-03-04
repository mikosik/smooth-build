package org.smoothbuild.layout;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.MapBinder;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Space;

public class SpaceUtils {
  public static void addMapBindingForSpaceFileSystem(Binder binder, SmoothSpace space) {
    var mapBinder = MapBinder.newMapBinder(binder, Space.class, FileSystem.class);
    mapBinder.addBinding(space).to(Key.get(FileSystem.class, forSpace(space)));
  }

  public static ForSpaceImpl forSpace(SmoothSpace space) {
    return new ForSpaceImpl(space);
  }
}
