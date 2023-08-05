package org.smoothbuild.common.filesystem.disk;

import org.smoothbuild.common.filesystem.base.FileSystemFactory;

import com.google.inject.AbstractModule;

public class DiskFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(DiskFileSystemFactory.class);
  }
}
