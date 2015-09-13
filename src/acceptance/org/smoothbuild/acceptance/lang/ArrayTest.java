package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArrayTest extends AcceptanceTestCase {

  @Test
  public void empty_array() throws Exception {
    givenBuildScript(script("result : [];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }

  @Test
  public void empty_array_with_comma_is_forbidden() throws Exception {
    givenBuildScript(script("result : [,];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
  }

  @Test
  public void array_with_one_element() throws Exception {
    givenBuildScript(script("result : ['abc'];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc"));
  }

  @Test
  public void array_with_trailing_comma() throws Exception {
    givenBuildScript(script("result : ['abc',];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc"));
  }

  @Test
  public void array_with_two_trailing_commas_is_forbidden() throws Exception {
    givenBuildScript(script("result : ['abc',,];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
  }

  @Test
  public void array_with_elements_of_the_same_type() throws Exception {
    givenBuildScript(script("result : ['abc', 'def'];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }

  @Test
  public void array_with_elements_of_compatible_types() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script("result: [file('file1.txt'), content(file('file2.txt'))];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }

  @Test
  public void array_with_elements_of_incompatible_types() throws Exception {
    givenFile("file1.txt", "abc");
    givenBuildScript(script("result: ['abc', content(file('file2.txt'))];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Array cannot contain elements of incompatible types."));
    thenPrinted(containsString(
        "First element has type 'String' while element at index 1 has type 'Blob'."));
  }

  @Test
  public void nesting_is_forbidden() throws IOException {
    givenBuildScript(script("myArray : []; result : [ myArray ];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Array cannot contain element with type 'Nothing[]'."));
  }

  @Test
  public void direct_nesting_is_forbidden() throws IOException {
    givenBuildScript(script("result : [ [] ];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Array cannot contain element with type 'Nothing[]'."));
  }

  @Test
  public void nested_arrays_error_message_contains_allowed_types() throws IOException {
    givenBuildScript(script("result : [ [] ];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Only following types are allowed: ['String', 'Blob', 'File']"));
  }
}
