package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.type.TestingTypes.a;
import static org.smoothbuild.lang.type.TestingTypes.array;
import static org.smoothbuild.lang.type.TestingTypes.array2;
import static org.smoothbuild.lang.type.TestingTypes.b;
import static org.smoothbuild.lang.type.TestingTypes.blob;
import static org.smoothbuild.lang.type.TestingTypes.personType;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.lang.type.TestingTypes.type;
import static org.smoothbuild.lang.type.TestingTypes.typesDb;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.testing.EqualsTester;

@RunWith(QuackeryRunner.class)
public class TypesTest {
  @Quackery
  public static Suite name() {
    return suite("Type.name").addAll(asList(
        typeNameIs(type, "Type"),
        typeNameIs(string, "String"),
        typeNameIs(blob, "Blob"),
        typeNameIs(personType, "Person"),
        typeNameIs(a, "a"),

        typeNameIs(array(type), "[Type]"),
        typeNameIs(array(string), "[String]"),
        typeNameIs(array(blob), "[Blob]"),
        typeNameIs(array(personType), "[Person]"),
        typeNameIs(array(a), "[a]"),

        typeNameIs(array2(type), "[[Type]]"),
        typeNameIs(array2(string), "[[String]]"),
        typeNameIs(array2(blob), "[[Blob]]"),
        typeNameIs(array2(personType), "[[Person]]"),
        typeNameIs(array2(a), "[[a]]")));
  }

  private static Case typeNameIs(Type type, String expected) {
    return newCase(
        "Type " + type.name(),
        () -> assertEquals(expected, type.name()));
  }

  @Quackery
  public static Suite to_string() {
    return suite("Type.toString").addAll(asList(
        typeToStringIs(type, "Type(\"Type\")"),
        typeToStringIs(string, "Type(\"String\")"),
        typeToStringIs(blob, "Type(\"Blob\")"),
        typeToStringIs(personType, "Type(\"Person\")"),
        typeToStringIs(a, "Type(\"a\")"),

        typeToStringIs(array(type), "Type(\"[Type]\")"),
        typeToStringIs(array(string), "Type(\"[String]\")"),
        typeToStringIs(array(blob), "Type(\"[Blob]\")"),
        typeToStringIs(array(personType), "Type(\"[Person]\")"),
        typeToStringIs(array(a), "Type(\"[a]\")"),

        typeToStringIs(array2(type), "Type(\"[[Type]]\")"),
        typeToStringIs(array2(string), "Type(\"[[String]]\")"),
        typeToStringIs(array2(blob), "Type(\"[[Blob]]\")"),
        typeToStringIs(array2(personType), "Type(\"[[Person]]\")"),
        typeToStringIs(array2(a), "Type(\"[[a]]\")")));
  }

  private static Case typeToStringIs(Type type, String expectedPrefix) {
    return newCase(
        type.name() + ".toString()",
        () -> assertEquals(expectedPrefix + ":" + type.hash(), type.toString()));
  }

  @Quackery
  public static Suite jType() {
    return suite("Type.jType").addAll(asList(
        jTypeIs(type, Type.class),
        jTypeIs(string, SString.class),
        jTypeIs(blob, Blob.class),
        jTypeIs(a, Value.class),
        jTypeIs(array(type), Array.class),
        jTypeIs(array(string), Array.class),
        jTypeIs(array(blob), Array.class),
        jTypeIs(array(a), Array.class)));
  }

  private static Case jTypeIs(Type type, Class<?> expected) {
    return newCase(
        type.name() + ".jType",
        () -> assertEquals(expected, type.jType()));
  }

  @Quackery
  public static Suite core_type() throws Exception {
    return suite("Type.jType").addAll(asList(
        coreTypeIs(type, type),
        coreTypeIs(string, string),
        coreTypeIs(personType, personType),
        coreTypeIs(a, a),

        coreTypeIs(array(string), string),
        coreTypeIs(array(personType), personType),
        coreTypeIs(array(a), a),

        coreTypeIs(array2(string), string),
        coreTypeIs(array2(personType), personType),
        coreTypeIs(array2(a), a)));
  }

