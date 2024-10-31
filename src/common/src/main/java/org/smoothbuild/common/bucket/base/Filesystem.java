package org.smoothbuild.common.bucket.base;

import static org.smoothbuild.common.bucket.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.common.bucket.base.SubBucket.subBucket;

import jakarta.inject.Inject;
import java.io.IOException;
import okio.Sink;
import okio.Source;

public class Filesystem {
  private final BucketResolver bucketResolver;

  @Inject
  public Filesystem(BucketResolver bucketResolver) {
    this.bucketResolver = bucketResolver;
  }

  public PathState pathState(FullPath path) {
    return bucketFor(path.bucketId()).pathState(path.path());
  }

  public PathIterator filesRecursively(FullPath dir) throws IOException {
    try {
      return recursivePathsIterator(bucketFor(dir.bucketId()), dir.path());
    } catch (IOException e) {
      throw new IOException(
          "Error listing files recursively in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  public Iterable<Path> files(FullPath dir) throws IOException {
    try {
      return bucketFor(dir.bucketId()).files(dir.path());
    } catch (IOException e) {
      throw new IOException("Error listing files in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  public void move(FullPath source, FullPath target) throws IOException {
    var bucketId = getBucketIdIfEqualOrFail(source, target);
    try {
      bucketFor(bucketId).move(source.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error moving %s to %s. %s".formatted(source.q(), target.q(), e.getMessage()));
    }
  }

  public void delete(FullPath path) throws IOException {
    bucketFor(path.bucketId()).delete(path.path());
  }

  public long size(FullPath path) throws IOException {
    try {
      return bucketFor(path.bucketId()).size(path.path());
    } catch (IOException e) {
      throw new IOException("Error fetching size of %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public Source source(FullPath path) throws IOException {
    try {
      return bucketFor(path.bucketId()).source(path.path());
    } catch (IOException e) {
      throw new IOException("Error reading file %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public Sink sink(FullPath path) throws IOException {
    try {
      return bucketFor(path.bucketId()).sink(path.path());
    } catch (IOException e) {
      throw new IOException("Error writing file %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public void createLink(FullPath link, FullPath target) throws IOException {
    var bucketId = getBucketIdIfEqualOrFail(link, target);
    try {
      bucketFor(bucketId).createLink(link.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error creating link %s -> %s. %s".formatted(link.q(), target.q(), e.getMessage()));
    }
  }

  public void createDir(FullPath path) throws IOException {
    try {
      bucketFor(path.bucketId()).createDir(path.path());
    } catch (IOException e) {
      throw new IOException("Error creating dir %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public Bucket bucketFor(FullPath path) {
    return subBucket(bucketResolver.bucketFor(path.bucketId()), path.path());
  }

  public Bucket bucketFor(BucketId bucketId) {
    return bucketResolver.bucketFor(bucketId);
  }

  private static BucketId getBucketIdIfEqualOrFail(FullPath source, FullPath target) {
    var sourceBucketId = source.bucketId();
    var targetBucketId = target.bucketId();
    if (sourceBucketId.equals(targetBucketId)) {
      return sourceBucketId;
    } else {
      throw new IllegalArgumentException("Source bucket '%s' and target bucket '%s' must be equal."
          .formatted(sourceBucketId.id(), targetBucketId.id()));
    }
  }
}
