package org.smoothbuild.io.fs;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.io.fs.base.Space.PRJ;
import static org.smoothbuild.io.fs.base.Space.SDK;

import java.nio.file.Path;
import java.util.Map.Entry;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.ForSpace;
import org.smoothbuild.io.fs.base.Space;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.disk.DiskFileSystem;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class FileSystemModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ImmutableMap<Space, FileSystem> provideFileSystems(ImmutableMap<Space, Path> spacePaths) {
    return spacePaths.entrySet()
        .stream()
        .collect(toImmutableMap(Entry::getKey, e -> newFileSystem(e.getValue())));
  }

  private static SynchronizedFileSystem newFileSystem(Path path) {
    return new SynchronizedFileSystem(new DiskFileSystem(path));
  }

  @Provides
  @Singleton
  @ForSpace(PRJ)
  public FileSystem providePrjFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
    return fileSystems.get(PRJ);
  }

  @Provides
  @Singleton
  @ForSpace(SDK)
  public FileSystem provideSdkFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
    return fileSystems.get(SDK);
  }
}
