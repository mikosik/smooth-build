package org.smoothbuild.filesystem.install;

import static org.smoothbuild.filesystem.space.Space.BINARY;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.filesystem.space.FileSystemFactory;
import org.smoothbuild.filesystem.space.ForSpace;

public class BinarySpaceModule extends AbstractModule {
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
