package org.smoothbuild.common.filesystem.mem;

import static okio.Okio.buffer;
import static org.smoothbuild.common.filesystem.base.FileSystemPart.fileSystemPart;
import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;

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
import org.smoothbuild.common.function.Function1;

public class MemoryFullFileSystem implements FileSystem<FullPath> {
  private final Map<Alias, MemoryDir> rootDirs;

  public MemoryFullFileSystem(Set<Alias> aliases) {
    this.rootDirs = aliases.toMap(a -> new MemoryDir(null, Path.root()));
  }

  @Override
  public PathState pathState(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot return state of " + path.q() + ".";
    return pathStateImpl(path, error);
  }

  private PathState pathStateImpl(FullPath path, Supplier<String> error)
      throws IOException {
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
  public PathIterator filesRecursively(FullPath dir) throws IOException {
    findElement(dir, () -> "Cannot list files recursively in " + dir.q() + ".");
    try {
      return recursivePathsIterator(fileSystemPart(this, dir), Path.root());
    } catch (IOException e) {
      throw new IOException("Cannot list files recursively in " + dir.q() + ". " + e.getMessage());
    }
  }

  @Override
  public Iterable<Path> files(FullPath dir) throws IOException {
    MemoryElement found = findElement(dir, () -> "Cannot list files in " + dir.q() + ".");
    if (found == null) {
      throw new IOException(
          "Error listing files in " + dir.q() + ". Dir " + dir.path().q() + " doesn't exist.");
    } else {
      if (found.isDir()) {
        return found.childNames();
      } else {
        throw new IOException("Error listing files in " + dir.q() + ". Dir "
            + dir.path().q() + " doesn't exist. It is a file.");
      }
    }
  }

  @Override
  public void move(FullPath source, FullPath target) throws IOException {
    assertAliasesAreEqual(source, target);
    var sourceState = pathStateImpl(source, () -> cannotMoveMessage(source, target));
    if (sourceState == PathState.NOTHING) {
      throw new IOException(cannotMoveMessage(source, target) + " Cannot move "
          + source.path().q() + ". It doesn't exist.");
    }
    if (sourceState == PathState.DIR) {
      throw new IOException(cannotMoveMessage(source, target) + " Cannot move "
          + source.path().q() + ". It is directory.");
    }
    var targetState = pathStateImpl(target, () -> cannotMoveMessage(source, target));
    if (targetState == PathState.DIR) {
      throw new IOException(cannotMoveMessage(source, target) + " Cannot move to "
          + target.path().q() + ". It is directory.");
    }
    try (var bufferedSource = buffer(source(source))) {
      try (var sink = sink(target)) {
        bufferedSource.readAll(sink);
      }
    }
    delete(source);
  }

  private static String cannotMoveMessage(FullPath source, FullPath target) {
    return "Cannot move " + source.q() + " to " + target.q() + ".";
  }

  @Override
  public void delete(FullPath path) throws IOException {
    Supplier<String> message = () -> "Cannot delete " + path.q() + ".";
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
    var file = getFile(path, () -> "Cannot fetch size of " + path.q() + ".");
    return file.size();
  }

  @Override
  public Source source(FullPath path) throws IOException {
    return getFile(path, () -> "Cannot read " + path.q() + ".").source();
  }

  @Override
  public Sink sink(FullPath path) throws IOException {
    return createObject(
        path.parent(),
        (parentDir) -> createSink(parentDir, path.path().lastPart()),
        () -> "Cannot create sink for " + path.q() + ".");
  }

  private <T> T createObject(
      FullPath parentPath,
      Function1<MemoryDir, T, IOException> creator,
      Supplier<String> error)
      throws IOException {
    var parent = findElement(parentPath, error);
    if (parent == null) {
      throw new IOException(
          error.get() + " No such dir " + parentPath.path().q() + ".");
    }
    return switch (resolveLinksFully(parent)) {
      case MemoryFile file -> throw parentExistAsFileException(error, parentPath);
      case MemoryDir dir -> creator.apply(dir);
      case MemoryLink link -> throw new RuntimeException("Should not happen");
    };
  }

  private static IOException parentExistAsFileException(
      Supplier<String> error, FullPath path) {
    return new IOException(error.get() + " Cannot create object because its parent "
        + path.path().q() + " exists and is a file.");
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
    Supplier<String> error =
        () -> "Cannot create link " + link.q() + " -> " + target.q() + ".";

    assertAliasesAreEqual(link, target);
    switch (pathStateImpl(link, error)) {
      case FILE, DIR -> throw new IOException(error.get() + " Cannot use "
          + link.path().q() + " path. It is already taken.");
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
    Supplier<String> error = () -> "Cannot create dir " + dir.q() + ".";
    MemoryDir currentDir = rootDirFor(dir, error);
    for (Path name : dir.path().parts()) {
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDir()) {
          currentDir = (MemoryDir) child;
        } else {
          throw new IOException(error.get() + " Cannot use " + dir.path()
              + ". It is already taken by file.");
        }
      } else {
        MemoryDir newDir = new MemoryDir(currentDir, name);
        currentDir.addChild(newDir);
        currentDir = newDir;
      }
    }
  }

  private MemoryDir rootDirFor(FullPath path, Supplier<String> error)
      throws IOException {
    var alias = path.alias();
    var bucket = rootDirs.get(alias);
    if (bucket == null) {
      throw new IOException(error.get() + " Unknown alias " + alias
          + ". Known aliases = " + rootDirs.keySet());
    }
    return bucket;
  }

  private MemoryElement getFile(FullPath path, Supplier<String> error)
      throws IOException {
    var found = getElement(path, error);
    if (found.isFile()) {
      return found;
    } else {
      throw new IOException(
          error.get() + " File " + path.path().q() + " doesn't exist. It is a dir.");
    }
  }

  private MemoryElement getElement(FullPath path, Supplier<String> error)
      throws IOException {
    MemoryElement found = findElement(path, error);
    if (found == null) {
      throw new IOException(
          error.get() + " File " + path.path().q() + " doesn't exist.");
    } else {
      return found;
    }
  }

  private MemoryElement findElement(FullPath path, Supplier<String> error)
      throws IOException {
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

  private static void assertAliasesAreEqual(FullPath source, FullPath target) throws IOException {
    var sourceAlias = source.alias();
    var targetAlias = target.alias();
    if (!sourceAlias.equals(targetAlias)) {
      throw new IOException("Alias '%s' in source is different from alias '%s' in target."
          .formatted(sourceAlias.name(), targetAlias.name()));
    }
  }
}
