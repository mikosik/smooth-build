package org.smoothbuild.testing.fs.base;

import javax.inject.Singleton;

import org.smoothbuild.fs.ProjectDir;
import org.smoothbuild.fs.base.FileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class FakeFileSystemModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @ProjectDir
  public FakeFileSystem provideFakeFileSystem() {
    return new FakeFileSystem();
  }

  @Provides
  @ProjectDir
  public FileSystem provideProjectFileSystem(@ProjectDir FakeFileSystem fakeFileSystem) {
    return fakeFileSystem;
  }
}
