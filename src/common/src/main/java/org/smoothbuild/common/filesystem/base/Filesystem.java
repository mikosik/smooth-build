package org.smoothbuild.common.filesystem.base;

import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.common.filesystem.base.SubBucket.subBucket;

import jakarta.inject.Inject;
import java.io.IOException;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.Map;

public class Filesystem {
  private final Map<Alias, Bucket> buckets;

  @Inject
  public Filesystem(Map<Alias, Bucket> buckets) {
    this.buckets = buckets;
  }

  public PathState pathState(FullPath path) throws IOException {
    return bucketFor(path.alias()).pathState(path.path());
  }

  public PathIterator filesRecursively(FullPath dir) throws IOException {
    try {
      return recursivePathsIterator(bucketFor(dir.alias()), dir.path());
    } catch (IOException e) {
      throw new IOException(
          "Error listing files recursively in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  public Iterable<Path> files(FullPath dir) throws IOException {
    try {
      return bucketFor(dir.alias()).files(dir.path());
    } catch (IOException e) {
      throw new IOException("Error listing files in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  public void move(FullPath source, FullPath target) throws IOException {
    var alias = getAliasIfEqualOrFail(source, target);
    try {
      bucketFor(alias).move(source.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error moving %s to %s. %s".formatted(source.q(), target.q(), e.getMessage()));
    }
  }

  public void delete(FullPath path) throws IOException {
    bucketFor(path.alias()).delete(path.path());
  }

  public long size(FullPath path) throws IOException {
    try {
      return bucketFor(path.alias()).size(path.path());
    } catch (IOException e) {
      throw new IOException("Error fetching size of %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public Source source(FullPath path) throws IOException {
    try {
      return bucketFor(path.alias()).source(path.path());
    } catch (IOException e) {
      throw new IOException("Error reading file %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public Sink sink(FullPath path) throws IOException {
    try {
      return bucketFor(path.alias()).sink(path.path());
    } catch (IOException e) {
      throw new IOException("Error writing file %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public void createLink(FullPath link, FullPath target) throws IOException {
    var alias = getAliasIfEqualOrFail(link, target);
    try {
      bucketFor(alias).createLink(link.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error creating link %s -> %s. %s".formatted(link.q(), target.q(), e.getMessage()));
    }
  }

  public void createDir(FullPath path) throws IOException {
    try {
      bucketFor(path.alias()).createDir(path.path());
    } catch (IOException e) {
      throw new IOException("Error creating dir %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public Bucket bucketFor(FullPath path) {
    return subBucket(bucketFor(path.alias()), path.path());
  }

  public Bucket bucketFor(Alias alias) {
    Bucket bucket = buckets.get(alias);
    if (bucket == null) {
      throw new IllegalArgumentException(
          "Unknown alias " + alias + ". Known aliases = " + buckets.keySet());
    }
    return bucket;
  }

  private static Alias getAliasIfEqualOrFail(FullPath source, FullPath target) {
    var sourceAlias = source.alias();
    var targetAlias = target.alias();
    if (sourceAlias.equals(targetAlias)) {
      return sourceAlias;
    } else {
      throw new IllegalArgumentException(
          "Alias '%s' in source is different from alias '%s' in target."
              .formatted(sourceAlias.name(), targetAlias.name()));
    }
  }
}
