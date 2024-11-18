package org.smoothbuild.common.filesystem.base;

import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.common.filesystem.base.SubBucket.subBucket;

import jakarta.inject.Inject;
import java.io.IOException;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.Map;

public class Filesystem {
  private final Map<Alias, FileSystem<Path>> buckets;

  @Inject
  public Filesystem(Map<Alias, FileSystem<Path>> buckets) {
    this.buckets = buckets;
  }

  public PathState pathState(FullPath path) throws IOException {
    return bucketFor(path.alias()).pathState(path.path());
  }

  public PathIterator filesRecursively(FullPath dir) throws IOException {
    var bucket = bucketFor(dir.alias());
    try {
      return recursivePathsIterator(bucket, dir.path());
    } catch (IOException e) {
      throw new IOException(
          "Error listing files recursively in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  public Iterable<Path> files(FullPath dir) throws IOException {
    var bucket = bucketFor(dir.alias());
    try {
      return bucket.files(dir.path());
    } catch (IOException e) {
      throw new IOException("Error listing files in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  public void move(FullPath source, FullPath target) throws IOException {
    var bucket = bucketFor(getAliasIfEqualOrFail(source, target));
    try {
      bucket.move(source.path(), target.path());
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
    var bucket = bucketFor(getAliasIfEqualOrFail(link, target));
    try {
      bucket.createLink(link.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error creating link %s -> %s. %s".formatted(link.q(), target.q(), e.getMessage()));
    }
  }

  public void createDir(FullPath path) throws IOException {
    var bucket = bucketFor(path.alias());
    try {
      bucket.createDir(path.path());
    } catch (IOException e) {
      throw new IOException("Error creating dir %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  public FileSystem<Path> bucketFor(FullPath path) throws IOException {
    return subBucket(bucketFor(path.alias()), path.path());
  }

  public FileSystem<Path> bucketFor(Alias alias) throws IOException {
    FileSystem<Path> bucket = buckets.get(alias);
    if (bucket == null) {
      throw new IOException("Unknown alias " + alias + ". Known aliases = " + buckets.keySet());
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
