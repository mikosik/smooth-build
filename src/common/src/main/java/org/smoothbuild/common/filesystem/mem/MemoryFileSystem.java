package org.smoothbuild.common.filesystem.mem;

import static okio.Okio.buffer;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Supplier;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathIterator;
import org.smoothbuild.common.filesystem.base.PathState;
import org.smoothbuild.common.filesystem.base.RecursivePathsIterator;
import org.smoothbuild.common.function.Function1;

/**
 * This class is NOT thread-safe.
 */
public class MemoryFileSystem implements FileSystem<FullPath> {
  private final Map<Alias, MemoryDir> rootDirs;

  public MemoryFileSystem(Set<Alias> aliases) {
    this.rootDirs = aliases.toMap(a -> new MemoryDir(null, Path.root()));
  }

  @Override
  public PathState pathState(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot check state of " + path.q() + ". ";
    return pathStateImpl(path, error);
  }

  private PathState pathStateImpl(FullPath path, Supplier<String> error) throws IOException {
    var elem = findElement(path, error);
    if (elem == null) {
      return PathState.NOTHING;
    }
    if (elem.isDir()) {
      return PathState.DIR;
    }
    return PathState.FILE;
  }

  @Override
  public PathIterator filesRecursively(FullPath dir) {
    return new RecursivePathsIterator(this, dir);
  }

  @Override
  public Iterable<Path> files(FullPath dir) throws IOException {
    Supplier<String> error = () -> "Cannot list files in " + dir.q() + ". ";
    MemoryElement found = findElement(dir, error);
    if (found == null) {
      throw new IOException(error.get() + "Dir " + dir.q() + " doesn't exist.");
    } else {
      if (found.isDir()) {
        return found.childNames();
      } else {
        throw new IOException(error.get() + "Dir " + dir.q() + " doesn't exist. It is a file.");
      }
    }
  }

  @Override
  public void move(FullPath source, FullPath target) throws IOException {
    assertAliasesAreEqual(source, target);
    Supplier<String> error = () -> "Cannot move " + source.q() + " to " + target.q() + ". ";
    var sourceState = pathStateImpl(source, error);
    if (sourceState == PathState.NOTHING) {
      throw new IOException(error.get() + "Source doesn't exist.");
    }
    if (sourceState == PathState.DIR) {
      throw new IOException(error.get() + "Source is a directory.");
    }
    var targetState = pathStateImpl(target, error);
    if (targetState == PathState.DIR) {
      throw new IOException(error.get() + "Target is a directory.");
    }
    try (var bufferedSource = buffer(source(source))) {
      try (var sink = sink(target)) {
        bufferedSource.readAll(sink);
      }
    }
    deleteRecursively(source);
  }

  @Override
  public void deleteRecursively(FullPath path) throws IOException {
    Supplier<String> message = () -> "Cannot delete " + path.q() + ". ";
    var root = rootDirFor(path, message);
    if (path.isRoot()) {
      root.removeAllChildren();
      return;
    }

    MemoryElement elem = findElement(path, message);
    if (elem == null) {
      return;
    }

    elem.parent().removeChild(elem);
  }

  @Override
  public long size(FullPath path) throws IOException {
    var file = getFile(path, () -> "Cannot fetch size of " + path.q() + ". ");
    return file.size();
  }

  @Override
  public Source source(FullPath path) throws IOException {
    return getFile(path, () -> "Cannot read " + path.q() + ". ").source();
  }

  @Override
  public Sink sink(FullPath path) throws IOException {
    return createObject(
        path.parent(),
        (parentDir) -> createSink(parentDir, path.path().lastPart()),
        () -> "Cannot create sink for " + path.q() + ". ");
  }

