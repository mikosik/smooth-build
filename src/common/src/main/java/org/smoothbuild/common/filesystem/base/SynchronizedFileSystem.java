package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

/**
 * This class is thread-safe.
 */
public class SynchronizedFileSystem<P extends PathI<P>> implements FileSystem<P> {
  private final FileSystem<P> fileSystem;

  public SynchronizedFileSystem(FileSystem<P> fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public synchronized PathState pathState(P path) throws IOException {
    return fileSystem.pathState(path);
  }

  @Override
  public synchronized PathIterator filesRecursively(P dir) {
    return fileSystem.filesRecursively(dir);
  }

  @Override
  public synchronized Iterable<Path> files(P dir) throws IOException {
    return fileSystem.files(dir);
  }

  @Override
  public synchronized void move(P source, P target) throws IOException {
    fileSystem.move(source, target);
  }

  @Override
  public synchronized void deleteRecursively(P path) throws IOException {
    fileSystem.deleteRecursively(path);
  }

  @Override
  public synchronized long size(P path) throws IOException {
    return fileSystem.size(path);
  }

  @Override
  public synchronized Source source(P path) throws IOException {
    return fileSystem.source(path);
  }

  @Override
  public synchronized Sink sink(P path) throws IOException {
    return fileSystem.sink(path);
  }

  @Override
  public synchronized void createLink(P link, P target) throws IOException {
    fileSystem.createLink(link, target);
  }

  @Override
  public synchronized void createDir(P path) throws IOException {
    fileSystem.createDir(path);
  }
}
