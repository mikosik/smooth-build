package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.util.Streams.inputStreamToByteArray;

import java.io.IOException;
import java.util.Map;

import org.smoothbuild.lang.value.Blob;
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
    try {
      byte[] byteArray = fileToByteArray(file);
      return defineClass(name, byteArray, 0, byteArray.length);
    } catch (IOException e) {
      sneakyRethrow(e);
      return null;
    }
  }

  private byte[] fileToByteArray(Struct file) throws IOException {
    return inputStreamToByteArray(((Blob) file.get("content")).openInputStream());
  }

  public static void sneakyRethrow(Throwable t) {
    FileClassLoader.<Error> sneakyThrow2(t);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow2(Throwable t) throws T {
    throw (T) t;
  }
}
