package org.smoothbuild.lang.base.type;

import static java.util.stream.Collectors.toList;
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

import java.util.ArrayList;
import java.util.List;

public class TestedAssignmentSpec extends TestedAssignment {
  public final boolean allowed;

  public TestedAssignmentSpec(TestedAssignment assignment, boolean allowed) {
    super(assignment.target, assignment.source);
    this.allowed = allowed;
  }

  TestedAssignmentSpec(TestedType target, TestedType source, boolean allowed) {
    super(target, source);
    this.allowed = allowed;
  }

  public static TestedAssignmentSpec illegalAssignment(TestedType target, TestedType source) {
    return new TestedAssignmentSpec(target, source, false);
  }

  public static TestedAssignmentSpec allowedAssignment(TestedType target, TestedType source) {
    return new TestedAssignmentSpec(target, source, true);
  }

  public static List<TestedAssignmentSpec> assignment_test_specs() {
    return List.of(
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

  public static List<TestedAssignmentSpec> parameter_assignment_test_specs() {
    ArrayList<TestedAssignmentSpec> result = new ArrayList<>();
    result.addAll(assignment_without_generics_test_specs());
    result.addAll(parameter_assignment_generic_test_specs());
    return result;
  }

  public static List<TestedAssignmentSpec> assignment_without_generics_test_specs() {
    return assignment_test_specs()
        .stream()
        .filter(a -> !(a.target.type().isGeneric() || a.source.type().isGeneric()))
        .collect(toList());
  }

  private static List<TestedAssignmentSpec> parameter_assignment_generic_test_specs() {
    return List.of(
        allowedAssignment(A, STRING),
        allowedAssignment(A, STRUCT_WITH_STRING),
        allowedAssignment(A, NOTHING),
        allowedAssignment(A, A),
        allowedAssignment(A, B),
        allowedAssignment(A, STRING_ARRAY),
        allowedAssignment(A, STRUCT_WITH_STRING_ARRAY),
        allowedAssignment(A, NOTHING_ARRAY),
        allowedAssignment(A, A_ARRAY),
        allowedAssignment(A, B_ARRAY),
        allowedAssignment(A, STRING_ARRAY2),
        allowedAssignment(A, STRUCT_WITH_STRING_ARRAY2),
        allowedAssignment(A, NOTHING_ARRAY2),
        allowedAssignment(A, A_ARRAY2),
        allowedAssignment(A, B_ARRAY2),

        illegalAssignment(A_ARRAY, STRING),
        illegalAssignment(A_ARRAY, STRUCT_WITH_STRING),
        allowedAssignment(A_ARRAY, NOTHING),
        illegalAssignment(A_ARRAY, A),
        illegalAssignment(A_ARRAY, B),
        allowedAssignment(A_ARRAY, STRING_ARRAY),
        allowedAssignment(A_ARRAY, STRUCT_WITH_STRING_ARRAY),
        allowedAssignment(A_ARRAY, NOTHING_ARRAY),
        allowedAssignment(A_ARRAY, A_ARRAY),
        allowedAssignment(A_ARRAY, B_ARRAY),
        allowedAssignment(A_ARRAY, STRING_ARRAY2),
        allowedAssignment(A_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        allowedAssignment(A_ARRAY, NOTHING_ARRAY2),
        allowedAssignment(A_ARRAY, A_ARRAY2),
        allowedAssignment(A_ARRAY, B_ARRAY2),

        illegalAssignment(A_ARRAY2, STRING),
        illegalAssignment(A_ARRAY2, STRUCT_WITH_STRING),
        allowedAssignment(A_ARRAY2, NOTHING),
        illegalAssignment(A_ARRAY2, A),
        illegalAssignment(A_ARRAY2, B),
        illegalAssignment(A_ARRAY2, STRING_ARRAY),
        illegalAssignment(A_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        allowedAssignment(A_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(A_ARRAY2, A_ARRAY),
        illegalAssignment(A_ARRAY2, B_ARRAY),
        allowedAssignment(A_ARRAY2, STRING_ARRAY2),
        allowedAssignment(A_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        allowedAssignment(A_ARRAY2, NOTHING_ARRAY2),
        allowedAssignment(A_ARRAY2, A_ARRAY2),
        allowedAssignment(A_ARRAY2, B_ARRAY2));
  }
}
