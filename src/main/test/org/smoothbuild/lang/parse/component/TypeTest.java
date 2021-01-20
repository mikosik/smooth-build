package org.smoothbuild.lang.parse.component;

import static java.util.function.Predicate.not;
import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.base.type.TestedType.A;
import static org.smoothbuild.lang.base.type.TestedType.BLOB;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_MONOTYPES;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_TYPES;
import static org.smoothbuild.lang.base.type.TestedType.a;
import static org.smoothbuild.lang.base.type.TestedType.a2;
import static org.smoothbuild.lang.base.type.TestedType.f;
import static org.smoothbuild.util.Strings.unlines;

import java.util.List;
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
          type.declarationsAsString(),
          type.name() + " myValue;"))
          .loadsSuccessfully();
    }

    @Test
    public void cannot_declare_its_type_as_type_variable() {
      module("""
             A myValue;
             """)
          .loadsWithError(1, "Value type cannot have type variables.");
    }

    @Test
    public void cannot_declare_its_type_as_array_type_with_type_variable() {
      module("""
             [A] myValue;
             """)
          .loadsWithError(1, "Value type cannot have type variables.");
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
      public void can_declare_monotype_result(TestedType type) {
        module(unlines(
            type.declarationsAsString(),
            type.name() + " myFunction();"))
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_polytype_result_when_some_param_has_its_variable_as_type() {
        module("""
            A testIdentity(A param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_polytype_result_when_some_param_is_polytype_with_its_variable() {
        module("""
            A myFunction([A] param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_polytype_array_result_when_some_param_has_its_variable_as_type() {
        module("""
            [A] myFunction(A param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_polytype_array_result_when_some_param_is_polytype_with_its_variable() {
        module("""
            [A] myFunction([A] param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_polytype_result_when_no_param_has_such_variable() {
        module("""
            A myFunction([B] param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_polytype_array_result_when_no_param_has_such_variable() {
        module("""
            [A] myFunction([B] param);
            """)
            .loadsSuccessfully();
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
      @ArgumentsSource(TestedTypes.class)
      public void can_declare_any_type(TestedType type) {
        module(unlines(
            type.declarationsAsString(),
            "String myFunction(" + type.name() + " param);"))
            .loadsSuccessfully();
      }
    }
  }

  @Nested
  class field {
    @ParameterizedTest
    @ArgumentsSource(FirstFieldAllowedTypes.class)
    public void first_field_can_declare_following_types(TestedType testedType) {
      module(unlines(
          testedType.declarationsAsString(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(FirstFieldForbiddenTypes.class)
    public void first_field_cannot_declare_following_types(TestedType testedType) {
      TestModuleLoader module = module(unlines(
          testedType.declarationsAsString(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"));
      module.loadsWithError(3, "Struct field type cannot have type variable.");
    }

    @ParameterizedTest
    @ArgumentsSource(FieldAllowedTypes.class)
    public void non_first_field_can_declare_monotype(TestedType testedType) {
      module(unlines(
          testedType.declarationsAsString(),
          "MyStruct {",
          "  String firstField,",
          "  " + testedType.name() + " secondField,",
          "}"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(FieldForbiddenTypes.class)
    public void non_first_field_cannot_declare_following_types(TestedType testedType) {
      TestModuleLoader module = module(unlines(
          "MyStruct {",
          "  String firstField,",
          "  " + testedType.name() + " field,",
          "}",
          testedType.declarationsAsString()));
      module.loadsWithError(3, "Struct field type cannot have type variable.");
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

  private static class TestedTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_TYPES.stream()
          .map(Arguments::of);
    }
  }

  private static class FirstFieldAllowedTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      List<TestedType> forbidden = firstFieldForbiddenTypes();
      return TESTED_MONOTYPES
          .stream()
          .filter(not(forbidden::contains))
          .map(Arguments::of);
    }
  }

  private static class FirstFieldForbiddenTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return firstFieldForbiddenTypes().stream().map(Arguments::of);
    }
  }

  private static List<TestedType> firstFieldForbiddenTypes() {
    return List.of(
        A,
        a(A),
        a(a(A)),
        f(A),
        f(BLOB, A)
    );
  }

  private static class FieldAllowedTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      List<TestedType> forbidden = fieldForbiddenTypes();
      return TESTED_MONOTYPES
          .stream()
          .filter(not(forbidden::contains))
          .map(Arguments::of);
    }
  }

  private static class FieldForbiddenTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return fieldForbiddenTypes().stream().map(Arguments::of);
    }
  }

  private static List<TestedType> fieldForbiddenTypes() {
    return List.of(
        A,
        a(A),
        a2(A),
        f(A),
        f(BLOB, A),
        f(f(A)),
        f(f(BLOB, A))
    );
  }
}
