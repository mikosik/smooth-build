package org.smoothbuild.acceptance.lang.assign.convert;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BLOB;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BLOB_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BLOB_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BOOL;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BOOL_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.BOOL_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.NOTHING_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.NOTHING_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRING;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRING_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRING_ARRAY2;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_STRING;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_STRING_ARRAY;
import static org.smoothbuild.acceptance.lang.assign.spec.TestedType.STRUCT_WITH_STRING_ARRAY2;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.assign.spec.TestSpec;
import org.smoothbuild.acceptance.lang.assign.spec.TestedType;
import org.smoothbuild.acceptance.testing.ReportError;

public abstract class AbstractConversionTestCase extends AcceptanceTestCase {
  @ParameterizedTest
  @MethodSource("conversion_test_specs")
  public void conversion_is_verified(ConversionTestSpec testSpec) throws IOException {
    givenNativeJar(ReportError.class);
    givenScript(createTestScript(testSpec));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    Object expected = testSpec.expectedResult;

    // Currently it is not possible to read artifact of a struct so we are not testing that.
    if (expected != null) {
      assertThat(artifactArray("result"))
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
        allowedConversion(STRING, STRUCT_WITH_STRING, "John"),

        // Struct
        allowedConversion(STRUCT_WITH_STRING, STRUCT_WITH_STRING),

        // [Blob]
        allowedConversion(BLOB_ARRAY, BLOB_ARRAY),
        allowedConversion(BLOB_ARRAY, NOTHING_ARRAY),

        // [Bool]
        allowedConversion(BOOL_ARRAY, BOOL_ARRAY),
        allowedConversion(BOOL_ARRAY, NOTHING_ARRAY),

        // [Nothing]
        allowedConversion(NOTHING_ARRAY, NOTHING_ARRAY),

        // [String]
        allowedConversion(STRING_ARRAY, NOTHING_ARRAY),
        allowedConversion(STRING_ARRAY, STRUCT_WITH_STRING_ARRAY, list("John")),
        allowedConversion(STRING_ARRAY, STRING_ARRAY),

        // [Struct]
        allowedConversion(STRUCT_WITH_STRING_ARRAY, NOTHING_ARRAY),
        allowedConversion(STRUCT_WITH_STRING_ARRAY, STRUCT_WITH_STRING_ARRAY),

        // [[Blob]]
        allowedConversion(BLOB_ARRAY2, NOTHING_ARRAY),

        allowedConversion(BLOB_ARRAY2, BLOB_ARRAY2),
        allowedConversion(BLOB_ARRAY2, NOTHING_ARRAY2),

        // [[Bool]]
        allowedConversion(BOOL_ARRAY2, NOTHING_ARRAY),

        allowedConversion(BOOL_ARRAY2, BOOL_ARRAY2),
        allowedConversion(BOOL_ARRAY2, NOTHING_ARRAY2),

        // [[Nothing]]
        allowedConversion(NOTHING_ARRAY2, NOTHING_ARRAY),

        allowedConversion(NOTHING_ARRAY2, NOTHING_ARRAY2),

        // [[String]]
        allowedConversion(STRING_ARRAY2, NOTHING_ARRAY),

        allowedConversion(STRING_ARRAY2, NOTHING_ARRAY2),
        allowedConversion(STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY2, list(list("John"))),
        allowedConversion(STRING_ARRAY2, STRING_ARRAY2),

        // [[Struct]]
        allowedConversion(STRUCT_WITH_STRING_ARRAY2, NOTHING_ARRAY),

        allowedConversion(STRUCT_WITH_STRING_ARRAY2, NOTHING_ARRAY2),
        allowedConversion(STRUCT_WITH_STRING_ARRAY2, STRUCT_WITH_STRING_ARRAY2,
            STRUCT_WITH_STRING_ARRAY2.value)
    );
  }

  public static ConversionTestSpec allowedConversion(TestedType target, TestedType source) {
    return new ConversionTestSpec(target, source, source.value);
  }

  public static ConversionTestSpec allowedConversion(TestedType target, TestedType source,
      Object expectedResult) {
    return new ConversionTestSpec(target, source, expectedResult);
  }

  public static class ConversionTestSpec extends TestSpec {
    public final Object expectedResult;

    private ConversionTestSpec(TestedType target, TestedType source, Object expectedResult) {
      super(target, source);
      this.expectedResult = expectedResult;
    }
  }
}
