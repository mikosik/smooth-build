package org.smoothbuild.lang.builtin.java.junit;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;

import java.io.IOException;
import java.util.Map;

import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.message.base.Message;

public class FileClassLoader extends ClassLoader {
  private final Map<String, SFile> binaryNameToFile;

  public FileClassLoader(Map<String, SFile> binaryNameToFile) {
    super(Thread.currentThread().getContextClassLoader());
    this.binaryNameToFile = binaryNameToFile;
  }

  @Override
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
      throw new Message(FATAL, "Error reading from " + file.path() + ". Java exception is:\n"
          + getStackTraceAsString(e));
    }
  }
}
