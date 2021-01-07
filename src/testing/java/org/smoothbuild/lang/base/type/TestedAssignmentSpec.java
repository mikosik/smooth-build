package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.TestedType.A;
import static org.smoothbuild.lang.base.type.TestedType.ANY;
import static org.smoothbuild.lang.base.type.TestedType.B;
import static org.smoothbuild.lang.base.type.TestedType.BLOB;
import static org.smoothbuild.lang.base.type.TestedType.BOOL;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING;
import static org.smoothbuild.lang.base.type.TestedType.STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_BOOL;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING;
import static org.smoothbuild.lang.base.type.TestedType.a;

import java.util.ArrayList;
import java.util.List;

public record TestedAssignmentSpec(TestedAssignment assignment, boolean allowed) {
  TestedAssignmentSpec(TestedType target, TestedType source, boolean allowed) {
    this(new TestedAssignment(target, source), allowed);
  }

  public TestedType source() {
    return assignment.source();
  }

  public TestedType target() {
    return assignment.target();
  }

  public String declarations() {
    return assignment.declarations();
  }

  @Override
  public String toString() {
    return assignment.toString() + " :" + (allowed ? "allowed" : "illegal");
  }

  public static TestedAssignmentSpec illegalAssignment(TestedType target, TestedType source) {
    return new TestedAssignmentSpec(target, source, false);
  }

  public static TestedAssignmentSpec allowedAssignment(TestedType target, TestedType source) {
    return new TestedAssignmentSpec(target, source, true);
  }

  public static List<TestedAssignmentSpec> assignment_test_specs() {
    var result = new ArrayList<TestedAssignmentSpec>();
    result.addAll(assignmentsCommonForNormalCaseAndParameterAssignment());
    result.addAll(testSpecSpecificForNormalAssignment());
    return result;
  }

  public static List<TestedAssignmentSpec> parameter_assignment_test_specs() {
    var result = new ArrayList<TestedAssignmentSpec>();
    result.addAll(assignmentsCommonForNormalCaseAndParameterAssignment());
    result.addAll(testSpecsSpecificForParameterAssignment());
    return result;
  }

