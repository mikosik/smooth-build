package org.smoothbuild.common.filesystem.base;

import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;

import jakarta.inject.Inject;
import java.io.IOException;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.Map;

public class FullFileSystem implements FileSystem<FullPath> {
  private final Map<Alias, FileSystem<Path>> buckets;

  @Inject
  public FullFileSystem(Map<Alias, FileSystem<Path>> buckets) {
    this.buckets = buckets;
  }

  @Override
  public PathState pathState(FullPath path) throws IOException {
    return fileSystemPart(path.alias()).pathState(path.path());
  }

  @Override
  public PathIterator filesRecursively(FullPath dir) throws IOException {
    var bucket = fileSystemPart(dir.alias());
    try {
      return recursivePathsIterator(bucket, dir.path());
    } catch (IOException e) {
      throw new IOException(
          "Error listing files recursively in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  @Override
  public Iterable<Path> files(FullPath dir) throws IOException {
    var bucket = fileSystemPart(dir.alias());
    try {
      return bucket.files(dir.path());
    } catch (IOException e) {
      throw new IOException("Error listing files in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  @Override
  public void move(FullPath source, FullPath target) throws IOException {
    var bucket = fileSystemPart(getAliasIfEqualOrFail(source, target));
    try {
      bucket.move(source.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error moving %s to %s. %s".formatted(source.q(), target.q(), e.getMessage()));
    }
  }

  @Override
  public void delete(FullPath path) throws IOException {
    fileSystemPart(path.alias()).delete(path.path());
  }

  @Override
  public long size(FullPath path) throws IOException {
    try {
      return fileSystemPart(path.alias()).size(path.path());
    } catch (IOException e) {
      throw new IOException("Error fetching size of %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  @Override
  public Source source(FullPath path) throws IOException {
    try {
      return fileSystemPart(path.alias()).source(path.path());
    } catch (IOException e) {
      throw new IOException("Error reading file %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  @Override
  public Sink sink(FullPath path) throws IOException {
    try {
      return fileSystemPart(path.alias()).sink(path.path());
    } catch (IOException e) {
      throw new IOException("Error writing file %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  @Override
  public void createLink(FullPath link, FullPath target) throws IOException {
    var bucket = fileSystemPart(getAliasIfEqualOrFail(link, target));
    try {
      bucket.createLink(link.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Error creating link %s -> %s. %s".formatted(link.q(), target.q(), e.getMessage()));
    }
  }

  @Override
  public void createDir(FullPath path) throws IOException {
    var bucket = fileSystemPart(path.alias());
    try {
      bucket.createDir(path.path());
    } catch (IOException e) {
      throw new IOException("Error creating dir %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  private FileSystem<Path> fileSystemPart(Alias alias) throws IOException {
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
