package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArrayTest extends AcceptanceTestCase {

  @Test
  public void empty_array() throws Exception {
    givenScript("result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_with_comma_is_forbidden() throws Exception {
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
  public void array_with_two_trailing_commas_is_forbidden() throws Exception {
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
    givenScript("result = [file('//file1.txt'), content(file('//file2.txt'))];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void array_with_elements_of_incompatible_types() throws Exception {
    givenFile("file1.txt", "abc");
    givenScript("result = ['abc', content(file('//file2.txt'))];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Array cannot contain elements of incompatible types.\n"
            + "First element has type 'String' while element at index 1 has type 'Blob'.\n"));
  }

  @Test
  public void nesting_is_forbidden() throws IOException {
    givenScript("myArray = []; result = [ myArray ];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Array type cannot be nested."));
  }

  @Test
  public void direct_nesting_is_forbidden() throws IOException {
    givenScript("result = [ [] ];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Array type cannot be nested."));
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
