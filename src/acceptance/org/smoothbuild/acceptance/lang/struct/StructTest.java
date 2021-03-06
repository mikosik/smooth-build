package org.smoothbuild.acceptance.lang.struct;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.assign.spec.TestedType;
import org.smoothbuild.acceptance.testing.ReportError;

public class StructTest extends AcceptanceTestCase {
  @Test
  public void struct_name_starting_with_lowercase_causes_error() throws Exception {
    createUserModule(
        "  myStruct {}  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "mismatched input '{' expecting {'(', '=', ';'}\n");
  }

  @Test
  public void struct_name_with_one_large_letter_causes_error() throws Exception {
    createUserModule(
        "  A {}  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "'A' is illegal struct name. It must have at least two characters.\n");
  }

  @Test
  public void empty_struct_is_allowed() throws Exception {
    createUserModule(
        "  MyStruct {}      ",
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void comma_after_last_field_is_allowed() throws Exception {
    createUserModule(
        "  MyStruct { String field , }  ",
        "  result = 'abc';              ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void illegal_struct_name_causes_error() throws Exception {
    createUserModule(
        "  My-Struct {};    ",
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
  }

  @Test
  public void fields_with_same_name_cause_error() throws Exception {
    createUserModule(
        "  MyStruct {        ",
        "    String field1,  ",
        "    String field1   ",
        "  }                 ",
        "  result = 'abc';   ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(3, "'field1' is already defined at build.smooth:2.\n");
  }

  @Test
  public void field_with_unknown_type_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {           ",
        "    Undefined myField  ",
        "  }                    ",
        "  result = 'abc';      ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void first_field_with_array_type_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {          ",
        "    [String] myField  ",
        "  }                   ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "First field of struct cannot have array type.\n");
  }

  @Test
  public void first_field_with_nothing_type_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {         ",
        "    Nothing myField  ",
        "  }                  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "First field of struct cannot have 'Nothing' type.\n");
  }

  @ParameterizedTest
  @MethodSource("non_first_field_types")
  public void non_first_field_can_have_type(TestedType testedType) throws Exception {
    createNativeJar(ReportError.class);
    createUserModule(
        testedType.declarations,
        "  MyStruct {               ",
        "    String firstField,        ",
        "    " + testedType.name + " secondField,  ",
        "  }                        ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  private static Stream<Arguments> non_first_field_types() {
    return TestedType.TESTED_TYPES
        .stream()
        .map(Arguments::of);
  }

  @Test
  public void first_field_with_generic_type_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {   ",
        "    A myField  ",
        "  }            ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "Struct field cannot have a generic type.\n");
  }

  @Test
  public void non_first_field_with_generic_type_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {         ",
        "    String myField,  ",
        "    A genericField,  ",
        "  }                  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(3, "Struct field cannot have a generic type.\n");
  }

  @Test
  public void field_type_can_be_defined_after_definition_of_enclosing_structure() throws Exception {
    createUserModule(
        "  MyStruct {             ",
        "    OtherStruct myField  ",
        "  }                      ",
        "  OtherStruct {}         ",
        "  result = 'abc';        ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void field_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    createUserModule(
        "  OtherStruct {}         ",
        "  MyStruct {             ",
        "    OtherStruct myField  ",
        "  }                      ",
        "  result = 'abc';        ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_after_definition_of_enclosing_structure()
      throws Exception {
    createUserModule(
        "  MyStruct {               ",
        "    String ignore,         ",
        "    [OtherStruct] myField  ",
        "  }                        ",
        "  OtherStruct {}           ",
        "  result = 'abc';          ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    createUserModule(
        "  OtherStruct {}           ",
        "  MyStruct {               ",
        "    String ignore,         ",
        "    [OtherStruct] myField  ",
        "  }                        ",
        "  result = 'abc';          ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void field_with_type_equal_to_enclosing_struct_type_causes_error() throws Exception {
    createUserModule(
        "  MyStruct {          ",
        "    MyStruct myField  ",
        "  }                   ",
        "  result = 'abc';     ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        "Type hierarchy contains cycle:",
        "build.smooth:2: MyStruct -> MyStruct");
  }

  @Test
  public void field_with_type_equal_to_array_of_enclosing_struct_type_causes_error()
      throws Exception {
    createUserModule(
        "  MyStruct {            ",
        "    [MyStruct] myField  ",
        "  }                     ",
        "  result = 'abc';       ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "First field of struct cannot have array type.");
  }

  @Test
  public void cycle_in_type_hierarchy_causes_error()
      throws Exception {
    createUserModule(
        "  MyStruct {             ",
        "    OtherStruct myField  ",
        "  }                      ",
        "  OtherStruct {          ",
        "    MyStruct otherField  ",
        "  }                      ",
        "  result = 'abc';        ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        "Type hierarchy contains cycle:",
        "build.smooth:2: MyStruct -> OtherStruct",
        "build.smooth:5: OtherStruct -> MyStruct");
  }

  @Test
  public void struct_can_be_declared_before_function_that_uses_it()
      throws Exception {
    createUserModule(
        "  MyStruct {}                           ",
        "  MyStruct myFunc(MyStruct arg) = arg;  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void struct_can_be_declared_after_function_that_uses_it()
      throws Exception {
    createUserModule(
        "  MyStruct myFunc(MyStruct arg) = arg;  ",
        "  MyStruct {}                           ");
    runSmoothList();
    assertFinishedWithSuccess();
  }
}
