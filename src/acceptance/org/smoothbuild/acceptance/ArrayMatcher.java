package org.smoothbuild.acceptance;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ArrayMatcher extends TypeSafeMatcher<File> {
  private final Object[] expected;

  public static Matcher<File> isArrayWith(Object... expectedElements) {
    return new ArrayMatcher(expectedElements);
  }

  private ArrayMatcher(Object... expectedElements) {
    this.expected = expectedElements;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is array dir with elements = " + objectToString(expected));
  }

  @Override
  protected boolean matchesSafely(File item) {
    try {
      Object actual = actual(item);
      return actual != null && actual.getClass().isArray() &&
          Arrays.deepEquals(expected, (Object[]) actual);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void describeMismatchSafely(File item, Description mismatchDescription) {
    try {
      mismatchDescription
          .appendText("was\n")
          .appendText(objectToString(actual(item)));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Object actual(File file) throws IOException {
    if (!file.exists()) {
      return null;
    }
    if (file.isDirectory()) {
      return actualArray(file);
    }
    return inputStreamToString(new FileInputStream(file));
  }

  private static Object actualArray(File file) throws IOException {
    int count = file.list().length;
    Object[] result = new Object[count];
    for (int i = 0; i < count; i++) {
      result[i] = actual(new File(file, Integer.toString(i)));
    }
    return result;
  }

  private static String objectToString(Object object) {
    if (object == null) {
      return "null";
    } else if (object.getClass().isArray()) {
      return arrayToString((Object[]) object);
    } else {
      return Objects.toString(object);
    }
  }

  private static String arrayToString(Object[] array) {
    return "[" + stream(array)
        .map(ArrayMatcher::objectToString)
        .collect(Collectors.joining(", ")) + "]";
  }
}
