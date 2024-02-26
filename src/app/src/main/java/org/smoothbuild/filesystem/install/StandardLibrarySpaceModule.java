package org.smoothbuild.filesystem.install;

import static org.smoothbuild.filesystem.space.SmoothSpace.STANDARD_LIBRARY;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.space.FileSystemFactory;
import org.smoothbuild.filesystem.space.ForSpace;

public class StandardLibrarySpaceModule extends AbstractModule {
  @Override
  protected void configure() {
    addMapBindingForSpaceFileSystem(binder(), STANDARD_LIBRARY);
  }

  @Provides
  @Singleton
  @ForSpace(STANDARD_LIBRARY)
  public FileSystem provideFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(STANDARD_LIBRARY);
  }
}
