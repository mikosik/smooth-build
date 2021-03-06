package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    createUserModule(
        "  func(String string) = string;  ",
        "  result = func(string='abc',);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void passing_more_positional_arguments_than_parameters_causes_error() throws Exception {
    createUserModule(
        "  myIdentity(String myArgument) = myArgument;  ",
        "  result = myIdentity('abc', 'def');           ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "In call to `myIdentity`: Too many positional arguments.\n");
  }

  @Test
  public void passing_less_positional_arguments_than_parameters_causes_error() throws Exception {
    createUserModule(
        "  returnFirst(String myArgument, String myArgument2) = myArgument;  ",
        "  result = returnFirst('abc');                                      ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "In call to `returnFirst`: Parameter 'myArgument2' must be specified.\n");
  }

  @Test
  public void assigning_by_name_which_doesnt_exist_causes_error() throws Exception {
    createUserModule(
        "  myIdentity(String myArgument) = myArgument;  ",
        "  result = myIdentity(wrongName='abc');        ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "In call to `myIdentity`: Unknown parameter 'wrongName'.\n");
  }

  @Test
  public void named_arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    createUserModule(
        "  returnFirst(String a, String b) = a;     ",
        "  result = returnFirst(a='abc', b='def');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void named_arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    createUserModule(
        "  returnFirst(String a, String b) = a;     ",
        "  result = returnFirst(b='def', a='abc');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void all_named_arguments_must_come_after_positional() throws Exception {
    createUserModule(
        "  returnFirst(String a, String b) = a;   ",
        "  result = returnFirst(b='def', 'abc');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "In call to `returnFirst`: Positional arguments must be placed before named arguments.\n");
  }

  @Test
  public void assigning_argument_by_name_twice_causes_error() throws Exception {
    createUserModule(
        "  myIdentity(String myArgument) = myArgument;               ",
        "  result = myIdentity(myArgument='abc', myArgument='abc');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "In call to `myIdentity`: Argument 'myArgument' is already assigned.\n");
  }

  @Test
  public void assigning_by_name_argument_that_is_assigned_by_position_causes_error() throws
      Exception {
    createUserModule(
        "  myIdentity(String myArgument) = myArgument;    ",
        "  result = myIdentity('abc', myArgument='abc');  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "In call to `myIdentity`: Argument 'myArgument' is already assigned.\n");
  }

  @Test
  public void default_parameters_can_be_assigned_positionally() throws Exception {
    createUserModule(
        "  myIdentity(String myArgument='abc', String myArgument2='def') = myArgument;  ",
        "  result = myIdentity('abc', 'def');                                           ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void default_parameters_can_be_assigned_by_name() throws Exception {
    createUserModule(
        "  myIdentity(String myArgument='abc', String myArgument2='def') = myArgument;  ",
        "  result = myIdentity(myArgument='abc', myArgument2='def');                    ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }
}
