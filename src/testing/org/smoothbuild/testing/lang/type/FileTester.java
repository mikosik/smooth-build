package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;

import org.smoothbuild.lang.type.SFile;

public class FileTester {

  public static void assertContentContainsFilePath(SFile file) throws IOException {
    assertContentContains(file, file.path().value());
  }

  public static void assertContentContains(SFile file, String content) throws IOException {
    assertContent(file.openInputStream(), content);
  }
}
