package org.smoothbuild.lang.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;

@RunWith(QuackeryRunner.class)
public class TypesTest {
  private static final TypesDb typesDb = new TestingTypesDb();
  private static final Type type = typesDb.type();
  private static final Type string = typesDb.string();
  private static final Type blob = typesDb.blob();
  private static final Type generic = typesDb.generic("a");

  @Test
  public void name() {
    assertEquals("Type", type.name());
    assertEquals("String", string.name());
    assertEquals("Blob", blob.name());
    assertEquals("Person", personType().name());
    assertEquals("a", generic.name());

    assertEquals("[Type]", array(type).name());
    assertEquals("[String]", array(string).name());
    assertEquals("[Blob]", array(blob).name());
    assertEquals("[Person]", array(personType()).name());
    assertEquals("[a]", array(generic).name());

    assertEquals("[[Type]]", array(array(type)).name());
    assertEquals("[[String]]", array(array(string)).name());
    assertEquals("[[Blob]]", array(array(blob)).name());
    assertEquals("[[Person]]", array(array(personType())).name());
    assertEquals("[[a]]", array(array(generic)).name());
  }

  @Test
  public void to_string() {
    assertEquals("Type(\"Type\"):" + type.hash(), type.toString());
    assertEquals("Type(\"String\"):" + string.hash(), string.toString());
    assertEquals("Type(\"Blob\"):" + blob.hash(), blob.toString());
    assertEquals("Type(\"Person\"):" + personType().hash(), personType().toString());
    assertEquals("Type(\"a\"):" + generic.hash(), generic.toString());

    assertEquals("Type(\"[Type]\"):" + array(type).hash(), array(type).toString());
    assertEquals("Type(\"[String]\"):" + array(string).hash(), array(string).toString());
    assertEquals("Type(\"[Blob]\"):" + array(blob).hash(), array(blob).toString());
    assertEquals("Type(\"[Person]\"):" + array(personType()).hash(),
        array(personType()).toString());
    assertEquals("Type(\"[a]\"):" + array(generic).hash(), array(generic).toString());

    assertEquals("Type(\"[[Type]]\"):" + array(array(type)).hash(), array(array(type)).toString());
    assertEquals("Type(\"[[String]]\"):" + array(array(string)).hash(),
        array(array(string)).toString());
    assertEquals("Type(\"[[Blob]]\"):" + array(array(blob)).hash(), array(array(blob)).toString());
    assertEquals("Type(\"[[Person]]\"):" + array(array(personType())).hash(),
        array(array(personType())).toString());
    assertEquals("Type(\"[[a]]\"):" + array(array(generic)).hash(),
        array(array(generic)).toString());
  }

  @Test
  public void jType() {
    assertEquals(Type.class, type.jType());
    assertEquals(SString.class, string.jType());
    assertEquals(Blob.class, blob.jType());
    assertEquals(Value.class, generic.jType());
    assertEquals(Array.class, array(type).jType());
    assertEquals(Array.class, array(string).jType());
    assertEquals(Array.class, array(blob).jType());
    assertEquals(Array.class, array(generic).jType());
  }

  @Test
  public void core_type() throws Exception {
    assertEquals(type, type.coreType());
    assertEquals(string, string.coreType());
    assertEquals(personType(), personType().coreType());
    assertEquals(generic, generic.coreType());

    assertEquals(string, array(string).coreType());
    assertEquals(personType(), array(personType()).coreType());
    assertEquals(generic, array(generic).coreType());

    assertEquals(string, array(array(string)).coreType());
    assertEquals(personType(), array(array(personType())).coreType());
    assertEquals(generic, array(array(generic)).coreType());
  }

  @Test
  public void core_depth() throws Exception {
    assertEquals(0, type.coreDepth());
    assertEquals(0, string.coreDepth());
    assertEquals(0, personType().coreDepth());
    assertEquals(0, generic.coreDepth());

    assertEquals(1, array(string).coreDepth());
    assertEquals(1, array(personType()).coreDepth());
    assertEquals(1, array(generic).coreDepth());

    assertEquals(2, array(array(string)).coreDepth());
    assertEquals(2, array(array(personType())).coreDepth());
    assertEquals(2, array(array(generic)).coreDepth());
  }

