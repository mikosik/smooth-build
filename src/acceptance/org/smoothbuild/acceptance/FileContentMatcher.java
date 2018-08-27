package org.smoothbuild.acceptance;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Okios.readAndClose;

import java.io.File;
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

  @Override
  public void describeTo(Description description) {
    description.appendText("is file with content = '" + expectedContent + "'");
  }

  @Override
  protected boolean matchesSafely(File item) {
    try {
      return item.isFile() && expectedContent.equals(content(item));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String content(File item) throws IOException {
    return readAndClose(buffer(source(item)), s -> s.readString(CHARSET));
  }
}
