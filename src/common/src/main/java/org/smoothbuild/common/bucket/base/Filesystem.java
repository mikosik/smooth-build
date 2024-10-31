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
    return recursivePathsIterator(bucketFor(dir.bucketId()), dir.path());
  }

  public Iterable<Path> files(FullPath dir) throws IOException {
    return bucketFor(dir.bucketId()).files(dir.path());
  }

  public void move(FullPath source, FullPath target) throws IOException {
    var bucketId = getBucketIdIfEqualOrFail(source, target);
    bucketFor(bucketId).move(source.path(), target.path());
  }

  public void delete(FullPath path) throws IOException {
    bucketFor(path.bucketId()).delete(path.path());
  }

  public long size(FullPath path) throws IOException {
    return bucketFor(path.bucketId()).size(path.path());
  }

  public Source source(FullPath path) throws IOException {
    return bucketFor(path.bucketId()).source(path.path());
  }

  public Sink sink(FullPath path) throws IOException {
    return bucketFor(path.bucketId()).sink(path.path());
  }

  public void createLink(FullPath link, FullPath target) throws IOException {
    var bucketId = getBucketIdIfEqualOrFail(link, target);
    bucketFor(bucketId).createLink(link.path(), target.path());
  }

  public void createDir(FullPath path) throws IOException {
    bucketFor(path.bucketId()).createDir(path.path());
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
      throw new IllegalArgumentException("source bucket '%s' and target bucket '%s' must be equal."
          .formatted(sourceBucketId.id(), targetBucketId.id()));
    }
  }
}
