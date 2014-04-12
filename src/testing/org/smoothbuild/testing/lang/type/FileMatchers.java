package org.smoothbuild.testing.lang.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.lang.base.SFile;

import com.google.common.io.ByteStreams;

public class FileMatchers {
  public static Matcher<SFile> equalTo(final SFile expected) {
    return new TypeSafeMatcher<SFile>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("File with path " + expected.path() + " and some specific bytes.");
      }

      @Override
      protected boolean matchesSafely(SFile actual) {
        if (!expected.path().equals(actual.path())) {
          return false;
        }
        try (InputStream expectedInputStream = expected.content().openInputStream();
            InputStream actualInputStream = actual.content().openInputStream()) {
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
