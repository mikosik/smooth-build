package org.smoothbuild.testing.lang.plugin;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;

import org.smoothbuild.lang.plugin.File;

public class FileTester {

  public static void assertContentContainsFilePath(File file) throws IOException {
    assertContentContains(file, file.path().value());
  }

  public static void assertContentContains(File file, String content) throws IOException {
    assertContent(file.openInputStream(), content);
  }
}
