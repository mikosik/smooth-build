package org.smoothbuild.systemtest.lang.convert;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestedTSF.BLOB;
import static org.smoothbuild.lang.base.type.TestedTSF.BOOL;
import static org.smoothbuild.lang.base.type.TestedTSF.INT;
import static org.smoothbuild.lang.base.type.TestedTSF.NOTHING;
import static org.smoothbuild.lang.base.type.TestedTSF.STRING;
import static org.smoothbuild.lang.base.type.TestedTSF.STRUCT;
import static org.smoothbuild.lang.base.type.TestedTSF.a;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedAssignS;
import org.smoothbuild.lang.base.type.TestedTS;
import org.smoothbuild.nativefunc.ReportError;
import org.smoothbuild.systemtest.SystemTestCase;

public abstract class AbstractConversionTestCase extends SystemTestCase {
  @ParameterizedTest
  @MethodSource("conversion_test_specs")
  public void conversion_is_verified(TestedAssignS assignment) throws IOException {
    createNativeJar(ReportError.class);
    createUserModule(createTestScript(assignment));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    Object expected = assignment.source().value();

    // Currently it is not possible to read artifact of a struct so we are not testing that.
    if (expected != null) {
      assertThat(artifactStringified("result"))
          .isEqualTo(expected);
    }
  }

  protected abstract String createTestScript(TestedAssignS assignment);

  private static Stream<TestedAssignS> conversion_test_specs() {
    return Stream.of(
        // Blob
        assignment(BLOB, BLOB),

        // Bool
        assignment(BOOL, BOOL),

        // Int
        assignment(INT, INT),

        // No test cases for Nothing - it cannot be converted to anything.

        // String
        assignment(STRING, STRING),

        // Struct
        assignment(STRUCT, STRUCT),

        // [Blob]
        assignment(a(BLOB), a(BLOB)),
        assignment(a(BLOB), a(NOTHING)),

        // [Bool]
        assignment(a(BOOL), a(BOOL)),
        assignment(a(BOOL), a(NOTHING)),

        // [Int]
        assignment(a(INT), a(INT)),
        assignment(a(INT), a(NOTHING)),

        // [Nothing]
        assignment(a(NOTHING), a(NOTHING)),

        // [String]
        assignment(a(STRING), a(NOTHING)),
        assignment(a(STRING), a(STRING)),

        // [Struct]
        assignment(a(STRUCT), a(NOTHING)),
        assignment(a(STRUCT), a(STRUCT)),

        // [[Blob]]
        assignment(a(a(BLOB)), a(NOTHING)),

        assignment(a(a(BLOB)), a(a(BLOB))),
        assignment(a(a(BLOB)), a(a(NOTHING))),

        // [[Bool]]
        assignment(a(a(BOOL)), a(NOTHING)),

        assignment(a(a(BOOL)), a(a(BOOL))),
        assignment(a(a(BOOL)), a(a(NOTHING))),

        // [[Int]]
        assignment(a(a(INT)), a(NOTHING)),

        assignment(a(a(INT)), a(a(INT))),
        assignment(a(a(INT)), a(a(NOTHING))),

        // [[Nothing]]
        assignment(a(a(NOTHING)), a(NOTHING)),

        assignment(a(a(NOTHING)), a(a(NOTHING))),

        // [[String]]
        assignment(a(a(STRING)), a(NOTHING)),

        assignment(a(a(STRING)), a(a(NOTHING))),
        assignment(a(a(STRING)), a(a(STRING))),

        // [[Struct]]
        assignment(a(a(STRUCT)), a(NOTHING)),

        assignment(a(a(STRUCT)), a(a(NOTHING))),
        assignment(a(a(STRUCT)), a(a(STRUCT)))
    );
  }

  public static TestedAssignS assignment(TestedTS target, TestedTS source) {
    return new TestedAssignS(target, source);
  }
}
