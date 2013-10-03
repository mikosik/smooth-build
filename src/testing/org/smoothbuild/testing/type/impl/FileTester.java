package org.smoothbuild.testing.type.impl;

import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import java.io.IOException;

import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.MutableFile;

public class FileTester {

  public static void createContentWithFilePath(MutableFile file) throws IOException {
    createContent(file, file.path().value());
  }

  public static void createContent(MutableFile file, String content) throws IOException {
    writeAndClose(file.openOutputStream(), content);
  }

  public static void assertContentContainsFilePath(File file) throws IOException {
    assertContentContains(file, file.path().value());
  }

  public static void assertContentContains(File file, String content) throws IOException,
      AssertionError {
    assertContent(file.openInputStream(), content);
  }
}
