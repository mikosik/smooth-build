package org.smoothbuild.filesystem.project;

import static org.smoothbuild.filesystem.space.SmoothSpace.PROJECT;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.space.FileSystemFactory;
import org.smoothbuild.filesystem.space.ForSpace;

public class ProjectSpaceModule extends AbstractModule {
  @Override
  protected void configure() {
    addMapBindingForSpaceFileSystem(binder(), PROJECT);
  }

  @Provides
  @Singleton
  @ForSpace(PROJECT)
  public FileSystem provideFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(PROJECT);
  }
}
