package org.smoothbuild.testing;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;

import com.google.inject.AbstractModule;

public class TestingFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestingFileSystem.class).in(Singleton.class);
    bind(FileSystem.class).to(TestingFileSystem.class);
  }
}
