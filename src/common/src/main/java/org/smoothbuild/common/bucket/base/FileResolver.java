package org.smoothbuild.common.bucket.base;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import okio.Sink;
import okio.Source;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileResolver {
  private final BucketResolver bucketResolver;

  @Inject
  public FileResolver(BucketResolver bucketResolver) {
    this.bucketResolver = bucketResolver;
  }

  public Source source(FullPath fullPath) throws IOException {
    return bucketResolver.bucketFor(fullPath.alias()).source(fullPath.path());
  }

  public Sink sink(FullPath fullPath) throws IOException {
    return bucketResolver.bucketFor(fullPath.alias()).sink(fullPath.path());
  }

  public PathState pathState(FullPath fullPath) {
    return bucketResolver.bucketFor(fullPath.alias()).pathState(fullPath.path());
  }
}
