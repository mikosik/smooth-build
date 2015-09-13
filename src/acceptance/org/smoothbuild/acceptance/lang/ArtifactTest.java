package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {

  @Test
  public void store_string_artifact() throws Exception {
    givenBuildScript(script("result: 'abc';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void store_blob_artifact() throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: file('file.txt') | content;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void store_file_artifact() throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: file('file.txt');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void store_string_array_artifact() throws Exception {
    givenBuildScript(script("result : ['abc', 'def']  ;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }

  @Test
  public void store_blob_array_artifact() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script("result: [content(file('file1.txt')), content(file('file2.txt'))];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }

  @Test
  public void store_file_array_artifact() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script("result: [file('file1.txt'), file('file2.txt')];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void cannot_store_file_array_with_duplicated_paths() throws Exception {
    givenFile("file1.txt", "abc");
    givenBuildScript(script("result: [file('file1.txt'), file('file1.txt')];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString(""));
  }
}
