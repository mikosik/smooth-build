package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;
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
    then(output(), containsString("Can't find parameter(s) of proper type in 'func'"
        + " function for some nameless argument(s)"));
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
    then(output(), containsString(
        "Can't decide unambiguously to which parameters in 'func'"
            + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_parameters_match_argument() throws Exception {
    givenScript("func(String stringA, String stringB) = stringA;"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Can't decide unambiguously to which parameters in 'func'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_parameters_match_two_arguments() throws Exception {
    givenScript("func(String stringA, String stringB) = stringA;"
        + "      result = func('abc', 'def');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Can't decide unambiguously to which parameters in 'func'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void assigns_most_specific_type_first() throws Exception {
    givenFile("file1.txt", "aaa");
    givenFile("file2.txt", "bbb");
    givenScript("fileAndBlob(File file, Blob blob) = content(file);"
        + "      result = fileAndBlob(file('//file1.txt'), content(file('//file2.txt')));");
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
    then(output(), containsString("Can't decide unambiguously to which parameters in 'twoBlobs'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_arguments_match_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("func(Blob blob) = blob;"
        + "      result = func(file('//file.txt'), file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Can't decide unambiguously to which parameters in 'func'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_arguments_match_parameter_and_other_parameter_with_supertype()
      throws Exception {
    givenFile("file.txt", "abc");
    givenScript("fileAndBlob(File file, Blob blob) = 'abc';"
        + "result = fileAndBlob(file('//file.txt'), file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Can't decide unambiguously to which parameters in 'fileAndBlob'"
        + " function some nameless arguments should be assigned"));
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
    then(output(), containsString(
        "build.smooth:1: error: Can't decide unambiguously to which parameters in "
            + "'func' function some nameless arguments should be assigned:\n"
            + "List of assignments that were successfully detected is following:\n"
            + "  File    : p3 <- File    : <nameless> #| [ build.smooth:1 ]\n"
            + "  [String]: p2 <- [String]: <nameless> #2 [ build.smooth:1 ]\n"
            + "  String  : p1 <- String  : <nameless> #1 [ build.smooth:1 ]\n"
            + "List of nameless arguments that caused problems:\n"
            + "  [Nothing]: <nameless> #3 [ build.smooth:1 ]\n"
            + "List of unassigned parameters of desired type is following:\n"
            + "  [Blob]: p5\n"
            + "  [File]: p4\n\n"));
  }
}
