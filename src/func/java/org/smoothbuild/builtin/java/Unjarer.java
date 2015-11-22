package org.smoothbuild.builtin.java;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class Unjarer {
  private static final Predicate<String> IS_DIR = (string) -> string.endsWith(SEPARATOR);

  private final Container container;
  private final byte[] buffer;

  public Unjarer(Container container) {
    this.container = container;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public Array<SFile> unjar(Blob jarBlob) {
    return unjar(jarBlob, (value) -> true);
  }

  public Array<SFile> unjar(Blob jarBlob, Predicate<String> nameFilter) {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder<SFile> fileArrayBuilder = container.create().arrayBuilder(SFile.class);
    Predicate<String> filter = IS_DIR.negate().and(nameFilter);
    try {
      try (JarInputStream jarInputStream = new JarInputStream(jarBlob.openInputStream())) {
        JarEntry entry;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
          String fileName = entry.getName();
          if (filter.test(fileName)) {
            SFile file = unjarEntry(jarInputStream, fileName);
            Path path = file.path();
            if (duplicatesDetector.addValue(path)) {
              throw new DuplicatePathInJarError(path);
            } else {
              fileArrayBuilder.add(file);
            }
          }
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return fileArrayBuilder.build();
  }

  private SFile unjarEntry(JarInputStream jarInputStream, String fileName) {
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInJarError(fileName);
    }

    Path path = path(fileName);
    Blob content = unjarEntryContent(jarInputStream);
    return container.create().file(path, content);
  }

  private Blob unjarEntryContent(JarInputStream jarInputStream) {
    BlobBuilder contentBuilder = container.create().blobBuilder();
    try {
      try (OutputStream outputStream = contentBuilder.openOutputStream()) {
        int len;
        while ((len = jarInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return contentBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
