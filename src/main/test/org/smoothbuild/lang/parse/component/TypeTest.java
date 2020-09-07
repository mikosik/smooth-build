package org.smoothbuild.lang.parse.component;

import static java.util.function.Predicate.not;
import static org.smoothbuild.lang.base.type.TestedType.A;
import static org.smoothbuild.lang.base.type.TestedType.BLOB_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.BLOB_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.BOOL_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.BOOL_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.CONCRETE_TESTED_TYPES;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BLOB_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BLOB_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BOOL_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BOOL_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_TYPES;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;
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
import org.smoothbuild.lang.base.type.TestedType;

public class TypeTest {
  @Nested
  class type_declared_below {
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
    @ArgumentsSource(TestedConcreteTypes.class)
    public void can_declare_concrete_type(TestedType type) {
      module(unlines(
          type.declarations(),
          type.name() + " myValue;"))
          .loadsSuccessfully();
    }

    @Test
    public void cannot_declare_generic_type() {
      module("""
             A valueWithGenericType;
             """)
          .loadsWithError(1, "Value cannot have generic type.");
    }

    @Test
    public void cannot_declare_generic_array_type() {
      module("""
             [A] valueWithGenericType;
             """)
          .loadsWithError(1, "Value cannot have generic type.");
    }

    @Test
    public void can_declare_type_which_is_supertype_of_its_body_type() {
      module("""
           MyStruct {
             String field
           }
           String myValue = myStruct("abc");
           """)
          .loadsSuccessfully();
    }

    @Test
    public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_is_convertible() {
      module("""
           MyStruct {
             String field
           }
           String myValue = myStruct("abc");
           MyStruct result = myValue;
         """)
          .loadsWithError(5, "`result` has body which type is 'String' and it is not "
              + "convertible to its declared type 'MyStruct'.");
    }
  }

  @Nested
  class function {
    @Nested
    class result {
      @ParameterizedTest
      @ArgumentsSource(TestedConcreteTypes.class)
      public void can_declare_concrete_result_type(TestedType type) {
        module(unlines(
            type.declarations(),
            type.name() + " myFunction();"))
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_generic_result_type_when_some_param_has_such_type() {
        module("""
            A testIdentity(A param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_generic_result_type_when_some_param_has_such_core_type() {
        module("""
            A genericResult([A] param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_generic_array_result_type_when_some_param_has_such_type() {
        module("""
            [A] testArrayIdentity(A param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void can_declare_generic_array_result_type_when_some_param_has_such_core_type() {
        module("""
            [A] testArrayIdentity([A] param);
            """)
            .loadsSuccessfully();
      }

      @Test
      public void cannot_declare_generic_result_type_when_no_param_has_such_core_type() {
        module("""
            A genericResult([B] param);
            """)
            .loadsWithError(1, "Undefined generic type 'A'. "
                +
                "Only generic types used in declaration of function parameters can be used here.");
      }

      @Test
      public void cannot_declare_generic_array_result_type_when_no_param_has_such_core_type() {
        module("""
            [A] result([B] param);
            """)
            .loadsWithError(1, "Undefined generic type 'A'. "
                +
                "Only generic types used in declaration of function parameters can be used here.");
      }

      @Test
      public void can_declare_result_type_which_is_supertype_of_function_expression() {
        module("""
            MyStruct {
              String field
            }
            String myFunction() = myStruct("abc");
            """)
            .loadsSuccessfully();
      }

      @Test
      public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_type_is_convertible() {
        module("""
            MyStruct {
              String field
            }
            String myFunction() = myStruct("abc");
            MyStruct result = myFunction();
            """)
            .loadsWithError(5, "`result` has body which type is 'String' and it is not "
                + "convertible to its declared type 'MyStruct'.");
      }
    }

    @Nested
    class parameter {
      @ParameterizedTest
      @ArgumentsSource(TestedTypes.class)
      public void can_declare_any_type(TestedType type) {
        module(unlines(
            type.declarations(),
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
          testedType.declarations(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"))
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(FirstFieldForbiddenTypes.class)
    public void first_field_cannot_declare_following_types(TestedType testedType) {
      TestModuleLoader module = module(unlines(
          testedType.declarations(),
          "MyStruct {",
          "  " + testedType.name() + " field,",
          "}"));
      if (testedType.name().startsWith("[")) {
        module.loadsWithError(3, "First field of struct cannot have array type.");
      } else if (testedType.name().equals("Nothing")) {
        module.loadsWithError(3, "First field of struct cannot have 'Nothing' type.");
      } else {
        module.loadsWithError(3, "Struct field cannot have a generic type.");
      }
    }

    @ParameterizedTest
    @ArgumentsSource(FieldAllowedTypes.class)
    public void non_first_field_can_declare_concrete_type(TestedType testedType) {
      module(unlines(
          testedType.declarations(),
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
          testedType.declarations(),
          "MyStruct {",
          "  String firstField,",
          "  " + testedType.name() + " field,",
          "}"));
      if (testedType.name().startsWith("[")) {
        module.loadsWithError(4, "First field of struct cannot have array type.");
      } else if (testedType.name().equals("Nothing")) {
        module.loadsWithError(4, "First field of struct cannot have 'Nothing' type.");
      } else {
        module.loadsWithError(4, "Struct field cannot have a generic type.");
      }
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
    public void cannot_declare_array_type_which_core_type_encloses_it() {
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

  private static class TestedConcreteTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return CONCRETE_TESTED_TYPES.stream()
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
      return CONCRETE_TESTED_TYPES
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
        NOTHING,
        NOTHING_ARRAY,
        NOTHING_ARRAY2,
        STRING_ARRAY,
        STRING_ARRAY2,
        BLOB_ARRAY,
        BOOL_ARRAY,
        STRUCT_WITH_BLOB_ARRAY,
        STRUCT_WITH_BOOL_ARRAY,
        STRUCT_WITH_STRING_ARRAY,
        BLOB_ARRAY2,
        BOOL_ARRAY2,
        STRING_ARRAY2,
        STRUCT_WITH_BLOB_ARRAY2,
        STRUCT_WITH_BOOL_ARRAY2,
        STRUCT_WITH_STRING_ARRAY2
    );
  }

  private static class FieldAllowedTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      List<TestedType> forbidden = fieldForbiddenTypes();
      return CONCRETE_TESTED_TYPES
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
    return List.of(A);
  }
}
