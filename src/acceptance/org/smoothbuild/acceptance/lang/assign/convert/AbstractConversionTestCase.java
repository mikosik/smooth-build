package org.smoothbuild.acceptance.lang.assign.convert;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestedType.BLOB;
import static org.smoothbuild.lang.base.type.TestedType.BOOL;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING;
import static org.smoothbuild.lang.base.type.TestedType.STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING;
import static org.smoothbuild.lang.base.type.TestedType.a;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ReportError;
import org.smoothbuild.lang.base.type.TestedAssignment;
import org.smoothbuild.lang.base.type.TestedType;

public abstract class AbstractConversionTestCase extends AcceptanceTestCase {
  @ParameterizedTest
  @MethodSource("conversion_test_specs")
  public void conversion_is_verified(ConversionTestSpec testSpec) throws IOException {
    createNativeJar(ReportError.class);
    createUserModule(createTestScript(testSpec));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    Object expected = testSpec.expectedResult;

    // Currently it is not possible to read artifact of a struct so we are not testing that.
    if (expected != null) {
      assertThat(stringifiedArtifact("result"))
          .isEqualTo(expected);
    }
  }

  protected abstract String createTestScript(ConversionTestSpec testSpec);

  public static Stream<ConversionTestSpec> conversion_test_specs() {
    return Stream.of(
        // Blob
        allowedConversion(BLOB, BLOB),

        // Bool
        allowedConversion(BOOL, BOOL),

        // Nothing

        // String
        allowedConversion(STRING, STRING),

        // Struct
        allowedConversion(STRUCT_WITH_STRING, STRUCT_WITH_STRING),

        // [Blob]
        allowedConversion(a(BLOB), a(BLOB)),
        allowedConversion(a(BLOB), a(NOTHING)),

        // [Bool]
        allowedConversion(a(BOOL), a(BOOL)),
        allowedConversion(a(BOOL), a(NOTHING)),

        // [Nothing]
        allowedConversion(a(NOTHING), a(NOTHING)),

        // [String]
        allowedConversion(a(STRING), a(NOTHING)),
        allowedConversion(a(STRING), a(STRING)),

        // [Struct]
        allowedConversion(a(STRUCT_WITH_STRING), a(NOTHING)),
        allowedConversion(a(STRUCT_WITH_STRING), a(STRUCT_WITH_STRING)),

        // [[Blob]]
        allowedConversion(a(a(BLOB)), a(NOTHING)),

        allowedConversion(a(a(BLOB)), a(a(BLOB))),
        allowedConversion(a(a(BLOB)), a(a(NOTHING))),

        // [[Bool]]
        allowedConversion(a(a(BOOL)), a(NOTHING)),

        allowedConversion(a(a(BOOL)), a(a(BOOL))),
        allowedConversion(a(a(BOOL)), a(a(NOTHING))),

        // [[Nothing]]
        allowedConversion(a(a(NOTHING)), a(NOTHING)),

        allowedConversion(a(a(NOTHING)), a(a(NOTHING))),

        // [[String]]
        allowedConversion(a(a(STRING)), a(NOTHING)),

        allowedConversion(a(a(STRING)), a(a(NOTHING))),
        allowedConversion(a(a(STRING)), a(a(STRING))),

        // [[Struct]]
        allowedConversion(a(a(STRUCT_WITH_STRING)), a(NOTHING)),

        allowedConversion(a(a(STRUCT_WITH_STRING)), a(a(NOTHING))),
        allowedConversion(a(a(STRUCT_WITH_STRING)), a(a(STRUCT_WITH_STRING)))
    );
  }

  public static ConversionTestSpec allowedConversion(TestedType target, TestedType source) {
    return new ConversionTestSpec(target, source, source.value());
  }

  public static ConversionTestSpec allowedConversion(TestedType target, TestedType source,
      Object expectedResult) {
    return new ConversionTestSpec(target, source, expectedResult);
  }

  public static class ConversionTestSpec extends TestedAssignment {
    public final Object expectedResult;

    private ConversionTestSpec(TestedType target, TestedType source, Object expectedResult) {
      super(target, source);
      this.expectedResult = expectedResult;
    }
  }
}
