package org.smoothbuild.common.filesystem.mem;

import org.smoothbuild.common.filesystem.base.FileSystemFactory;

import com.google.inject.AbstractModule;

public class MemoryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(MemoryFileSystemFactory.class);
  }
}
