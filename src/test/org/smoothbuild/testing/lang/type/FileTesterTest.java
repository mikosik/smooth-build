package org.smoothbuild.testing.lang.type;

import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SFile;

public class FileTesterTest {
  String content = "some content";
  Path path = path("my/path");

  @Test
  public void asserting_content_succeeds_when_file_content_equals_expected() throws IOException {
    SFile file = new FakeFile(path, content);
    FileTester.assertContentContains(file, content);
  }

  @Test
  public void asserting_content_fails_when_file_content_does_not_equal_expected()
      throws IOException {
    SFile file = new FakeFile(path, content);
    try {
      FileTester.assertContentContains(file, content + "suffix");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

  @Test
  public void asserting_content_equals_path_succeeds_when_it_does_contain_path() throws IOException {
    SFile file = new FakeFile(path, path.value());
    FileTester.assertContentContainsFilePath(file);
  }

  @Test
  public void asserting_content_equals_path_fails_when_it_does_not_contain_path()
      throws IOException {
    SFile file = new FakeFile(path, path.value() + " suffix");
    try {
      FileTester.assertContentContainsFilePath(file);
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
