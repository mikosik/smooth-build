package org.smoothbuild.io.util;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathIterator;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.task.exec.Container;

import okio.BufferedSink;

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
    return rootPath.toJPath().toString();
  }

  public String asOsPath(Path path) {
    return rootPath.append(path).toJPath().toString();
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
    for (Struct file : files.asIterable(Struct.class)) {
      writeFileImpl(file);
    }
  }

  public void writeFile(Struct file) throws IOException {
    assertNotDestroyed();
    writeFileImpl(file);
  }

  private void writeFileImpl(Struct file) throws IOException {
    Path path = path(((SString) file.get("path")).data());
    Blob content = (Blob) file.get("content");
    try (BufferedSink sink = fileSystem.sink(rootPath.append(path))) {
      sink.writeAll(content.source());
    }
  }

  public Array readFiles() throws IOException {
    assertNotDestroyed();
    return readFilesImpl();
  }

  private Array readFilesImpl() throws IOException {
    ArrayBuilder arrayBuilder = container.create().arrayBuilder(container.types().file());
    for (PathIterator it = recursivePathsIterator(fileSystem, rootPath); it.hasNext();) {
      Path path = it.next();
      Blob content = readContentImpl(path);
      Struct file = container.create().file(container.create().string(path.value()), content);
      arrayBuilder.add(file);
    }
    return arrayBuilder.build();
  }

  public Blob readContent(Path path) throws IOException {
    assertNotDestroyed();
    return readContentImpl(path);
  }

  private Blob readContentImpl(Path path) throws IOException {
    BlobBuilder blobBuilder = container.create().blobBuilder();
    blobBuilder.sink().writeAll(fileSystem.source(rootPath.append(path)));
    return blobBuilder.build();
  }

  private void assertNotDestroyed() {
    checkState(!isDestroyed, "This TempDir is destroyed.");
  }
}
