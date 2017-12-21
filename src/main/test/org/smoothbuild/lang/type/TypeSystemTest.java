package org.smoothbuild.lang.type;

import static java.text.MessageFormat.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableSet;

@RunWith(QuackeryRunner.class)
public class TypeSystemTest {
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();
  private static final Type BLOB = TYPE_SYSTEM.blob();
  private static final Type FILE = TYPE_SYSTEM.file();
  private static final Type NOTHING = TYPE_SYSTEM.nothing();

  private TypeSystem typeSystem;

  @Quackery
  public static Suite can_convert() {
    Suite suite = suite("test canConvert");
    ImmutableSet<Type> types = ImmutableSet.of(STRING, BLOB, FILE, NOTHING, arrayOf(STRING),
        arrayOf(BLOB), arrayOf(FILE), arrayOf(NOTHING));
    for (Type from : types) {
      for (Type to : types) {
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

  @Test
  public void string_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("String"));
    thenReturned(typeSystem.string());
  }

  @Test
  public void blob_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("Blob"));
    thenReturned(typeSystem.blob());
  }

  @Test
  public void file_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("File"));
    thenReturned(typeSystem.file());
  }

  @Test
  public void nothing_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("Nothing"));
    thenReturned(typeSystem.nothing());
  }

  @Test
  public void string_array_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("[String]"));
    thenReturned(null);
  }

  @Test
  public void blob_array_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("[Blob]"));
    thenReturned(null);
  }

  @Test
  public void file_array_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("[File]"));
    thenReturned(null);
  }

  @Test
  public void nil_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("[Nothing]"));
    thenReturned(null);
  }

  @Test
  public void unknown_type_basic_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.basicTypeFromString("notAType"));
    thenReturned(null);
  }
}