  public static List<TestedAssignmentSpec> assignmentsCommonForNormalCaseAndParameterAssignment() {
    return List.of(
        // Any
        allowedAssignment(ANY, ANY),
        allowedAssignment(ANY, BLOB),
        allowedAssignment(ANY, BOOL),
        allowedAssignment(ANY, NOTHING),
        allowedAssignment(ANY, STRING),
        allowedAssignment(ANY, STRUCT_WITH_STRING),
        allowedAssignment(ANY, A),

        allowedAssignment(ANY, a(ANY)),
        allowedAssignment(ANY, a(BLOB)),
        allowedAssignment(ANY, a(BOOL)),
        allowedAssignment(ANY, a(NOTHING)),
        allowedAssignment(ANY, a(STRUCT_WITH_STRING)),
        allowedAssignment(ANY, a(STRING)),
        allowedAssignment(ANY, a(A)),

        allowedAssignment(ANY, a(a(ANY))),
        allowedAssignment(ANY, a(a(BLOB))),
        allowedAssignment(ANY, a(a(BOOL))),
        allowedAssignment(ANY, a(a(NOTHING))),
        allowedAssignment(ANY, a(a(STRUCT_WITH_STRING))),
        allowedAssignment(ANY, a(a(STRING))),
        allowedAssignment(ANY, a(a(A))),

        // Blob
        illegalAssignment(BLOB, ANY),
        allowedAssignment(BLOB, BLOB),
        illegalAssignment(BLOB, BOOL),
        allowedAssignment(BLOB, NOTHING),
        illegalAssignment(BLOB, STRING),
        illegalAssignment(BLOB, STRUCT_WITH_STRING),
        illegalAssignment(BLOB, A),

        illegalAssignment(BLOB, a(ANY)),
        illegalAssignment(BLOB, a(BLOB)),
        illegalAssignment(BLOB, a(BOOL)),
        illegalAssignment(BLOB, a(NOTHING)),
        illegalAssignment(BLOB, a(STRUCT_WITH_STRING)),
        illegalAssignment(BLOB, a(STRING)),
        illegalAssignment(BLOB, a(A)),

        illegalAssignment(BLOB, a(a(ANY))),
        illegalAssignment(BLOB, a(a(BLOB))),
        illegalAssignment(BLOB, a(a(BOOL))),
        illegalAssignment(BLOB, a(a(NOTHING))),
        illegalAssignment(BLOB, a(a(STRUCT_WITH_STRING))),
        illegalAssignment(BLOB, a(a(STRING))),
        illegalAssignment(BLOB, a(a(A))),

        // Bool
        illegalAssignment(BOOL, ANY),
        illegalAssignment(BOOL, BLOB),
        allowedAssignment(BOOL, BOOL),
        allowedAssignment(BOOL, NOTHING),
        illegalAssignment(BOOL, STRING),
        illegalAssignment(BOOL, STRUCT_WITH_STRING),
        illegalAssignment(BOOL, A),

        illegalAssignment(BOOL, a(ANY)),
        illegalAssignment(BOOL, a(BLOB)),
        illegalAssignment(BOOL, a(BOOL)),
        illegalAssignment(BOOL, a(NOTHING)),
        illegalAssignment(BOOL, a(STRUCT_WITH_STRING)),
        illegalAssignment(BOOL, a(STRING)),
        illegalAssignment(BOOL, a(A)),

        illegalAssignment(BOOL, a(a(ANY))),
        illegalAssignment(BOOL, a(a(BLOB))),
        illegalAssignment(BOOL, a(a(BOOL))),
        illegalAssignment(BOOL, a(a(NOTHING))),
        illegalAssignment(BOOL, a(a(STRUCT_WITH_STRING))),
        illegalAssignment(BOOL, a(a(STRING))),
        illegalAssignment(BOOL, a(a(A))),

        // Nothing
        illegalAssignment(NOTHING, ANY),
        illegalAssignment(NOTHING, BLOB),
        illegalAssignment(NOTHING, BOOL),
        allowedAssignment(NOTHING, NOTHING),
        illegalAssignment(NOTHING, STRING),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING),
        illegalAssignment(NOTHING, A),

        illegalAssignment(NOTHING, a(ANY)),
        illegalAssignment(NOTHING, a(BLOB)),
        illegalAssignment(NOTHING, a(BOOL)),
        illegalAssignment(NOTHING, a(NOTHING)),
        illegalAssignment(NOTHING, a(STRUCT_WITH_STRING)),
        illegalAssignment(NOTHING, a(STRING)),
        illegalAssignment(NOTHING, a(A)),

        illegalAssignment(NOTHING, a(a(ANY))),
        illegalAssignment(NOTHING, a(a(BLOB))),
        illegalAssignment(NOTHING, a(a(BOOL))),
        illegalAssignment(NOTHING, a(a(NOTHING))),
        illegalAssignment(NOTHING, a(a(STRUCT_WITH_STRING))),
        illegalAssignment(NOTHING, a(a(STRING))),
        illegalAssignment(NOTHING, a(a(A))),

        // String
        illegalAssignment(STRING, ANY),
        illegalAssignment(STRING, BLOB),
        illegalAssignment(STRING, BOOL),
        allowedAssignment(STRING, NOTHING),
        allowedAssignment(STRING, STRING),
        illegalAssignment(STRING, STRUCT_WITH_STRING),
        illegalAssignment(STRING, A),

        illegalAssignment(STRING, a(ANY)),
        illegalAssignment(STRING, a(BLOB)),
        illegalAssignment(STRING, a(BOOL)),
        illegalAssignment(STRING, a(NOTHING)),
        illegalAssignment(STRING, a(STRUCT_WITH_STRING)),
        illegalAssignment(STRING, a(STRING)),
        illegalAssignment(STRING, a(A)),

        illegalAssignment(STRING, a(a(ANY))),
        illegalAssignment(STRING, a(a(BLOB))),
        illegalAssignment(STRING, a(a(BOOL))),
        illegalAssignment(STRING, a(a(NOTHING))),
        illegalAssignment(STRING, a(a(STRUCT_WITH_STRING))),
        illegalAssignment(STRING, a(a(STRING))),
        illegalAssignment(STRING, a(a(A))),

        // Struct
        illegalAssignment(STRUCT_WITH_STRING, ANY),
        illegalAssignment(STRUCT_WITH_STRING, BLOB),
        illegalAssignment(STRUCT_WITH_STRING, BOOL),
        allowedAssignment(STRUCT_WITH_STRING, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING, STRING),
        allowedAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING),
        illegalAssignment(STRUCT_WITH_STRING, A),

