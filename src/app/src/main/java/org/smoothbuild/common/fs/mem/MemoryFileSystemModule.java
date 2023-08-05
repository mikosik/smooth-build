package org.smoothbuild.common.fs.mem;

import org.smoothbuild.common.fs.base.FileSystemFactory;

import com.google.inject.AbstractModule;

public class MemoryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(MemoryFileSystemFactory.class);
  }
}
