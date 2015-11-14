package org.smoothbuild.acceptance;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class FileArrayMatcher extends TypeSafeMatcher<File> {
  private final String[] params;

  public static Matcher<File> isFileArrayWith(String... params) {
    return new FileArrayMatcher(params);
  }

  private FileArrayMatcher(String... params) {
    if (params.length % 2 != 0) {
      throw new IllegalArgumentException("Even number of arguments expected, got " + params.length);
    }
    this.params = params;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is array dir with = " + Arrays.toString(params));
  }

  @Override
  protected boolean matchesSafely(File item) {
    try {
      return item.isDirectory() && containsExpectedElements(item) && item
          .list().length == params.length / 2;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean containsExpectedElements(File dir) throws IOException {
    int i = 0;
    while (i < params.length) {
      String path = params[i];
      String content = params[i + 1];
      i += 2;
      File file = new File(dir, path);
      if (!content.equals(inputStreamToString(new FileInputStream(file)))) {
        return false;
      }
    }
    return true;
  }
}
