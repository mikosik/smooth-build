package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenScript("func(String string) = string;     \n"
        + "      result = func(string='abc',);     \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void passing_more_positional_arguments_than_parameters_causes_error() throws Exception {
    givenScript("myIdentity(String myArgument) = myArgument;    \n"
        + "      result = myIdentity('abc', 'def');             \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Too many positional arguments.\n");
  }

  @Test
  public void passing_less_positional_arguments_than_parameters_causes_error() throws Exception {
    givenScript("myIdentity(String myArgument, String myArgument2) = myArgument;    \n"
        + "      result = myIdentity('abc');                                        \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Required parameter 'myArgument2' must be specified.\n");
  }

  @Test
  public void assigning_by_name_which_doesnt_exist_causes_error() throws Exception {
    givenScript("myIdentity(String myArgument) = myArgument;       \n"
        + "      result = myIdentity(wrongName='abc');             \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Function 'myIdentity' has no parameter 'wrongName'.\n");
  }

  @Test
  public void named_arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    givenScript("returnFirst(String a, String b) = a;     \n"
        + "      result = returnFirst(a='abc', b='def');  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void named_arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    givenScript("returnFirst(String a, String b) = a;      \n"
        + "      result = returnFirst(b='def', a='abc');   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void all_named_arguments_must_come_after_positional() throws Exception {
    givenScript("returnFirst(String a, String b) = a;      \n"
        + "      result = returnFirst(b='def', 'abc');     \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Positional arguments must be placed before named arguments.\n");
  }

  @Test
  public void assigning_argument_by_name_twice_causes_error() throws Exception {
    givenScript("myIdentity(String myArgument) = myArgument;                \n"
        + "      result = myIdentity(myArgument='abc', myArgument='abc');     \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Argument 'myArgument' is already assigned.\n");
  }

  @Test
  public void assigning_by_name_argument_that_is_assigned_by_position_causes_error() throws Exception {
    givenScript("myIdentity(String myArgument) = myArgument;       \n"
        + "      result = myIdentity('abc', myArgument='abc');     \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Argument 'myArgument' is already assigned.\n");
  }

  @Test
  public void default_parameters_can_be_assigned_positionally() throws Exception {
    givenScript("myIdentity(String myArgument='abc', String myArgument2='def') = myArgument; \n"
        + "      result = myIdentity('abc', 'def');                                          \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void default_parameters_can_be_assigned_by_name() throws Exception {
    givenScript("myIdentity(String myArgument='abc', String myArgument2='def') = myArgument; \n"
        + "      result = myIdentity(myArgument='abc', myArgument2='def');                   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
