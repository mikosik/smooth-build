package org.smoothbuild.app.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.wiring.FileSystemFactory;

public class StandardLibrarySpaceModule extends AbstractModule {
  @Override
  protected void configure() {
    SpaceUtils.addMapBindingForSpaceFileSystem(binder(), SmoothSpace.STANDARD_LIBRARY);
  }

  @Provides
  @Singleton
  @ForSpace(SmoothSpace.STANDARD_LIBRARY)
  public FileSystem provideFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(SmoothSpace.STANDARD_LIBRARY);
  }
}
