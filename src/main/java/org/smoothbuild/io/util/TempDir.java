package org.smoothbuild.io.util;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.task.exec.Container;
import org.smoothbuild.util.Streams;

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

  public void destroy() {
    fileSystem.delete(rootPath);
    isDestroyed = true;
  }

  public void writeFiles(Array files) {
    assertNotDestroyed();
    try {
      writeFilesImpl(files);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void writeFilesImpl(Array files) throws IOException {
    for (Struct file : files.asIterable(Struct.class)) {
      writeFileImpl(file);
    }
  }

  public void writeFile(Struct file) {
    assertNotDestroyed();
    try {
      writeFileImpl(file);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void writeFileImpl(Struct file) throws IOException {
    Path path = path(((SString) file.get("path")).data());
    Blob content = (Blob) file.get("content");
    InputStream inputStream = content.openInputStream();
    OutputStream outputStream = fileSystem.openOutputStream(rootPath.append(path));
    Streams.copy(inputStream, outputStream);
  }

  public Array readFiles() {
    assertNotDestroyed();
    try {
      return readFilesImpl();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private Array readFilesImpl() throws IOException {
    ArrayBuilder arrayBuilder = container.create().arrayBuilder(container.types().file());
    for (Path path : recursiveFilesIterable(fileSystem, rootPath)) {
      Blob content = readContentImpl(path);
      Struct file = container.create().file(container.create().string(path.value()), content);
      arrayBuilder.add(file);
    }
    return arrayBuilder.build();
  }

  public Blob readContent(Path path) {
    assertNotDestroyed();
    try {
      return readContentImpl(path);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private Blob readContentImpl(Path path) throws IOException {
    BlobBuilder blobBuilder = container.create().blobBuilder();
    Streams.copy(fileSystem.openInputStream(rootPath.append(path)), blobBuilder);
    return blobBuilder.build();
  }

  private void assertNotDestroyed() {
    checkState(!isDestroyed, "This TempDir is destroyed.");
  }
}
