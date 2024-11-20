package org.smoothbuild.common.filesystem.base;

import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.function.Supplier;
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
    return fileSystemPart(path.alias(), () -> "").pathState(path.path());
  }

  @Override
  public PathIterator filesRecursively(FullPath dir) throws IOException {
    var bucket =
        fileSystemPart(dir.alias(), () -> "Cannot list files recursively in " + dir.q() + ".");
    try {
      return recursivePathsIterator(bucket, dir.path());
    } catch (IOException e) {
      throw new IOException(
          "Error listing files recursively in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  @Override
  public Iterable<Path> files(FullPath dir) throws IOException {
    var bucket = fileSystemPart(dir.alias(), () -> "Cannot list files in " + dir.q() + ".");
    try {
      return bucket.files(dir.path());
    } catch (IOException e) {
      throw new IOException("Error listing files in %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  @Override
  public void move(FullPath source, FullPath target) throws IOException {
    var bucket = fileSystemPart(
        getAliasIfEqualOrFail(source, target),
        () -> "Cannot move " + source.q() + " to " + target.q() + ".");
    try {
      bucket.move(source.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Cannot move %s to %s. %s".formatted(source.q(), target.q(), e.getMessage()));
    }
  }

  @Override
  public void delete(FullPath path) throws IOException {
    var fileSystemPart = fileSystemPart(path.alias(), () -> "Cannot delete " + path.q() + ".");
    fileSystemPart.delete(path.path());
  }

  @Override
  public long size(FullPath path) throws IOException {
    try {
      return fileSystemPart(path.alias(), () -> "").size(path.path());
    } catch (IOException e) {
      throw new IOException("Cannot fetch size of %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  @Override
  public Source source(FullPath path) throws IOException {
    try {
      return fileSystemPart(path.alias(), () -> "").source(path.path());
    } catch (IOException e) {
      throw new IOException("Cannot read %s. %s".formatted(path.q(), e.getMessage()));
    }
  }

  @Override
  public Sink sink(FullPath path) throws IOException {
    try {
      return fileSystemPart(path.alias(), () -> "").sink(path.path());
    } catch (IOException e) {
      throw new IOException("Cannot create sink for %s. %s".formatted(path.q(), e.getMessage()), e);
    }
  }

  @Override
  public void createLink(FullPath link, FullPath target) throws IOException {
    var bucket = fileSystemPart(
        getAliasIfEqualOrFail(link, target),
        () -> "Cannot create link " + link.q() + " -> " + target.q() + ".");
    try {
      bucket.createLink(link.path(), target.path());
    } catch (IOException e) {
      throw new IOException(
          "Cannot create link %s -> %s. %s".formatted(link.q(), target.q(), e.getMessage()));
    }
  }

  @Override
  public void createDir(FullPath dir) throws IOException {
    var bucket = fileSystemPart(dir.alias(), () -> "Cannot create dir " + dir.q() + ".");
    try {
      bucket.createDir(dir.path());
    } catch (IOException e) {
      throw new IOException("Cannot create dir %s. %s".formatted(dir.q(), e.getMessage()));
    }
  }

  private FileSystem<Path> fileSystemPart(Alias alias, Supplier<String> error) throws IOException {
    FileSystem<Path> bucket = buckets.get(alias);
    if (bucket == null) {
      throw new IOException(
          error.get() + " Unknown alias " + alias + ". Known aliases = " + buckets.keySet());
    }
    return bucket;
  }

  private static Alias getAliasIfEqualOrFail(FullPath source, FullPath target) throws IOException {
    var sourceAlias = source.alias();
    var targetAlias = target.alias();
    if (sourceAlias.equals(targetAlias)) {
      return sourceAlias;
    } else {
      throw new IOException("Alias '%s' in source is different from alias '%s' in target."
          .formatted(sourceAlias.name(), targetAlias.name()));
    }
  }
}
