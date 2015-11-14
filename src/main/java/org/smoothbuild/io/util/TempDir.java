package org.smoothbuild.io.util;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.io.fs.disk.DiskFileSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.Streams;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;

public class TempDir {
  private final ValuesDb valuesDb;
  private final FileSystem fileSystem;
  private final java.nio.file.Path rootPath;
  private boolean isDestroyed;

  @Inject
  public TempDir(ValuesDb valuesDb) {
    this(valuesDb, Paths.get(Files.createTempDir().getAbsolutePath()));
  }

  @VisibleForTesting
  public TempDir(ValuesDb valuesDb, java.nio.file.Path path) {
    this(valuesDb, path, new DiskFileSystem(path));
  }

  @VisibleForTesting
  public TempDir(ValuesDb valuesDb, java.nio.file.Path rootPath, FileSystem fileSystem) {
    this.valuesDb = valuesDb;
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.isDestroyed = false;
  }

  public String rootOsPath() {
    return rootPath.toString();
  }

  public String asOsPath(Path path) {
    return rootPath.resolve(path.value()).toString();
  }

  public void destroy() {
    assertNotDestroyed();
    fileSystem.delete(Path.root());
    isDestroyed = true;
  }

  public void writeFiles(Array<SFile> files) {
    assertNotDestroyed();
    try {
      writeFilesImpl(files);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void writeFilesImpl(Array<SFile> files) throws IOException {
    for (SFile file : files) {
      writeFileImpl(file.path(), file.content());
    }
  }

  public void writeFile(SFile file) {
    writeFile(file.path(), file.content());
  }

  public void writeFile(Path path, Blob content) {
    assertNotDestroyed();
    try {
      writeFileImpl(path, content);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void writeFileImpl(Path path, Blob content) throws IOException {
    InputStream inputStream = content.openInputStream();
    OutputStream outputStream = fileSystem.openOutputStream(path);
    Streams.copy(inputStream, outputStream);
  }

  public Array<SFile> readFiles() {
    assertNotDestroyed();
    try {
      return readFilesImpl();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private Array<SFile> readFilesImpl() throws IOException {
    ArrayBuilder<SFile> arrayBuilder = valuesDb.arrayBuilder(SFile.class);
    for (Path path : recursiveFilesIterable(fileSystem, Path.root())) {
      Blob content = readContentImpl(path);
      SFile file = valuesDb.file(path, content);
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
    BlobBuilder blobBuilder = valuesDb.blobBuilder();
    Streams.copy(fileSystem.openInputStream(path), blobBuilder.openOutputStream());
    return blobBuilder.build();
  }

  private void assertNotDestroyed() {
    checkState(!isDestroyed, "This TempDir is destroyed.");
  }
}
