package org.smoothbuild.acceptance.lang.assign.nongeneric;

import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BLOB;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BLOB_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BLOB_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BOOL;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BOOL_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BOOL_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.NOTHING;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.NOTHING_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.NOTHING_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRING;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRING_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRING_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_BOOL;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_BOOL_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_BOOL_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_STRING;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_STRING_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_STRING_ARRAY2;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.assign.spec.TestSpec;
import org.smoothbuild.acceptance.lang.assign.spec.TestedType;
import org.smoothbuild.acceptance.testing.ReportError;

public abstract class AbstractAssignmentTestCase extends AcceptanceTestCase {
  @ParameterizedTest
  @MethodSource("assignment_test_specs")
  public void assignment_is_verified(AssignmentTestSpec testSpec) throws IOException {
    givenNativeJar(ReportError.class);
    givenScript(createTestScript(testSpec));
    whenSmoothList();
    if (testSpec.allowed) {
      thenFinishedWithSuccess();
    } else {
      thenFinishedWithError();
      assertAssignmentError(testSpec.target.name, testSpec.source.name);
    }
  }

  protected abstract String createTestScript(AssignmentTestSpec testSpec);

  protected abstract void assertAssignmentError(String targetType, String sourceType);

  public static Stream<AssignmentTestSpec> assignment_test_specs() {
    return Stream.of(
        // Blob
        allowedAssignment(BLOB, BLOB),
        illegalAssignment(BLOB, BOOL),
        allowedAssignment(BLOB, NOTHING),
        illegalAssignment(BLOB, STRING),
        illegalAssignment(BLOB, STRUCT_WITH_STRING),

        illegalAssignment(BLOB, BLOB_ARRAY),
        illegalAssignment(BLOB, BOOL_ARRAY),
        illegalAssignment(BLOB, NOTHING_ARRAY),
        illegalAssignment(BLOB, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BLOB, STRING_ARRAY),

        illegalAssignment(BLOB, BLOB_ARRAY2),
        illegalAssignment(BLOB, BOOL_ARRAY2),
        illegalAssignment(BLOB, NOTHING_ARRAY2),
        illegalAssignment(BLOB, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BLOB, STRING_ARRAY2),

        // Bool
        illegalAssignment(BOOL, BLOB),
        allowedAssignment(BOOL, BOOL),
        allowedAssignment(BOOL, NOTHING),
        illegalAssignment(BOOL, STRING),
        illegalAssignment(BOOL, STRUCT_WITH_STRING),

        illegalAssignment(BOOL, BLOB_ARRAY),
        illegalAssignment(BOOL, BOOL_ARRAY),
        illegalAssignment(BOOL, NOTHING_ARRAY),
        illegalAssignment(BOOL, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BOOL, STRING_ARRAY),

        illegalAssignment(BOOL, BLOB_ARRAY2),
        illegalAssignment(BOOL, BOOL_ARRAY2),
        illegalAssignment(BOOL, NOTHING_ARRAY2),
        illegalAssignment(BOOL, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BOOL, STRING_ARRAY2),

        // Nothing
        illegalAssignment(NOTHING, BLOB),
        illegalAssignment(NOTHING, BOOL),
        allowedAssignment(NOTHING, NOTHING),
        illegalAssignment(NOTHING, STRING),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING),

        illegalAssignment(NOTHING, BLOB_ARRAY),
        illegalAssignment(NOTHING, BOOL_ARRAY),
        illegalAssignment(NOTHING, NOTHING_ARRAY),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(NOTHING, STRING_ARRAY),

        illegalAssignment(NOTHING, BLOB_ARRAY2),
        illegalAssignment(NOTHING, BOOL_ARRAY2),
        illegalAssignment(NOTHING, NOTHING_ARRAY2),
        illegalAssignment(NOTHING, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(NOTHING, STRING_ARRAY2),

        // String
        illegalAssignment(STRING, BLOB),
        illegalAssignment(STRING, BOOL),
        allowedAssignment(STRING, NOTHING),
        allowedAssignment(STRING, STRING),
        allowedAssignment(STRING, STRUCT_WITH_STRING),

        illegalAssignment(STRING, BLOB_ARRAY),
        illegalAssignment(STRING, BOOL_ARRAY),
        illegalAssignment(STRING, NOTHING_ARRAY),
        illegalAssignment(STRING, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRING, STRING_ARRAY),

        illegalAssignment(STRING, BLOB_ARRAY2),
        illegalAssignment(STRING, BOOL_ARRAY2),
        illegalAssignment(STRING, NOTHING_ARRAY2),
        illegalAssignment(STRING, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRING, STRING_ARRAY2),

        // Struct
        illegalAssignment(STRUCT_WITH_STRING, BLOB),
        illegalAssignment(STRUCT_WITH_STRING, BOOL),
        allowedAssignment(STRUCT_WITH_STRING, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING, STRING),
        allowedAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING),

        illegalAssignment(STRUCT_WITH_STRING, BLOB_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, NOTHING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, STRING_ARRAY),

        illegalAssignment(STRUCT_WITH_STRING, BLOB_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, BOOL_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, NOTHING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING, STRING_ARRAY2),

        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING, STRUCT_WITH_BOOL_ARRAY2),

