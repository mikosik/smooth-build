package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArrayTest extends AcceptanceTestCase {
  @Test
  public void empty_array_of_nothings() throws Exception {
    givenScript("result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_of_strings() throws Exception {
    givenScript("[String] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_of_blobs() throws Exception {
    givenScript("[Blob] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_of_files() throws Exception {
    givenScript("[File] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void array_of_strings() throws Exception {
    givenScript("result = ['abc', 'def'];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void array_of_blobs() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result = [file('//file1.txt').content, file('//file2.txt').content];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void array_of_files() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result = [file('//file1.txt'), file('//file2.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void empty_array_of_arrays_of_nothings() throws Exception {
    givenScript("[[Nothing]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void array_of_arrays_of_nothings_with_one_element() throws Exception {
    givenScript("[[Nothing]] result = [[]];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] {} }));
  }

  @Test
  public void array_of_arrays_of_nothings_with_two_elements() throws Exception {
    givenScript("[[Nothing]] result = [[], []];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] {}, new Object[] {} }));
  }

  @Test
  public void empty_array_of_arrays_of_strings() throws Exception {
    givenScript("[[String]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_of_arrays_of_blobs() throws Exception {
    givenScript("[[Blob]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_of_arrays_of_files() throws Exception {
    givenScript("[[File]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void array_of_arrays_of_strings() throws Exception {
    givenScript("[[String]] result = [[], ['abc'], ['def', 'ghi']];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(
        new Object[] {},
        new Object[] { "abc" },
        new Object[] { "def", "ghi" }));
  }

  @Test
  public void empty_array_of_arrays_of_arrays_of_nothings() throws Exception {
    givenScript("[[[Nothing]]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void array_of_arrays_of_arrays_of_strings() throws Exception {
    givenScript("[[[String]]] result = [[[]], [['abc'], ['def', 'ghi']]];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(
        new Object[] { new Object[] {} },
        new Object[] { new Object[] { "abc" }, new Object[] { "def", "ghi" } }));
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result = [file('//file.txt'), file('//file.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Can't store array of Files as it contains files with duplicated paths:\n"
            + "  file.txt\n"));
  }

  @Test
  public void empty_array_with_comma_causes_error() throws Exception {
    givenScript("result = [,];");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void array_with_one_element() throws Exception {
    givenScript("result = ['abc'];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc"));
  }

  @Test
  public void array_with_trailing_comma() throws Exception {
    givenScript("result = ['abc',];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc"));
  }

  @Test
  public void array_with_two_trailing_commas_causes_error() throws Exception {
    givenScript("result = ['abc',,];");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void array_with_elements_of_the_same_type() throws Exception {
    givenScript("result = ['abc', 'def'];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void array_with_elements_of_compatible_types() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result = [file('//file1.txt'), file('//file2.txt').content];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void array_with_elements_of_incompatible_types() throws Exception {
    givenFile("file1.txt", "abc");
    givenScript("result = ['abc', file('//file2.txt').content];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Array cannot contain elements of incompatible types.\n"
            + "First element has type 'String' while element at index 1 has type 'Blob'.\n"));
  }

  @Test
  public void first_element_expression_error_doesnt_suppress_second_element_expression_error()
      throws IOException {
    givenScript("function1 = 'abc';"
        + "      result = [ function1(unknown1=''), function1(unknown2='') ];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Function 'function1' has no parameter 'unknown1'."));
    then(output(), containsString("Function 'function1' has no parameter 'unknown2'."));
  }
}
