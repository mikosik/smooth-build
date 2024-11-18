package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

public class SubBucket implements Bucket {
  private final Bucket bucket;
  private final Path root;

  public static Bucket subBucket(Bucket bucket, Path path) {
    if (path.isRoot()) {
      return bucket;
    } else {
      return new SubBucket(bucket, path);
    }
  }

  private SubBucket(Bucket bucket, Path root) {
    this.bucket = bucket;
    this.root = root;
  }

  @Override
  public PathState pathState(Path path) throws IOException {
    return bucket.pathState(fullPath(path));
  }

  @Override
  public Iterable<Path> files(Path dir) throws IOException {
    return bucket.files(fullPath(dir));
  }

  @Override
  public void move(Path source, Path target) throws IOException {
    bucket.move(fullPath(source), fullPath(target));
  }

  @Override
  public void delete(Path path) throws IOException {
    bucket.delete(fullPath(path));
  }

  @Override
  public long size(Path path) throws IOException {
    return bucket.size(fullPath(path));
  }

  @Override
  public Source source(Path path) throws IOException {
    return bucket.source(fullPath(path));
  }

  @Override
  public Sink sink(Path path) throws IOException {
    return bucket.sink(fullPath(path));
  }

  @Override
  public void createLink(Path link, Path target) throws IOException {
    bucket.createLink(fullPath(link), fullPath(target));
  }

  @Override
  public void createDir(Path path) throws IOException {
    bucket.createDir(fullPath(path));
  }

  private Path fullPath(Path path) {
    return root.append(path);
  }
}
