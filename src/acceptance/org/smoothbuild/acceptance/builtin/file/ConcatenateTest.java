package org.smoothbuild.acceptance.builtin.file;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.smoothbuild.testing.db.values.ValueCreators.falseByteString;
import static org.smoothbuild.testing.db.values.ValueCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.testing.db.values.ValueCreators;

public class ConcatenateTest extends AcceptanceTestCase {
  @Test
  public void concatenate_bool_arrays_function() throws Exception {
    givenScript("result = concatenate([true()], [false()]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertEquals(list(trueByteString(), falseByteString()), artifactAsByteStrings("result"));
  }

  @Test
  public void concatenate_string_arrays_function() throws Exception {
    givenScript("result = concatenate(['abc'], ['def']);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertEquals(list("abc", "def"), artifactArray("result"));
  }

  @Test
  public void concatenate_file_arrays() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result = concatenate([file('//file1.txt')], [file('//file2.txt')]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    givenFile("0", "abc");
    givenFile("1", "def");
    givenScript("result = concatenate([file('//0')], [file('//1')]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertEquals(list("abc", "def"), artifactArray("result"));
  }
}
