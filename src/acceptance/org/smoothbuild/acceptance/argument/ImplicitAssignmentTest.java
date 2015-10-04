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
    givenScript("result: blobIdentity('abc');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Can't find parameter(s) of proper type in 'blobIdentity'"
        + " function for some nameless argument(s)"));
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenScript("result: stringIdentity('abc');");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: blobIdentity(file('file.txt'));");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void fails_when_one_parameter_matches_two_arguments() throws Exception {
    givenScript("result: stringIdentity('abc', 'def');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString(
        "Can't decide unambiguously to which parameters in 'stringIdentity'"
            + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_parameters_match_argument() throws Exception {
    givenScript("result: twoStrings('abc');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Can't decide unambiguously to which parameters in 'twoStrings'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_parameters_match_two_arguments() throws Exception {
    givenScript("result: twoStrings('abc', 'def');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Can't decide unambiguously to which parameters in 'twoStrings'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void assigns_most_specific_type_first() throws Exception {
    givenFile("file1.txt", "aaa");
    givenFile("file2.txt", "bbb");
    givenScript("result: fileAndBlob(file('file1.txt'), content(file('file2.txt')));");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("aaa:bbb"));
  }

  @Test
  public void fails_when_argument_matches_two_parameters_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: twoBlobs(file('file.txt'));");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Can't decide unambiguously to which parameters in 'twoBlobs'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_arguments_match_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: blobIdentity(file('file.txt'), file('file.txt'));");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Can't decide unambiguously to which parameters in 'blobIdentity'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_arguments_match_parameter_and_other_parameter_with_supertype()
      throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: fileAndBlob(file('file.txt'), file('file.txt'));");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Can't decide unambiguously to which parameters in 'fileAndBlob'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenScript("result: stringArrayIdentity([]);");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isArrayWith());
  }
}
