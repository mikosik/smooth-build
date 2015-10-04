package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {

  @Test
  public void store_string_artifact() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_blob_artifact() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: file('file.txt') | content;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_file_artifact() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: file('file.txt');");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_string_array_artifact() throws Exception {
    givenScript("result : ['abc', 'def']  ;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void store_blob_array_artifact() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result: [content(file('file1.txt')), content(file('file2.txt'))];");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void store_file_array_artifact() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result: [file('file1.txt'), file('file2.txt')];");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void cannot_store_file_array_with_duplicated_paths() throws Exception {
    givenFile("file1.txt", "abc");
    givenScript("result: [file('file1.txt'), file('file1.txt')];");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString(""));
  }
}
