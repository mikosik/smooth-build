package org.smoothbuild.acceptance.struct;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StructFieldTest extends AcceptanceTestCase {
  @Test
  public void struct_field_can_be_accessed_via_accessor() throws Exception {
    givenScript("MyStruct {                              \n"
        + "        String field,                         \n"
        + "      };                                      \n"
        + "      String result = MyStruct('abc').field;  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void accessors_can_be_chained() throws Exception {
    givenScript("S1 {                                         \n"
        + "        S2 f1,                                     \n"
        + "      };                                           \n"
        + "      S2 {                                         \n"
        + "        S3 f2,                                     \n"
        + "      };                                           \n"
        + "      S3 {                                         \n"
        + "        String f3,                                 \n"
        + "      };                                           \n"
        + "      String result = S1(S2(S3('abc'))).f1.f2.f3;  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void applying_accessor_to_string_value_causes_error() throws Exception {
    givenScript("value = 'abc';                       \n"
        + "      result = value.accessedField;        \n");
    whenSmoothList();
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:2: error: Type 'String' doesn't have field 'accessedField'."));
  }

  @Test
  public void applying_accessor_to_struct_without_given_field_causes_error() throws Exception {
    givenScript("MyStruct {                               \n"
        + "        String field,                          \n"
        + "      };                                       \n"
        + "      result = MyStruct('abc').otherField;     \n");
    whenSmoothList();
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:4: error: Type 'MyStruct' doesn't have field 'otherField'."));
  }
}
