package org.smoothbuild.db.values;

import javax.inject.Singleton;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TestValuesDbModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new ValuesDbModule());
  }

  @Provides
  @Singleton
  @SmoothDir
  public FileSystem provideSmoothFileSystem() {
    return new MemoryFileSystem();
  }
}
