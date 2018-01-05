package org.smoothbuild.lang.type;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;

@RunWith(QuackeryRunner.class)
public class TypesTest {
  private static final TypesDb typesDb = new TypesDb();
  private static final Type type = typesDb.type();
  private static final Type string = typesDb.string();
  private static final Type blob = typesDb.blob();
  private static final Type file = typesDb.file();
  private static final Type nothing = typesDb.nothing();

  @Test
  public void name() {
    assertEquals("Type", type.name());
    assertEquals("String", string.name());
    assertEquals("Blob", blob.name());
    assertEquals("File", file.name());
    assertEquals("Person", personType().name());
    assertEquals("Nothing", nothing.name());

    assertEquals("[Type]", array(type).name());
    assertEquals("[String]", array(string).name());
    assertEquals("[Blob]", array(blob).name());
    assertEquals("[File]", array(file).name());
    assertEquals("[Person]", array(personType()).name());
    assertEquals("[Nothing]", array(nothing).name());

    assertEquals("[[Type]]", array(array(type)).name());
    assertEquals("[[String]]", array(array(string)).name());
    assertEquals("[[Blob]]", array(array(blob)).name());
    assertEquals("[[File]]", array(array(file)).name());
    assertEquals("[[Person]]", array(array(personType())).name());
    assertEquals("[[Nothing]]", array(array(nothing)).name());
  }

  @Test
  public void to_string() {
    assertEquals("Type(\"Type\"):" + type.hash(), type.toString());
    assertEquals("Type(\"String\"):" + string.hash(), string.toString());
    assertEquals("Type(\"Blob\"):" + blob.hash(), blob.toString());
    assertEquals("Type(\"File\"):" + file.hash(), file.toString());
    assertEquals("Type(\"Person\"):" + personType().hash(), personType().toString());
    assertEquals("Type(\"Nothing\"):" + nothing.hash(), nothing.toString());

    assertEquals("Type(\"[Type]\"):" + array(type).hash(), array(type).toString());
    assertEquals("Type(\"[String]\"):" + array(string).hash(), array(string).toString());
    assertEquals("Type(\"[Blob]\"):" + array(blob).hash(), array(blob).toString());
    assertEquals("Type(\"[File]\"):" + array(file).hash(), array(file).toString());
    assertEquals("Type(\"[Person]\"):" + array(personType()).hash(),
        array(personType()).toString());
    assertEquals("Type(\"[Nothing]\"):" + array(nothing).hash(), array(nothing).toString());

    assertEquals("Type(\"[[Type]]\"):" + array(array(type)).hash(), array(array(type)).toString());
    assertEquals("Type(\"[[String]]\"):" + array(array(string)).hash(),
        array(array(string)).toString());
    assertEquals("Type(\"[[Blob]]\"):" + array(array(blob)).hash(), array(array(blob)).toString());
    assertEquals("Type(\"[[File]]\"):" + array(array(file)).hash(), array(array(file)).toString());
    assertEquals("Type(\"[[Person]]\"):" + array(array(personType())).hash(),
        array(array(personType())).toString());
    assertEquals("Type(\"[[Nothing]]\"):" + array(array(nothing)).hash(),
        array(array(nothing)).toString());
  }

  @Test
  public void jType() {
    assertEquals(Type.class, type.jType());
    assertEquals(SString.class, string.jType());
    assertEquals(Blob.class, blob.jType());
    assertEquals(Struct.class, file.jType());
    assertEquals(Nothing.class, nothing.jType());
    assertEquals(Array.class, array(type).jType());
    assertEquals(Array.class, array(string).jType());
    assertEquals(Array.class, array(blob).jType());
    assertEquals(Array.class, array(file).jType());
    assertEquals(Array.class, array(nothing).jType());
  }

  @Test
  public void core_type() throws Exception {
    assertEquals(type, type.coreType());
    assertEquals(string, string.coreType());
    assertEquals(personType(), personType().coreType());
    assertEquals(nothing, nothing.coreType());

    assertEquals(string, array(string).coreType());
    assertEquals(personType(), array(personType()).coreType());
    assertEquals(nothing, array(nothing).coreType());

    assertEquals(string, array(array(string)).coreType());
    assertEquals(personType(), array(array(personType())).coreType());
    assertEquals(nothing, array(array(nothing)).coreType());
  }

  @Test
  public void core_depth() throws Exception {
    assertEquals(0, type.coreDepth());
    assertEquals(0, string.coreDepth());
    assertEquals(0, personType().coreDepth());
    assertEquals(0, nothing.coreDepth());

    assertEquals(1, array(string).coreDepth());
    assertEquals(1, array(personType()).coreDepth());
    assertEquals(1, array(nothing).coreDepth());

    assertEquals(2, array(array(string)).coreDepth());
    assertEquals(2, array(array(personType())).coreDepth());
    assertEquals(2, array(array(nothing)).coreDepth());
  }