  private static Case coreTypeIs(Type type, Type expected) {
    return newCase(
        type.name() + ".coreType()",
        () -> assertEquals(expected, type.coreType()));
  }

  @Quackery
  public static Suite core_depth() throws Exception {
    return suite("Type.coreDepth").addAll(asList(
        coreDepthIs(type, 0),
        coreDepthIs(string, 0),
        coreDepthIs(personType, 0),
        coreDepthIs(a, 0),

        coreDepthIs(array(string), 1),
        coreDepthIs(array(personType), 1),
        coreDepthIs(array(a), 1),

        coreDepthIs(array2(string), 2),
        coreDepthIs(array2(personType), 2),
        coreDepthIs(array2(a), 2)));
  }

  private static Case coreDepthIs(Type type, int depth) {
    return newCase(
        type.name() + ".coreDepth()",
        () -> assertEquals(depth, type.coreDepth()));
  }

  @Quackery
  public static Suite is_array() throws Exception {
    return suite("Type.isArray").addAll(asList(
        isNotArrayType(type),
        isNotArrayType(string),
        isNotArrayType(blob),
        isNotArrayType(personType),
        isNotArrayType(a),
        isArrayType(array(type)),
        isArrayType(array(string)),
        isArrayType(array(blob)),
        isArrayType(array(personType)),
        isArrayType(array(a)),
        isArrayType(array2(type)),
        isArrayType(array2(string)),
        isArrayType(array2(blob)),
        isArrayType(array2(personType)),
        isArrayType(array2(a))));
  }

  private static Case isArrayType(Type type) {
    return newCase(type.name() + " is array type", () -> assertTrue(type.isArray()));
  }

  private static Case isNotArrayType(Type type) {
    return newCase(type.name() + " is NOT array type", () -> assertFalse(type.isArray()));
  }

  @Quackery
  public static Suite super_type() throws Exception {
    return suite("Type.superType").addAll(asList(
        superTypeIs(null, type),
        superTypeIs(null, string),
        superTypeIs(null, blob),
        superTypeIs(string, personType),
        superTypeIs(null, a),

        superTypeIs(null, array(type)),
        superTypeIs(null, array(string)),
        superTypeIs(null, array(blob)),
        superTypeIs(array(string), array(personType)),
        superTypeIs(null, array(a)),

        superTypeIs(null, array2(type)),
        superTypeIs(null, array2(string)),
        superTypeIs(null, array2(blob)),
        superTypeIs(array2(string), array2(personType)),
        superTypeIs(null, array2(a))));
  }

  private static Case superTypeIs(Object expected, Type type) {
    return newCase(
        type.name() + " superType()",
        () -> assertEquals(expected, type.superType()));
  }

  @Quackery
  public static Suite hierarchy() throws Exception {
    return suite("Type.hierarchy").addAll(asList(
        hierarchyTest(list(string)),
        hierarchyTest(list(string, personType)),
        hierarchyTest(list(a)),
        hierarchyTest(list(array(string))),
        hierarchyTest(list(array(string), array(personType))),
        hierarchyTest(list(array(a))),
        hierarchyTest(list(array2(string))),
        hierarchyTest(list(array2(string), array2(personType))),
        hierarchyTest(list(array2(a)))));
  }

  private static Case hierarchyTest(List<Type> hierarchy) {
    Type root = hierarchy.get(hierarchy.size() - 1);
    return newCase(
        "Hierarchy of " + root.name() + " is " + hierarchy.toString(),
        () -> assertEquals(hierarchy, root.hierarchy()));
  }

  @Quackery
  public static Suite is_generic() throws Exception {
    return suite("Type.isGeneric").addAll(asList(
        typeIsGeneric(a),
        typeIsNotGeneric(type),
        typeIsNotGeneric(string),
        typeIsNotGeneric(blob),
        typeIsNotGeneric(personType),
        typeIsGeneric(array(a)),
        typeIsNotGeneric(array(type)),
        typeIsNotGeneric(array(string)),
        typeIsNotGeneric(array(blob)),
        typeIsNotGeneric(array(personType))));
  }

