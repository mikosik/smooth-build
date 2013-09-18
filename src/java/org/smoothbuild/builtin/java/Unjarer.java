package org.smoothbuild.builtin.java;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.builtin.java.err.DuplicatePathInJarException;
import org.smoothbuild.builtin.java.err.IllegalPathInJarException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;

public class Unjarer {
  private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

  public void unjarFile(File jarFile, MutableFileSet resultFiles) throws IOException,
      DuplicatePathInJarException, IllegalPathInJarException {
    try (JarInputStream jarInputStream = new JarInputStream(jarFile.openInputStream());) {
      JarEntry entry = null;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        unjarEntry(jarInputStream, entry, resultFiles);
      }
    }
  }

  private File unjarEntry(JarInputStream jarInputStream, JarEntry entry, MutableFileSet resultFiles)
      throws IOException, DuplicatePathInJarException, IllegalPathInJarException {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInJarException(fileName);
    }
    Path path = path(fileName);
    if (resultFiles.contains(path)) {
      throw new DuplicatePathInJarException(path);
    }
    MutableFile file = resultFiles.createFile(path);
    try (OutputStream outputStream = file.openOutputStream()) {
      int len = 0;
      while ((len = jarInputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    }
    return file;
  }
}
