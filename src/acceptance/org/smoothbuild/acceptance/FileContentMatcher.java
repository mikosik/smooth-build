package org.smoothbuild.acceptance;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class FileContentMatcher extends TypeSafeMatcher<File> {
  private final String expectedContent;

  public static Matcher<File> hasContent(String content) {
    return new FileContentMatcher(content);
  }

  private FileContentMatcher(String expectedContent) {
    this.expectedContent = expectedContent;
  }

  public void describeTo(Description description) {
    description.appendText("is file with content = '" + expectedContent + "'");
  }

  protected boolean matchesSafely(File item) {
    try {
      return item.isFile()
          && expectedContent.equals(inputStreamToString(new FileInputStream(item)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
