package org.smoothbuild.fs.mem;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;

import com.google.inject.AbstractModule;

public class InMemoryFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(InMemoryFileSystem.class).in(Singleton.class);
    bind(FileSystem.class).to(InMemoryFileSystem.class);
  }
}
