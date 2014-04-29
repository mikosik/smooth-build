package org.smoothbuild.db.objects;

import javax.inject.Singleton;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TestObjectsDbModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new ObjectsDbModule());
  }

  @Provides
  @Singleton
  @SmoothDir
  public FileSystem provideSmoothFileSystem() {
    return new MemoryFileSystem();
  }
}
