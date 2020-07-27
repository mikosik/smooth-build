package org.smoothbuild.acceptance.slib.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatTest extends AcceptanceTestCase {
  @Test
  public void concatenate_bool_arrays_function() throws Exception {
    createUserModule(
        "  result = concat([true()], [false()]);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void concatenate_string_arrays_function() throws Exception {
    createUserModule(
        "  result = concat(['abc'], ['def']);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void concatenate_file_arrays() throws Exception {
    createUserModule(
        "  result = concat(                    ",
        "    [ file(toBlob('abc'), 'file1.txt') ],  ",
        "    [ file(toBlob('def'), 'file2.txt') ],  ",
        "  );                                       ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    createFile("0", "abc");
    createFile("1", "def");
    createUserModule(
        "  result = concat([ aFile('0') ], [ aFile('1') ]);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc", "def"));
  }
}
