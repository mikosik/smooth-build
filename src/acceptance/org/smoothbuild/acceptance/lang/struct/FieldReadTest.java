package org.smoothbuild.acceptance.lang.struct;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FieldReadTest extends AcceptanceTestCase {
  @Test
  public void struct_field_can_be_accessed_via_field_read() throws Exception {
    createUserModule("""
            MyStruct {
              String field,
            }
            String result = myStruct("abc").field;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void field_cannot_be_called_as_normal_function() throws Exception {
    createUserModule("""
            MyStruct {
              String field,
            }  
            String result = MyStruct.field(myStruct("abc"));
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        4, "mismatched input 'MyStruct' expecting {'[', IDENTIFIER, STRING, BLOB}");
  }

  @Test
  public void field_read_with_parentheses_causes_error() throws Exception {
    createUserModule("""
            MyStruct {
              String field,
            } 
            String result = myStruct("abc").field();
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(4, "mismatched input '(' expecting ");
  }

  @Test
  public void field_reading_can_be_chained() throws Exception {
    createUserModule("""
            S1 {
              S2 f1,
            }
            S2 {
              S3 f2,
            }
            S3 {
              String f3,
            }
            String result = s1(s2(s3("abc"))).f1.f2.f3;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void field_reading_from_string_value_causes_error() throws Exception {
    createUserModule("""
            result = "abc".accessedField;
            """);
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Type 'String' doesn't have field 'accessedField'.");
  }

  @Test
  public void reading_field_that_does_not_exist_causes_error() throws Exception {
    createUserModule("""
            MyStruct {
              String field,
            }
            result = myStruct("abc").otherField;
            """);
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(4, "Type 'MyStruct' doesn't have field 'otherField'.");
  }
}
