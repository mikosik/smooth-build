package org.smoothbuild.systemtest.slib.array;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ConcatTest extends SystemTestCase {
  @Test
  public void concatenate_bool_arrays_func() throws Exception {
    createUserModule("""
            result = concat([true], [false]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void concatenate_string_arrays_func() throws Exception {
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
              [file("file1.txt", 0x41)],
              [file("file2.txt", 0x42)],
            );
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file1.txt", "A", "file2.txt", "B");
  }

  @Test
  public void concatenate_blob_arrays_func() throws Exception {
    createFile("0", "abc");
    createFile("1", "def");
    createUserModule("""
            result = concat([projectFile("0")], [projectFile("1")]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void concatenate_that_requires_conversion() throws Exception {
    createUserModule("""
            result = concat([], ["abc", "def"]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("abc", "def"));
  }
}
