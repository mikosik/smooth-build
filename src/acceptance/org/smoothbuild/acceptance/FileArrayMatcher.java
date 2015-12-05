package org.smoothbuild.acceptance;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
      return item.isDirectory() && containsExpectedElements(item)
          && filesCount(item) == params.length / 2;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void describeMismatchSafely(File dir, Description mismatchDescription) {
    try {
      mismatchDescription.appendText("actual: [" + actualFiles(dir, dir.getPath().length() + 1)
          .stream().collect(joining(", ")) + "]");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<String> actualFiles(File dir, int rootPathLength) throws IOException {
    ArrayList<String> result = new ArrayList<>();
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        result.addAll(actualFiles(dir, rootPathLength));
      } else {
        result.add(file.getPath().substring(rootPathLength));
        result.add(inputStreamToString(new FileInputStream(file)));
      }
    }
    return result;
  }

  private static int filesCount(File item) {
    int count = 0;
    for (File file : item.listFiles()) {
      if (file.isDirectory()) {
        count += filesCount(file);
      } else {
        count++;
      }
    }
    return count;
  }

  private boolean containsExpectedElements(File dir) throws IOException {
    int i = 0;
    while (i < params.length) {
      String path = params[i];
      String content = params[i + 1];
      i += 2;
      File file = new File(dir, path);
      if (!(file.exists() && file.isFile())) {
        return false;
      }
      if (!content.equals(inputStreamToString(new FileInputStream(file)))) {
        return false;
      }
    }
    return true;
  }
}
