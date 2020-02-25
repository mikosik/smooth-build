package org.smoothbuild.acceptance.struct;

import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class AccessorTest extends AcceptanceTestCase {
  @Test
  public void struct_field_can_be_accessed_via_accessor() throws Exception {
    givenScript("MyStruct {                              \n"
        + "        String field,                         \n"
        + "      }                                       \n"
        + "      String result = myStruct('abc').field;  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void accessor_cannot_be_called_as_normal_function() throws Exception {
    givenScript("MyStruct {                                        \n"
        + "        String field,                                   \n"
        + "      }                                                 \n"
        + "      String result = MyStruct.field(myStruct('abc'));  \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(4, "mismatched input 'MyStruct' expecting {'[', IDENTIFIER, STRING}");
  }

  @Test
  public void accessor_with_parentheses_causes_error() throws Exception {
    givenScript("MyStruct {                                \n"
        + "        String field,                           \n"
        + "      }                                         \n"
        + "      String result = myStruct('abc').field();  \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(4, "mismatched input '(' expecting ");
  }

  @Test
  public void accessors_can_be_chained() throws Exception {
    givenScript("S1 {                                         \n"
        + "        S2 f1,                                     \n"
        + "      }                                            \n"
        + "      S2 {                                         \n"
        + "        S3 f2,                                     \n"
        + "      }                                            \n"
        + "      S3 {                                         \n"
        + "        String f3,                                 \n"
        + "      }                                            \n"
        + "      String result = s1(s2(s3('abc'))).f1.f2.f3;  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void applying_accessor_to_string_object_causes_error() throws Exception {
    givenScript("value = 'abc';                       \n"
        + "      result = value.accessedField;        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(2, "Type 'String' doesn't have field 'accessedField'.");
  }

  @Test
  public void applying_accessor_to_struct_without_given_field_causes_error() throws Exception {
    givenScript("MyStruct {                               \n"
        + "        String field,                          \n"
        + "      }                                        \n"
        + "      result = myStruct('abc').otherField;     \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(4, "Type 'MyStruct' doesn't have field 'otherField'.");
  }
}
