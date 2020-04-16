package org.smoothbuild.acceptance.struct;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class AccessorTest extends AcceptanceTestCase {
  @Test
  public void struct_field_can_be_accessed_via_accessor() throws Exception {
    givenScript(
        "  MyStruct {                              ",
        "    String field,                         ",
        "  }                                       ",
        "  String result = myStruct('abc').field;  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void accessor_cannot_be_called_as_normal_function() throws Exception {
    givenScript(
        "  MyStruct {                                        ",
        "    String field,                                   ",
        "  }                                                 ",
        "  String result = MyStruct.field(myStruct('abc'));  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(4, "mismatched input 'MyStruct' expecting {'[', IDENTIFIER, STRING}");
  }

  @Test
  public void accessor_with_parentheses_causes_error() throws Exception {
    givenScript(
        "  MyStruct {                                ",
        "    String field,                           ",
        "  }                                         ",
        "  String result = myStruct('abc').field();  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(4, "mismatched input '(' expecting ");
  }

  @Test
  public void accessors_can_be_chained() throws Exception {
    givenScript(
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
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void applying_accessor_to_string_object_causes_error() throws Exception {
    givenScript(
        "  value = 'abc';                 ",
        "  result = value.accessedField;  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "Type 'String' doesn't have field 'accessedField'.");
  }

  @Test
  public void applying_accessor_to_struct_without_given_field_causes_error() throws Exception {
    givenScript(
        "  MyStruct {                            ",
        "    String field,                       ",
        "  }                                     ",
        "  result = myStruct('abc').otherField;  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(4, "Type 'MyStruct' doesn't have field 'otherField'.");
  }
}