  private static Case typeIsGeneric(Type type) {
    return newCase(
        type.name() + " is generic",
        () -> assertTrue(type.isGeneric()));
  }

  private static Case typeIsNotGeneric(Type type) {
    return newCase(
        type.name() + " is NOT generic",
        () -> assertFalse(type.isGeneric()));
  }

  @Quackery
  public static Suite is_assignable_from() throws Exception {
    return suite("Type.isAssignableFrom").addAll(asList(
        allowedAssignment(type, type),
        allowedAssignment(type, a),
        illegalAssignment(type, array(type)),
        illegalAssignment(type, array2(type)),
        illegalAssignment(type, string),
        illegalAssignment(type, array(string)),
        illegalAssignment(type, array2(string)),
        illegalAssignment(type, blob),
        illegalAssignment(type, array(blob)),
        illegalAssignment(type, array2(blob)),
        illegalAssignment(type, personType),
        illegalAssignment(type, array(personType)),
        illegalAssignment(type, array2(personType)),
        illegalAssignment(type, array(a)),
        illegalAssignment(type, array2(a)),

        allowedAssignment(array(type), a),
        allowedAssignment(array(type), array(type)),
        allowedAssignment(array(type), array(a)),
        illegalAssignment(array(type), type),
        illegalAssignment(array(type), array2(type)),
        illegalAssignment(array(type), string),
        illegalAssignment(array(type), array(string)),
        illegalAssignment(array(type), array2(string)),
        illegalAssignment(array(type), blob),
        illegalAssignment(array(type), array(blob)),
        illegalAssignment(array(type), array2(blob)),
        illegalAssignment(array(type), personType),
        illegalAssignment(array(type), array(personType)),
        illegalAssignment(array(type), array2(personType)),
        illegalAssignment(array(type), array2(a)),

        allowedAssignment(array2(type), array2(type)),
        allowedAssignment(array2(type), a),
        allowedAssignment(array2(type), array(a)),
        allowedAssignment(array2(type), array2(a)),
        illegalAssignment(array2(type), type),
        illegalAssignment(array2(type), array(type)),
        illegalAssignment(array2(type), string),
        illegalAssignment(array2(type), array(string)),
        illegalAssignment(array2(type), array2(string)),
        illegalAssignment(array2(type), blob),
        illegalAssignment(array2(type), array(blob)),
        illegalAssignment(array2(type), array2(blob)),
        illegalAssignment(array2(type), personType),
        illegalAssignment(array2(type), array(personType)),
        illegalAssignment(array2(type), array2(personType)),

        allowedAssignment(string, string),
        allowedAssignment(string, personType),
        allowedAssignment(string, a),
        illegalAssignment(string, type),
        illegalAssignment(string, array(type)),
        illegalAssignment(string, array2(type)),
        illegalAssignment(string, array(string)),
        illegalAssignment(string, array2(string)),
        illegalAssignment(string, blob),
        illegalAssignment(string, array(blob)),
        illegalAssignment(string, array2(blob)),
        illegalAssignment(string, array(personType)),
        illegalAssignment(string, array2(personType)),
        illegalAssignment(string, array(a)),
        illegalAssignment(string, array2(a)),

        allowedAssignment(array(string), array(string)),
        allowedAssignment(array(string), array(personType)),
        allowedAssignment(array(string), a),
        allowedAssignment(array(string), array(a)),
        illegalAssignment(array(string), type),
        illegalAssignment(array(string), array(type)),
        illegalAssignment(array(string), array2(type)),
        illegalAssignment(array(string), string),
        illegalAssignment(array(string), array2(string)),
        illegalAssignment(array(string), blob),
        illegalAssignment(array(string), array(blob)),
        illegalAssignment(array(string), array2(blob)),
        illegalAssignment(array(string), personType),
        illegalAssignment(array(string), array2(personType)),
        illegalAssignment(array(string), array2(a)),

        allowedAssignment(array2(string), array2(string)),
        allowedAssignment(array2(string), array2(personType)),
        allowedAssignment(array2(string), a),
        allowedAssignment(array2(string), array(a)),
        allowedAssignment(array2(string), array2(a)),
        illegalAssignment(array2(string), type),
        illegalAssignment(array2(string), array(type)),
        illegalAssignment(array2(string), array2(type)),
        illegalAssignment(array2(string), string),
        illegalAssignment(array2(string), array(string)),
        illegalAssignment(array2(string), blob),
        illegalAssignment(array2(string), array(blob)),
        illegalAssignment(array2(string), array2(blob)),
        illegalAssignment(array2(string), personType),
        illegalAssignment(array2(string), array(personType)),

        allowedAssignment(blob, blob),
        allowedAssignment(blob, a),
        illegalAssignment(blob, type),
        illegalAssignment(blob, array(type)),
        illegalAssignment(blob, array2(type)),
        illegalAssignment(blob, string),
        illegalAssignment(blob, array(string)),
        illegalAssignment(blob, array2(string)),
        illegalAssignment(blob, array(blob)),
        illegalAssignment(blob, array2(blob)),
        illegalAssignment(blob, personType),
        illegalAssignment(blob, array(personType)),
        illegalAssignment(blob, array2(personType)),
        illegalAssignment(blob, array(a)),
        illegalAssignment(blob, array2(a)),

        allowedAssignment(array(blob), array(blob)),
        allowedAssignment(array(blob), a),
        allowedAssignment(array(blob), array(a)),
        illegalAssignment(array(blob), type),
        illegalAssignment(array(blob), array(type)),
        illegalAssignment(array(blob), array2(type)),
        illegalAssignment(array(blob), string),
        illegalAssignment(array(blob), array(string)),
        illegalAssignment(array(blob), array2(string)),
        illegalAssignment(array(blob), blob),
        illegalAssignment(array(blob), array2(blob)),
        illegalAssignment(array(blob), personType),
        illegalAssignment(array(blob), array(personType)),
        illegalAssignment(array(blob), array2(personType)),
        illegalAssignment(array(blob), array2(a)),

        allowedAssignment(array2(blob), array2(blob)),
        allowedAssignment(array2(blob), a),
        allowedAssignment(array2(blob), array(a)),
        allowedAssignment(array2(blob), array2(a)),
        illegalAssignment(array2(blob), type),
        illegalAssignment(array2(blob), array(type)),
        illegalAssignment(array2(blob), array2(type)),
        illegalAssignment(array2(blob), string),
        illegalAssignment(array2(blob), array(string)),
        illegalAssignment(array2(blob), array2(string)),
        illegalAssignment(array2(blob), blob),
        illegalAssignment(array2(blob), array(blob)),
        illegalAssignment(array2(blob), personType),
        illegalAssignment(array2(blob), array(personType)),
        illegalAssignment(array2(blob), array2(personType)),

        allowedAssignment(personType, personType),
        allowedAssignment(personType, a),
        illegalAssignment(personType, type),
        illegalAssignment(personType, array(type)),
        illegalAssignment(personType, array2(type)),
        illegalAssignment(personType, string),
        illegalAssignment(personType, array(string)),
        illegalAssignment(personType, array2(string)),
        illegalAssignment(personType, blob),
        illegalAssignment(personType, array(blob)),
        illegalAssignment(personType, array2(blob)),
        illegalAssignment(personType, array(personType)),
        illegalAssignment(personType, array2(personType)),
        illegalAssignment(personType, array(a)),
        illegalAssignment(personType, array2(a)),

        allowedAssignment(array(personType), array(personType)),
        allowedAssignment(array(personType), a),
        allowedAssignment(array(personType), array(a)),
        illegalAssignment(array(personType), type),
        illegalAssignment(array(personType), array(type)),
        illegalAssignment(array(personType), array2(type)),
        illegalAssignment(array(personType), string),
        illegalAssignment(array(personType), array(string)),
        illegalAssignment(array(personType), array2(string)),
        illegalAssignment(array(personType), blob),
        illegalAssignment(array(personType), array(blob)),
        illegalAssignment(array(personType), array2(blob)),
        illegalAssignment(array(personType), personType),
        illegalAssignment(array(personType), array2(personType)),
        illegalAssignment(array(personType), array2(a)),

        allowedAssignment(array2(personType), array2(personType)),
        allowedAssignment(array2(personType), a),
        allowedAssignment(array2(personType), array(a)),
        allowedAssignment(array2(personType), array2(a)),
        illegalAssignment(array2(personType), type),
        illegalAssignment(array2(personType), array(type)),
        illegalAssignment(array2(personType), array2(type)),
        illegalAssignment(array2(personType), string),
        illegalAssignment(array2(personType), array(string)),
        illegalAssignment(array2(personType), array2(string)),
        illegalAssignment(array2(personType), blob),
        illegalAssignment(array2(personType), array(blob)),
        illegalAssignment(array2(personType), array2(blob)),
        illegalAssignment(array2(personType), personType),
        illegalAssignment(array2(personType), array(personType)),

        allowedAssignment(a, type),
        allowedAssignment(a, array(type)),
        allowedAssignment(a, array2(type)),
        allowedAssignment(a, string),
        allowedAssignment(a, array(string)),
        allowedAssignment(a, array2(string)),
        allowedAssignment(a, blob),
        allowedAssignment(a, array(blob)),
        allowedAssignment(a, array2(blob)),
        allowedAssignment(a, personType),
        allowedAssignment(a, array(personType)),
        allowedAssignment(a, array2(personType)),
        allowedAssignment(a, a),
        allowedAssignment(a, b),
        allowedAssignment(a, array(b)),
        allowedAssignment(a, array2(b)),
        illegalAssignment(a, array(a)),
        illegalAssignment(a, array2(a)),

        allowedAssignment(array(a), array(type)),
        allowedAssignment(array(a), array2(type)),
        allowedAssignment(array(a), array(string)),
        allowedAssignment(array(a), array2(string)),
        allowedAssignment(array(a), array(blob)),
        allowedAssignment(array(a), array2(blob)),
        allowedAssignment(array(a), array(personType)),
        allowedAssignment(array(a), array2(personType)),
        allowedAssignment(array(a), array(a)),
        allowedAssignment(array(a), b),
        allowedAssignment(array(a), array(b)),
        allowedAssignment(array(a), array2(b)),
        illegalAssignment(array(a), type),
        illegalAssignment(array(a), string),
        illegalAssignment(array(a), blob),
        illegalAssignment(array(a), personType),
        illegalAssignment(array(a), a),
        illegalAssignment(array(a), array2(a)),

        allowedAssignment(array2(a), array2(type)),
        allowedAssignment(array2(a), array2(string)),
        allowedAssignment(array2(a), array2(blob)),
        allowedAssignment(array2(a), array2(personType)),
        allowedAssignment(array2(a), array2(a)),
        allowedAssignment(array2(a), b),
        allowedAssignment(array2(a), array(b)),
        allowedAssignment(array2(a), array2(b)),
        illegalAssignment(array2(a), type),
        illegalAssignment(array2(a), array(type)),
        illegalAssignment(array2(a), string),
        illegalAssignment(array2(a), array(string)),
        illegalAssignment(array2(a), blob),
        illegalAssignment(array2(a), array(blob)),
        illegalAssignment(array2(a), personType),
        illegalAssignment(array2(a), array(personType)),
        illegalAssignment(array2(a), a),
        illegalAssignment(array2(a), array(a))));
  }

