package org.smoothbuild.acceptance.builtin.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateTest extends AcceptanceTestCase {
  @Test
  public void concatenate_bool_arrays_function() throws Exception {
    givenScript(
        "  result = concatenate([true()], [false()]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void concatenate_string_arrays_function() throws Exception {
    givenScript(
        "  result = concatenate(['abc'], ['def']);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void concatenate_file_arrays() throws Exception {
    givenScript(
        "  result = concatenate(                    ",
        "    [ file(toBlob('abc'), 'file1.txt') ],  ",
        "    [ file(toBlob('def'), 'file2.txt') ],  ",
        "  );                                       ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactDir("result"))
        .containsExactly("file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    givenFile("0", "abc");
    givenFile("1", "def");
    givenScript(
        "  result = concatenate([ aFile('//0') ], [ aFile('//1') ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }
}
