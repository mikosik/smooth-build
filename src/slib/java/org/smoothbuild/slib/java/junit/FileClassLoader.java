package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.exec.base.FileStruct.fileContent;

import java.io.IOException;
import java.util.Map;

import org.smoothbuild.db.object.obj.val.Tuple;

import okio.BufferedSource;

public class FileClassLoader extends ClassLoader {
  private final Map<String, Tuple> binaryNameToFile;

  public FileClassLoader(Map<String, Tuple> binaryNameToFile) {
    super(FileClassLoader.class.getClassLoader());
    this.binaryNameToFile = binaryNameToFile;
  }

  @Override
  public Class<?> findClass(String name) throws ClassNotFoundException {
    Tuple file = binaryNameToFile.get(name);
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

  private byte[] fileToByteArray(Tuple file) throws IOException {
    try (BufferedSource source = fileContent(file).source()) {
      return source.readByteArray();
    }
  }

  public static void sneakyRethrow(Throwable t) {
    FileClassLoader.<Error>sneakyThrow2(t);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow2(Throwable t) throws T {
    throw (T) t;
  }
}
