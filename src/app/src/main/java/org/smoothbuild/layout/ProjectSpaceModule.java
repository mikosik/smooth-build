package org.smoothbuild.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.space.FileSystemFactory;

public class ProjectSpaceModule extends AbstractModule {
  @Override
  protected void configure() {
    SpaceUtils.addMapBindingForSpaceFileSystem(binder(), SmoothSpace.PROJECT);
  }

  @Provides
  @Singleton
  @ForSpace(SmoothSpace.PROJECT)
  public FileSystem provideFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(SmoothSpace.PROJECT);
  }
}
