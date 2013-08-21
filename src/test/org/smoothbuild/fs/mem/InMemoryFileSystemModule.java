package org.smoothbuild.fs.mem;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.mem.InMemoryFileSystem;

import com.google.inject.AbstractModule;

public class InMemoryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FileSystem.class).to(InMemoryFileSystem.class);
  }
}
