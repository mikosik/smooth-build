package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

public class FileSetMatchers {

  public static Matcher<Array<File>> containsFileContainingItsPath(Path path) throws IOException {
    return containsFileContaining(path, path.value());
  }

  public static Matcher<Array<File>> containsFileContaining(final Path path, final String content)
      throws IOException {

    return new TypeSafeMatcher<Array<File>>() {

      @Override
      public void describeTo(Description description) {
        description.appendText("FileSet containing file " + path + " with content = " + content);
      }

      @Override
      protected boolean matchesSafely(Array<File> fileSet) {
        for (File file : fileSet) {
          if (file.path().equals(path)) {
            try {
              return inputStreamToString(file.openInputStream()).equals(content);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        }
        return false;
      }
    };
  }

  public static Matcher<Array<File>> containsFile(final Path path) throws IOException {
    return new TypeSafeMatcher<Array<File>>() {

      @Override
      public void describeTo(Description description) {
        description.appendText("FileSet containing file " + path);
      }

      @Override
      protected boolean matchesSafely(Array<File> fileSet) {
        for (File file : fileSet) {
          if (file.path().equals(path)) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
