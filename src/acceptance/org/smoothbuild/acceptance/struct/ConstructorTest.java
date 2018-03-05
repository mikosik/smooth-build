package org.smoothbuild.acceptance.struct;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConstructorTest extends AcceptanceTestCase {
  @Test
  public void creating_empty_struct_is_possible() throws Exception {
    givenScript("MyStruct {};                                \n"
        + "      String takeStruct(MyStruct struct) = 'abc'; \n"
        + "      result = takeStruct(MyStruct());            \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void creating_non_empty_struct_is_possible() throws Exception {
    givenScript("MyStruct {                                  \n"
        + "        String field,                             \n"
        + "      };                                          \n"
        + "      String takeStruct(MyStruct struct) = 'abc'; \n"
        + "      result = takeStruct(MyStruct('def'));       \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void calling_constructor_without_all_parameters_causes_error() throws Exception {
    givenScript("MyStruct {                                  \n"
        + "        String field,                             \n"
        + "      };                                          \n"
        + "      String takeStruct(MyStruct struct) = 'abc'; \n"
        + "      result = takeStruct(MyStruct());            \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Not all parameters required by 'MyStruct' function has been specified.");
  }
}
