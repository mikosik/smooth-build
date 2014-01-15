package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.io.Constants.SMOOTH_DIR;

import javax.inject.Singleton;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SubFileSystem;

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

  @Provides
  @SmoothDir
  public FileSystem provideSmoothFileSystem(@ProjectDir FakeFileSystem fakeFileSystem) {
    return new SubFileSystem(fakeFileSystem, SMOOTH_DIR);
  }
}
