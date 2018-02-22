package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.util.Exceptions.stackTraceToString;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;

import java.io.IOException;
import java.util.Map;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class FileClassLoader extends ClassLoader {
  private final Map<String, Struct> binaryNameToFile;

  public FileClassLoader(Map<String, Struct> binaryNameToFile) {
    super(FileClassLoader.class.getClassLoader());
    this.binaryNameToFile = binaryNameToFile;
  }

  @Override
  public Class<?> findClass(String name) throws ClassNotFoundException {
    Struct file = binaryNameToFile.get(name);
    if (file == null) {
      throw new ClassNotFoundException(name);
    }
    byte[] byteArray = fileToByteArray(file);
    return defineClass(name, byteArray, 0, byteArray.length);
  }

  private byte[] fileToByteArray(Struct file) {
    try {
      return inputStreamToByteArray(((Blob) file.get("content")).openInputStream());
    } catch (IOException e) {
      throw new FileSystemException("Error reading from " + (SString) file.get("path") + ". Java exception is:\n"
          + stackTraceToString(e));
    }
  }
}
