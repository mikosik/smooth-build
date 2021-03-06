package org.smoothbuild.io.util;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.IOException;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.ArrayBuilder;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathIterator;

import okio.BufferedSource;

public class TempDir {
  private final Container container;
  private final FileSystem fileSystem;
  private final Path rootPath;
  private boolean isDestroyed;

  public TempDir(Container container, FileSystem fileSystem, Path rootPath) {
    this.container = container;
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.isDestroyed = false;
  }

  public String rootOsPath() {
    return fileSystem.rootDirJPath().resolve(rootPath.value()).toString();
  }

  public void destroy() throws IOException {
    fileSystem.delete(rootPath);
    isDestroyed = true;
  }

  public void writeFiles(Array files) throws IOException {
    assertNotDestroyed();
    writeFilesImpl(files);
  }

  private void writeFilesImpl(Array files) throws IOException {
    for (Tuple file : files.asIterable(Tuple.class)) {
      writeFileImpl(file);
    }
  }

  public void writeFile(Tuple file) throws IOException {
    assertNotDestroyed();
    writeFileImpl(file);
  }

  private void writeFileImpl(Tuple file) throws IOException {
    Path path = path(filePath(file).jValue());
    Blob content = fileContent(file);
    copyAllAndClose(content.source(), fileSystem.sink(rootPath.append(path)));
  }

  public Array readFiles() throws IOException {
    assertNotDestroyed();
    return readFilesImpl();
  }

  private Array readFilesImpl() throws IOException {
    ArrayBuilder arrayBuilder = container.factory().arrayBuilder(container.factory().fileSpec());
    for (PathIterator it = recursivePathsIterator(fileSystem, rootPath); it.hasNext(); ) {
      Path path = it.next();
      Blob content = readContentImpl(path);
      Tuple file = container.factory().file(container.factory().string(path.toString()), content);
      arrayBuilder.add(file);
    }
    return arrayBuilder.build();
  }

  public Blob readContent(Path path) throws IOException {
    assertNotDestroyed();
    return readContentImpl(path);
  }

  private Blob readContentImpl(Path path) throws IOException {
    try (BufferedSource source = fileSystem.source(rootPath.append(path))) {
      return container.factory().blob(sink -> sink.writeAll(source));
    }
  }

  private void assertNotDestroyed() {
    checkState(!isDestroyed, "This TempDir is destroyed.");
  }
}
