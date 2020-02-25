package org.smoothbuild.acceptance.struct;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StructTest extends AcceptanceTestCase {
  @Test
  public void struct_name_starting_with_lowercase_causes_error() throws Exception {
    givenScript("myStruct {}");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(1, "mismatched input '{' expecting {'(', '=', ';'}\n");
  }

  @Test
  public void struct_name_with_one_large_letter_causes_error() throws Exception {
    givenScript("A {}");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(
        1, "'A' is illegal struct name. It must have at least two characters.\n");
  }

  @Test
  public void empty_struct_is_allowed() throws Exception {
    givenScript("MyStruct {}              \n"
        + "      result = 'abc';          \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void comma_after_last_field_is_allowed() throws Exception {
    givenScript("MyStruct { String field , }          \n"
        + "      result = 'abc';                      \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void illegal_struct_name_causes_error() throws Exception {
    givenScript("My-Struct {};"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void fields_with_same_name_cause_error() throws Exception {
    givenScript("MyStruct {          \n"
        + "        String field1,    \n"
        + "        String field1     \n"
        + "      }                   \n"
        + "      result = 'abc';     \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(3, "'field1' is already defined at build.smooth:2.\n");
  }

  @Test
  public void two_structs_with_same_name_causes_error() throws Exception {
    givenScript("MyStruct {}         \n"
        + "      MyStruct {}         \n"
        + "      result = 'abc';     \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "'MyStruct' is already defined at build.smooth:1.\n");
  }

  @Test
  public void struct_with_same_name_as_basic_type_causes_error() throws Exception {
    givenScript("String {}             \n"
        + "      result = 'abc';       \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "'String' is already defined.\n");
  }

  @Test
  public void struct_with_same_name_as_struct_type_from_platform_api_causes_error()
      throws Exception {
    givenScript("File {}              \n"
        + "      result = 'abc';      \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "'File' is already defined");
  }

  @Test
  public void field_with_unknown_type_causes_error() throws Exception {
    givenScript("MyStruct {             \n"
        + "        Undefined myField    \n"
        + "      }                      \n"
        + "      result = 'abc';        \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void first_field_with_array_type_causes_error() throws Exception {
    givenScript("MyStruct {            \n"
        + "        [String] myField    \n"
        + "      }                     \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(2, "First field of struct cannot have array type.\n");
  }

  @Test
  public void first_field_with_nothing_type_causes_error() throws Exception {
    givenScript("MyStruct {               \n"
        + "        Nothing myField        \n"
        + "      }                        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(2, "Struct field cannot have 'Nothing' type.\n");
  }

  @Test
  public void non_first_field_with_nothing_type_causes_error() throws Exception {
    givenScript("MyStruct {               \n"
        + "        String myField,        \n"
        + "        Nothing genericField,  \n"
        + "      }                        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(3, "Struct field cannot have 'Nothing' type.\n");
  }

  @Test
  public void first_field_with_generic_type_causes_error() throws Exception {
    givenScript("MyStruct {               \n"
        + "        A myField              \n"
        + "      }                        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(2, "Struct field cannot have a generic type.\n");
  }

  @Test
  public void non_first_field_with_generic_type_causes_error() throws Exception {
    givenScript("MyStruct {               \n"
        + "        String myField,        \n"
        + "        A genericField,        \n"
        + "      }                        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(3, "Struct field cannot have a generic type.\n");
  }

  @Test
  public void field_type_can_be_defined_after_definition_of_enclosing_structure() throws Exception {
    givenScript("MyStruct {                   \n"
        + "        OtherStruct myField        \n"
        + "      }                            \n"
        + "      OtherStruct {}               \n"
        + "      result = 'abc';              \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    givenScript("OtherStruct {}             \n"
        + "      MyStruct {                 \n"
        + "        OtherStruct myField      \n"
        + "      }                          \n"
        + "      result = 'abc';            \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_after_definition_of_enclosing_structure()
      throws Exception {
    givenScript("MyStruct {                 \n"
        + "        String ignore,           \n"
        + "        [OtherStruct] myField    \n"
        + "      }                          \n"
        + "      OtherStruct {}             \n"
        + "      result = 'abc';            \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    givenScript("OtherStruct {}              \n"
        + "      MyStruct {                  \n"
        + "        String ignore,            \n"
        + "        [OtherStruct] myField     \n"
        + "      }                           \n"
        + "      result = 'abc';             \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_with_type_equal_to_enclosing_struct_type_causes_error() throws Exception {
    givenScript("MyStruct {             \n"
        + "        MyStruct myField     \n"
        + "      }                      \n"
        + "      result = 'abc';        \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("build.smooth:2: error: "
        + "Type hierarchy contains cycle:\n"
        + "build.smooth:2: MyStruct -> MyStruct\n");
  }

  @Test
  public void field_with_type_equal_to_array_of_enclosing_struct_type_causes_error()
      throws Exception {
    givenScript("MyStruct {                \n"
        + "        [MyStruct] myField      \n"
        + "      }                         \n"
        + "      result = 'abc';           \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "First field of struct cannot have array type.");
  }

  @Test
  public void cycle_in_type_hierarchy_causes_error()
      throws Exception {
    givenScript("MyStruct {                 \n"
        + "        OtherStruct myField      \n"
        + "      }                          \n"
        + "      OtherStruct {              \n"
        + "        MyStruct otherField      \n"
        + "      }                          \n"
        + "      result = 'abc';            \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Type hierarchy contains cycle:\n"
        + "build.smooth:2: MyStruct -> OtherStruct\n"
        + "build.smooth:5: OtherStruct -> MyStruct\n");
  }

  @Test
  public void struct_can_be_declared_before_function_that_uses_it()
      throws Exception {
    givenScript("MyStruct {}                           \n"
        + "      MyStruct myFunc(MyStruct arg) = arg;  \n");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void struct_can_be_declared_after_function_that_uses_it()
      throws Exception {
    givenScript("MyStruct myFunc(MyStruct arg) = arg;  \n"
        + "      MyStruct {}                           \n");
    whenSmoothList();
    thenFinishedWithSuccess();
  }
}
