package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
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
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;

public class TypesTest {
  private final TypeSystem typeSystem = new TypeSystem();
  private final Type type = typeSystem.type();
  private final Type string = typeSystem.string();
  private final Type blob = typeSystem.blob();
  private final Type file = typeSystem.file();
  private final Type nothing = typeSystem.nothing();

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
    assertEquals("Type", type.toString());
    assertEquals("String", string.toString());
    assertEquals("Blob", blob.toString());
    assertEquals("File", file.toString());
    assertEquals("Nothing", nothing.toString());

    assertEquals("[Type]", array(type).toString());
    assertEquals("[String]", array(string).toString());
    assertEquals("[Blob]", array(blob).toString());
    assertEquals("[File]", array(file).toString());
    assertEquals("[Nothing]", array(nothing).toString());

    assertEquals("[[Type]]", array(array(type)).toString());
    assertEquals("[[String]]", array(array(string)).toString());
    assertEquals("[[Blob]]", array(array(blob)).toString());
    assertEquals("[[File]]", array(array(file)).toString());
    assertEquals("[[Person]]", array(array(personType())).toString());
    assertEquals("[[Nothing]]", array(array(nothing)).toString());
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

  @Test
  public void direct_convertible_to() throws Exception {
    assertEquals(null, type.directConvertibleTo());
    assertEquals(null, string.directConvertibleTo());
    assertEquals(null, blob.directConvertibleTo());
    assertEquals(blob, file.directConvertibleTo());
    assertEquals(string, personType().directConvertibleTo());
    assertEquals(null, nothing.directConvertibleTo());

    assertEquals(null, array(type).directConvertibleTo());
    assertEquals(null, array(string).directConvertibleTo());
    assertEquals(null, array(blob).directConvertibleTo());
    assertEquals(array(blob), array(file).directConvertibleTo());
    assertEquals(array(string), array(personType()).directConvertibleTo());
    assertEquals(null, array(nothing).directConvertibleTo());

    assertEquals(null, array(array(type)).directConvertibleTo());
    assertEquals(null, array(array(string)).directConvertibleTo());
    assertEquals(null, array(array(blob)).directConvertibleTo());
    assertEquals(array(array(blob)), array(array(file)).directConvertibleTo());
    assertEquals(array(array(string)), array(array(personType())).directConvertibleTo());
    assertEquals(null, array(array(nothing)).directConvertibleTo());
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

    assertCommon(array(file), typeSystem.struct("Struct", ImmutableMap.of("field", array(blob))),
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
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(typeSystem.type(), typeSystem.type());
    tester.addEqualityGroup(typeSystem.string(), typeSystem.string());
    tester.addEqualityGroup(typeSystem.blob(), typeSystem.blob());
    tester.addEqualityGroup(typeSystem.file(), typeSystem.file());
    tester.addEqualityGroup(personType(), personType());
    tester.addEqualityGroup(typeSystem.nothing(), typeSystem.nothing());

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
    tester.addEqualityGroup(array(array(personType())), typeSystem.array(array(personType())));
    tester.addEqualityGroup(array(array(nothing)), array(array(nothing)));
    tester.testEquals();
  }

  private StructType personType() {
    return typeSystem.struct(
        "Person", ImmutableMap.of("firstName", string, "lastName", string));
  }

  private ArrayType array(Type elementType) {
    return typeSystem.array(elementType);
  }
}
