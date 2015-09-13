package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ImplicitConversionTest extends AcceptanceTestCase {
  @Test
  public void file_is_implicitly_converted_to_blob() throws IOException {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: file('file.txt') | toString;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void file_array_is_implicitly_converted_to_blob_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script(
        "result: concatenateBlobArrays([file('file1.txt')], with=[file('file2.txt')]);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }

  @Test
  public void nil_is_implicitly_converted_to_string_array() throws IOException {
    givenBuildScript("result: concatenateStrings([], with=[]);");
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }

  @Test
  public void nil_is_implicitly_converted_to_blob_array() throws IOException {
    givenBuildScript("result: concatenateBlobArrays([], with=[]);");
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }

  @Test
  public void nil_is_implicitly_converted_to_file_array() throws IOException {
    givenBuildScript("result: concatenateFileArrays([], with=[]);");
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }
}
