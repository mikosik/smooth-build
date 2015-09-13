package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ImplicitAssignmentTest extends AcceptanceTestCase {
  @Test
  public void fails_when_there_is_no_parameter_matching() throws Exception {
    givenBuildScript(script("result: blobIdentity('abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't find parameter(s) of proper type in 'blobIdentity'"
        + " function for some nameless argument(s)"));
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenBuildScript(script("result: stringIdentity('abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: blobIdentity(file('file.txt'));"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void fails_when_one_parameter_matches_two_arguments() throws Exception {
    givenBuildScript(script("result: stringIdentity('abc', 'def');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't decide unambiguously to which parameters in 'stringIdentity'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_parameters_match_argument() throws Exception {
    givenBuildScript(script("result: twoStrings('abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't decide unambiguously to which parameters in 'twoStrings'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_parameters_match_two_arguments() throws Exception {
    givenBuildScript(script("result: twoStrings('abc', 'def');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't decide unambiguously to which parameters in 'twoStrings'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void assigns_most_specific_type_first() throws Exception {
    givenFile("file1.txt", "aaa");
    givenFile("file2.txt", "bbb");
    givenBuildScript(script("result: fileAndBlob(file('file1.txt'), content(file('file2.txt')));"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("aaa:bbb"));
  }

  @Test
  public void fails_when_argument_matches_two_parameters_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: twoBlobs(file('file.txt'));"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't decide unambiguously to which parameters in 'twoBlobs'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_arguments_match_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: blobIdentity(file('file.txt'), file('file.txt'));"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't decide unambiguously to which parameters in 'blobIdentity'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void fails_when_two_arguments_match_parameter_and_other_parameter_with_supertype()
      throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: fileAndBlob(file('file.txt'), file('file.txt'));"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Can't decide unambiguously to which parameters in 'fileAndBlob'"
        + " function some nameless arguments should be assigned"));
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenBuildScript(script("result: stringArrayIdentity([]);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }
}
