package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;
import java.util.Set;

import org.junit.Test;

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

  private ArrayType arrayType;

  @Test
  public void core_type() throws Exception {
    assertEquals(type, type.coreType());
    assertEquals(string, string.coreType());
    assertEquals(personType(), personType().coreType());
    assertEquals(nothing, nothing.coreType());

    assertEquals(string, typeSystem.array(string).coreType());
    assertEquals(personType(), typeSystem.array(personType()).coreType());
    assertEquals(nothing, typeSystem.array(nothing).coreType());

    assertEquals(string, typeSystem.array(typeSystem.array(string)).coreType());
    assertEquals(personType(), typeSystem.array(typeSystem.array(personType())).coreType());
    assertEquals(nothing, typeSystem.array(typeSystem.array(nothing)).coreType());
  }

  @Test
  public void core_depth() throws Exception {
    assertEquals(0, type.coreDepth());
    assertEquals(0, string.coreDepth());
    assertEquals(0, personType().coreDepth());
    assertEquals(0, nothing.coreDepth());

    assertEquals(1, typeSystem.array(string).coreDepth());
    assertEquals(1, typeSystem.array(personType()).coreDepth());
    assertEquals(1, typeSystem.array(nothing).coreDepth());

    assertEquals(2, typeSystem.array(typeSystem.array(string)).coreDepth());
    assertEquals(2, typeSystem.array(typeSystem.array(personType())).coreDepth());
    assertEquals(2, typeSystem.array(typeSystem.array(nothing)).coreDepth());
  }

  @Test
  public void hierarchy() throws Exception {
    assertEquals(list(string), string.hierarchy());
    assertEquals(list(string, personType()), personType().hierarchy());
    assertEquals(list(nothing), nothing.hierarchy());

    assertEquals(list(typeSystem.array(string)), typeSystem.array(string).hierarchy());
    assertEquals(list(typeSystem.array(string), typeSystem.array(personType())),
        typeSystem.array(personType()).hierarchy());
    assertEquals(list(typeSystem.array(nothing)), typeSystem.array(nothing).hierarchy());

    assertEquals(list(typeSystem.array(typeSystem.array(string))),
        typeSystem.array(typeSystem.array(string)).hierarchy());
    assertEquals(list(typeSystem.array(typeSystem.array(string)),
        typeSystem.array(typeSystem.array(personType()))),
        typeSystem.array(typeSystem.array(personType())).hierarchy());
    assertEquals(list(typeSystem.array(typeSystem.array(nothing))),
        typeSystem.array(typeSystem.array(nothing)).hierarchy());
  }

  @Test
  public void is_nothing() throws Exception {
    assertTrue(nothing.isNothing());
    assertFalse(type.isNothing());
    assertFalse(string.isNothing());
    assertFalse(blob.isNothing());
    assertFalse(file.isNothing());
    assertFalse(personType().isNothing());
    assertFalse(typeSystem.array(nothing).isNothing());
    assertFalse(typeSystem.array(type).isNothing());
    assertFalse(typeSystem.array(string).isNothing());
    assertFalse(typeSystem.array(blob).isNothing());
    assertFalse(typeSystem.array(file).isNothing());
    assertFalse(typeSystem.array(personType()).isNothing());
  }

  @Test
  public void is_assignable_from() throws Exception {
    List<Type> types = asList(
        type,
        typeSystem.array(type),
        typeSystem.array(typeSystem.array(type)),
        string,
        typeSystem.array(string),
        typeSystem.array(typeSystem.array(string)),
        blob,
        typeSystem.array(blob),
        typeSystem.array(typeSystem.array(blob)),
        file,
        typeSystem.array(file),
        typeSystem.array(typeSystem.array(file)),
        personType(),
        typeSystem.array(personType()),
        typeSystem.array(typeSystem.array(personType())),
        nothing,
        typeSystem.array(nothing),
        typeSystem.array(typeSystem.array(nothing)));
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

        new Conversion(typeSystem.array(type), typeSystem.array(type)),
        new Conversion(typeSystem.array(type), typeSystem.array(nothing)),
        new Conversion(typeSystem.array(type), nothing),
        new Conversion(typeSystem.array(string), typeSystem.array(string)),
        new Conversion(typeSystem.array(string), typeSystem.array(personType())),
        new Conversion(typeSystem.array(string), typeSystem.array(nothing)),
        new Conversion(typeSystem.array(string), nothing),
        new Conversion(typeSystem.array(blob), typeSystem.array(blob)),
        new Conversion(typeSystem.array(blob), typeSystem.array(nothing)),
        new Conversion(typeSystem.array(blob), nothing),
        new Conversion(typeSystem.array(blob), typeSystem.array(file)),
        new Conversion(typeSystem.array(file), typeSystem.array(file)),
        new Conversion(typeSystem.array(file), typeSystem.array(nothing)),
        new Conversion(typeSystem.array(file), nothing),
        new Conversion(typeSystem.array(personType()), typeSystem.array(personType())),
        new Conversion(typeSystem.array(personType()), typeSystem.array(nothing)),
        new Conversion(typeSystem.array(personType()), nothing),
        new Conversion(typeSystem.array(nothing), typeSystem.array(nothing)),
        new Conversion(typeSystem.array(nothing), nothing),

        new Conversion(
            typeSystem.array(typeSystem.array(type)),
            typeSystem.array(typeSystem.array(type))),
        new Conversion(
            typeSystem.array(typeSystem.array(type)),
            typeSystem.array(typeSystem.array(nothing))),
        new Conversion(
            typeSystem.array(typeSystem.array(type)),
            typeSystem.array(nothing)),
        new Conversion(
            typeSystem.array(typeSystem.array(type)),
            nothing),
        new Conversion(
            typeSystem.array(typeSystem.array(string)),
            typeSystem.array(typeSystem.array(string))),
        new Conversion(
            typeSystem.array(typeSystem.array(string)),
            typeSystem.array(typeSystem.array(personType()))),
        new Conversion(
            typeSystem.array(typeSystem.array(string)),
            typeSystem.array(typeSystem.array(nothing))),
        new Conversion(
            typeSystem.array(typeSystem.array(string)),
            typeSystem.array(nothing)),
        new Conversion(
            typeSystem.array(typeSystem.array(string)),
            nothing),
        new Conversion(
            typeSystem.array(typeSystem.array(blob)),
            typeSystem.array(typeSystem.array(blob))),
        new Conversion(
            typeSystem.array(typeSystem.array(blob)),
            typeSystem.array(typeSystem.array(nothing))),
        new Conversion(
            typeSystem.array(typeSystem.array(blob)),
            typeSystem.array(typeSystem.array(file))),
        new Conversion(
            typeSystem.array(typeSystem.array(blob)),
            typeSystem.array(nothing)),
        new Conversion(
            typeSystem.array(typeSystem.array(blob)),
            nothing),
        new Conversion(
            typeSystem.array(typeSystem.array(file)),
            typeSystem.array(typeSystem.array(file))),
        new Conversion(
            typeSystem.array(typeSystem.array(file)),
            typeSystem.array(typeSystem.array(nothing))),
        new Conversion(
            typeSystem.array(typeSystem.array(file)),
            typeSystem.array(nothing)),
        new Conversion(
            typeSystem.array(typeSystem.array(file)),
            nothing),
        new Conversion(
            typeSystem.array(typeSystem.array(personType())),
            typeSystem.array(typeSystem.array(personType()))),
        new Conversion(
            typeSystem.array(typeSystem.array(personType())),
            typeSystem.array(typeSystem.array(nothing))),
        new Conversion(
            typeSystem.array(typeSystem.array(personType())),
            typeSystem.array(nothing)),
        new Conversion(
            typeSystem.array(typeSystem.array(personType())),
            nothing),
        new Conversion(
            typeSystem.array(typeSystem.array(nothing)),
            typeSystem.array(typeSystem.array(nothing))),
        new Conversion(
            typeSystem.array(typeSystem.array(nothing)),
            typeSystem.array(nothing)),
        new Conversion(
            typeSystem.array(typeSystem.array(nothing)),
            nothing));
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
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(typeSystem.type(), typeSystem.type());
    tester.addEqualityGroup(typeSystem.string(), typeSystem.string());
    tester.addEqualityGroup(typeSystem.blob(), typeSystem.blob());
    tester.addEqualityGroup(typeSystem.file(), typeSystem.file());
    tester.addEqualityGroup(personType(), personType());
    tester.addEqualityGroup(typeSystem.nothing(), typeSystem.nothing());

    tester.addEqualityGroup(typeSystem.array(type), typeSystem.array(type));
    tester.addEqualityGroup(typeSystem.array(string), typeSystem.array(string));
    tester.addEqualityGroup(typeSystem.array(blob), typeSystem.array(blob));
    tester.addEqualityGroup(typeSystem.array(file), typeSystem.array(file));
    tester.addEqualityGroup(typeSystem.array(personType()), typeSystem.array(personType()));
    tester.addEqualityGroup(typeSystem.array(nothing), typeSystem.array(nothing));

    tester.addEqualityGroup(typeSystem.array(typeSystem.array(type)), typeSystem.array(
        typeSystem.array(type)));
    tester.addEqualityGroup(typeSystem.array(typeSystem.array(string)), typeSystem.array(
        typeSystem.array(string)));
    tester.addEqualityGroup(typeSystem.array(typeSystem.array(blob)), typeSystem.array(
        typeSystem.array(blob)));
    tester.addEqualityGroup(typeSystem.array(typeSystem.array(file)), typeSystem.array(
        typeSystem.array(file)));
    tester.addEqualityGroup(typeSystem.array(typeSystem.array(personType())), typeSystem
        .array(typeSystem.array(personType())));
    tester.addEqualityGroup(typeSystem.array(typeSystem.array(nothing)), typeSystem.array(
        typeSystem.array(nothing)));
    tester.testEquals();
  }

  @Test
  public void to_string() {
    assertEquals("String", string.toString());
  }

  @Test
  public void string_array_name() throws Exception {
    given(arrayType = typeSystem.array(string));
    when(() -> arrayType.name());
    thenReturned("[String]");
  }

  @Test
  public void common_super_type() throws Exception {
    StringBuilder builder = new StringBuilder();

    assertCommon(type, type, type, builder);
    assertCommon(type, string, null, builder);
    assertCommon(type, blob, null, builder);
    assertCommon(type, file, null, builder);
    assertCommon(type, nothing, type, builder);
    assertCommon(type, typeSystem.array(type), null, builder);
    assertCommon(type, typeSystem.array(string), null, builder);
    assertCommon(type, typeSystem.array(blob), null, builder);
    assertCommon(type, typeSystem.array(file), null, builder);
    assertCommon(type, typeSystem.array(nothing), null, builder);

    assertCommon(string, string, string, builder);
    assertCommon(string, blob, null, builder);
    assertCommon(string, file, null, builder);
    assertCommon(string, nothing, string, builder);
    assertCommon(string, typeSystem.array(string), null, builder);
    assertCommon(string, typeSystem.array(type), null, builder);
    assertCommon(string, typeSystem.array(blob), null, builder);
    assertCommon(string, typeSystem.array(file), null, builder);
    assertCommon(string, typeSystem.array(nothing), null, builder);

    assertCommon(blob, blob, blob, builder);
    assertCommon(blob, file, blob, builder);
    assertCommon(blob, nothing, blob, builder);
    assertCommon(blob, typeSystem.array(type), null, builder);
    assertCommon(blob, typeSystem.array(string), null, builder);
    assertCommon(blob, typeSystem.array(blob), null, builder);
    assertCommon(blob, typeSystem.array(file), null, builder);
    assertCommon(blob, typeSystem.array(nothing), null, builder);

    assertCommon(file, file, file, builder);
    assertCommon(file, nothing, file, builder);
    assertCommon(file, typeSystem.array(type), null, builder);
    assertCommon(file, typeSystem.array(string), null, builder);
    assertCommon(file, typeSystem.array(blob), null, builder);
    assertCommon(file, typeSystem.array(file), null, builder);
    assertCommon(file, typeSystem.array(nothing), null, builder);

    assertCommon(nothing, nothing, nothing, builder);
    assertCommon(nothing, typeSystem.array(type), typeSystem.array(type), builder);
    assertCommon(nothing, typeSystem.array(string), typeSystem.array(string), builder);
    assertCommon(nothing, typeSystem.array(blob), typeSystem.array(blob), builder);
    assertCommon(nothing, typeSystem.array(file), typeSystem.array(file), builder);
    assertCommon(nothing, typeSystem.array(nothing), typeSystem.array(nothing), builder);

    assertCommon(typeSystem.array(type), typeSystem.array(type), typeSystem.array(type),
        builder);
    assertCommon(typeSystem.array(type), typeSystem.array(string), null, builder);
    assertCommon(typeSystem.array(type), typeSystem.array(blob), null, builder);
    assertCommon(typeSystem.array(type), typeSystem.array(file), null, builder);
    assertCommon(typeSystem.array(type), typeSystem.array(nothing), typeSystem.array(type),
        builder);
    assertCommon(typeSystem.array(string), typeSystem.array(string), typeSystem.array(string),
        builder);
    assertCommon(typeSystem.array(string), typeSystem.array(blob), null, builder);
    assertCommon(typeSystem.array(string), typeSystem.array(file), null, builder);
    assertCommon(typeSystem.array(string), typeSystem.array(nothing), typeSystem.array(
        string), builder);
    assertCommon(typeSystem.array(string), nothing, typeSystem.array(string), builder);

    assertCommon(typeSystem.array(blob), typeSystem.array(blob), typeSystem.array(blob),
        builder);
    assertCommon(typeSystem.array(blob), typeSystem.array(file), typeSystem.array(blob),
        builder);
    assertCommon(typeSystem.array(blob), typeSystem.array(nothing), typeSystem.array(blob),
        builder);
    assertCommon(typeSystem.array(blob), nothing, typeSystem.array(blob), builder);

    assertCommon(typeSystem.array(file), typeSystem.array(file), typeSystem.array(file),
        builder);
    assertCommon(typeSystem.array(file), typeSystem.array(nothing), typeSystem.array(file),
        builder);
    assertCommon(typeSystem.array(file), nothing, typeSystem.array(file), builder);

    assertCommon(typeSystem.array(nothing), typeSystem.array(nothing), typeSystem.array(
        nothing), builder);
    assertCommon(typeSystem.array(nothing), nothing, typeSystem.array(nothing), builder);

    assertCommon(typeSystem.array(file),
        typeSystem.struct("Struct", ImmutableMap.of("field", typeSystem.array(blob))),
        typeSystem.array(blob), builder);

    String errors = builder.toString();
    if (0 < errors.length()) {
      fail(errors);
    }
  }

  private static void assertCommon(Type type1, Type type2, Type expected, StringBuilder builder) {
    assertCommonSuperTypeImpl(type1, type2, expected, builder);
    assertCommonSuperTypeImpl(type2, type1, expected, builder);
  }

  private static void assertCommonSuperTypeImpl(Type type1, Type type2, Type expected,
      StringBuilder builder) {
    Type actual = type1.commonSuperType(type2);
    if (!Objects.equal(expected, actual)) {
      builder.append(type1 + ".commonSuperType(" + type2 + ") = " + actual
          + " but should = " + expected + "\n");
    }
  }

  private StructType personType() {
    return typeSystem.struct(
        "Person", ImmutableMap.of("firstName", string, "lastName", string));
  }
}