  private static Case allowedAssignment(Type destination, Type source) {
    return newCase(
        destination.name() + " is assignable from " + source.name(),
        () -> assertTrue(destination.isAssignableFrom(source)));
  }

  private static Case illegalAssignment(Type destination, Type source) {
    return newCase(
        destination.name() + " is NOT assignable from " + source.name(),
        () -> assertFalse(destination.isAssignableFrom(source)));
  }

  @Quackery
  public static Suite common_super_type() throws Exception {
    return suite("Type.commonSuperType").addAll(asList(
        assertCommon(type, type, type),
        assertCommon(type, string, null),
        assertCommon(type, blob, null),
        assertCommon(type, a, type),
        assertCommon(type, array(type), null),
        assertCommon(type, array(string), null),
        assertCommon(type, array(blob), null),
        assertCommon(type, array(a), null),

        assertCommon(string, string, string),
        assertCommon(string, blob, null),
        assertCommon(string, a, string),
        assertCommon(string, array(string), null),
        assertCommon(string, array(type), null),
        assertCommon(string, array(blob), null),
        assertCommon(string, array(a), null),

        assertCommon(blob, blob, blob),
        assertCommon(blob, a, blob),
        assertCommon(blob, array(type), null),
        assertCommon(blob, array(string), null),
        assertCommon(blob, array(blob), null),
        assertCommon(blob, array(a), null),

        assertCommon(a, a, a),
        assertCommon(a, array(type), array(type)),
        assertCommon(a, array(string), array(string)),
        assertCommon(a, array(blob), array(blob)),
        assertCommon(a, array(a), array(a)),

        assertCommon(array(type), array(type), array(type)),
        assertCommon(array(type), array(string), null),
        assertCommon(array(type), array(blob), null),
        assertCommon(array(type), array(a), array(type)),
        assertCommon(array(string), array(string), array(string)),
        assertCommon(array(string), array(blob), null),
        assertCommon(array(string), array(a), array(string)),
        assertCommon(array(string), a, array(string)),

        assertCommon(array(blob), array(blob), array(blob)),
        assertCommon(array(blob), array(a), array(blob)),
        assertCommon(array(blob), a, array(blob)),

        assertCommon(array(a), array(a), array(a)),
        assertCommon(array(a), array2(a), array2(a)),
        assertCommon(array(a), array(type), array(type)),
        assertCommon(array(a), array(string), array(string)),
        assertCommon(array(a), array(blob), array(blob))));
  }

