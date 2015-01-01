package org.smoothbuild.io.util;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.io.fs.disk.DiskFileSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.Streams;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;

public class TempDirectory {
  private final ObjectsDb objectsDb;
  private final FileSystem fileSystem;
  private final java.nio.file.Path rootPath;
  private boolean isDestroyed;

  @Inject
  public TempDirectory(ObjectsDb objectsDb) {
    this(objectsDb, Paths.get(Files.createTempDir().getAbsolutePath()));
  }

  @VisibleForTesting
  public TempDirectory(ObjectsDb objectsDb, java.nio.file.Path path) {
    this(objectsDb, path, new DiskFileSystem(path));
  }

  @VisibleForTesting
  public TempDirectory(ObjectsDb objectsDb, java.nio.file.Path rootPath, FileSystem fileSystem) {
    this.objectsDb = objectsDb;
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
    fileSystem.delete(Path.rootPath());
    isDestroyed = true;
  }

  public void writeFiles(Array<SFile> files) {
    assertNotDestroyed();
    try {
      writeFilesImpl(files);
    } catch (IOException e) {
      throw new FileSystemError(e);
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
      throw new FileSystemError(e);
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
      throw new FileSystemError(e);
    }
  }

  private Array<SFile> readFilesImpl() throws IOException {
    ArrayBuilder<SFile> arrayBuilder = objectsDb.arrayBuilder(SFile.class);
    for (Path path : fileSystem.filesFromRecursive(Path.rootPath())) {
      Blob content = readContentImpl(path);
      SFile file = objectsDb.file(path, content);
      arrayBuilder.add(file);
    }
    return arrayBuilder.build();
  }

  public Blob readContent(Path path) {
    assertNotDestroyed();
    try {
      return readContentImpl(path);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  private Blob readContentImpl(Path path) throws IOException {
    BlobBuilder blobBuilder = objectsDb.blobBuilder();
    Streams.copy(fileSystem.openInputStream(path), blobBuilder.openOutputStream());
    return blobBuilder.build();
  }

  private void assertNotDestroyed() {
    checkState(!isDestroyed, "This TempDirectory is destroyed.");
  }
}
