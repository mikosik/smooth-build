package org.smoothbuild.acceptance.struct;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConstructorTest extends AcceptanceTestCase {
  @Test
  public void creating_empty_struct_is_possible() throws Exception {
    givenScript("MyStruct {}                                 \n"
        + "      String takeStruct(MyStruct struct) = 'abc'; \n"
        + "      result = takeStruct(myStruct());            \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void creating_non_empty_struct_is_possible() throws Exception {
    givenScript("MyStruct {                                  \n"
        + "        String field,                             \n"
        + "      }                                           \n"
        + "      String takeStruct(MyStruct struct) = 'abc'; \n"
        + "      result = takeStruct(myStruct('def'));       \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void calling_constructor_without_all_parameters_causes_error() throws Exception {
    givenScript("MyStruct {                                  \n"
        + "        String field,                             \n"
        + "      }                                           \n"
        + "      String takeStruct(myStruct struct) = 'abc'; \n"
        + "      result = takeStruct(myStruct());            \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Parameter 'field' must be specified.");
  }
}
