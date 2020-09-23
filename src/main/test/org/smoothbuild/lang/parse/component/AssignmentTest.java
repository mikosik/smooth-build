package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestedType.A;
import static org.smoothbuild.lang.base.type.TestedType.A_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.A_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.B;
import static org.smoothbuild.lang.base.type.TestedType.BLOB;
import static org.smoothbuild.lang.base.type.TestedType.BLOB_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.BLOB_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.BOOL;
import static org.smoothbuild.lang.base.type.TestedType.BOOL_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.BOOL_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.B_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.B_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BOOL;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BOOL_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BOOL_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING_ARRAY2;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedAssignment;
import org.smoothbuild.lang.base.type.TestedType;

public class AssignmentTest {
  @ParameterizedTest
  @MethodSource("assignment_without_generics_test_specs")
  public void value_body_type_must_be_assignable_to_declared_type(AssignmentTestSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        target.name() + " result = " + source.literal() + ";",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "`result` has body which type is " + source.q()
               + " and it is not convertible to its declared type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("assignment_test_specs")
  public void function_body_type_must_be_assignable_to_declared_type(AssignmentTestSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "%s myFunction(%s param, %s probablyGeneric) = param;"
            .formatted(target.name(), source.name(), target.name()),
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "`myFunction` has body which type is " + source.q()
               + " and it is not convertible to its declared type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("assignment_without_generics_test_specs")
  public void argument_type_must_be_assignable_to_assigned_parameter_type(
      AssignmentTestSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "myFunction(" + target.name() + " param) = param;",
        "result = myFunction(" + source.literal() + ");",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(2, "In call to `myFunction`: Cannot assign argument of type "
              + source.q() + " to parameter `param` of type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("assignment_without_generics_test_specs")
  public void argument_type_must_be_assignable_to_assigned_named_parameter_type(
      AssignmentTestSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "myFunction(" + target.name() + " param) = param;",
        "result = myFunction(param=" + source.literal() + ");",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(2, "In call to `myFunction`: Cannot assign argument of type "
              + source.q() + " to parameter `param` of type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("assignment_without_generics_test_specs")
  public void default_value_type_must_be_assignable_to_parameter_type(AssignmentTestSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "myFunction(" + target.name() + " param = " + source.literal() + ") = param; ",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "Parameter `param` is of type " + target.q()
               + " so it cannot have default value of type " + source.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("array_element_assignment_test_specs")
  public void array_literal_element_types_must_be_assignable_to_common_super_type(
      AssignmentTestSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "result = [" + source.literal() + ", " + target.literal() + "];",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1,
              "Array cannot contain elements of incompatible types. First element has type " +
               source.q() + " while element at index 1 has type " + target.q() + ".");
    }
  }

  /**
   * Type compatibility between two array literal elements is satisfied when type of any of those
   * elements is assignable to the type of the other.
   */
  public static Stream<AssignmentTestSpec> array_element_assignment_test_specs() {
    var map = new HashMap<TestedType, Map<TestedType, AssignmentTestSpec>>();
    assignment_without_generics_test_specs().forEach(spec -> {
      map.computeIfAbsent(spec.source, testedType -> new HashMap<>());
      map.get(spec.source).put(spec.target, spec);
    });

    var result = new ArrayList<AssignmentTestSpec>();
    assignment_without_generics_test_specs().forEach(spec -> {
      AssignmentTestSpec reversed = map.get(spec.target).get(spec.source);
      boolean reversedAllowed = reversed != null && reversed.allowed;
      if (!spec.allowed && reversedAllowed) {
        result.add(new AssignmentTestSpec(spec, true));
      } else {
        result.add(spec);
      }
    });
    return result.stream();
  }

  public static Stream<AssignmentTestSpec> assignment_without_generics_test_specs() {
    return assignment_test_specs()
        .filter(a -> !(a.target.type().isGeneric() || a.source.type().isGeneric()));
  }

  public static Stream<AssignmentTestSpec> assignment_test_specs() {
    return Stream.of(
        // A
        illegalAssignment(A, BLOB),
        illegalAssignment(A, BOOL),
        allowedAssignment(A, NOTHING),
        illegalAssignment(A, STRING),
        illegalAssignment(A, STRUCT_WITH_STRING),
        allowedAssignment(A, A),
        illegalAssignment(A, B),

        illegalAssignment(A, BLOB_ARRAY),
        illegalAssignment(A, BOOL_ARRAY),
        illegalAssignment(A, NOTHING_ARRAY),
        illegalAssignment(A, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(A, STRING_ARRAY),
        illegalAssignment(A, A_ARRAY),
        illegalAssignment(A, B_ARRAY),

        illegalAssignment(A, BLOB_ARRAY2),
        illegalAssignment(A, BOOL_ARRAY2),
        illegalAssignment(A, NOTHING_ARRAY2),
        illegalAssignment(A, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(A, STRING_ARRAY2),
        illegalAssignment(A, A_ARRAY2),
        illegalAssignment(A, B_ARRAY2),

        // Blob
        allowedAssignment(BLOB, BLOB),
        illegalAssignment(BLOB, BOOL),
        allowedAssignment(BLOB, NOTHING),
        illegalAssignment(BLOB, STRING),
        illegalAssignment(BLOB, STRUCT_WITH_STRING),
        illegalAssignment(BLOB, A),

        illegalAssignment(BLOB, BLOB_ARRAY),
        illegalAssignment(BLOB, BOOL_ARRAY),
        illegalAssignment(BLOB, NOTHING_ARRAY),
        illegalAssignment(BLOB, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BLOB, STRING_ARRAY),
        illegalAssignment(BLOB, A_ARRAY),

        illegalAssignment(BLOB, BLOB_ARRAY2),
        illegalAssignment(BLOB, BOOL_ARRAY2),
        illegalAssignment(BLOB, NOTHING_ARRAY2),
        illegalAssignment(BLOB, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BLOB, STRING_ARRAY2),
        illegalAssignment(BLOB, A_ARRAY2),

        // Bool
        illegalAssignment(BOOL, BLOB),
        allowedAssignment(BOOL, BOOL),
        allowedAssignment(BOOL, NOTHING),
        illegalAssignment(BOOL, STRING),
        illegalAssignment(BOOL, STRUCT_WITH_STRING),
        illegalAssignment(BOOL, A),

        illegalAssignment(BOOL, BLOB_ARRAY),
        illegalAssignment(BOOL, BOOL_ARRAY),
        illegalAssignment(BOOL, NOTHING_ARRAY),
        illegalAssignment(BOOL, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BOOL, STRING_ARRAY),
        illegalAssignment(BOOL, A_ARRAY),

        illegalAssignment(BOOL, BLOB_ARRAY2),
        illegalAssignment(BOOL, BOOL_ARRAY2),
        illegalAssignment(BOOL, NOTHING_ARRAY2),
        illegalAssignment(BOOL, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BOOL, STRING_ARRAY2),
        illegalAssignment(BOOL, A_ARRAY2),

        // Nothing
        illegalAssignment(NOTHING, BLOB),
        illegalAssignment(NOTHING, BOOL),
        allowedAssignment(NOTHING, NOTHING),
        illegalAssignment(NOTHING, STRING),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING),
        illegalAssignment(NOTHING, A),

        illegalAssignment(NOTHING, BLOB_ARRAY),
        illegalAssignment(NOTHING, BOOL_ARRAY),
        illegalAssignment(NOTHING, NOTHING_ARRAY),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(NOTHING, STRING_ARRAY),
        illegalAssignment(NOTHING, A_ARRAY),

        illegalAssignment(NOTHING, BLOB_ARRAY2),
        illegalAssignment(NOTHING, BOOL_ARRAY2),
        illegalAssignment(NOTHING, NOTHING_ARRAY2),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(NOTHING, STRING_ARRAY2),
        illegalAssignment(NOTHING, A_ARRAY2),

        // String
        illegalAssignment(STRING, BLOB),
        illegalAssignment(STRING, BOOL),
        allowedAssignment(STRING, NOTHING),
        allowedAssignment(STRING, STRING),
        allowedAssignment(STRING, STRUCT_WITH_STRING),
        illegalAssignment(STRING, A),

        illegalAssignment(STRING, BLOB_ARRAY),
        illegalAssignment(STRING, BOOL_ARRAY),
        illegalAssignment(STRING, NOTHING_ARRAY),
        illegalAssignment(STRING, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRING, STRING_ARRAY),
        illegalAssignment(STRING, A_ARRAY),

        illegalAssignment(STRING, BLOB_ARRAY2),
        illegalAssignment(STRING, BOOL_ARRAY2),
        illegalAssignment(STRING, NOTHING_ARRAY2),
        illegalAssignment(STRING, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRING, STRING_ARRAY2),
        illegalAssignment(STRING, A_ARRAY2),

        // Struct
        illegalAssignment(STRUCT_WITH_STRING, BLOB),
        illegalAssignment(STRUCT_WITH_STRING, BOOL),
        allowedAssignment(STRUCT_WITH_STRING, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING, STRING),
        allowedAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING),
        illegalAssignment(STRUCT_WITH_STRING, A),

        illegalAssignment(STRUCT_WITH_STRING, BLOB_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, NOTHING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, A_ARRAY),

        illegalAssignment(STRUCT_WITH_STRING, BLOB_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, BOOL_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, NOTHING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, A_ARRAY2),

        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL_ARRAY2),

        // [A]
        illegalAssignment(A_ARRAY, BLOB),
        illegalAssignment(A_ARRAY, BOOL),
        allowedAssignment(A_ARRAY, NOTHING),
        illegalAssignment(A_ARRAY, STRING),
        illegalAssignment(A_ARRAY, STRUCT_WITH_STRING),
        illegalAssignment(A_ARRAY, A),
        illegalAssignment(A_ARRAY, B),

        illegalAssignment(A_ARRAY, BLOB_ARRAY),
        illegalAssignment(A_ARRAY, BOOL_ARRAY),
        allowedAssignment(A_ARRAY, NOTHING_ARRAY),
        illegalAssignment(A_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(A_ARRAY, STRING_ARRAY),
        allowedAssignment(A_ARRAY, A_ARRAY),
        illegalAssignment(A_ARRAY, B_ARRAY),

        illegalAssignment(A_ARRAY, BLOB_ARRAY2),
        illegalAssignment(A_ARRAY, BOOL_ARRAY2),
        illegalAssignment(A_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(A_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(A_ARRAY, STRING_ARRAY2),
        illegalAssignment(A_ARRAY, A_ARRAY2),
        illegalAssignment(A_ARRAY, B_ARRAY2),

        // [Blob]
        illegalAssignment(BLOB_ARRAY, BLOB),
        illegalAssignment(BLOB_ARRAY, BOOL),
        allowedAssignment(BLOB_ARRAY, NOTHING),
        illegalAssignment(BLOB_ARRAY, STRING),
        illegalAssignment(BLOB_ARRAY, STRUCT_WITH_STRING),
        illegalAssignment(BLOB_ARRAY, A),

        allowedAssignment(BLOB_ARRAY, BLOB_ARRAY),
        illegalAssignment(BLOB_ARRAY, BOOL_ARRAY),
        allowedAssignment(BLOB_ARRAY, NOTHING_ARRAY),
        illegalAssignment(BLOB_ARRAY, STRING_ARRAY),
        illegalAssignment(BLOB_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BLOB_ARRAY, A_ARRAY),

        illegalAssignment(BLOB_ARRAY, BLOB_ARRAY2),
        illegalAssignment(BLOB_ARRAY, BOOL_ARRAY2),
        illegalAssignment(BLOB_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(BLOB_ARRAY, STRING_ARRAY2),
        illegalAssignment(BLOB_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BLOB_ARRAY, A_ARRAY2),

        // [Bool]
        illegalAssignment(BOOL_ARRAY, BLOB),
        illegalAssignment(BOOL_ARRAY, BOOL),
        allowedAssignment(BOOL_ARRAY, NOTHING),
        illegalAssignment(BOOL_ARRAY, STRING),
        illegalAssignment(BOOL_ARRAY, STRUCT_WITH_STRING),
        illegalAssignment(BOOL_ARRAY, A),

        illegalAssignment(BOOL_ARRAY, BLOB_ARRAY),
        allowedAssignment(BOOL_ARRAY, BOOL_ARRAY),
        allowedAssignment(BOOL_ARRAY, NOTHING_ARRAY),
        illegalAssignment(BOOL_ARRAY, STRING_ARRAY),
        illegalAssignment(BOOL_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BOOL_ARRAY, A_ARRAY),

        illegalAssignment(BOOL_ARRAY, BLOB_ARRAY2),
        illegalAssignment(BOOL_ARRAY, BOOL_ARRAY2),
        illegalAssignment(BOOL_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(BOOL_ARRAY, STRING_ARRAY2),
        illegalAssignment(BOOL_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BOOL_ARRAY, A_ARRAY2),

        // [Nothing]
        illegalAssignment(NOTHING_ARRAY, BLOB),
        illegalAssignment(NOTHING_ARRAY, BOOL),
        allowedAssignment(NOTHING_ARRAY, NOTHING),
        illegalAssignment(NOTHING_ARRAY, STRING),
        illegalAssignment(NOTHING_ARRAY, STRUCT_WITH_STRING),
        illegalAssignment(NOTHING_ARRAY, A),

        illegalAssignment(NOTHING_ARRAY, BLOB_ARRAY),
        illegalAssignment(NOTHING_ARRAY, BOOL_ARRAY),
        allowedAssignment(NOTHING_ARRAY, NOTHING_ARRAY),
        illegalAssignment(NOTHING_ARRAY, STRING_ARRAY),
        illegalAssignment(NOTHING_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(NOTHING_ARRAY, A_ARRAY),

        illegalAssignment(NOTHING_ARRAY, BLOB_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, BOOL_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, STRING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, A_ARRAY2),

        // [String]
        illegalAssignment(STRING_ARRAY, BLOB),
        illegalAssignment(STRING_ARRAY, BOOL),
        allowedAssignment(STRING_ARRAY, NOTHING),
        illegalAssignment(STRING_ARRAY, STRING),
        illegalAssignment(STRING_ARRAY, STRUCT_WITH_STRING),
        illegalAssignment(STRING_ARRAY, A),

        illegalAssignment(STRING_ARRAY, BLOB_ARRAY),
        illegalAssignment(STRING_ARRAY, BOOL_ARRAY),
        allowedAssignment(STRING_ARRAY, NOTHING_ARRAY),
        allowedAssignment(STRING_ARRAY, STRING_ARRAY),
        allowedAssignment(STRING_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRING_ARRAY, A_ARRAY),

        illegalAssignment(STRING_ARRAY, BLOB_ARRAY2),
        illegalAssignment(STRING_ARRAY, BOOL_ARRAY2),
        illegalAssignment(STRING_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(STRING_ARRAY, STRING_ARRAY2),
        illegalAssignment(STRING_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRING_ARRAY, A_ARRAY2),

        // [Struct]
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BLOB),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BOOL),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, A),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BLOB_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BOOL_ARRAY),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY, NOTHING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRING_ARRAY),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, A_ARRAY),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BLOB_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BOOL_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, A_ARRAY2),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_BOOL),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_BOOL_ARRAY2),

        // [[A]]
        illegalAssignment(A_ARRAY2, BLOB),
        illegalAssignment(A_ARRAY2, BOOL),
        allowedAssignment(A_ARRAY2, NOTHING),
        illegalAssignment(A_ARRAY2, STRING),
        illegalAssignment(A_ARRAY2, STRUCT_WITH_STRING),
        illegalAssignment(A_ARRAY2, A),
        illegalAssignment(A_ARRAY2, B),

        illegalAssignment(A_ARRAY2, BLOB_ARRAY),
        illegalAssignment(A_ARRAY2, BOOL_ARRAY),
        allowedAssignment(A_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(A_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(A_ARRAY2, STRING_ARRAY),
        illegalAssignment(A_ARRAY2, A_ARRAY),
        illegalAssignment(A_ARRAY2, B_ARRAY),

        illegalAssignment(A_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(A_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(A_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(A_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(A_ARRAY2, STRING_ARRAY2),
        allowedAssignment(A_ARRAY2, A_ARRAY2),
        illegalAssignment(A_ARRAY2, B_ARRAY2),

        // [[Blob]]
        illegalAssignment(BLOB_ARRAY2, BLOB),
        illegalAssignment(BLOB_ARRAY2, BOOL),
        allowedAssignment(BLOB_ARRAY2, NOTHING),
        illegalAssignment(BLOB_ARRAY2, STRING),
        illegalAssignment(BLOB_ARRAY2, STRUCT_WITH_STRING),
        illegalAssignment(BLOB_ARRAY2, A),

        illegalAssignment(BLOB_ARRAY2, BLOB_ARRAY),
        illegalAssignment(BLOB_ARRAY2, BOOL_ARRAY),
        allowedAssignment(BLOB_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(BLOB_ARRAY2, STRING_ARRAY),
        illegalAssignment(BLOB_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BLOB_ARRAY2, A_ARRAY),

        allowedAssignment(BLOB_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(BLOB_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, STRING_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, A_ARRAY2),

        // [[Bool]]
        illegalAssignment(BOOL_ARRAY2, BLOB),
        illegalAssignment(BOOL_ARRAY2, BOOL),
        allowedAssignment(BOOL_ARRAY2, NOTHING),
        illegalAssignment(BOOL_ARRAY2, STRING),
        illegalAssignment(BOOL_ARRAY2, STRUCT_WITH_STRING),
        illegalAssignment(BOOL_ARRAY2, A),

        illegalAssignment(BOOL_ARRAY2, BLOB_ARRAY),
        illegalAssignment(BOOL_ARRAY2, BOOL_ARRAY),
        allowedAssignment(BOOL_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(BOOL_ARRAY2, STRING_ARRAY),
        illegalAssignment(BOOL_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BOOL_ARRAY2, A_ARRAY),

        illegalAssignment(BOOL_ARRAY2, BLOB_ARRAY2),
        allowedAssignment(BOOL_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(BOOL_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(BOOL_ARRAY2, STRING_ARRAY2),
        illegalAssignment(BOOL_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BOOL_ARRAY2, A_ARRAY2),

        // [[Nothing]]
        illegalAssignment(NOTHING_ARRAY2, BLOB),
        illegalAssignment(NOTHING_ARRAY2, BOOL),
        allowedAssignment(NOTHING_ARRAY2, NOTHING),
        illegalAssignment(NOTHING_ARRAY2, STRING),
        illegalAssignment(NOTHING_ARRAY2, STRUCT_WITH_STRING),
        illegalAssignment(NOTHING_ARRAY2, A),

        illegalAssignment(NOTHING_ARRAY2, BLOB_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, BOOL_ARRAY),
        allowedAssignment(NOTHING_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, STRING_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, A_ARRAY),

        illegalAssignment(NOTHING_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(NOTHING_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, STRING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, A_ARRAY2),

        // [[String]]
        illegalAssignment(STRING_ARRAY2, BLOB),
        illegalAssignment(STRING_ARRAY2, BOOL),
        allowedAssignment(STRING_ARRAY2, NOTHING),
        illegalAssignment(STRING_ARRAY2, STRING),
        illegalAssignment(STRING_ARRAY2, STRUCT_WITH_STRING),
        illegalAssignment(STRING_ARRAY2, A),

        illegalAssignment(STRING_ARRAY2, BLOB_ARRAY),
        illegalAssignment(STRING_ARRAY2, BOOL_ARRAY),
        allowedAssignment(STRING_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(STRING_ARRAY2, STRING_ARRAY),
        illegalAssignment(STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRING_ARRAY2, A_ARRAY),

        illegalAssignment(STRING_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(STRING_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(STRING_ARRAY2, NOTHING_ARRAY2),
        allowedAssignment(STRING_ARRAY2, STRING_ARRAY2),
        allowedAssignment(STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRING_ARRAY2, A_ARRAY2),

        // [[Struct]]
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BLOB),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BOOL),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, A),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BLOB_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BOOL_ARRAY),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, A_ARRAY),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRING_ARRAY2),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, A_ARRAY2),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_BOOL),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_BOOL_ARRAY2)
    );
  }

  public static AssignmentTestSpec allowedAssignment(TestedType target, TestedType source) {
    return new AssignmentTestSpec(target, source, true);
  }

  public static AssignmentTestSpec illegalAssignment(TestedType target, TestedType source) {
    return new AssignmentTestSpec(target, source, false);
  }

  public static class AssignmentTestSpec extends TestedAssignment {
    public final boolean allowed;

    private AssignmentTestSpec(TestedAssignment assignment, boolean allowed) {
      super(assignment.target, assignment.source);
      this.allowed = allowed;
    }

    private AssignmentTestSpec(TestedType target, TestedType source, boolean allowed) {
      super(target, source);
      this.allowed = allowed;
    }
  }
}