        illegalAssignment(STRUCT_WITH_STRING, a(ANY)),
        illegalAssignment(STRUCT_WITH_STRING, a(BLOB)),
        illegalAssignment(STRUCT_WITH_STRING, a(BOOL)),
        illegalAssignment(STRUCT_WITH_STRING, a(NOTHING)),
        illegalAssignment(STRUCT_WITH_STRING, a(STRUCT_WITH_STRING)),
        illegalAssignment(STRUCT_WITH_STRING, a(STRING)),
        illegalAssignment(STRUCT_WITH_STRING, a(A)),

        illegalAssignment(STRUCT_WITH_STRING, a(a(ANY))),
        illegalAssignment(STRUCT_WITH_STRING, a(a(BLOB))),
        illegalAssignment(STRUCT_WITH_STRING, a(a(BOOL))),
        illegalAssignment(STRUCT_WITH_STRING, a(a(NOTHING))),
        illegalAssignment(STRUCT_WITH_STRING, a(a(STRUCT_WITH_STRING))),
        illegalAssignment(STRUCT_WITH_STRING, a(a(STRING))),
        illegalAssignment(STRUCT_WITH_STRING, a(a(A))),

        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL),
        illegalAssignment(STRUCT_WITH_STRING, a(STRUCT_WITH_BOOL)),
        illegalAssignment(STRUCT_WITH_STRING, a(a(STRUCT_WITH_BOOL))),

        // [Any]
        illegalAssignment(a(ANY), ANY),
        illegalAssignment(a(ANY), BLOB),
        illegalAssignment(a(ANY), BOOL),
        allowedAssignment(a(ANY), NOTHING),
        illegalAssignment(a(ANY), STRING),
        illegalAssignment(a(ANY), STRUCT_WITH_STRING),
        illegalAssignment(a(ANY), A),

        allowedAssignment(a(ANY), a(ANY)),
        allowedAssignment(a(ANY), a(BLOB)),
        allowedAssignment(a(ANY), a(BOOL)),
        allowedAssignment(a(ANY), a(NOTHING)),
        allowedAssignment(a(ANY), a(STRING)),
        allowedAssignment(a(ANY), a(STRUCT_WITH_STRING)),
        allowedAssignment(a(ANY), a(A)),

        allowedAssignment(a(ANY), a(a(ANY))),
        allowedAssignment(a(ANY), a(a(BLOB))),
        allowedAssignment(a(ANY), a(a(BOOL))),
        allowedAssignment(a(ANY), a(a(NOTHING))),
        allowedAssignment(a(ANY), a(a(STRING))),
        allowedAssignment(a(ANY), a(a(STRUCT_WITH_STRING))),
        allowedAssignment(a(ANY), a(a(A))),

        // [Blob]
        illegalAssignment(a(BLOB), ANY),
        illegalAssignment(a(BLOB), BLOB),
        illegalAssignment(a(BLOB), BOOL),
        allowedAssignment(a(BLOB), NOTHING),
        illegalAssignment(a(BLOB), STRING),
        illegalAssignment(a(BLOB), STRUCT_WITH_STRING),
        illegalAssignment(a(BLOB), A),

        illegalAssignment(a(BLOB), a(ANY)),
        allowedAssignment(a(BLOB), a(BLOB)),
        illegalAssignment(a(BLOB), a(BOOL)),
        allowedAssignment(a(BLOB), a(NOTHING)),
        illegalAssignment(a(BLOB), a(STRING)),
        illegalAssignment(a(BLOB), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(BLOB), a(A)),

        illegalAssignment(a(BLOB), a(a(ANY))),
        illegalAssignment(a(BLOB), a(a(BLOB))),
        illegalAssignment(a(BLOB), a(a(BOOL))),
        illegalAssignment(a(BLOB), a(a(NOTHING))),
        illegalAssignment(a(BLOB), a(a(STRING))),
        illegalAssignment(a(BLOB), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(BLOB), a(a(A))),

        // [Bool]
        illegalAssignment(a(BOOL), ANY),
        illegalAssignment(a(BOOL), BLOB),
        illegalAssignment(a(BOOL), BOOL),
        allowedAssignment(a(BOOL), NOTHING),
        illegalAssignment(a(BOOL), STRING),
        illegalAssignment(a(BOOL), STRUCT_WITH_STRING),
        illegalAssignment(a(BOOL), A),

        illegalAssignment(a(BOOL), a(ANY)),
        illegalAssignment(a(BOOL), a(BLOB)),
        allowedAssignment(a(BOOL), a(BOOL)),
        allowedAssignment(a(BOOL), a(NOTHING)),
        illegalAssignment(a(BOOL), a(STRING)),
        illegalAssignment(a(BOOL), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(BOOL), a(A)),

        illegalAssignment(a(BOOL), a(a(ANY))),
        illegalAssignment(a(BOOL), a(a(BLOB))),
        illegalAssignment(a(BOOL), a(a(BOOL))),
        illegalAssignment(a(BOOL), a(a(NOTHING))),
        illegalAssignment(a(BOOL), a(a(STRING))),
        illegalAssignment(a(BOOL), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(BOOL), a(a(A))),

        // [Nothing]
        illegalAssignment(a(NOTHING), ANY),
        illegalAssignment(a(NOTHING), BLOB),
        illegalAssignment(a(NOTHING), BOOL),
        allowedAssignment(a(NOTHING), NOTHING),
        illegalAssignment(a(NOTHING), STRING),
        illegalAssignment(a(NOTHING), STRUCT_WITH_STRING),
        illegalAssignment(a(NOTHING), A),

        illegalAssignment(a(NOTHING), a(ANY)),
        illegalAssignment(a(NOTHING), a(BLOB)),
        illegalAssignment(a(NOTHING), a(BOOL)),
        allowedAssignment(a(NOTHING), a(NOTHING)),
        illegalAssignment(a(NOTHING), a(STRING)),
        illegalAssignment(a(NOTHING), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(NOTHING), a(A)),

        illegalAssignment(a(NOTHING), a(a(ANY))),
        illegalAssignment(a(NOTHING), a(a(BLOB))),
        illegalAssignment(a(NOTHING), a(a(BOOL))),
        illegalAssignment(a(NOTHING), a(a(NOTHING))),
        illegalAssignment(a(NOTHING), a(a(STRING))),
        illegalAssignment(a(NOTHING), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(NOTHING), a(a(A))),

        // [String]
        illegalAssignment(a(STRING), ANY),
        illegalAssignment(a(STRING), BLOB),
        illegalAssignment(a(STRING), BOOL),
        allowedAssignment(a(STRING), NOTHING),
        illegalAssignment(a(STRING), STRING),
        illegalAssignment(a(STRING), STRUCT_WITH_STRING),
        illegalAssignment(a(STRING), A),

        illegalAssignment(a(STRING), a(ANY)),
        illegalAssignment(a(STRING), a(BLOB)),
        illegalAssignment(a(STRING), a(BOOL)),
        allowedAssignment(a(STRING), a(NOTHING)),
        allowedAssignment(a(STRING), a(STRING)),
        illegalAssignment(a(STRING), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(STRING), a(A)),

        illegalAssignment(a(STRING), a(a(ANY))),
        illegalAssignment(a(STRING), a(a(BLOB))),
        illegalAssignment(a(STRING), a(a(BOOL))),
        illegalAssignment(a(STRING), a(a(NOTHING))),
        illegalAssignment(a(STRING), a(a(STRING))),
        illegalAssignment(a(STRING), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(STRING), a(a(A))),

        // [Struct]
        illegalAssignment(a(STRUCT_WITH_STRING), ANY),
        illegalAssignment(a(STRUCT_WITH_STRING), BLOB),
        illegalAssignment(a(STRUCT_WITH_STRING), BOOL),
        allowedAssignment(a(STRUCT_WITH_STRING), NOTHING),
        illegalAssignment(a(STRUCT_WITH_STRING), STRING),
        illegalAssignment(a(STRUCT_WITH_STRING), STRUCT_WITH_STRING),
        illegalAssignment(a(STRUCT_WITH_STRING), A),

        illegalAssignment(a(STRUCT_WITH_STRING), a(ANY)),
        illegalAssignment(a(STRUCT_WITH_STRING), a(BLOB)),
        illegalAssignment(a(STRUCT_WITH_STRING), a(BOOL)),
        allowedAssignment(a(STRUCT_WITH_STRING), a(NOTHING)),
        illegalAssignment(a(STRUCT_WITH_STRING), a(STRING)),
        allowedAssignment(a(STRUCT_WITH_STRING), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(STRUCT_WITH_STRING), a(A)),

        illegalAssignment(a(STRUCT_WITH_STRING), a(a(ANY))),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(BLOB))),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(BOOL))),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(NOTHING))),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(STRING))),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(A))),

        illegalAssignment(a(STRUCT_WITH_STRING), STRUCT_WITH_BOOL),
        illegalAssignment(a(STRUCT_WITH_STRING), a(STRUCT_WITH_BOOL)),
        illegalAssignment(a(STRUCT_WITH_STRING), a(a(STRUCT_WITH_BOOL))),

        // [[Any]]
        illegalAssignment(a(a(ANY)), ANY),
        illegalAssignment(a(a(ANY)), BLOB),
        illegalAssignment(a(a(ANY)), BOOL),
        allowedAssignment(a(a(ANY)), NOTHING),
        illegalAssignment(a(a(ANY)), STRING),
        illegalAssignment(a(a(ANY)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(ANY)), A),

        illegalAssignment(a(a(ANY)), a(ANY)),
        illegalAssignment(a(a(ANY)), a(BLOB)),
        illegalAssignment(a(a(ANY)), a(BOOL)),
        allowedAssignment(a(a(ANY)), a(NOTHING)),
        illegalAssignment(a(a(ANY)), a(STRING)),
        illegalAssignment(a(a(ANY)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(ANY)), a(A)),

        allowedAssignment(a(a(ANY)), a(a(ANY))),
        allowedAssignment(a(a(ANY)), a(a(BLOB))),
        allowedAssignment(a(a(ANY)), a(a(BOOL))),
        allowedAssignment(a(a(ANY)), a(a(NOTHING))),
        allowedAssignment(a(a(ANY)), a(a(STRING))),
        allowedAssignment(a(a(ANY)), a(a(STRUCT_WITH_STRING))),
        allowedAssignment(a(a(ANY)), a(a(A))),

        // [[Blob]]
        illegalAssignment(a(a(BLOB)), ANY),
        illegalAssignment(a(a(BLOB)), BLOB),
        illegalAssignment(a(a(BLOB)), BOOL),
        allowedAssignment(a(a(BLOB)), NOTHING),
        illegalAssignment(a(a(BLOB)), STRING),
        illegalAssignment(a(a(BLOB)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(BLOB)), A),

        illegalAssignment(a(a(BLOB)), a(ANY)),
        illegalAssignment(a(a(BLOB)), a(BLOB)),
        illegalAssignment(a(a(BLOB)), a(BOOL)),
        allowedAssignment(a(a(BLOB)), a(NOTHING)),
        illegalAssignment(a(a(BLOB)), a(STRING)),
        illegalAssignment(a(a(BLOB)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(BLOB)), a(A)),

        illegalAssignment(a(a(BLOB)), a(a(ANY))),
        allowedAssignment(a(a(BLOB)), a(a(BLOB))),
        illegalAssignment(a(a(BLOB)), a(a(BOOL))),
        allowedAssignment(a(a(BLOB)), a(a(NOTHING))),
        illegalAssignment(a(a(BLOB)), a(a(STRING))),
        illegalAssignment(a(a(BLOB)), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(a(BLOB)), a(a(A))),

        // [[Bool]]
        illegalAssignment(a(a(BOOL)), ANY),
        illegalAssignment(a(a(BOOL)), BLOB),
        illegalAssignment(a(a(BOOL)), BOOL),
        allowedAssignment(a(a(BOOL)), NOTHING),
        illegalAssignment(a(a(BOOL)), STRING),
        illegalAssignment(a(a(BOOL)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(BOOL)), A),

        illegalAssignment(a(a(BOOL)), a(ANY)),
        illegalAssignment(a(a(BOOL)), a(BLOB)),
        illegalAssignment(a(a(BOOL)), a(BOOL)),
        allowedAssignment(a(a(BOOL)), a(NOTHING)),
        illegalAssignment(a(a(BOOL)), a(STRING)),
        illegalAssignment(a(a(BOOL)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(BOOL)), a(A)),

        illegalAssignment(a(a(BOOL)), a(a(ANY))),
        illegalAssignment(a(a(BOOL)), a(a(BLOB))),
        allowedAssignment(a(a(BOOL)), a(a(BOOL))),
        allowedAssignment(a(a(BOOL)), a(a(NOTHING))),
        illegalAssignment(a(a(BOOL)), a(a(STRING))),
        illegalAssignment(a(a(BOOL)), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(a(BOOL)), a(a(A))),

        // [[Nothing]]
        illegalAssignment(a(a(NOTHING)), ANY),
        illegalAssignment(a(a(NOTHING)), BLOB),
        illegalAssignment(a(a(NOTHING)), BOOL),
        allowedAssignment(a(a(NOTHING)), NOTHING),
        illegalAssignment(a(a(NOTHING)), STRING),
        illegalAssignment(a(a(NOTHING)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(NOTHING)), A),

        illegalAssignment(a(a(NOTHING)), a(ANY)),
        illegalAssignment(a(a(NOTHING)), a(BLOB)),
        illegalAssignment(a(a(NOTHING)), a(BOOL)),
        allowedAssignment(a(a(NOTHING)), a(NOTHING)),
        illegalAssignment(a(a(NOTHING)), a(STRING)),
        illegalAssignment(a(a(NOTHING)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(NOTHING)), a(A)),

        illegalAssignment(a(a(NOTHING)), a(a(ANY))),
        illegalAssignment(a(a(NOTHING)), a(a(BLOB))),
        illegalAssignment(a(a(NOTHING)), a(a(BOOL))),
        allowedAssignment(a(a(NOTHING)), a(a(NOTHING))),
        illegalAssignment(a(a(NOTHING)), a(a(STRING))),
        illegalAssignment(a(a(NOTHING)), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(a(NOTHING)), a(a(A))),

        // [[String]]
        illegalAssignment(a(a(STRING)), ANY),
        illegalAssignment(a(a(STRING)), BLOB),
        illegalAssignment(a(a(STRING)), BOOL),
        allowedAssignment(a(a(STRING)), NOTHING),
        illegalAssignment(a(a(STRING)), STRING),
        illegalAssignment(a(a(STRING)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(STRING)), A),

        illegalAssignment(a(a(STRING)), a(ANY)),
        illegalAssignment(a(a(STRING)), a(BLOB)),
        illegalAssignment(a(a(STRING)), a(BOOL)),
        allowedAssignment(a(a(STRING)), a(NOTHING)),
        illegalAssignment(a(a(STRING)), a(STRING)),
        illegalAssignment(a(a(STRING)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(STRING)), a(A)),

        illegalAssignment(a(a(STRING)), a(a(ANY))),
        illegalAssignment(a(a(STRING)), a(a(BLOB))),
        illegalAssignment(a(a(STRING)), a(a(BOOL))),
        allowedAssignment(a(a(STRING)), a(a(NOTHING))),
        allowedAssignment(a(a(STRING)), a(a(STRING))),
        illegalAssignment(a(a(STRING)), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(a(STRING)), a(a(A))),

        // [[Struct]]
        illegalAssignment(a(a(STRUCT_WITH_STRING)), ANY),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), BLOB),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), BOOL),
        allowedAssignment(a(a(STRUCT_WITH_STRING)), NOTHING),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), STRING),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), A),

        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(ANY)),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(BLOB)),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(BOOL)),
        allowedAssignment(a(a(STRUCT_WITH_STRING)), a(NOTHING)),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(STRING)),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(A)),

        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(a(ANY))),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(a(BLOB))),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(a(BOOL))),
        allowedAssignment(a(a(STRUCT_WITH_STRING)), a(a(NOTHING))),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(a(STRING))),
        allowedAssignment(a(a(STRUCT_WITH_STRING)), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(a(A))),

        illegalAssignment(a(a(STRUCT_WITH_STRING)), STRUCT_WITH_BOOL),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(STRUCT_WITH_BOOL)),
        illegalAssignment(a(a(STRUCT_WITH_STRING)), a(a(STRUCT_WITH_BOOL)))
    );
  }

  public static List<TestedAssignmentSpec> testSpecSpecificForNormalAssignment() {
    return List.of(
        // A
        illegalAssignment(A, ANY),
        illegalAssignment(A, BLOB),
        illegalAssignment(A, BOOL),
        allowedAssignment(A, NOTHING),
        illegalAssignment(A, STRING),
        illegalAssignment(A, STRUCT_WITH_STRING),
        allowedAssignment(A, A),
        illegalAssignment(A, B),

        illegalAssignment(A, a(ANY)),
        illegalAssignment(A, a(BLOB)),
        illegalAssignment(A, a(BOOL)),
        illegalAssignment(A, a(NOTHING)),
        illegalAssignment(A, a(STRUCT_WITH_STRING)),
        illegalAssignment(A, a(STRING)),
        illegalAssignment(A, a(A)),
        illegalAssignment(A, a(B)),

        illegalAssignment(A, a(a(ANY))),
        illegalAssignment(A, a(a(BLOB))),
        illegalAssignment(A, a(a(BOOL))),
        illegalAssignment(A, a(a(NOTHING))),
        illegalAssignment(A, a(a(STRUCT_WITH_STRING))),
        illegalAssignment(A, a(a(STRING))),
        illegalAssignment(A, a(a(A))),
        illegalAssignment(A, a(a(B))),

        // [A]
        illegalAssignment(a(A), ANY),
        illegalAssignment(a(A), BLOB),
        illegalAssignment(a(A), BOOL),
        allowedAssignment(a(A), NOTHING),
        illegalAssignment(a(A), STRING),
        illegalAssignment(a(A), STRUCT_WITH_STRING),
        illegalAssignment(a(A), A),
        illegalAssignment(a(A), B),

        illegalAssignment(a(A), a(ANY)),
        illegalAssignment(a(A), a(BLOB)),
        illegalAssignment(a(A), a(BOOL)),
        allowedAssignment(a(A), a(NOTHING)),
        illegalAssignment(a(A), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(A), a(STRING)),
        allowedAssignment(a(A), a(A)),
        illegalAssignment(a(A), a(B)),

        illegalAssignment(a(A), a(a(ANY))),
        illegalAssignment(a(A), a(a(BLOB))),
        illegalAssignment(a(A), a(a(BOOL))),
        illegalAssignment(a(A), a(a(NOTHING))),
        illegalAssignment(a(A), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(A), a(a(STRING))),
        illegalAssignment(a(A), a(a(A))),
        illegalAssignment(a(A), a(a(B))),

        // [[A]]
        illegalAssignment(a(a(A)), ANY),
        illegalAssignment(a(a(A)), BLOB),
        illegalAssignment(a(a(A)), BOOL),
        allowedAssignment(a(a(A)), NOTHING),
        illegalAssignment(a(a(A)), STRING),
        illegalAssignment(a(a(A)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(A)), A),
        illegalAssignment(a(a(A)), B),

        illegalAssignment(a(a(A)), a(ANY)),
        illegalAssignment(a(a(A)), a(BLOB)),
        illegalAssignment(a(a(A)), a(BOOL)),
        allowedAssignment(a(a(A)), a(NOTHING)),
        illegalAssignment(a(a(A)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(A)), a(STRING)),
        illegalAssignment(a(a(A)), a(A)),
        illegalAssignment(a(a(A)), a(B)),

        illegalAssignment(a(a(A)), a(a(ANY))),
        illegalAssignment(a(a(A)), a(a(BLOB))),
        illegalAssignment(a(a(A)), a(a(BOOL))),
        allowedAssignment(a(a(A)), a(a(NOTHING))),
        illegalAssignment(a(a(A)), a(a(STRUCT_WITH_STRING))),
        illegalAssignment(a(a(A)), a(a(STRING))),
        allowedAssignment(a(a(A)), a(a(A))),
        illegalAssignment(a(a(A)), a(a(B)))
      );
  }

  public static List<TestedAssignmentSpec> testSpecsSpecificForParameterAssignment() {
    return List.of(
        // A
        allowedAssignment(A, ANY),
        allowedAssignment(A, BLOB),
        allowedAssignment(A, BOOL),
        allowedAssignment(A, NOTHING),
        allowedAssignment(A, STRING),
        allowedAssignment(A, STRUCT_WITH_STRING),
        allowedAssignment(A, A),
        allowedAssignment(A, B),

        allowedAssignment(A, a(ANY)),
        allowedAssignment(A, a(BLOB)),
        allowedAssignment(A, a(BOOL)),
        allowedAssignment(A, a(NOTHING)),
        allowedAssignment(A, a(STRUCT_WITH_STRING)),
        allowedAssignment(A, a(STRING)),
        allowedAssignment(A, a(A)),
        allowedAssignment(A, a(B)),

        allowedAssignment(A, a(a(ANY))),
        allowedAssignment(A, a(a(BLOB))),
        allowedAssignment(A, a(a(BOOL))),
        allowedAssignment(A, a(a(NOTHING))),
        allowedAssignment(A, a(a(STRUCT_WITH_STRING))),
        allowedAssignment(A, a(a(STRING))),
        allowedAssignment(A, a(a(A))),
        allowedAssignment(A, a(a(B))),

        // [A]
        illegalAssignment(a(A), ANY),
        illegalAssignment(a(A), BLOB),
        illegalAssignment(a(A), BOOL),
        allowedAssignment(a(A), NOTHING),
        illegalAssignment(a(A), STRING),
        illegalAssignment(a(A), STRUCT_WITH_STRING),
        illegalAssignment(a(A), A),
        illegalAssignment(a(A), B),

        allowedAssignment(a(A), a(ANY)),
        allowedAssignment(a(A), a(BLOB)),
        allowedAssignment(a(A), a(BOOL)),
        allowedAssignment(a(A), a(NOTHING)),
        allowedAssignment(a(A), a(STRUCT_WITH_STRING)),
        allowedAssignment(a(A), a(STRING)),
        allowedAssignment(a(A), a(A)),
        allowedAssignment(a(A), a(B)),

        allowedAssignment(a(A), a(a(ANY))),
        allowedAssignment(a(A), a(a(BLOB))),
        allowedAssignment(a(A), a(a(BOOL))),
        allowedAssignment(a(A), a(a(NOTHING))),
        allowedAssignment(a(A), a(a(STRUCT_WITH_STRING))),
        allowedAssignment(a(A), a(a(STRING))),
        allowedAssignment(a(A), a(a(A))),
        allowedAssignment(a(A), a(a(B))),

        // [[A]]
        illegalAssignment(a(a(A)), ANY),
        illegalAssignment(a(a(A)), BLOB),
        illegalAssignment(a(a(A)), BOOL),
        allowedAssignment(a(a(A)), NOTHING),
        illegalAssignment(a(a(A)), STRING),
        illegalAssignment(a(a(A)), STRUCT_WITH_STRING),
        illegalAssignment(a(a(A)), A),
        illegalAssignment(a(a(A)), B),

        illegalAssignment(a(a(A)), a(ANY)),
        illegalAssignment(a(a(A)), a(BLOB)),
        illegalAssignment(a(a(A)), a(BOOL)),
        allowedAssignment(a(a(A)), a(NOTHING)),
        illegalAssignment(a(a(A)), a(STRUCT_WITH_STRING)),
        illegalAssignment(a(a(A)), a(STRING)),
        illegalAssignment(a(a(A)), a(A)),
        illegalAssignment(a(a(A)), a(B)),

        allowedAssignment(a(a(A)), a(a(ANY))),
        allowedAssignment(a(a(A)), a(a(BLOB))),
        allowedAssignment(a(a(A)), a(a(BOOL))),
        allowedAssignment(a(a(A)), a(a(NOTHING))),
        allowedAssignment(a(a(A)), a(a(STRUCT_WITH_STRING))),
        allowedAssignment(a(a(A)), a(a(STRING))),
        allowedAssignment(a(a(A)), a(a(A))),
        allowedAssignment(a(a(A)), a(a(B)))
    );
  }
}
