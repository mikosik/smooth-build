package org.smoothbuild.testing.fs.base;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;

import com.google.inject.AbstractModule;

public class TestFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestFileSystem.class).in(Singleton.class);
    bind(FileSystem.class).to(TestFileSystem.class);
  }
}
