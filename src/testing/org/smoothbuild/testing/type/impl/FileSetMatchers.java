package org.smoothbuild.testing.type.impl;

import static org.smoothbuild.testing.common.StreamTester.inputStreamToString;

import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

public class FileSetMatchers {

  public static Matcher<FileSet> containsFileContainingItsPath(Path path) throws IOException {
    return containsFileContaining(path, path.value());
  }

  public static Matcher<FileSet> containsFileContaining(final Path path, final String content)
      throws IOException {

    return new TypeSafeMatcher<FileSet>() {

      @Override
      public void describeTo(Description description) {
        description.appendText("FileSet containing file " + path + " with content = " + content);
      }

      @Override
      protected boolean matchesSafely(FileSet fileSet) {
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

  public static Matcher<FileSet> containsFile(final Path path) throws IOException {
    return new TypeSafeMatcher<FileSet>() {

      @Override
      public void describeTo(Description description) {
        description.appendText("FileSet containing file " + path);
      }

      @Override
      protected boolean matchesSafely(FileSet fileSet) {
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
