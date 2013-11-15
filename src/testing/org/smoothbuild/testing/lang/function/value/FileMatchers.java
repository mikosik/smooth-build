package org.smoothbuild.testing.lang.function.value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.lang.function.value.File;

import com.google.common.io.ByteStreams;

public class FileMatchers {
  public static Matcher<File> equalTo(final File expected) {
    return new TypeSafeMatcher<File>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("File with path " + expected.path() + " and some specific bytes.");
      }

      @Override
      protected boolean matchesSafely(File actual) {
        if (!expected.path().equals(actual.path())) {
          return false;
        }
        try (InputStream expectedInputStream = expected.openInputStream();
            InputStream actualInputStream = actual.openInputStream()) {
          byte[] expectedBytes = ByteStreams.toByteArray(expectedInputStream);
          byte[] actualBytes = ByteStreams.toByteArray(actualInputStream);
          return Arrays.equals(expectedBytes, actualBytes);
        } catch (IOException e) {
          return false;
        }
      }
    };
  }
}
