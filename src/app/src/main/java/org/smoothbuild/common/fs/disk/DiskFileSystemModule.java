package org.smoothbuild.common.fs.disk;

import org.smoothbuild.common.fs.base.FileSystemFactory;

import com.google.inject.AbstractModule;

public class DiskFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(DiskFileSystemFactory.class);
  }
}
