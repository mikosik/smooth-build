package org.smoothbuild.fs.install;

import static org.smoothbuild.fs.space.Space.BINARY;
import static org.smoothbuild.fs.space.SpaceUtils.addMapBindingForSpaceFileSystem;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.FileSystemFactory;
import org.smoothbuild.fs.space.ForSpace;

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
  public FileSystem provideBinFileSystem(FileSystemFactory fileSystemFactory) {
    return fileSystemFactory.create(BINARY);
  }
}
