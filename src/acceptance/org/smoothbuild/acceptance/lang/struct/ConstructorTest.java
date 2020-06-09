package org.smoothbuild.acceptance.lang.struct;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConstructorTest extends AcceptanceTestCase {
  @Test
  public void creating_empty_struct_is_possible() throws Exception {
    createUserModule(
        "  MyStruct {}                                  ",
        "  String takeStruct(MyStruct struct) = 'abc';  ",
        "  result = takeStruct(myStruct());             ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void creating_non_empty_struct_is_possible() throws Exception {
    createUserModule(
        "  MyStruct {                                   ",
        "    String field,                              ",
        "  }                                            ",
        "  String takeStruct(MyStruct struct) = 'abc';  ",
        "  result = takeStruct(myStruct('def'));        ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void calling_constructor_without_all_parameters_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {                                   ",
        "    String field,                              ",
        "  }                                            ",
        "  String takeStruct(MyStruct struct) = 'abc';  ",
        "  result = takeStruct(myStruct());             ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Parameter 'field' must be specified.");
  }
}
