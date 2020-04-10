package org.smoothbuild.acceptance.struct;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConstructorTest extends AcceptanceTestCase {
  @Test
  public void creating_empty_struct_is_possible() throws Exception {
    givenScript(
        "  MyStruct {}                                  ",
        "  String takeStruct(MyStruct struct) = 'abc';  ",
        "  result = takeStruct(myStruct());             ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void creating_non_empty_struct_is_possible() throws Exception {
    givenScript(
        "  MyStruct {                                   ",
        "    String field,                              ",
        "  }                                            ",
        "  String takeStruct(MyStruct struct) = 'abc';  ",
        "  result = takeStruct(myStruct('def'));        ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void calling_constructor_without_all_parameters_causes_error() throws Exception {
    givenScript(
        "  MyStruct {                                   ",
        "    String field,                              ",
        "  }                                            ",
        "  String takeStruct(MyStruct struct) = 'abc';  ",
        "  result = takeStruct(myStruct());             ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Parameter 'field' must be specified.");
  }
}
