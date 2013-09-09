package org.smoothbuild.testing.plugin.internal;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;

import org.smoothbuild.plugin.api.File;

public class FileTester {

  public static void assertContentContainsFilePath(File file) throws IOException {
    assertContent(file.openInputStream(), file.path().value());
  }

}