  private <T> T createObject(
      FullPath parentPath, Function1<MemoryDir, T, IOException> creator, Supplier<String> error)
      throws IOException {
    var parent = findElement(parentPath, error);
    if (parent == null) {
      throw new IOException(error.get() + "No such dir " + parentPath.q() + ".");
    }
    return switch (resolveLinksFully(parent)) {
      case MemoryFile file -> throw new IOException(
          error.get() + "One of parents exists and is a file.");
      case MemoryDir dir -> creator.apply(dir);
      case MemoryLink link -> throw new RuntimeException("Should not happen");
    };
  }

  private MemoryElement resolveLinksFully(MemoryElement element) {
    while (element instanceof MemoryLink link) {
      element = link.target();
    }
    return element;
  }

  private static Sink createSink(MemoryDir parentDir, Path name) throws IOException {
    if (parentDir.hasChild(name)) {
      return parentDir.child(name).sink();
    } else {
      MemoryFile child = new MemoryFile(parentDir, name);
      parentDir.addChild(child);
      return child.sink();
    }
  }

  @Override
  public void createLink(FullPath link, FullPath target) throws IOException {
    Supplier<String> error = () -> "Cannot create link " + link.q() + " -> " + target.q() + ". ";

    assertAliasesAreEqual(link, target);
    switch (pathStateImpl(link, error)) {
      case FILE, DIR -> throw new IOException(
          error.get() + "Cannot use " + link.q() + " path. It is already taken.");
      case NOTHING -> {}
    }

    MemoryElement targetElement = findElement(target, error);
    createObject(
        link.parent(),
        (dir) -> {
          Path name = link.path().lastPart();
          dir.addChild(new MemoryLink(dir, name, targetElement));
          return (Void) null;
        },
        error);
  }

  @Override
  public void createDir(FullPath dir) throws IOException {
    Supplier<String> error = () -> "Cannot create dir " + dir.q() + ". ";
    MemoryDir currentDir = rootDirFor(dir, error);
    for (Path name : dir.path().parts()) {
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDir()) {
          currentDir = (MemoryDir) child;
        } else {
          throw new IOException(
              error.get() + "Cannot use " + dir.q() + ". It is already taken by file.");
        }
      } else {
        MemoryDir newDir = new MemoryDir(currentDir, name);
        currentDir.addChild(newDir);
        currentDir = newDir;
      }
    }
  }

  private MemoryDir rootDirFor(FullPath path, Supplier<String> error) throws IOException {
    var alias = path.alias();
    var dir = rootDirs.get(alias);
    if (dir == null) {
      throw new IOException(
          error.get() + "Unknown alias " + alias + ". Known aliases = " + rootDirs.keySet());
    }
    return dir;
  }

  private MemoryElement getFile(FullPath path, Supplier<String> error) throws IOException {
    var found = getElement(path, error);
    if (found.isFile()) {
      return found;
    } else {
      throw new IOException(error.get() + "File " + path.q() + " doesn't exist. It is a dir.");
    }
  }

  private MemoryElement getElement(FullPath path, Supplier<String> error) throws IOException {
    MemoryElement found = findElement(path, error);
    if (found == null) {
      throw new IOException(error.get() + "File " + path.q() + " doesn't exist.");
    } else {
      return found;
    }
  }

  private MemoryElement findElement(FullPath path, Supplier<String> error) throws IOException {
    var memoryDir = rootDirFor(path, error);
    return findElement(memoryDir, path.path());
  }

  private MemoryElement findElement(MemoryDir rootDir, Path path) {
    Iterator<Path> it = path.parts().iterator();
    MemoryElement current = rootDir;
    while (it.hasNext()) {
      Path name = it.next();
      if (current.hasChild(name)) {
        current = current.child(name);
      } else {
        return null;
      }
    }
    return current;
  }

  public static void assertAliasesAreEqual(FullPath source, FullPath target) throws IOException {
    var sourceAlias = source.alias();
    var targetAlias = target.alias();
    if (!sourceAlias.equals(targetAlias)) {
      throw new IOException("Alias '%s' in source is different from alias '%s' in target."
          .formatted(sourceAlias.name(), targetAlias.name()));
    }
  }
}
