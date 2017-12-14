package org.smoothbuild.lang.type;

import static java.text.MessageFormat.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.ALL_TYPES;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.function.base.Name;

@RunWith(QuackeryRunner.class)
public class TypeSystemTest {
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
    boolean canConvert = from.equals(to) ||
        from.equals(FILE) && to.equals(BLOB) ||
        from.equals(arrayOf(FILE)) && to.equals(arrayOf(BLOB)) ||
        from.equals(arrayOf(NOTHING)) && (to instanceof ArrayType);
    String canOrCannot = canConvert ? "can" : "cannot";
    TypeSystem typeSystem = new TypeSystem();
    return newCase(format("{0} convert from {1} to {2}", canOrCannot, from, to),
        () -> assertEquals(typeSystem.canConvert(from, to), canConvert));
  }

  @Quackery
  public static Suite test_convert_function_name() {
    return suite("test convert function name")
        .add(testConvertFunctionName(FILE, BLOB, "fileToBlob"))
        .add(testConvertFunctionName(arrayOf(FILE), arrayOf(BLOB), "fileArrayToBlobArray"))
        .add(testConvertFunctionName(arrayOf(NOTHING), arrayOf(STRING), "nilToStringArray"))
        .add(testConvertFunctionName(arrayOf(NOTHING), arrayOf(BLOB), "nilToBlobArray"))
        .add(testConvertFunctionName(arrayOf(NOTHING), arrayOf(FILE), "nilToFileArray"));
  }

  private static Case testConvertFunctionName(Type from, Type to, String functionName) {
    return newCase(format("{0} to {1} is named {2}", from, to, functionName),
        () -> assertEquals(new TypeSystem().convertFunctionName(from, to), new Name(functionName)));
  }
}