  @Test
  public void super_type() throws Exception {
    assertEquals(null, type.superType());
    assertEquals(null, string.superType());
    assertEquals(null, blob.superType());
    assertEquals(string, personType().superType());
    assertEquals(null, generic.superType());

    assertEquals(null, array(type).superType());
    assertEquals(null, array(string).superType());
    assertEquals(null, array(blob).superType());
    assertEquals(array(string), array(personType()).superType());
    assertEquals(null, array(generic).superType());

    assertEquals(null, array(array(type)).superType());
    assertEquals(null, array(array(string)).superType());
    assertEquals(null, array(array(blob)).superType());
    assertEquals(array(array(string)), array(array(personType())).superType());
    assertEquals(null, array(array(generic)).superType());
  }

  @Test
  public void hierarchy() throws Exception {
    assertHierarchy(list(string));
    assertHierarchy(list(string, personType()));
    assertHierarchy(list(generic));
    assertHierarchy(list(array(string)));
    assertHierarchy(list(array(string), array(personType())));
    assertHierarchy(list(array(generic)));
    assertHierarchy(list(array(array(string))));
    assertHierarchy(list(array(array(string)), array(array(personType()))));
    assertHierarchy(list(array(array(generic))));
  }

  private static void assertHierarchy(List<Type> hierarchy) {
    Type type;
    given(type = hierarchy.get(hierarchy.size() - 1));
    when(() -> type.hierarchy());
    thenReturned(hierarchy);
  }

  @Test
  public void is_generic() throws Exception {
    assertTrue(generic.isGeneric());
    assertFalse(type.isGeneric());
    assertFalse(string.isGeneric());
    assertFalse(blob.isGeneric());
    assertFalse(personType().isGeneric());
    assertFalse(array(generic).isGeneric());
    assertFalse(array(type).isGeneric());
    assertFalse(array(string).isGeneric());
    assertFalse(array(blob).isGeneric());
    assertFalse(array(personType()).isGeneric());
  }

