package org.smoothbuild.acceptance.struct;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StructTest extends AcceptanceTestCase {
  @Test
  public void struct_name_starting_with_lowercase_causes_error() throws Exception {
    givenScript("myStruct {};");
    whenSmoothList();
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Struct name 'myStruct' should start with capital letter.\n"));
  }

  @Test
  public void empty_struct_is_allowed() throws Exception {
    givenScript("MyStruct {};"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void comma_after_last_field_is_allowed() throws Exception {
    givenScript("MyStruct { String field , };"
        + "      result = 'abc';");
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
    givenScript("MyStruct {       \n"
        + "        String field1,  \n"
        + "        String field1   \n"
        + "      };                \n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:3: error: "
        + "'field1' is already defined at build.smooth:2.\n"));
  }

  @Test
  public void two_structs_with_same_name_causes_error() throws Exception {
    givenScript("MyStruct {};\n"
        + "      MyStruct {};\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "'MyStruct' is already defined at build.smooth:1.\n"));
  }

  @Test
  public void struct_with_same_name_as_function_causes_error() throws Exception {
    givenScript("String myFunction = 'abc';\n"
        + "      myFunction {};\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "'myFunction' is already defined at build.smooth:1.\n"));
  }

  @Test
  public void struct_with_same_name_as_basic_type_causes_error() throws Exception {
    givenScript("String {};\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:1: error: 'String' is already defined.\n"));
  }

  @Test
  public void struct_with_same_name_as_struct_type_from_platform_api_causes_error()
      throws Exception {
    givenScript("File {};\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:1: error: 'File' is already defined"));
  }

  @Test
  public void field_with_unknown_type_causes_error() throws Exception {
    givenScript("MyStruct {\n"
        + "        Unknown myField\n"
        + "      };\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "Unknown type 'Unknown'.\n"));
  }

  @Test
  public void field_with_array_type_causes_error() throws Exception {
    givenScript("MyStruct {\n"
        + "        [String] myField\n"
        + "      };\n");
    whenSmoothList();
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "First field of struct cannot have array type.\n"));
  }

  @Test
  public void field_with_nothing_type_causes_error() throws Exception {
    givenScript("MyStruct {\n"
        + "        Nothing myField\n"
        + "      };\n");
    whenSmoothList();
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "First field of struct cannot have 'Nothing' type.\n"));
  }

  @Test
  public void field_type_can_be_defined_after_definition_of_enclosing_structure() throws Exception {
    givenScript("MyStruct {\n"
        + "        OtherStruct myField\n"
        + "      };\n"
        + "      OtherStruct {};\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    givenScript("OtherStruct {};\n"
        + "      MyStruct {\n"
        + "        OtherStruct myField\n"
        + "      };\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_after_definition_of_enclosing_structure()
      throws Exception {
    givenScript("MyStruct {\n"
        + "        String ignore,\n"
        + "        [OtherStruct] myField\n"
        + "      };\n"
        + "      OtherStruct {};\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_array_type_can_be_defined_before_definition_of_enclosing_structure()
      throws Exception {
    givenScript("OtherStruct {};\n"
        + "      MyStruct {\n"
        + "        String ignore,\n"
        + "        [OtherStruct] myField\n"
        + "      };\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void field_with_type_equal_to_enclosing_struct_type_causes_error() throws Exception {
    givenScript("MyStruct {\n"
        + "        MyStruct myField\n"
        + "      };\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "Type hierarchy contains cycle:\n"
        + "build.smooth:2: MyStruct -> MyStruct\n"));
  }

  @Test
  public void field_with_type_equal_to_array_of_enclosing_struct_type_causes_error()
      throws Exception {
    givenScript("MyStruct {\n"
        + "        [MyStruct] myField\n"
        + "      };\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:2: error: "
        + "First field of struct cannot have array type."));
  }

  @Test
  public void cycle_in_type_hierarchy_causes_error()
      throws Exception {
    givenScript("MyStruct {\n"
        + "        OtherStruct myField\n"
        + "      };\n"
        + "      OtherStruct {\n"
        + "        MyStruct otherField\n"
        + "      };\n"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Type hierarchy contains cycle:\n"
        + "build.smooth:2: MyStruct -> OtherStruct\n"
        + "build.smooth:5: OtherStruct -> MyStruct\n"));
  }

  @Test
  public void struct_can_be_declared_before_function_that_uses_it()
      throws Exception {
    givenScript("MyStruct {};                          \n"
        + "      MyStruct myFunc(MyStruct arg) = arg;  \n");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void struct_can_be_declared_after_function_that_uses_it()
      throws Exception {
    givenScript("MyStruct myFunc(MyStruct arg) = arg;  \n"
        + "      MyStruct {};                          \n");
    whenSmoothList();
    thenFinishedWithSuccess();
  }
}
