package org.smoothbuild.acceptance.struct;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StructTest extends AcceptanceTestCase {
  @Test
  public void struct_name_starting_with_lowercase_causes_error() throws Exception {
    givenScript(
        "  myStruct {}  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "mismatched input '{' expecting {'(', '=', ';'}\n");
  }

  @Test
  public void struct_name_with_one_large_letter_causes_error() throws Exception {
    givenScript(
        "  A {}  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(
        1, "'A' is illegal struct name. It must have at least two characters.\n");
  }

  @Test
  public void empty_struct_is_allowed() throws Exception {
    givenScript(
        "  MyStruct {}      ",
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void comma_after_last_field_is_allowed() throws Exception {
    givenScript(
        "  MyStruct { String field , }  ",
        "  result = 'abc';              ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void illegal_struct_name_causes_error() throws Exception {
    givenScript(
        "  My-Struct {};    ",
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void fields_with_same_name_cause_error() throws Exception {
    givenScript(
        "  MyStruct {        ",
        "    String field1,  ",
        "    String field1   ",
        "  }                 ",
        "  result = 'abc';   ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(3, "'field1' is already defined at build.smooth:2.\n");
  }

  @Test
  public void two_structs_with_same_name_causes_error() throws Exception {
    givenScript(
        "  MyStruct {}      ",
        "  MyStruct {}      ",
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "'MyStruct' is already defined at build.smooth:1.\n");
  }

  @Test
  public void struct_with_same_name_as_basic_type_causes_error() throws Exception {
    givenScript(
        "  String {}        ",
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "'String' is already defined.\n");
  }

  @Test
  public void struct_with_same_name_as_struct_type_from_standard_library_causes_error()
      throws Exception {
    givenScript(
        "  File {}          ",
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "'File' is already defined");
  }

  @Test
  public void field_with_unknown_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {           ",
        "    Undefined myField  ",
        "  }                    ",
        "  result = 'abc';      ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void first_field_with_array_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {          ",
        "    [String] myField  ",
        "  }                   ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "First field of struct cannot have array type.\n");
  }

  @Test
  public void first_field_with_nothing_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {         ",
        "    Nothing myField  ",
        "  }                  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "Struct field cannot have 'Nothing' type.\n");
  }

  @Test
  public void non_first_field_with_nothing_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {               ",
        "    String myField,        ",
        "    Nothing genericField,  ",
        "  }                        ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(3, "Struct field cannot have 'Nothing' type.\n");
  }

  @Test
  public void first_field_with_generic_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {   ",
        "    A myField  ",
        "  }            ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "Struct field cannot have a generic type.\n");
  }

  @Test
  public void non_first_field_with_generic_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {         ",
        "    String myField,  ",
        "    A genericField,  ",
        "  }                  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(3, "Struct field cannot have a generic type.\n");
  }

  @Test
  public void field_type_can_be_defined_after_definition_of_enclosing_structure() throws Exception {
    givenScript(
        "  MyStruct {             ",
        "    OtherStruct myField  ",
        "  }                      ",
        "  OtherStruct {}         ",
        "  result = 'abc';        ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    givenScript(
        "  OtherStruct {}         ",
        "  MyStruct {             ",
        "    OtherStruct myField  ",
        "  }                      ",
        "  result = 'abc';        ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_after_definition_of_enclosing_structure()
      throws Exception {
    givenScript(
        "  MyStruct {               ",
        "    String ignore,         ",
        "    [OtherStruct] myField  ",
        "  }                        ",
        "  OtherStruct {}           ",
        "  result = 'abc';          ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    givenScript(
        "  OtherStruct {}           ",
        "  MyStruct {               ",
        "    String ignore,         ",
        "    [OtherStruct] myField  ",
        "  }                        ",
        "  result = 'abc';          ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_with_type_equal_to_enclosing_struct_type_causes_error() throws Exception {
    givenScript(
        "  MyStruct {          ",
        "    MyStruct myField  ",
        "  }                   ",
        "  result = 'abc';     ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(
        "Type hierarchy contains cycle:",
        "build.smooth:2: MyStruct -> MyStruct");
  }

  @Test
  public void field_with_type_equal_to_array_of_enclosing_struct_type_causes_error()
      throws Exception {
    givenScript(
        "  MyStruct {            ",
        "    [MyStruct] myField  ",
        "  }                     ",
        "  result = 'abc';       ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "First field of struct cannot have array type.");
  }

  @Test
  public void cycle_in_type_hierarchy_causes_error()
      throws Exception {
    givenScript(
        "  MyStruct {             ",
        "    OtherStruct myField  ",
        "  }                      ",
        "  OtherStruct {          ",
        "    MyStruct otherField  ",
        "  }                      ",
        "  result = 'abc';        ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(
        "Type hierarchy contains cycle:",
        "build.smooth:2: MyStruct -> OtherStruct",
        "build.smooth:5: OtherStruct -> MyStruct");
  }

  @Test
  public void struct_can_be_declared_before_function_that_uses_it()
      throws Exception {
    givenScript(
        "  MyStruct {}                           ",
        "  MyStruct myFunc(MyStruct arg) = arg;  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void struct_can_be_declared_after_function_that_uses_it()
      throws Exception {
    givenScript(
        "  MyStruct myFunc(MyStruct arg) = arg;  ",
        "  MyStruct {}                           ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }
}
