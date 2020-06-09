package org.smoothbuild.acceptance.lang.struct;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class AccessorTest extends AcceptanceTestCase {
  @Test
  public void struct_field_can_be_accessed_via_accessor() throws Exception {
    createUserModule(
        "  MyStruct {                              ",
        "    String field,                         ",
        "  }                                       ",
        "  String result = myStruct('abc').field;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void accessor_cannot_be_called_as_normal_function() throws Exception {
    createUserModule(
        "  MyStruct {                                        ",
        "    String field,                                   ",
        "  }                                                 ",
        "  String result = MyStruct.field(myStruct('abc'));  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(4, "mismatched input 'MyStruct' expecting {'[', IDENTIFIER, STRING}");
  }

  @Test
  public void accessor_with_parentheses_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {                                ",
        "    String field,                           ",
        "  }                                         ",
        "  String result = myStruct('abc').field();  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(4, "mismatched input '(' expecting ");
  }

  @Test
  public void accessors_can_be_chained() throws Exception {
    createUserModule(
        "  S1 {                                         ",
        "    S2 f1,                                     ",
        "  }                                            ",
        "  S2 {                                         ",
        "    S3 f2,                                     ",
        "  }                                            ",
        "  S3 {                                         ",
        "    String f3,                                 ",
        "  }                                            ",
        "  String result = s1(s2(s3('abc'))).f1.f2.f3;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void applying_accessor_to_string_object_causes_error() throws Exception {
    createUserModule(
        "  value = 'abc';                 ",
        "  result = value.accessedField;  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "Type 'String' doesn't have field 'accessedField'.");
  }

  @Test
  public void applying_accessor_to_struct_without_given_field_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {                            ",
        "    String field,                       ",
        "  }                                     ",
        "  result = myStruct('abc').otherField;  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(4, "Type 'MyStruct' doesn't have field 'otherField'.");
  }
}