  @Test
  public void is_assignable_from() throws Exception {
    List<Type> types = list(
        type,
        array(type),
        array(array(type)),
        string,
        array(string),
        array(array(string)),
        blob,
        array(blob),
        array(array(blob)),
        personType(),
        array(personType()),
        array(array(personType())),
        generic,
        array(generic),
        array(array(generic)));
    Set<Conversion> conversions = ImmutableSet.of(
        new Conversion(type, type),
        new Conversion(type, generic),
        new Conversion(string, string),
        new Conversion(string, personType()),
        new Conversion(string, generic),
        new Conversion(blob, blob),
        new Conversion(blob, generic),
        new Conversion(personType(), personType()),
        new Conversion(personType(), generic),
        new Conversion(generic, generic),

        new Conversion(array(type), array(type)),
        new Conversion(array(type), array(generic)),
        new Conversion(array(type), generic),
        new Conversion(array(string), array(string)),
        new Conversion(array(string), array(personType())),
        new Conversion(array(string), array(generic)),
        new Conversion(array(string), generic),
        new Conversion(array(blob), array(blob)),
        new Conversion(array(blob), array(generic)),
        new Conversion(array(blob), generic),
        new Conversion(array(personType()), array(personType())),
        new Conversion(array(personType()), array(generic)),
        new Conversion(array(personType()), generic),
        new Conversion(array(generic), array(generic)),
        new Conversion(array(generic), generic),

        new Conversion(array(array(type)), array(array(type))),
        new Conversion(array(array(type)), array(array(generic))),
        new Conversion(array(array(type)), array(generic)),
        new Conversion(array(array(type)), generic),
        new Conversion(array(array(string)), array(array(string))),
        new Conversion(array(array(string)), array(array(personType()))),
        new Conversion(array(array(string)), array(array(generic))),
        new Conversion(array(array(string)), array(generic)),
        new Conversion(array(array(string)), generic),
        new Conversion(array(array(blob)), array(array(blob))),
        new Conversion(array(array(blob)), array(array(generic))),
        new Conversion(array(array(blob)), array(generic)),
        new Conversion(array(array(blob)), generic),
        new Conversion(array(array(personType())), array(array(personType()))),
        new Conversion(array(array(personType())), array(array(generic))),
        new Conversion(array(array(personType())), array(generic)),
        new Conversion(array(array(personType())), generic),
        new Conversion(array(array(generic)), array(array(generic))),
        new Conversion(array(array(generic)), array(generic)),
        new Conversion(array(array(generic)), generic));
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
    assertCommon(type, generic, type);
    assertCommon(type, array(type), null);
    assertCommon(type, array(string), null);
    assertCommon(type, array(blob), null);
    assertCommon(type, array(generic), null);

    assertCommon(string, string, string);
    assertCommon(string, blob, null);
    assertCommon(string, generic, string);
    assertCommon(string, array(string), null);
    assertCommon(string, array(type), null);
    assertCommon(string, array(blob), null);
    assertCommon(string, array(generic), null);

    assertCommon(blob, blob, blob);
    assertCommon(blob, generic, blob);
    assertCommon(blob, array(type), null);
    assertCommon(blob, array(string), null);
    assertCommon(blob, array(blob), null);
    assertCommon(blob, array(generic), null);

    assertCommon(generic, generic, generic);
    assertCommon(generic, array(type), array(type));
    assertCommon(generic, array(string), array(string));
    assertCommon(generic, array(blob), array(blob));
    assertCommon(generic, array(generic), array(generic));

    assertCommon(array(type), array(type), array(type));
    assertCommon(array(type), array(string), null);
    assertCommon(array(type), array(blob), null);
    assertCommon(array(type), array(generic), array(type));
    assertCommon(array(string), array(string), array(string));
    assertCommon(array(string), array(blob), null);
    assertCommon(array(string), array(generic), array(string));
    assertCommon(array(string), generic, array(string));

    assertCommon(array(blob), array(blob), array(blob));
    assertCommon(array(blob), array(generic), array(blob));
    assertCommon(array(blob), generic, array(blob));

    assertCommon(array(generic), array(generic), array(generic));
    assertCommon(array(generic), generic, array(generic));
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
    assertEquals(generic, array(generic).elemType());

    assertEquals(array(type), array(array(type)).elemType());
    assertEquals(array(string), array(array(string)).elemType());
    assertEquals(array(blob), array(array(blob)).elemType());
    assertEquals(array(personType()), array(array(personType())).elemType());
    assertEquals(array(generic), array(array(generic)).elemType());
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(typesDb.type(), typesDb.type());
    tester.addEqualityGroup(typesDb.string(), typesDb.string());
    tester.addEqualityGroup(typesDb.blob(), typesDb.blob());
    tester.addEqualityGroup(personType(), personType());
    tester.addEqualityGroup(typesDb.generic("a"), typesDb.generic("a"));

    tester.addEqualityGroup(array(type), array(type));
    tester.addEqualityGroup(array(string), array(string));
    tester.addEqualityGroup(array(blob), array(blob));
    tester.addEqualityGroup(array(personType()), array(personType()));
    tester.addEqualityGroup(array(generic), array(generic));

    tester.addEqualityGroup(array(array(type)), array(array(type)));
    tester.addEqualityGroup(array(array(string)), array(array(string)));
    tester.addEqualityGroup(array(array(blob)), array(array(blob)));
    tester.addEqualityGroup(array(array(personType())), array(array(personType())));
    tester.addEqualityGroup(array(array(generic)), array(array(generic)));
    tester.testEquals();
  }

  private StructType personType() {
    return typesDb.struct(
        "Person", ImmutableMap.of("firstName", string, "lastName", string));
  }

  private ArrayType array(Type elementType) {
    return typesDb.array(elementType);
  }

  @Test
  public void type_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("Type"));
    thenReturned(null);
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
    when(typesDb.nonArrayTypeFromString("[a]"));
    thenReturned(null);
  }

  @Test
  public void unknown_type_non_array_type_from_string() throws Exception {
    when(typesDb.nonArrayTypeFromString("notAType"));
    thenReturned(null);
  }
}
