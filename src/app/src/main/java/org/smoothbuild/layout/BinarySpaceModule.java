package org.smoothbuild.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.space.FileSystemFactory;

public class BinarySpaceModule extends AbstractModule {
  @Override
  protected void configure() {
    SpaceUtils.addMapBindingForSpaceFileSystem(binder(), SmoothSpace.BINARY);
  }

  @Provides
  @Singleton
  @ForSpace(SmoothSpace.BINARY)
  public FileSystem provideFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(SmoothSpace.BINARY);
  }
}
