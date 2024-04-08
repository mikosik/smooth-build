package org.smoothbuild.common.bucket.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

/**
 * This class is thread-safe.
 */
public class SynchronizedBucket implements Bucket {
  private final Bucket bucket;

  public SynchronizedBucket(Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public synchronized PathState pathState(Path path) {
    return bucket.pathState(path);
  }

  @Override
  public synchronized Iterable<Path> files(Path dir) throws IOException {
    return bucket.files(dir);
  }

  @Override
  public synchronized void move(Path source, Path target) throws IOException {
    bucket.move(source, target);
  }

  @Override
  public synchronized void delete(Path path) throws IOException {
    bucket.delete(path);
  }

  @Override
  public synchronized long size(Path path) throws IOException {
    return bucket.size(path);
  }

  @Override
  public synchronized Source source(Path path) throws IOException {
    return bucket.source(path);
  }

  @Override
  public synchronized Sink sink(Path path) throws IOException {
    return bucket.sink(path);
  }

  @Override
  public synchronized void createLink(Path link, Path target) throws IOException {
    bucket.createLink(link, target);
  }

  @Override
  public synchronized void createDir(Path path) throws IOException {
    bucket.createDir(path);
  }
}
