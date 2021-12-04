package org.smoothbuild.acceptance.lang.convert;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestedT.BLOB;
import static org.smoothbuild.lang.base.type.TestedT.BOOL;
import static org.smoothbuild.lang.base.type.TestedT.INT;
import static org.smoothbuild.lang.base.type.TestedT.NOTHING;
import static org.smoothbuild.lang.base.type.TestedT.STRING;
import static org.smoothbuild.lang.base.type.TestedT.STRUCT;
import static org.smoothbuild.lang.base.type.TestedT.a;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ReportError;
import org.smoothbuild.lang.base.type.TestedAssignment;
import org.smoothbuild.lang.base.type.TestedT;

public abstract class AbstractConversionTestCase extends AcceptanceTestCase {
  @ParameterizedTest
  @MethodSource("conversion_test_specs")
  public void conversion_is_verified(TestedAssignment testSpec) throws IOException {
    createNativeJar(ReportError.class);
    createUserModule(createTestScript(testSpec));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    Object expected = testSpec.source().value();

    // Currently it is not possible to read artifact of a struct so we are not testing that.
    if (expected != null) {
      assertThat(artifactStringified("result"))
          .isEqualTo(expected);
    }
  }

  protected abstract String createTestScript(TestedAssignment testSpec);

  private static Stream<TestedAssignment> conversion_test_specs() {
    return Stream.of(
        // Blob
        allowedConversion(BLOB, BLOB),

        // Bool
        allowedConversion(BOOL, BOOL),

        // Int
        allowedConversion(INT, INT),

        // No test cases for Nothing - it cannot be converted to anything.

        // String
        allowedConversion(STRING, STRING),

        // Struct
        allowedConversion(STRUCT, STRUCT),

        // [Blob]
        allowedConversion(a(BLOB), a(BLOB)),
        allowedConversion(a(BLOB), a(NOTHING)),

        // [Bool]
        allowedConversion(a(BOOL), a(BOOL)),
        allowedConversion(a(BOOL), a(NOTHING)),

        // [Int]
        allowedConversion(a(INT), a(INT)),
        allowedConversion(a(INT), a(NOTHING)),

        // [Nothing]
        allowedConversion(a(NOTHING), a(NOTHING)),

        // [String]
        allowedConversion(a(STRING), a(NOTHING)),
        allowedConversion(a(STRING), a(STRING)),

        // [Struct]
        allowedConversion(a(STRUCT), a(NOTHING)),
        allowedConversion(a(STRUCT), a(STRUCT)),

        // [[Blob]]
        allowedConversion(a(a(BLOB)), a(NOTHING)),

        allowedConversion(a(a(BLOB)), a(a(BLOB))),
        allowedConversion(a(a(BLOB)), a(a(NOTHING))),

        // [[Bool]]
        allowedConversion(a(a(BOOL)), a(NOTHING)),

        allowedConversion(a(a(BOOL)), a(a(BOOL))),
        allowedConversion(a(a(BOOL)), a(a(NOTHING))),

        // [[Int]]
        allowedConversion(a(a(INT)), a(NOTHING)),

        allowedConversion(a(a(INT)), a(a(INT))),
        allowedConversion(a(a(INT)), a(a(NOTHING))),

        // [[Nothing]]
        allowedConversion(a(a(NOTHING)), a(NOTHING)),

        allowedConversion(a(a(NOTHING)), a(a(NOTHING))),

        // [[String]]
        allowedConversion(a(a(STRING)), a(NOTHING)),

        allowedConversion(a(a(STRING)), a(a(NOTHING))),
        allowedConversion(a(a(STRING)), a(a(STRING))),

        // [[Struct]]
        allowedConversion(a(a(STRUCT)), a(NOTHING)),

        allowedConversion(a(a(STRUCT)), a(a(NOTHING))),
        allowedConversion(a(a(STRUCT)), a(a(STRUCT)))
    );
  }

  public static TestedAssignment allowedConversion(TestedT target, TestedT source) {
    return new TestedAssignment(target, source);
  }
}
