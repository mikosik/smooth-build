package org.smoothbuild.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestingJdkFile {

  public static File createDir(File root, String dirName) {
    File dir = new File(root, dirName);
    dir.mkdirs();
    return dir;
  }

  public static File createEmptyFile(File root, String fileName) throws IOException {
    return createFileContent(root, fileName, "");
  }

  public static File createFileContent(File root, String fileName, String content)
      throws IOException {
    File file = new File(root, fileName);
    FileOutputStream outputStream = new FileOutputStream(file);
    TestingStream.writeAndClose(outputStream, content);
    return file;
  }

  public static void assertContent(File root, String fileName, String content) throws IOException {
    TestingStream.assertContent(new FileInputStream(new File(root, fileName)), content);
  }
}
