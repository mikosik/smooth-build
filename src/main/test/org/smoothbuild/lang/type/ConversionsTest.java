package org.smoothbuild.lang.type;

import static java.text.MessageFormat.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Conversions.convertFunctionName;
import static org.smoothbuild.lang.type.Types.ALL_TYPES;
import static org.smoothbuild.lang.type.Types.ARRAY_TYPES;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.function.base.Name;

@RunWith(QuackeryRunner.class)
public class ConversionsTest {
  @Quackery
  public static Suite can_convert() {
    Suite suite = suite("test canConvert");
    for (Type from : ALL_TYPES) {
      for (Type to : ALL_TYPES) {
        suite = suite.add(testConversion(from, to));
      }
    }
    return suite;
  }

  private static Case testConversion(Type from, Type to) {
    boolean canConvert = from == to ||
        from == FILE && to == BLOB ||
        from == FILE_ARRAY && to == BLOB_ARRAY ||
        from == NIL && ARRAY_TYPES.contains(to);
    String canOrCannot = canConvert ? "can" : "cannot";
    return newCase(format("{0} convert from {1} to {2}", canOrCannot, from, to), () -> assertEquals(
        canConvert(from, to), canConvert));
  }

  @Quackery
  public static Suite test_convert_function_name() {
    return suite("test convert function name")
        .add(testConvertFunctionName(FILE, BLOB, "fileToBlob"))
        .add(testConvertFunctionName(FILE_ARRAY, BLOB_ARRAY, "fileArrayToBlobArray"))
        .add(testConvertFunctionName(NIL, STRING_ARRAY, "nilToStringArray"))
        .add(testConvertFunctionName(NIL, BLOB_ARRAY, "nilToBlobArray"))
        .add(testConvertFunctionName(NIL, FILE_ARRAY, "nilToFileArray"));
  }

  private static Case testConvertFunctionName(Type from, Type to, String functionName) {
    return newCase(format("{0} to {1} is named {2}", from, to, functionName), () -> assertEquals(
        convertFunctionName(from, to), new Name(functionName)));
  }
}
