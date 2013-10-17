package org.smoothbuild.builtin.java;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.smoothbuild.fs.base.Path.SEPARATOR;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.impl.FileSetBuilder;
import org.smoothbuild.type.impl.FileSetBuilderInterface;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Unjarer {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

  public void unjarFile(File jarFile, FileSetBuilder result) {
    unjarFile(jarFile, Predicates.<String> alwaysTrue(), result);
  }

  public void unjarFile(File jarFile, Predicate<String> nameFilter,
      FileSetBuilderInterface resultFiles) {
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

  private void unjarEntry(JarInputStream jarInputStream, String fileName,
      FileSetBuilderInterface resultFiles) {
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessageException(new IllegalPathInJarError(fileName));
    }
    Path path = path(fileName);
    if (resultFiles.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathInJarError(path));
    }
    try {
      try (OutputStream outputStream = resultFiles.openFileOutputStream(path)) {
        int len = 0;
        while ((len = jarInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