  private static Case assertCommon(Type type1, Type type2, Type expected) {
    String expectedName = expected == null ? "null" : expected.name();
    return newCase(
        "commonSuperType of " + type1.name() + " and " + type2.name() + " is " + expectedName,
        () -> {
          assertEquals(expected, type1.commonSuperType(type2));
          assertEquals(expected, type2.commonSuperType(type1));
        });
  }

  @Quackery
  public static Suite array_element_types() {
    return suite("Type.elemType").addAll(asList(
        elementTypeOf(array(type), type),
        elementTypeOf(array(string), string),
        elementTypeOf(array(blob), blob),
        elementTypeOf(array(personType), personType),
        elementTypeOf(array(a), a),

        elementTypeOf(array2(type), array(type)),
        elementTypeOf(array2(string), array(string)),
        elementTypeOf(array2(blob), array(blob)),
        elementTypeOf(array2(personType), array(personType)),
        elementTypeOf(array2(a), array(a))));
  }

  private static Case elementTypeOf(ArrayType arrayType, Type expected) {
    return newCase(
        arrayType.name() + ".elemType() == " + expected,
        () -> assertEquals(expected, arrayType.elemType()));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(typesDb.type(), typesDb.type());
    tester.addEqualityGroup(typesDb.string(), typesDb.string());
    tester.addEqualityGroup(typesDb.blob(), typesDb.blob());
    tester.addEqualityGroup(personType, personType);
    tester.addEqualityGroup(typesDb.generic("a"), typesDb.generic("a"));

    tester.addEqualityGroup(array(type), array(type));
    tester.addEqualityGroup(array(string), array(string));
    tester.addEqualityGroup(array(blob), array(blob));
    tester.addEqualityGroup(array(personType), array(personType));
    tester.addEqualityGroup(array(a), array(a));

    tester.addEqualityGroup(array2(type), array2(type));
    tester.addEqualityGroup(array2(string), array2(string));
    tester.addEqualityGroup(array2(blob), array2(blob));
    tester.addEqualityGroup(array2(personType), array2(personType));
    tester.addEqualityGroup(array2(a), array2(a));
    tester.testEquals();
  }
}
