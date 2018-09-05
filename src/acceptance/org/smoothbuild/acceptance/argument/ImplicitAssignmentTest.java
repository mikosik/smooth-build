package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ImplicitAssignmentTest extends AcceptanceTestCase {
  @Test
  public void fails_when_there_is_no_parameter_matching() throws Exception {
    givenScript("func(Blob blob) = blob;"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'func'. "
        + "Cannot find any valid assignment between implicit arguments and parameters.");
  }

  @Test
  public void generic_parameter_cant_be_assigned_implicitly() throws Exception {
    givenScript("a testIdentity(a value) = value;     \n"
        + "      result = testIdentity('abc');        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(2, "Generic parameter 'value' must be assigned explicitly");
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenScript("func(String string) = string;"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("func(Blob blob) = blob;"
        + "      result = func(file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void fails_when_one_parameter_matches_two_arguments() throws Exception {
    givenScript("func(String string) = string;"
        + "result = func('abc', 'def');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'func'. "
        + "Found more than one valid assignment between implicit arguments and parameters.");
  }

  @Test
  public void fails_when_two_parameters_match_argument() throws Exception {
    givenScript("func(String stringA, String stringB) = stringA;"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'func'.");
  }

  @Test
  public void fails_when_two_parameters_match_two_arguments() throws Exception {
    givenScript("func(String stringA, String stringB) = stringA;"
        + "      result = func('abc', 'def');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'func'. "
        + "Found more than one valid assignment between implicit arguments and parameters.");
  }

  @Test
  public void assigns_most_specific_type_first() throws Exception {
    givenFile("file1.txt", "aaa");
    givenFile("file2.txt", "bbb");
    givenScript("fileAndBlob(File file, Blob blob) = file.content;"
        + "      result = fileAndBlob(file('//file1.txt'), file('//file2.txt').content);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("aaa"));
  }

  @Test
  public void fails_when_argument_matches_two_parameters_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("twoBlobs(Blob a, Blob b) = 'abc';"
        + "      result = twoBlobs(file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'twoBlobs'.");
  }

  @Test
  public void fails_when_two_arguments_match_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("func(Blob blob) = blob;"
        + "      result = func(file('//file.txt'), file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'func'."
        + " Found more than one valid assignment between implicit arguments and parameters.");
  }

  @Test
  public void fails_when_two_arguments_match_parameter_and_other_parameter_with_supertype()
      throws Exception {
    givenFile("file.txt", "abc");
    givenScript("fileAndBlob(File file, Blob blob) = 'abc';"
        + "result = fileAndBlob(file('//file.txt'), file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot infer arguments to parameters assignment in call to 'fileAndBlob'."
        + " Found more than one valid assignment between implicit arguments and parameters.");
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenScript("func([String] array) = array;"
        + "      result = func([]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void ambiguous_error_message() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("func( String p1, [String] p2, File p3, [File] p4, [Blob] p5) = '';"
        + "      result = file('//file.txt') | func('abc', ['abc'], []);");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1,
        "Cannot infer arguments to parameters assignment in call to 'func'.");
  }
}
