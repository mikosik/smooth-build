package org.smoothbuild.io.temp;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.io.fs.disk.DiskFileSystem;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.util.Streams;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;

public class TempDirectory {
  private final SValueBuilders valueBuilders;
  private final FileSystem fileSystem;
  private final java.nio.file.Path rootPath;
  private boolean isDestroyed;

  @Inject
  public TempDirectory(SValueBuilders valueBuilders) {
    this(valueBuilders, Paths.get(Files.createTempDir().getAbsolutePath()));
  }

  @VisibleForTesting
  public TempDirectory(SValueBuilders valueBuilders, java.nio.file.Path path) {
    this(valueBuilders, path, new DiskFileSystem(path));
  }

  @VisibleForTesting
  public TempDirectory(SValueBuilders valueBuilders, java.nio.file.Path rootPath,
      FileSystem fileSystem) {
    this.valueBuilders = valueBuilders;
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

  public void writeFiles(SArray<SFile> files) {
    assertNotDestroyed();
    try {
      writeFilesImpl(files);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void writeFilesImpl(SArray<SFile> files) throws IOException {
    for (SFile file : files) {
      writeFileImpl(file.path(), file.content());
    }
  }

  public void writeFile(SFile file) {
    writeFile(file.path(), file.content());
  }

  public void writeFile(Path path, SBlob content) {
    assertNotDestroyed();
    try {
      writeFileImpl(path, content);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void writeFileImpl(Path path, SBlob content) throws IOException {
    InputStream inputStream = content.openInputStream();
    OutputStream outputStream = fileSystem.openOutputStream(path);
    Streams.copy(inputStream, outputStream);
  }

  public SArray<SFile> readFiles() {
    assertNotDestroyed();
    try {
      return readFilesImpl();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private SArray<SFile> readFilesImpl() throws IOException {
    ArrayBuilder<SFile> arrayBuilder = valueBuilders.arrayBuilder(FILE_ARRAY);
    for (Path path : fileSystem.filesFrom(Path.rootPath())) {
      arrayBuilder.add(readFileImpl(path).build());
    }
    return arrayBuilder.build();
  }

  private FileBuilder readFileImpl(Path path) throws IOException {
    FileBuilder fileBuilder = valueBuilders.fileBuilder();
    fileBuilder.setPath(path);
    fileBuilder.setContent(readContentImpl(path));
    return fileBuilder;
  }

  public SBlob readContent(Path path) {
    assertNotDestroyed();
    try {
      return readContentImpl(path);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private SBlob readContentImpl(Path path) throws IOException {
    BlobBuilder blobBuilder = valueBuilders.blobBuilder();
    Streams.copy(fileSystem.openInputStream(path), blobBuilder.openOutputStream());
    return blobBuilder.build();
  }

  private void assertNotDestroyed() {
    checkState(!isDestroyed, "This TempDirectory is destroyed.");
  }
}
