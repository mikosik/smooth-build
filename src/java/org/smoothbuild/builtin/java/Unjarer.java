package org.smoothbuild.builtin.java;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.smoothbuild.type.api.Path.SEPARATOR;
import static org.smoothbuild.type.api.Path.path;
import static org.smoothbuild.type.api.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;
import org.smoothbuild.type.api.Path;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Unjarer {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

  public void unjarFile(File jarFile, MutableFileSet resultFiles) {
    unjarFile(jarFile, Predicates.<String> alwaysTrue(), resultFiles);
  }

  public void unjarFile(File jarFile, Predicate<String> nameFilter, MutableFileSet resultFiles) {
    Predicate<String> filter = and(not(IS_DIRECTORY), nameFilter);
    try {
      try (JarInputStream jarInputStream = new JarInputStream(jarFile.openInputStream());) {
        JarEntry entry = null;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
          String fileName = entry.getName();
          if (filter.apply(fileName)) {
            unjarEntry(jarInputStream, fileName, resultFiles);
          }
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private File unjarEntry(JarInputStream jarInputStream, String fileName, MutableFileSet resultFiles) {
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessageException(new IllegalPathInJarError(fileName));
    }
    Path path = path(fileName);
    if (resultFiles.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathInJarError(path));
    }
    MutableFile file = resultFiles.createFile(path);
    try {
      try (OutputStream outputStream = file.openOutputStream()) {
        int len = 0;
        while ((len = jarInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return file;
  }
}
