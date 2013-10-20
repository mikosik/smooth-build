package org.smoothbuild.testing.fs.base;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;

import com.google.inject.AbstractModule;

public class FakeFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FakeFileSystem.class).in(Singleton.class);
    bind(FileSystem.class).to(FakeFileSystem.class);
  }
}
