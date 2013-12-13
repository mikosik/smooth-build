package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;

public class FileArrayMatchers {

  public static Matcher<SArray<SFile>> containsFileContainingItsPath(Path path) throws IOException {
    return containsFileContaining(path, path.value());
  }

  public static Matcher<SArray<SFile>> containsFileContaining(final Path path, final String content)
      throws IOException {

    return new TypeSafeMatcher<SArray<SFile>>() {

      @Override
      public void describeTo(Description description) {
        description
            .appendText("Array<File> containing file " + path + " with content = " + content);
      }

      @Override
      protected boolean matchesSafely(SArray<SFile> fileArray) {
        for (SFile file : fileArray) {
          if (file.path().equals(path)) {
            try {
              return inputStreamToString(file.content().openInputStream()).equals(content);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        }
        return false;
      }
    };
  }

  public static Matcher<SArray<SFile>> containsFile(final Path path) throws IOException {
    return new TypeSafeMatcher<SArray<SFile>>() {

      @Override
      public void describeTo(Description description) {
        description.appendText("Array<File> containing file " + path);
      }

      @Override
      protected boolean matchesSafely(SArray<SFile> fileArray) {
        for (SFile file : fileArray) {
          if (file.path().equals(path)) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
