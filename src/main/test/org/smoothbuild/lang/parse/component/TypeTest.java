package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_INVALID_POLYTYPES;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_MONOTYPES;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_VALID_POLYTYPES;
import static org.smoothbuild.util.Strings.unlines;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.lang.TestModuleLoader;
import org.smoothbuild.lang.base.type.TestedType;

public class TypeTest {
  @Nested
  class _type_declared_below {
    @Test
    public void struct_can_be_used_as_field_type() {
      module("""
             ReferencingStruct {
               MyStruct field
             }
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void struct_can_be_used_as_field_arrayed_type() {
      module("""
             ReferencingStruct {
               String firstField,
               [MyStruct] field
             }
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void value_can_be_used_as_its_type() {
      module("""
             MyStruct myValue;
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void value_can_be_used_as_its_arrayed_type() {
      module("""
             [MyStruct] myValue;
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_parameter_type() {
      module("""
             String myFunction(MyStruct param);
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_parameter_arrayed_type() {
      module("""
             String myFunction([MyStruct] param);
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_result_type() {
      module("""
             MyStruct myFunction(String param);
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_arrayed_result_type() {
      module("""
             [MyStruct] myFunction(String param);
             MyStruct {}
             """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class value {
    @ParameterizedTest
    @ArgumentsSource(TestedMonotypes.class)
    public void can_declare_monotype(TestedType type) {
      module(unlines(
          type.typeDeclarationsAsString(),
          type.name() + " myValue;"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(TestedValidPolytypes.class)
    public void can_declare_valid_polytype(TestedType type) {
      module(unlines(
          type.typeDeclarationsAsString(),
          type.name() + " myValue;"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(TestedInvalidPolytypes.class)
    public void cannot_declare_invalid_polytype(TestedType type) {
      module(unlines(
          type.name() + " myValue;",
          type.typeDeclarationsAsString()))
          .loadsWithError(1, "Type variable(s) `A` are used once in declaration of `myValue`." +
              " This means each one can be replaced with `Any`.");
    }

    @Test
    public void can_declare_type_which_is_supertype_of_its_body_type() {
      module("""
           Nothing nothing;
           String myValue = nothing;
           """)
          .loadsSuccessfully();
    }

    @Test
    public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_is_convertible() {
      module("""
           Nothing nothing;
           String myValue = nothing;
           Nothing result = myValue;
         """)
          .loadsWithError(3, "`result` has body which type is `String` and it is not "
              + "convertible to its declared type `Nothing`.");
    }
  }

  @Nested
  class function {
    @Nested
    class result {
      @ParameterizedTest
      @ArgumentsSource(TestedMonotypes.class)
      public void can_declare_monotype(TestedType type) {
        module(unlines(
            type.declarationsAsString(),
            type.name() + " myFunction();"))
            .loadsSuccessfully();
      }

      @ParameterizedTest
      @ArgumentsSource(TestedValidPolytypes.class)
      public void can_declare_valid_polytype(TestedType type) {
        module(unlines(
            type.typeDeclarationsAsString(),
            type.name() + " myFunction();"))
            .loadsSuccessfully();
      }

      @ParameterizedTest
      @ArgumentsSource(TestedInvalidPolytypes.class)
      public void cannot_declare_invalid_polytype(TestedType type) {
        module(unlines(
            type.name() + " myFunction();",
            type.typeDeclarationsAsString()))
            .loadsWithError(1, "Type variable(s) `A` are used once in declaration of `myFunction`."
                + " This means each one can be replaced with `Any`.");
      }

      @Test
      public void can_declare_result_type_which_is_supertype_of_function_expression() {
        module("""
            Nothing nothing;
            String myFunction() = nothing;
            """)
            .loadsSuccessfully();
      }

      @Test
      public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_type_is_convertible() {
        module("""
            Nothing nothing;
            String myFunction() = nothing;
            Nothing result = myFunction();
            """)
            .loadsWithError(3, "`result` has body which type is `String` and it is not "
                + "convertible to its declared type `Nothing`.");
      }
    }

    @Nested
    class parameter {
      @ParameterizedTest
      @ArgumentsSource(TestedMonotypes.class)
      public void can_declare_monotype(TestedType type) {
        module(unlines(
            type.typeDeclarationsAsString(),
            "String myFunction(" + type.name() + " param);"))
            .loadsSuccessfully();
      }

      @ParameterizedTest
      @ArgumentsSource(TestedValidPolytypes.class)
      public void can_declare_valid_polytype(TestedType type) {
        module(unlines(
            type.typeDeclarationsAsString(),
            "String myFunction(" + type.name() + " param);"))
            .loadsSuccessfully();
      }

      @ParameterizedTest
      @ArgumentsSource(TestedInvalidPolytypes.class)
      public void cannot_declare_invalid_polytype(TestedType type) {
        module(unlines(
            "String myFunction(" + type.name() + " param);",
            type.typeDeclarationsAsString()))
            .loadsWithError(1, "Type variable(s) `A` are used once in declaration of `myFunction`."
                + " This means each one can be replaced with `Any`.");
      }
    }

    @ParameterizedTest
    @ArgumentsSource(TestedInvalidPolytypes.class)
    public void can_declare_invalid_polytype_when_param_has_such_type(TestedType type) {
      module(unlines(
          type.name() + " myFunction(" + type.name() + " param);",
          type.typeDeclarationsAsString()))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(TestedInvalidPolytypes.class)
    public void can_declare_invalid_polytype_param_when_some_other_param_has_such_type(
        TestedType type) {
      module(unlines(
          "Blob myFunction(" + type.name() + " param, " + type.name() + " param2);",
          type.typeDeclarationsAsString()))
          .loadsSuccessfully();
    }
  }

  @Nested
  class field {
    @ParameterizedTest
    @ArgumentsSource(TestedMonotypes.class)
    public void can_declare_monotype(TestedType testedType) {
      module(unlines(
          testedType.typeDeclarationsAsString(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(TestedValidPolytypes.class)
    public void can_declare_valid_polytype(TestedType testedType) {
      module(unlines(
          testedType.typeDeclarationsAsString(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(TestedInvalidPolytypes.class)
    public void cannot_declare_invalid_polytype(TestedType testedType) {
      TestModuleLoader module = module(unlines(
          testedType.typeDeclarationsAsString(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"));
      module.loadsWithError(3, "Type variable(s) `A` are used once in declaration of `field`. " +
          "This means each one can be replaced with `Any`.");
    }

    @Test
    public void cannot_declare_type_which_encloses_it() {
      module("""
             MyStruct {
               MyStruct field
             }
             """)
          .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct -> MyStruct""");
    }

    @Test
    public void cannot_declare_array_type_which_element_type_encloses_it() {
      module("""
             MyStruct {
               String firstField,
               [MyStruct] field
             }
             """)
          .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:3: MyStruct -> MyStruct""");
    }
  }

  private static class TestedMonotypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_MONOTYPES.stream()
          .map(Arguments::of);
    }
  }

  private static class TestedValidPolytypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_VALID_POLYTYPES.stream()
          .map(Arguments::of);
    }
  }

  private static class TestedInvalidPolytypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_INVALID_POLYTYPES.stream()
          .map(Arguments::of);
    }
  }
}
