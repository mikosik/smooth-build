package org.smoothbuild.fs.install;

import static org.smoothbuild.fs.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.fs.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.FileSystemFactory;
import org.smoothbuild.fs.space.ForSpace;

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
