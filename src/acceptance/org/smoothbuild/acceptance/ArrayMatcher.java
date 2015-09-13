package org.smoothbuild.acceptance;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ArrayMatcher extends TypeSafeMatcher<File> {
  private final String[] expectedElements;

  public static Matcher<File> isArrayWith(String... expectedElements) {
    return new ArrayMatcher(expectedElements);
  }

  private ArrayMatcher(String... expectedElements) {
    this.expectedElements = expectedElements;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is array directory with elements = "
        + Arrays.toString(expectedElements));
  }

  @Override
  protected boolean matchesSafely(File item) {
    try {
      return item.isDirectory() && containsExpectedElements(item)
          && item.list().length == expectedElements.length;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean containsExpectedElements(File dir) throws IOException {
    for (int i = 0; i < expectedElements.length; i++) {
      File file = new File(dir, Integer.toString(i));
      if (!expectedElements[i].equals(inputStreamToString(new FileInputStream(file)))) {
        return false;
      }
    }
    return true;
  }
}
