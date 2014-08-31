package org.smoothbuild.builtin.java;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.builtin.util.EndsWithPredicate;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValueFactory;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Unjarer {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);

  private final SValueFactory valueFactory;
  private final byte[] buffer;

  public Unjarer(SValueFactory valueFactory) {
    this.valueFactory = valueFactory;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public SArray<SFile> unjar(SBlob jarBlob) {
    return unjar(jarBlob, Predicates.<String>alwaysTrue());
  }

  public SArray<SFile> unjar(SBlob jarBlob, Predicate<String> nameFilter) {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder<SFile> fileArrayBuilder = valueFactory.arrayBuilder(FILE_ARRAY);
    Predicate<String> filter = and(not(IS_DIRECTORY), nameFilter);
    try {
      try (JarInputStream jarInputStream = new JarInputStream(jarBlob.openInputStream())) {
        JarEntry entry = null;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
          String fileName = entry.getName();
          if (filter.apply(fileName)) {
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
      throw new FileSystemError(e);
    }
    return fileArrayBuilder.build();
  }

  private SFile unjarEntry(JarInputStream jarInputStream, String fileName) {
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInJarError(fileName);
    }

    Path path = path(fileName);
    SBlob content = unjarEntryContent(jarInputStream);
    return valueFactory.file(path, content);
  }

  private SBlob unjarEntryContent(JarInputStream jarInputStream) {
    BlobBuilder contentBuilder = valueFactory.blobBuilder();
    try {
      try (OutputStream outputStream = contentBuilder.openOutputStream()) {
        int len = 0;
        while ((len = jarInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return contentBuilder.build();
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