  @Test
  public void super_type() throws Exception {
    assertEquals(null, type.superType());
    assertEquals(null, string.superType());
    assertEquals(null, blob.superType());
    assertEquals(blob, file.superType());
    assertEquals(string, personType().superType());
    assertEquals(null, nothing.superType());

    assertEquals(null, array(type).superType());
    assertEquals(null, array(string).superType());
    assertEquals(null, array(blob).superType());
    assertEquals(array(blob), array(file).superType());
    assertEquals(array(string), array(personType()).superType());
    assertEquals(null, array(nothing).superType());

    assertEquals(null, array(array(type)).superType());
    assertEquals(null, array(array(string)).superType());
    assertEquals(null, array(array(blob)).superType());
    assertEquals(array(array(blob)), array(array(file)).superType());
    assertEquals(array(array(string)), array(array(personType())).superType());
    assertEquals(null, array(array(nothing)).superType());
  }

  @Test
  public void hierarchy() throws Exception {
    assertHierarchy(list(string));
    assertHierarchy(list(string, personType()));
    assertHierarchy(list(nothing));
    assertHierarchy(list(array(string)));
    assertHierarchy(list(array(string), array(personType())));
    assertHierarchy(list(array(nothing)));
    assertHierarchy(list(array(array(string))));
    assertHierarchy(list(array(array(string)), array(array(personType()))));
    assertHierarchy(list(array(array(nothing))));
  }

  private static void assertHierarchy(List<Type> hierarchy) {
    Type type;
    given(type = hierarchy.get(hierarchy.size() - 1));
    when(() -> type.hierarchy());
    thenReturned(hierarchy);
  }

  @Test
  public void is_nothing() throws Exception {
    assertTrue(nothing.isNothing());
    assertFalse(type.isNothing());
    assertFalse(string.isNothing());
    assertFalse(blob.isNothing());
    assertFalse(file.isNothing());
    assertFalse(personType().isNothing());
    assertFalse(array(nothing).isNothing());
    assertFalse(array(type).isNothing());
    assertFalse(array(string).isNothing());
    assertFalse(array(blob).isNothing());
    assertFalse(array(file).isNothing());
    assertFalse(array(personType()).isNothing());
  }

  @Test
  public void is_assignable_from() throws Exception {
    List<Type> types = asList(
        type,
        array(type),
        array(array(type)),
        string,
        array(string),
        array(array(string)),
        blob,
        array(blob),
        array(array(blob)),
        file,
        array(file),
        array(array(file)),
        personType(),
        array(personType()),
        array(array(personType())),
        nothing,
        array(nothing),
        array(array(nothing)));
    Set<Conversion> conversions = ImmutableSet.of(
        new Conversion(type, type),
        new Conversion(type, nothing),
        new Conversion(string, string),
        new Conversion(string, personType()),
        new Conversion(string, nothing),
        new Conversion(blob, blob),
        new Conversion(blob, nothing),
        new Conversion(blob, file),
        new Conversion(file, file),
        new Conversion(file, nothing),
        new Conversion(personType(), personType()),
        new Conversion(personType(), nothing),
        new Conversion(nothing, nothing),

        new Conversion(array(type), array(type)),
        new Conversion(array(type), array(nothing)),
        new Conversion(array(type), nothing),
        new Conversion(array(string), array(string)),
        new Conversion(array(string), array(personType())),
        new Conversion(array(string), array(nothing)),
        new Conversion(array(string), nothing),
        new Conversion(array(blob), array(blob)),
        new Conversion(array(blob), array(nothing)),
        new Conversion(array(blob), nothing),
        new Conversion(array(blob), array(file)),
        new Conversion(array(file), array(file)),
        new Conversion(array(file), array(nothing)),
        new Conversion(array(file), nothing),
        new Conversion(array(personType()), array(personType())),
        new Conversion(array(personType()), array(nothing)),
        new Conversion(array(personType()), nothing),
        new Conversion(array(nothing), array(nothing)),
        new Conversion(array(nothing), nothing),

        new Conversion(array(array(type)), array(array(type))),
        new Conversion(array(array(type)), array(array(nothing))),
        new Conversion(array(array(type)), array(nothing)),
        new Conversion(array(array(type)), nothing),
        new Conversion(array(array(string)), array(array(string))),
        new Conversion(array(array(string)), array(array(personType()))),
        new Conversion(array(array(string)), array(array(nothing))),
        new Conversion(array(array(string)), array(nothing)),
        new Conversion(array(array(string)), nothing),
        new Conversion(array(array(blob)), array(array(blob))),
        new Conversion(array(array(blob)), array(array(nothing))),
        new Conversion(array(array(blob)), array(array(file))),
        new Conversion(array(array(blob)), array(nothing)),
        new Conversion(array(array(blob)), nothing),
        new Conversion(array(array(file)), array(array(file))),
        new Conversion(array(array(file)), array(array(nothing))),
        new Conversion(array(array(file)), array(nothing)),
        new Conversion(array(array(file)), nothing),
        new Conversion(array(array(personType())), array(array(personType()))),
        new Conversion(array(array(personType())), array(array(nothing))),
        new Conversion(array(array(personType())), array(nothing)),
        new Conversion(array(array(personType())), nothing),
        new Conversion(array(array(nothing)), array(array(nothing))),
        new Conversion(array(array(nothing)), array(nothing)),
        new Conversion(array(array(nothing)), nothing));
    for (Type destination : types) {
      for (Type source : types) {
        boolean expected = conversions.contains(new Conversion(destination, source));
        assertEquals(destination.toString() + ".isAssignableFrom(" + source + ")",
            expected,
            destination.isAssignableFrom(source));
      }
    }
  }

