package org.smoothbuild.testing;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.testing.fs.base.TestFileSystem;

import com.google.inject.AbstractModule;

public class TestingFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestFileSystem.class).in(Singleton.class);
    bind(FileSystem.class).to(TestFileSystem.class);
  }
}
