package org.smoothbuild.common.filesystem.wiring;

import com.google.inject.AbstractModule;

public class MemoryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FileSystemFactory.class).to(MemoryFileSystemFactory.class);
  }
}