        // [Blob]
        illegalAssignment(BLOB_ARRAY, BLOB),
        illegalAssignment(BLOB_ARRAY, BOOL),
        allowedAssignment(BLOB_ARRAY, NOTHING),
        illegalAssignment(BLOB_ARRAY, STRING),
        illegalAssignment(BLOB_ARRAY, STRUCT_WITH_STRING),

        allowedAssignment(BLOB_ARRAY, BLOB_ARRAY),
        illegalAssignment(BLOB_ARRAY, BOOL_ARRAY),
        allowedAssignment(BLOB_ARRAY, NOTHING_ARRAY),
        illegalAssignment(BLOB_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BLOB_ARRAY, STRING_ARRAY),

        illegalAssignment(BLOB_ARRAY, BLOB_ARRAY2),
        illegalAssignment(BLOB_ARRAY, BOOL_ARRAY2),
        illegalAssignment(BLOB_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(BLOB_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BLOB_ARRAY, STRING_ARRAY2),

        // [Bool]
        illegalAssignment(BOOL_ARRAY, BLOB),
        illegalAssignment(BOOL_ARRAY, BOOL),
        allowedAssignment(BOOL_ARRAY, NOTHING),
        illegalAssignment(BOOL_ARRAY, STRING),
        illegalAssignment(BOOL_ARRAY, STRUCT_WITH_STRING),

        illegalAssignment(BOOL_ARRAY, BLOB_ARRAY),
        allowedAssignment(BOOL_ARRAY, BOOL_ARRAY),
        allowedAssignment(BOOL_ARRAY, NOTHING_ARRAY),
        illegalAssignment(BOOL_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BOOL_ARRAY, STRING_ARRAY),

        illegalAssignment(BOOL_ARRAY, BLOB_ARRAY2),
        illegalAssignment(BOOL_ARRAY, BOOL_ARRAY2),
        illegalAssignment(BOOL_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(BOOL_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BOOL_ARRAY, STRING_ARRAY2),

        // [Nothing]
        illegalAssignment(NOTHING_ARRAY, BLOB),
        illegalAssignment(NOTHING_ARRAY, BOOL),
        allowedAssignment(NOTHING_ARRAY, NOTHING),
        illegalAssignment(NOTHING_ARRAY, STRING),
        illegalAssignment(NOTHING_ARRAY, STRUCT_WITH_STRING),

        illegalAssignment(NOTHING_ARRAY, BLOB_ARRAY),
        illegalAssignment(NOTHING_ARRAY, BOOL_ARRAY),
        allowedAssignment(NOTHING_ARRAY, NOTHING_ARRAY),
        illegalAssignment(NOTHING_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(NOTHING_ARRAY, STRING_ARRAY),

        illegalAssignment(NOTHING_ARRAY, BLOB_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, BOOL_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY, STRING_ARRAY2),

        // [String]
        illegalAssignment(STRING_ARRAY, BLOB),
        illegalAssignment(STRING_ARRAY, BOOL),
        allowedAssignment(STRING_ARRAY, NOTHING),
        illegalAssignment(STRING_ARRAY, STRING),
        illegalAssignment(STRING_ARRAY, STRUCT_WITH_STRING),

        illegalAssignment(STRING_ARRAY, BLOB_ARRAY),
        illegalAssignment(STRING_ARRAY, BOOL_ARRAY),
        allowedAssignment(STRING_ARRAY, NOTHING_ARRAY),
        allowedAssignment(STRING_ARRAY, STRUCT_WITH_STRING_ARRAY),
        allowedAssignment(STRING_ARRAY, STRING_ARRAY),

        illegalAssignment(STRING_ARRAY, BLOB_ARRAY2),
        illegalAssignment(STRING_ARRAY, BOOL_ARRAY2),
        illegalAssignment(STRING_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(STRING_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRING_ARRAY, STRING_ARRAY2),

        // [Struct]
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BLOB),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BOOL),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BLOB_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BOOL_ARRAY),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY, NOTHING_ARRAY),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRING_ARRAY),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BLOB_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, BOOL_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, NOTHING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRING_ARRAY2),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_BOOL),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_BOOL_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_BOOL_ARRAY2),

        // [[Blob]]
        illegalAssignment(BLOB_ARRAY2, BLOB),
        illegalAssignment(BLOB_ARRAY2, BOOL),
        allowedAssignment(BLOB_ARRAY2, NOTHING),
        illegalAssignment(BLOB_ARRAY2, STRING),
        illegalAssignment(BLOB_ARRAY2, STRUCT_WITH_STRING),

        illegalAssignment(BLOB_ARRAY2, BLOB_ARRAY),
        illegalAssignment(BLOB_ARRAY2, BOOL_ARRAY),
        allowedAssignment(BLOB_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(BLOB_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BLOB_ARRAY2, STRING_ARRAY),

        allowedAssignment(BLOB_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(BLOB_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BLOB_ARRAY2, STRING_ARRAY2),

        // [[Bool]]
        illegalAssignment(BOOL_ARRAY2, BLOB),
        illegalAssignment(BOOL_ARRAY2, BOOL),
        allowedAssignment(BOOL_ARRAY2, NOTHING),
        illegalAssignment(BOOL_ARRAY2, STRING),
        illegalAssignment(BOOL_ARRAY2, STRUCT_WITH_STRING),

        illegalAssignment(BOOL_ARRAY2, BLOB_ARRAY),
        illegalAssignment(BOOL_ARRAY2, BOOL_ARRAY),
        allowedAssignment(BOOL_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(BOOL_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(BOOL_ARRAY2, STRING_ARRAY),

        illegalAssignment(BOOL_ARRAY2, BLOB_ARRAY2),
        allowedAssignment(BOOL_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(BOOL_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(BOOL_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(BOOL_ARRAY2, STRING_ARRAY2),

        // [[Nothing]]
        illegalAssignment(NOTHING_ARRAY2, BLOB),
        illegalAssignment(NOTHING_ARRAY2, BOOL),
        allowedAssignment(NOTHING_ARRAY2, NOTHING),
        illegalAssignment(NOTHING_ARRAY2, STRING),
        illegalAssignment(NOTHING_ARRAY2, STRUCT_WITH_STRING),

        illegalAssignment(NOTHING_ARRAY2, BLOB_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, BOOL_ARRAY),
        allowedAssignment(NOTHING_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(NOTHING_ARRAY2, STRING_ARRAY),

        illegalAssignment(NOTHING_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(NOTHING_ARRAY2, NOTHING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(NOTHING_ARRAY2, STRING_ARRAY2),

        // [[String]]
        illegalAssignment(STRING_ARRAY2, BLOB),
        illegalAssignment(STRING_ARRAY2, BOOL),
        allowedAssignment(STRING_ARRAY2, NOTHING),
        illegalAssignment(STRING_ARRAY2, STRING),
        illegalAssignment(STRING_ARRAY2, STRUCT_WITH_STRING),

        illegalAssignment(STRING_ARRAY2, BLOB_ARRAY),
        illegalAssignment(STRING_ARRAY2, BOOL_ARRAY),
        allowedAssignment(STRING_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRING_ARRAY2, STRING_ARRAY),

        illegalAssignment(STRING_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(STRING_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(STRING_ARRAY2, NOTHING_ARRAY2),
        allowedAssignment(STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        allowedAssignment(STRING_ARRAY2, STRING_ARRAY2),

        // [[Struct]]
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BLOB),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BOOL),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, NOTHING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRING),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BLOB_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BOOL_ARRAY),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, NOTHING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRING_ARRAY),

        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BLOB_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, BOOL_ARRAY2),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, NOTHING_ARRAY2),
        allowedAssignment(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        illegalAssignment(STRUCT_WITH_STRING_ARRAY2, STRING_ARRAY2),

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

  public static class AssignmentTestSpec extends TestSpec {
    public final boolean allowed;

    private AssignmentTestSpec(TestedType target, TestedType source, boolean allowed) {
      super(target, source);
      this.allowed = allowed;
    }
  }
}