  private static class Conversion {
    private final Type destination;
    private final Type source;

    public Conversion(Type destination, Type source) {
      this.destination = destination;
      this.source = source;
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof Conversion)) {
        return false;
      }
      Conversion that = (Conversion) object;
      return this.destination.equals(that.destination)
          && this.source.equals(that.source);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(destination, source);
    }
  }

  @Test
  public void common_super_type() throws Exception {
    assertCommon(type, type, type);
    assertCommon(type, string, null);
    assertCommon(type, blob, null);
    assertCommon(type, file, null);
    assertCommon(type, nothing, type);
    assertCommon(type, array(type), null);
    assertCommon(type, array(string), null);
    assertCommon(type, array(blob), null);
    assertCommon(type, array(file), null);
    assertCommon(type, array(nothing), null);

    assertCommon(string, string, string);
    assertCommon(string, blob, null);
    assertCommon(string, file, null);
    assertCommon(string, nothing, string);
    assertCommon(string, array(string), null);
    assertCommon(string, array(type), null);
    assertCommon(string, array(blob), null);
    assertCommon(string, array(file), null);
    assertCommon(string, array(nothing), null);

    assertCommon(blob, blob, blob);
    assertCommon(blob, file, blob);
    assertCommon(blob, nothing, blob);
    assertCommon(blob, array(type), null);
    assertCommon(blob, array(string), null);
    assertCommon(blob, array(blob), null);
    assertCommon(blob, array(file), null);
    assertCommon(blob, array(nothing), null);

    assertCommon(file, file, file);
    assertCommon(file, nothing, file);
    assertCommon(file, array(type), null);
    assertCommon(file, array(string), null);
    assertCommon(file, array(blob), null);
    assertCommon(file, array(file), null);
    assertCommon(file, array(nothing), null);

    assertCommon(nothing, nothing, nothing);
    assertCommon(nothing, array(type), array(type));
    assertCommon(nothing, array(string), array(string));
    assertCommon(nothing, array(blob), array(blob));
    assertCommon(nothing, array(file), array(file));
    assertCommon(nothing, array(nothing), array(nothing));

    assertCommon(array(type), array(type), array(type));
    assertCommon(array(type), array(string), null);
    assertCommon(array(type), array(blob), null);
    assertCommon(array(type), array(file), null);
    assertCommon(array(type), array(nothing), array(type));
    assertCommon(array(string), array(string), array(string));
    assertCommon(array(string), array(blob), null);
    assertCommon(array(string), array(file), null);
    assertCommon(array(string), array(nothing), array(string));
    assertCommon(array(string), nothing, array(string));

    assertCommon(array(blob), array(blob), array(blob));
    assertCommon(array(blob), array(file), array(blob));
    assertCommon(array(blob), array(nothing), array(blob));
    assertCommon(array(blob), nothing, array(blob));

    assertCommon(array(file), array(file), array(file));
    assertCommon(array(file), array(nothing), array(file));
    assertCommon(array(file), nothing, array(file));

    assertCommon(array(nothing), array(nothing), array(nothing));
    assertCommon(array(nothing), nothing, array(nothing));

    assertCommon(array(file), typesDb.struct("Struct", ImmutableMap.of("field", array(blob))),
        array(blob));
  }

  private static void assertCommon(Type type1, Type type2, Type expected) {
    assertCommonSuperTypeImpl(type1, type2, expected);
    assertCommonSuperTypeImpl(type2, type1, expected);
  }

  private static void assertCommonSuperTypeImpl(Type type1, Type type2, Type expected) {
    when(() -> type1.commonSuperType(type2));
    thenReturned(expected);
  }

  @Test
  public void array_element_types() {
    assertEquals(type, array(type).elemType());
    assertEquals(string, array(string).elemType());
    assertEquals(blob, array(blob).elemType());
    assertEquals(personType(), array(personType()).elemType());
    assertEquals(nothing, array(nothing).elemType());

    assertEquals(array(type), array(array(type)).elemType());
    assertEquals(array(string), array(array(string)).elemType());
    assertEquals(array(blob), array(array(blob)).elemType());
    assertEquals(array(personType()), array(array(personType())).elemType());
    assertEquals(array(nothing), array(array(nothing)).elemType());
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(typesDb.type(), typesDb.type());
    tester.addEqualityGroup(typesDb.string(), typesDb.string());
    tester.addEqualityGroup(typesDb.blob(), typesDb.blob());
    tester.addEqualityGroup(typesDb.file(), typesDb.file());
    tester.addEqualityGroup(personType(), personType());
    tester.addEqualityGroup(typesDb.nothing(), typesDb.nothing());

    tester.addEqualityGroup(array(type), array(type));
    tester.addEqualityGroup(array(string), array(string));
    tester.addEqualityGroup(array(blob), array(blob));
    tester.addEqualityGroup(array(file), array(file));
    tester.addEqualityGroup(array(personType()), array(personType()));
    tester.addEqualityGroup(array(nothing), array(nothing));

    tester.addEqualityGroup(array(array(type)), array(array(type)));
    tester.addEqualityGroup(array(array(string)), array(array(string)));
    tester.addEqualityGroup(array(array(blob)), array(array(blob)));
    tester.addEqualityGroup(array(array(file)), array(array(file)));
    tester.addEqualityGroup(array(array(personType())), array(array(personType())));
    tester.addEqualityGroup(array(array(nothing)), array(array(nothing)));
    tester.testEquals();
  }

  private StructType personType() {
    return typesDb.struct(
        "Person", ImmutableMap.of("firstName", string, "lastName", string));
  }

  private ArrayType array(Type elementType) {
    return typesDb.array(elementType);
  }

  @Quackery
  public static Suite can_convert() {
    Suite suite = suite("test canConvert");
    TypesDb typesDb = new TypesDb();
    ImmutableSet<Type> types = ImmutableSet.of(string, blob, file, nothing,
        typesDb.array(string), typesDb.array(blob), typesDb.array(file),
        typesDb.array(nothing));
    for (Type from : types) {
      for (Type to : types) {
        suite = suite.add(testConversion(from, to));
      }
    }
    return suite;
  }

  private static Case testConversion(Type from, Type to) {
    TypesDb typesDb = new TypesDb();
    boolean canConvert = from.equals(to) ||
        from.equals(file) && to.equals(blob) ||
        from.equals(typesDb.array(file)) && to.equals(typesDb.array(blob)) ||
        from.equals(typesDb.array(nothing)) && (to instanceof ArrayType);
    String canOrCannot = canConvert ? "can" : "cannot";
    return newCase(format("{0} convert from {1} to {2}", canOrCannot, from, to),
        () -> assertEquals(typesDb.canConvert(from, to), canConvert));
  }

  @Quackery
  public static Suite test_convert_function_name() {
    TypesDb db = new TypesDb();
    return suite("test convert function name")
        .add(testConvertFunctionName(file, blob, "fileToBlob"))
        .add(testConvertFunctionName(db.array(file), db.array(blob), "fileArrayToBlobArray"))
        .add(testConvertFunctionName(db.array(nothing), db.array(string), "nilToStringArray"))
        .add(testConvertFunctionName(db.array(nothing), db.array(blob), "nilToBlobArray"))
        .add(testConvertFunctionName(db.array(nothing), db.array(file), "nilToFileArray"));
  }

  private static Case testConvertFunctionName(Type from, Type to, String functionName) {
    return newCase(format("{0} to {1} is named {2}", from, to, functionName),
        () -> assertEquals(new TypesDb().convertFunctionName(from, to), new Name(
            functionName)));
  }

  @Test
  public void type_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("Type"));
    thenReturned(typesDb.type());
  }

  @Test
  public void string_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("String"));
    thenReturned(typesDb.string());
  }

  @Test
  public void blob_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("Blob"));
    thenReturned(typesDb.blob());
  }

  @Test
  public void file_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("File"));
    thenReturned(typesDb.file());
  }

  @Test
  public void nothing_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("Nothing"));
    thenReturned(typesDb.nothing());
  }

  @Test
  public void type_array_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("[Type]"));
    thenReturned(null);
  }

  @Test
  public void string_array_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("[String]"));
    thenReturned(null);
  }

  @Test
  public void blob_array_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("[Blob]"));
    thenReturned(null);
  }

  @Test
  public void file_array_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("[File]"));
    thenReturned(null);
  }

  @Test
  public void nil_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("[Nothing]"));
    thenReturned(null);
  }

  @Test
  public void unknown_type_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("notAType"));
    thenReturned(null);
  }
}
