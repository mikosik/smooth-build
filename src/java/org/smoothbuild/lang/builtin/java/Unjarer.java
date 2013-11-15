package org.smoothbuild.lang.builtin.java;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.builtin.compress.Constants;
import org.smoothbuild.lang.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.lang.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Unjarer {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);

  private final Sandbox sandbox;
  private final byte[] buffer;

  public Unjarer(Sandbox sandbox) {
    this.sandbox = sandbox;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public FileSet unjarFile(File jarFile) {
    return unjarFile(jarFile, Predicates.<String> alwaysTrue());
  }

  public FileSet unjarFile(File jarFile, Predicate<String> nameFilter) {
    FileSetBuilder fileSetBuilder = sandbox.fileSetBuilder();
    Predicate<String> filter = and(not(IS_DIRECTORY), nameFilter);
    try {
      try (JarInputStream jarInputStream = new JarInputStream(jarFile.openInputStream());) {
        JarEntry entry = null;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
          String fileName = entry.getName();
          if (filter.apply(fileName)) {
            File file = unjarEntry(jarInputStream, fileName);
            Path path = file.path();
            if (fileSetBuilder.contains(path)) {
              throw new ErrorMessageException(new DuplicatePathInJarError(path));
            } else {
              fileSetBuilder.add(file);
            }
          }
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return fileSetBuilder.build();
  }

  private File unjarEntry(JarInputStream jarInputStream, String fileName) {
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessageException(new IllegalPathInJarError(fileName));
    }
    Path path = path(fileName);
    FileBuilder fileBuilder = sandbox.fileBuilder();
    fileBuilder.setPath(path);
    try {
      try (OutputStream outputStream = fileBuilder.openOutputStream()) {
        int len = 0;
        while ((len = jarInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return fileBuilder.build();
  }
}
