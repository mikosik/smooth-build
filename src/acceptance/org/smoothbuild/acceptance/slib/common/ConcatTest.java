package org.smoothbuild.acceptance.slib.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatTest extends AcceptanceTestCase {
  @Test
  public void concatenate_bool_arrays_function() throws Exception {
    createUserModule("""
            result = concat([true], [false]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void concatenate_string_arrays_function() throws Exception {
    createUserModule("""
            result = concat(["abc"], ["def"]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void concatenate_file_arrays() throws Exception {
    createUserModule("""
            result = concat(
              [ file(0x41, "file1.txt") ],
              [ file(0x42, "file2.txt") ],
            );
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file1.txt", "A", "file2.txt", "B");
  }

  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    createFile("0", "abc");
    createFile("1", "def");
    createUserModule("""
            result = concat([ projectFile("0") ], [ projectFile("1") ]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("abc", "def"));
  }
}
