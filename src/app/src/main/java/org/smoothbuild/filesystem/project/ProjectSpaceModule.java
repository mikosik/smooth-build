package org.smoothbuild.filesystem.project;

import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.filesystem.space.FileSystemFactory;
import org.smoothbuild.filesystem.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

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