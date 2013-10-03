package org.smoothbuild.testing.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.common.StreamTester.inputStreamWithContent;
import static org.smoothbuild.type.api.Path.path;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.Path;

public class FileTesterTest {
  String content = "some content";
  Path path = path("my/path");
  MutableFile file = mock(MutableFile.class);

  @Test
  public void testCreateContentWithFilePath() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(file.openOutputStream()).thenReturn(outputStream);
    when(file.path()).thenReturn(path);

    FileTester.createContentWithFilePath(file);

    assertThat(new String(outputStream.toByteArray())).isEqualTo(path.value());
  }

  @Test
  public void testCreateContent() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(file.openOutputStream()).thenReturn(outputStream);

    FileTester.createContent(file, content);

    assertThat(new String(outputStream.toByteArray())).isEqualTo(content);
  }

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
