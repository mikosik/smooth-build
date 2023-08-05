package org.smoothbuild.filesystem.install;

import static org.smoothbuild.filesystem.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.filesystem.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FileSystemFactory;
import org.smoothbuild.filesystem.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class StandardLibraryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    addMapBindingForSpaceFileSystem(binder(), STANDARD_LIBRARY);
  }

  @Provides
  @Singleton
  @ForSpace(STANDARD_LIBRARY)
  public FileSystem provideStdLibFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(STANDARD_LIBRARY);
  }
}
