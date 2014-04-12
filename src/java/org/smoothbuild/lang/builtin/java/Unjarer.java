package org.smoothbuild.lang.builtin.java;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.builtin.compress.Constants;
import org.smoothbuild.lang.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.lang.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.util.DuplicatesDetector;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Unjarer {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);

  private final NativeApi nativeApi;
  private final byte[] buffer;
  private DuplicatesDetector<Path> duplicatesDetector;

  public Unjarer(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public SArray<SFile> unjar(SBlob jarBlob) {
    return unjar(jarBlob, Predicates.<String> alwaysTrue());
  }

  public SArray<SFile> unjar(SBlob jarBlob, Predicate<String> nameFilter) {
    this.duplicatesDetector = new DuplicatesDetector<Path>();
    ArrayBuilder<SFile> fileArrayBuilder = nativeApi.arrayBuilder(FILE_ARRAY);
    Predicate<String> filter = and(not(IS_DIRECTORY), nameFilter);
    try {
      try (JarInputStream jarInputStream = new JarInputStream(jarBlob.openInputStream());) {
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

    FileBuilder fileBuilder = nativeApi.fileBuilder();
    fileBuilder.setPath(path(fileName));
    fileBuilder.setContent(unjarEntryContent(jarInputStream));
    return fileBuilder.build();
  }

  private SBlob unjarEntryContent(JarInputStream jarInputStream) {
    BlobBuilder contentBuilder = nativeApi.blobBuilder();
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
