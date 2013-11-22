package org.smoothbuild.lang.builtin.java;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.builtin.compress.Constants;
import org.smoothbuild.lang.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.lang.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

public class Unjarer {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);

  private final Sandbox sandbox;
  private final byte[] buffer;
  private Set<Path> alreadyUnjared;

  public Unjarer(Sandbox sandbox) {
    this.sandbox = sandbox;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public Array<File> unjarFile(File jarFile) {
    return unjarFile(jarFile, Predicates.<String> alwaysTrue());
  }

  public Array<File> unjarFile(File jarFile, Predicate<String> nameFilter) {
    this.alreadyUnjared = Sets.newHashSet();
    ArrayBuilder<File> fileArrayBuilder = sandbox.fileArrayBuilder();
    Predicate<String> filter = and(not(IS_DIRECTORY), nameFilter);
    try {
      try (JarInputStream jarInputStream = new JarInputStream(jarFile.openInputStream());) {
        JarEntry entry = null;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
          String fileName = entry.getName();
          if (filter.apply(fileName)) {
            File file = unjarEntry(jarInputStream, fileName);
            Path path = file.path();
            if (alreadyUnjared.contains(path)) {
              throw new ErrorMessageException(new DuplicatePathInJarError(path));
            } else {
              alreadyUnjared.add(path);
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
