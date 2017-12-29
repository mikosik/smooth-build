package org.smoothbuild.lang.type;

import static java.text.MessageFormat.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
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
    TypeSystem typeSystem = new TypeSystem();
    ImmutableSet<Type> types = ImmutableSet.of(STRING, BLOB, FILE, NOTHING,
        typeSystem.array(STRING), typeSystem.array(BLOB), typeSystem.array(FILE),
        typeSystem.array(NOTHING));
    for (Type from : types) {
      for (Type to : types) {
        suite = suite.add(testConversion(from, to));
      }
    }
    return suite;
  }

  private static Case testConversion(Type from, Type to) {
    TypeSystem typeSystem = new TypeSystem();
    boolean canConvert = from.equals(to) ||
        from.equals(FILE) && to.equals(BLOB) ||
        from.equals(typeSystem.array(FILE)) && to.equals(typeSystem.array(BLOB)) ||
        from.equals(typeSystem.array(NOTHING)) && (to instanceof ArrayType);
    String canOrCannot = canConvert ? "can" : "cannot";
    return newCase(format("{0} convert from {1} to {2}", canOrCannot, from, to),
        () -> assertEquals(typeSystem.canConvert(from, to), canConvert));
  }

  @Quackery
  public static Suite test_convert_function_name() {
    TypeSystem ts = new TypeSystem();
    return suite("test convert function name")
        .add(testConvertFunctionName(FILE, BLOB, "fileToBlob"))
        .add(testConvertFunctionName(ts.array(FILE), ts.array(BLOB), "fileArrayToBlobArray"))
        .add(testConvertFunctionName(ts.array(NOTHING), ts.array(STRING), "nilToStringArray"))
        .add(testConvertFunctionName(ts.array(NOTHING), ts.array(BLOB), "nilToBlobArray"))
        .add(testConvertFunctionName(ts.array(NOTHING), ts.array(FILE), "nilToFileArray"));
  }

  private static Case testConvertFunctionName(Type from, Type to, String functionName) {
    return newCase(format("{0} to {1} is named {2}", from, to, functionName),
        () -> assertEquals(new TypeSystem().convertFunctionName(from, to), new Name(
            functionName)));
  }

  @Test
  public void type_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("Type"));
    thenReturned(typeSystem.type());
  }

  @Test
  public void string_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("String"));
    thenReturned(typeSystem.string());
  }

  @Test
  public void blob_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("Blob"));
    thenReturned(typeSystem.blob());
  }

  @Test
  public void file_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("File"));
    thenReturned(typeSystem.file());
  }

  @Test
  public void nothing_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("Nothing"));
    thenReturned(typeSystem.nothing());
  }

  @Test
  public void type_array_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("[Type]"));
    thenReturned(null);
  }

  @Test
  public void string_array_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("[String]"));
    thenReturned(null);
  }

  @Test
  public void blob_array_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("[Blob]"));
    thenReturned(null);
  }

  @Test
  public void file_array_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("[File]"));
    thenReturned(null);
  }

  @Test
  public void nil_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("[Nothing]"));
    thenReturned(null);
  }

  @Test
  public void unknown_type_non_array_type_from_string() throws Exception {
    given(typeSystem = new TypeSystem());
    when(typeSystem.nonArrayTypeFromString("notAType"));
    thenReturned(null);
  }
}
