package org.smoothbuild.testing.plugin.internal;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.inputStreamWithContent;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;

public class FileTesterTest {
  String content = "some content";
  Path path = path("my/path");
  File file = mock(File.class);

  @Test
  public void assertFileContainsSucceedsWhenContentIsEqualToExpected() throws IOException {
    InputStream inputStream = inputStreamWithContent(content);
    when(file.openInputStream()).thenReturn(inputStream);

    FileTester.assertContentContains(file, content);
  }

  @Test
  public void assertFileContainsFailsWhenContentIsNotEqualToExpected() throws IOException {
    InputStream inputStream = inputStreamWithContent(content);
    when(file.openInputStream()).thenReturn(inputStream);

    try {
      FileTester.assertContentContains(file, content + "suffix");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

  @Test
  public void assertFileContainsFilePathSucceedsWhenContentIsEqualToExpected() throws IOException {
    InputStream inputStream = inputStreamWithContent(path.value());
    when(file.openInputStream()).thenReturn(inputStream);
    when(file.path()).thenReturn(path);

    FileTester.assertContentContainsFilePath(file);
  }

  @Test
  public void assertFileContainsFilePathFailsWhenContentIsNotEqualToExpected() throws IOException {
    InputStream inputStream = inputStreamWithContent(path.value() + "suffix");
    when(file.openInputStream()).thenReturn(inputStream);
    when(file.path()).thenReturn(path);

    try {
      FileTester.assertContentContainsFilePath(file);
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
