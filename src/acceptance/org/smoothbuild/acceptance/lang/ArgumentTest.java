package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenScript(
        "  func(String string) = string;  ",
        "  result = func(string='abc',);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void passing_more_positional_arguments_than_parameters_causes_error() throws Exception {
    givenScript(
        "  myIdentity(String myArgument) = myArgument;  ",
        "  result = myIdentity('abc', 'def');           ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Too many positional arguments.\n");
  }

  @Test
  public void passing_less_positional_arguments_than_parameters_causes_error() throws Exception {
    givenScript(
        "  myIdentity(String myArgument, String myArgument2) = myArgument;  ",
        "  result = myIdentity('abc');                                      ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Parameter 'myArgument2' must be specified.\n");
  }

  @Test
  public void assigning_by_name_which_doesnt_exist_causes_error() throws Exception {
    givenScript(
        "  myIdentity(String myArgument) = myArgument;  ",
        "  result = myIdentity(wrongName='abc');        ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Function 'myIdentity' has no parameter 'wrongName'.\n");
  }

  @Test
  public void named_arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    givenScript(
        "  returnFirst(String a, String b) = a;     ",
        "  result = returnFirst(a='abc', b='def');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void named_arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    givenScript(
        "  returnFirst(String a, String b) = a;     ",
        "  result = returnFirst(b='def', a='abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void all_named_arguments_must_come_after_positional() throws Exception {
    givenScript(
        "  returnFirst(String a, String b) = a;   ",
        "  result = returnFirst(b='def', 'abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Positional arguments must be placed before named arguments.\n");
  }

  @Test
  public void assigning_argument_by_name_twice_causes_error() throws Exception {
    givenScript(
        "  myIdentity(String myArgument) = myArgument;               ",
        "  result = myIdentity(myArgument='abc', myArgument='abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Argument 'myArgument' is already assigned.\n");
  }

  @Test
  public void assigning_by_name_argument_that_is_assigned_by_position_causes_error() throws
      Exception {
    givenScript(
        "  myIdentity(String myArgument) = myArgument;    ",
        "  result = myIdentity('abc', myArgument='abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Argument 'myArgument' is already assigned.\n");
  }

  @Test
  public void default_parameters_can_be_assigned_positionally() throws Exception {
    givenScript(
        "  myIdentity(String myArgument='abc', String myArgument2='def') = myArgument;  ",
        "  result = myIdentity('abc', 'def');                                           ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void default_parameters_can_be_assigned_by_name() throws Exception {
    givenScript(
        "  myIdentity(String myArgument='abc', String myArgument2='def') = myArgument;  ",
        "  result = myIdentity(myArgument='abc', myArgument2='def');                    ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
