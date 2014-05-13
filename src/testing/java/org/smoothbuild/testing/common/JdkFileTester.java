package org.smoothbuild.testing.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JdkFileTester {

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
    return createFileContent(new File(root, fileName), content);
  }

  public static File createFileContent(File file, String content) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(file);
    StreamTester.writeAndClose(outputStream, content);
    return file;
  }
}
