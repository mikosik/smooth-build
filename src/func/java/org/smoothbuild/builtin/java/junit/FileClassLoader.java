package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.util.Exceptions.stackTraceToString;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;

import java.io.IOException;
import java.util.Map;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.value.SFile;

public class FileClassLoader extends ClassLoader {
  private final Map<String, SFile> binaryNameToFile;

  public FileClassLoader(Map<String, SFile> binaryNameToFile) {
    super(FileClassLoader.class.getClassLoader());
    this.binaryNameToFile = binaryNameToFile;
  }

  public Class<?> findClass(String name) throws ClassNotFoundException {
    SFile file = binaryNameToFile.get(name);
    if (file == null) {
      throw new ClassNotFoundException(name);
    }
    byte[] byteArray = fileToByteArray(file);
    return defineClass(name, byteArray, 0, byteArray.length);
  }

  private byte[] fileToByteArray(SFile file) {
    try {
      return inputStreamToByteArray(file.content().openInputStream());
    } catch (IOException e) {
      throw new FileSystemException("Error reading from " + file.path() + ". Java exception is:\n"
          + stackTraceToString(e));
    }
  }
}
