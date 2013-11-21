package org.smoothbuild.lang.builtin.java.junit;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.message.base.MessageType.FATAL;

import java.io.IOException;
import java.util.Map;

import org.smoothbuild.lang.type.File;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.io.ByteStreams;

public class FileClassLoader extends ClassLoader {
  private final Map<String, File> binaryNameToFile;

  public FileClassLoader(Map<String, File> binaryNameToFile) {
    this.binaryNameToFile = binaryNameToFile;
  }

  @Override
  public Class<?> findClass(String name) throws ClassNotFoundException {
    File file = binaryNameToFile.get(name);
    if (file == null) {
      throw new ClassNotFoundException(name);
    }
    byte[] byteArray = fileToByteArray(file);
    return defineClass(name, byteArray, 0, byteArray.length);
  }

  private byte[] fileToByteArray(File file) {
    try {
      return ByteStreams.toByteArray(file.openInputStream());
    } catch (IOException e) {
      throw new ErrorMessageException(new Message(FATAL, "Error reading from " + file.path()
          + ". Java exception is:\n" + getStackTraceAsString(e)));
    }
  }
}
