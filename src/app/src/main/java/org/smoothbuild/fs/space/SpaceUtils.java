package org.smoothbuild.fs.space;

import org.smoothbuild.common.fs.base.FileSystem;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.MapBinder;

public class SpaceUtils {
  public static void addMapBindingForSpaceFileSystem(Binder binder, Space space) {
    var mapBinder = MapBinder.newMapBinder(binder, Space.class, FileSystem.class);
    mapBinder.addBinding(space).to(Key.get(FileSystem.class, forSpace(space)));
  }

  public static ForSpaceImpl forSpace(Space space) {
    return new ForSpaceImpl(space);
  }
}
