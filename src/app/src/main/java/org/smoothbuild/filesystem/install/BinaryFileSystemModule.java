package org.smoothbuild.filesystem.install;

import static org.smoothbuild.filesystem.space.Space.BINARY;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FileSystemFactory;
import org.smoothbuild.filesystem.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class BinaryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    addMapBindingForSpaceFileSystem(binder(), BINARY);
  }

  @Provides
  @Singleton
  @ForSpace(BINARY)
  public FileSystem provideFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(BINARY);
  }
}
