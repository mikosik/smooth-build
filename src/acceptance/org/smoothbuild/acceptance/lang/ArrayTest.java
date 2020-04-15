package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArrayTest extends AcceptanceTestCase {
  @Test
  public void empty_array_of_nothings() throws Exception {
    givenScript(
        "  result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_bools() throws Exception {
    givenScript(
        "  [Bool] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_strings() throws Exception {
    givenScript(
        "  [String] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_blobs() throws Exception {
    givenScript(
        "  [Blob] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_files() throws Exception {
    givenScript(
        "  [File] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());

  }

  @Test
  public void array_of_bools() throws Exception {
    givenScript(
        "  result = [ true(), false() ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void array_of_strings() throws Exception {
    givenScript(
        "  result = [ 'abc', 'def' ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void array_of_blobs() throws Exception {
    givenScript(
        "  result = [ toBlob('abc'), toBlob('def') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void array_of_files() throws Exception {
    givenScript(
        "  result = [ file(toBlob('abc'), 'file1.txt'), file(toBlob('def'), 'file2.txt') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactDir("result"))
        .containsExactly("file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void empty_array_of_arrays_of_nothings() throws Exception {
    givenScript(
        "  [[Nothing]] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void array_of_arrays_of_nothings_with_one_element() throws Exception {
    givenScript(
        "  [[Nothing]] result = [ [] ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list(list()));
  }

  @Test
  public void array_of_arrays_of_nothings_with_two_elements() throws Exception {
    givenScript(
        "  [[Nothing]] result = [ [], [] ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list(list(), list()));
  }

  @Test
  public void empty_array_of_arrays_of_bools() throws Exception {
    givenScript(
        "  [[Bool]] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_arrays_of_strings() throws Exception {
    givenScript(
        "  [[String]] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_arrays_of_blobs() throws Exception {
    givenScript(
        "  [[Blob]] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void empty_array_of_arrays_of_files() throws Exception {
    givenScript(
        "  [[File]] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void array_of_arrays_of_strings() throws Exception {
    givenScript(
        "  [[String]] result = [ [], [ 'abc' ], [ 'def', 'ghi' ] ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list(list(), list("abc"), list("def", "ghi")));
  }

  @Test
  public void empty_array_of_arrays_of_arrays_of_nothings() throws Exception {
    givenScript(
        "  [[[Nothing]]] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void array_of_arrays_of_arrays_of_strings() throws Exception {
    givenScript(
        "  [[[String]]] result = [ [ [] ], [ [ 'abc' ], [ 'def', 'ghi' ] ] ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list(list(list()), list(list("abc"), list("def", "ghi"))));
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    givenScript(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, myFile ];                 ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains(
        "Saving artifact(s)",
        "  result -> '.smooth/artifacts/result'",
        "   + ERROR: Can't store array of Files as it contains files with duplicated paths:",
        "       file.txt",
        "");
  }

  @Test
  public void empty_array_with_comma_causes_error() throws Exception {
    givenScript(
        "  result = [,];  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void array_with_one_element() throws Exception {
    givenScript(
        "  result = [ 'abc' ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc"));
  }

  @Test
  public void array_with_trailing_comma() throws Exception {
    givenScript(
        "  result = [ 'abc', ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc"));
  }

  @Test
  public void array_with_two_trailing_commas_causes_error() throws Exception {
    givenScript(
        "  result = [ 'abc', , ];  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void array_with_elements_of_the_same_type() throws Exception {
    givenScript(
        "  result = [ 'abc', 'def' ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void array_with_elements_of_compatible_types() throws Exception {
    givenScript(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, toBlob('def') ];          ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void array_with_elements_of_incompatible_types() throws Exception {
    givenScript(
        "  result = [ 'abc', toBlob('abc') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsError(1, "Array cannot contain elements of incompatible types.\n"
        + "First element has type 'String' while element at index 1 has type 'Blob'.\n");
  }

  @Test
  public void first_element_expression_error_doesnt_suppress_second_element_expression_error()
      throws IOException {
    givenScript(
        "  function1 = 'abc';                                            ",
        "  result = [ function1(unknown1=''), function1(unknown2='') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Function 'function1' has no parameter 'unknown1'.");
    thenSysOutContains("Function 'function1' has no parameter 'unknown2'.");
  }
}
