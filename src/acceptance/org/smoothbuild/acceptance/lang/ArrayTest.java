package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArrayTest extends AcceptanceTestCase {
  @Nested
  class empty_array_of {
    @Test
    public void blobs() throws Exception {
      createUserModule(
          "  [Blob] result = [];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list());
    }

    @Test
    public void bools() throws Exception {
      createUserModule(
          "  [Bool] result = [];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list());
    }

    @Test
    public void nothings() throws Exception {
      createUserModule(
          "  result = [];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list());
    }

    @Test
    public void strings() throws Exception {
      createUserModule(
          "  [String] result = [];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list());
    }

    @Test
    public void files() throws Exception {
      createUserModule(
          "  [File] result = [];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list());
    }

    @Nested
    class arrays_of {
      @Test
      public void nothings() throws Exception {
        createUserModule(
            "  [[Nothing]] result = [];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list());
      }

      @Test
      public void bools() throws Exception {
        createUserModule(
            "  [[Bool]] result = [];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list());
      }

      @Test
      public void strings() throws Exception {
        createUserModule(
            "  [[String]] result = [];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list());
      }

      @Test
      public void blobs() throws Exception {
        createUserModule(
            "  [[Blob]] result = [];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list());
      }

      @Test
      public void files() throws Exception {
        createUserModule(
            "  [[File]] result = [];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list());
      }

      @Test
      public void arrays_of_nothings() throws Exception {
        createUserModule(
            "  [[[Nothing]]] result = [];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list());
      }
    }
  }

  @Nested
  class array_of {
    @Test
    public void blobs() throws Exception {
      createUserModule(
          "  result = [ toBlob('abc'), toBlob('def') ];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list("abc", "def"));
    }

    @Test
    public void bools() throws Exception {
      createUserModule(
          "  result = [ true(), false() ];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsByteStrings("result"))
          .isEqualTo(list(trueByteString(), falseByteString()));
    }

    @Test
    public void strings() throws Exception {
      createUserModule(
          "  result = [ 'abc', 'def' ];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(list("abc", "def"));
    }

    @Test
    public void files() throws Exception {
      createUserModule(
          "  result = [ file(toBlob('abc'), 'file1.txt'), file(toBlob('def'), 'file2.txt') ];  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactTreeContentAsStrings("result"))
          .containsExactly("file1.txt", "abc", "file2.txt", "def");
    }

    @Nested
    class arrays_of {
      @Test
      public void nothings_with_one_element() throws Exception {
        createUserModule(
            "  [[Nothing]] result = [ [] ];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list(list()));
      }

      @Test
      public void nothings_with_two_elements() throws Exception {
        createUserModule(
            "  [[Nothing]] result = [ [], [] ];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list(list(), list()));
      }

      @Test
      public void strings() throws Exception {
        createUserModule(
            "  [[String]] result = [ [], [ 'abc' ], [ 'def', 'ghi' ] ];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list(list(), list("abc"), list("def", "ghi")));
      }

      @Test
      public void arrays_of_strings() throws Exception {
        createUserModule(
            "  [[[String]]] result = [ [ [] ], [ [ 'abc' ], [ 'def', 'ghi' ] ] ];  ");
        runSmoothBuild("result");
        assertFinishedWithSuccess();
        assertThat(stringifiedArtifact("result"))
            .isEqualTo(list(list(list()), list(list("abc"), list("def", "ghi"))));
      }
    }
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    createUserModule(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, myFile ];                 ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Saving artifact(s)",
        "  result -> ???",
        "   + ERROR: Can't store array of Files as it contains files with duplicated paths:",
        "       'file.txt'",
        "");
  }

  @Test
  public void empty_array_with_comma_causes_error() throws Exception {
    createUserModule(
        "  result = [,];  ");
    runSmoothBuild("result");
    assertFinishedWithError();
  }

  @Test
  public void array_with_one_element() throws Exception {
    createUserModule(
        "  result = [ 'abc' ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc"));
  }

  @Test
  public void array_with_trailing_comma() throws Exception {
    createUserModule(
        "  result = [ 'abc', ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc"));
  }

  @Test
  public void array_with_two_trailing_commas_causes_error() throws Exception {
    createUserModule(
        "  result = [ 'abc', , ];  ");
    runSmoothBuild("result");
    assertFinishedWithError();
  }

  @Test
  public void array_with_elements_of_the_same_type() throws Exception {
    createUserModule(
        "  result = [ 'abc', 'def' ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void array_with_elements_of_compatible_types() throws Exception {
    createUserModule(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, toBlob('def') ];          ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void array_with_elements_of_incompatible_types() throws Exception {
    createUserModule(
        "  result = [ 'abc', toBlob('abc') ];  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1,
        "Array cannot contain elements of incompatible types.",
        "First element has type 'String' while element at index 1 has type 'Blob'.");
  }

  @Test
  public void first_element_expression_error_doesnt_suppress_second_element_expression_error()
      throws IOException {
    createUserModule(
        "  function1 = 'abc';                                            ",
        "  result = [ function1(unknown1=''), function1(unknown2='') ];  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("In call to `function1`: Unknown parameter 'unknown1'.");
    assertSysOutContains("In call to `function1`: Unknown parameter 'unknown2'.");
  }
}
