package org.smoothbuild.acceptance.builtin.file;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateFileArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_file_arrays_function() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript(
        "result: concatenateFileArrays(files=[file('file1.txt')], with=[file('file2.txt')]);");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }
}
