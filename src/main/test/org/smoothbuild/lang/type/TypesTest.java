package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.type.TestingTypes.a;
import static org.smoothbuild.lang.type.TestingTypes.array2A;
import static org.smoothbuild.lang.type.TestingTypes.array2B;
import static org.smoothbuild.lang.type.TestingTypes.array2Blob;
import static org.smoothbuild.lang.type.TestingTypes.array2Nothing;
import static org.smoothbuild.lang.type.TestingTypes.array2Person;
import static org.smoothbuild.lang.type.TestingTypes.array2String;
import static org.smoothbuild.lang.type.TestingTypes.array2Type;
import static org.smoothbuild.lang.type.TestingTypes.arrayA;
import static org.smoothbuild.lang.type.TestingTypes.arrayB;
import static org.smoothbuild.lang.type.TestingTypes.arrayBlob;
import static org.smoothbuild.lang.type.TestingTypes.arrayNothing;
import static org.smoothbuild.lang.type.TestingTypes.arrayPerson;
import static org.smoothbuild.lang.type.TestingTypes.arrayString;
import static org.smoothbuild.lang.type.TestingTypes.arrayType;
import static org.smoothbuild.lang.type.TestingTypes.b;
import static org.smoothbuild.lang.type.TestingTypes.blob;
import static org.smoothbuild.lang.type.TestingTypes.nothing;
import static org.smoothbuild.lang.type.TestingTypes.person;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.lang.type.TestingTypes.type;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

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
        typeNameIs(nothing, "Nothing"),
        typeNameIs(person, "Person"),
        typeNameIs(a, "a"),

        typeNameIs(arrayType, "[Type]"),
        typeNameIs(arrayString, "[String]"),
        typeNameIs(arrayBlob, "[Blob]"),
        typeNameIs(arrayNothing, "[Nothing]"),
        typeNameIs(arrayPerson, "[Person]"),
        typeNameIs(arrayA, "[a]"),

        typeNameIs(array2Type, "[[Type]]"),
        typeNameIs(array2String, "[[String]]"),
        typeNameIs(array2Blob, "[[Blob]]"),
        typeNameIs(array2Nothing, "[[Nothing]]"),
        typeNameIs(array2Person, "[[Person]]"),
        typeNameIs(array2A, "[[a]]")));
  }

  private static Case typeNameIs(Type type, String expected) {
    return newCase(
        "Type " + type.name(),
        () -> assertEquals(expected, type.name()));
  }

  @Quackery
  public static Suite quoted_name() {
    return suite("Type.q").addAll(asList(
        typeQuotedNameIs(type, "'Type'"),
        typeQuotedNameIs(string, "'String'"),
        typeQuotedNameIs(blob, "'Blob'"),
        typeQuotedNameIs(nothing, "'Nothing'"),
        typeQuotedNameIs(person, "'Person'"),
        typeQuotedNameIs(a, "'a'"),

        typeQuotedNameIs(arrayType, "'[Type]'"),
        typeQuotedNameIs(arrayString, "'[String]'"),
        typeQuotedNameIs(arrayBlob, "'[Blob]'"),
        typeQuotedNameIs(arrayNothing, "'[Nothing]'"),
        typeQuotedNameIs(arrayPerson, "'[Person]'"),
        typeQuotedNameIs(arrayA, "'[a]'"),

        typeQuotedNameIs(array2Type, "'[[Type]]'"),
        typeQuotedNameIs(array2String, "'[[String]]'"),
        typeQuotedNameIs(array2Blob, "'[[Blob]]'"),
        typeQuotedNameIs(array2Nothing, "'[[Nothing]]'"),
        typeQuotedNameIs(array2Person, "'[[Person]]'"),
        typeQuotedNameIs(array2A, "'[[a]]'")));
  }

  private static Case typeQuotedNameIs(Type type, String expected) {
    return newCase(
        "Type " + type.q(),
        () -> assertEquals(expected, type.q()));
  }

  @Quackery
  public static Suite to_string() {
    return suite("Type.toString").addAll(asList(
        typeToStringIs(type, "Type(\"Type\")"),
        typeToStringIs(string, "Type(\"String\")"),
        typeToStringIs(blob, "Type(\"Blob\")"),
        typeToStringIs(nothing, "Type(\"Nothing\")"),
        typeToStringIs(person, "Type(\"Person\")"),
        typeToStringIs(a, "Type(\"a\")"),

        typeToStringIs(arrayType, "Type(\"[Type]\")"),
        typeToStringIs(arrayString, "Type(\"[String]\")"),
        typeToStringIs(arrayBlob, "Type(\"[Blob]\")"),
        typeToStringIs(arrayNothing, "Type(\"[Nothing]\")"),
        typeToStringIs(arrayPerson, "Type(\"[Person]\")"),
        typeToStringIs(arrayA, "Type(\"[a]\")"),

        typeToStringIs(array2Type, "Type(\"[[Type]]\")"),
        typeToStringIs(array2String, "Type(\"[[String]]\")"),
        typeToStringIs(array2Blob, "Type(\"[[Blob]]\")"),
        typeToStringIs(array2Nothing, "Type(\"[[Nothing]]\")"),
        typeToStringIs(array2Person, "Type(\"[[Person]]\")"),
        typeToStringIs(array2A, "Type(\"[[a]]\")")));
  }

  private static Case typeToStringIs(Type type, String expectedPrefix) {
    String suffix = type.isGeneric() ? "" : ":" + ((ConcreteType) type).hash();
    return newCase(
        type.name() + ".toString()",
        () -> assertEquals(expectedPrefix + suffix, type.toString()));
  }

  @Quackery
  public static Suite jType() {
    return suite("Type.jType").addAll(asList(
        jTypeIs(type, ConcreteType.class),
        jTypeIs(string, SString.class),
        jTypeIs(blob, Blob.class),
        jTypeIs(nothing, Nothing.class),
        jTypeIs(a, Value.class),
        jTypeIs(arrayType, Array.class),
        jTypeIs(arrayString, Array.class),
        jTypeIs(arrayBlob, Array.class),
        jTypeIs(arrayNothing, Array.class),
        jTypeIs(arrayA, Array.class)));
  }

  private static Case jTypeIs(Type type, Class<?> expected) {
    return newCase(
        type.name() + ".jType",
        () -> assertEquals(expected, type.jType()));
  }

  @Quackery
  public static Suite core_type() throws Exception {
    return suite("Type.coreType").addAll(asList(
        coreTypeIs(type, type),
        coreTypeIs(string, string),
        coreTypeIs(blob, blob),
        coreTypeIs(nothing, nothing),
        coreTypeIs(person, person),
        coreTypeIs(a, a),

        coreTypeIs(arrayString, string),
        coreTypeIs(arrayBlob, blob),
        coreTypeIs(arrayNothing, nothing),
        coreTypeIs(arrayPerson, person),
        coreTypeIs(arrayA, a),

        coreTypeIs(array2String, string),
        coreTypeIs(array2Blob, blob),
        coreTypeIs(array2Nothing, nothing),
        coreTypeIs(array2Person, person),
        coreTypeIs(array2A, a)));
  }

  private static Case coreTypeIs(Type type, Type expected) {
    return newCase(
        type.name() + ".coreType() == " + expected.name(),
        () -> assertEquals(expected, type.coreType()));
  }

  @Quackery
  public static Suite replace_core_type() throws Exception {
    return suite("Type.replaceCoreType").addAll(asList(
        replaceCoreType(type, type, type),
        replaceCoreType(type, string, string),
        replaceCoreType(type, blob, blob),
        replaceCoreType(type, nothing, nothing),
        replaceCoreType(type, person, person),
        replaceCoreType(type, a, a),

        replaceCoreType(string, type, type),
        replaceCoreType(string, string, string),
        replaceCoreType(string, blob, blob),
        replaceCoreType(string, nothing, nothing),
        replaceCoreType(string, person, person),
        replaceCoreType(string, a, a),

        replaceCoreType(blob, type, type),
        replaceCoreType(blob, string, string),
        replaceCoreType(blob, blob, blob),
        replaceCoreType(blob, nothing, nothing),
        replaceCoreType(blob, person, person),
        replaceCoreType(blob, a, a),

        replaceCoreType(nothing, type, type),
        replaceCoreType(nothing, string, string),
        replaceCoreType(nothing, blob, blob),
        replaceCoreType(nothing, nothing, nothing),
        replaceCoreType(nothing, person, person),
        replaceCoreType(nothing, a, a),

        replaceCoreType(person, type, type),
        replaceCoreType(person, string, string),
        replaceCoreType(person, blob, blob),
        replaceCoreType(person, nothing, nothing),
        replaceCoreType(person, person, person),
        replaceCoreType(person, a, a),

        //

        replaceCoreType(type, arrayType, arrayType),
        replaceCoreType(type, arrayString, arrayString),
        replaceCoreType(type, arrayBlob, arrayBlob),
        replaceCoreType(type, arrayNothing, arrayNothing),
        replaceCoreType(type, arrayPerson, arrayPerson),
        replaceCoreType(type, arrayA, arrayA),

        replaceCoreType(string, arrayType, arrayType),
        replaceCoreType(string, arrayString, arrayString),
        replaceCoreType(string, arrayBlob, arrayBlob),
        replaceCoreType(string, arrayNothing, arrayNothing),
        replaceCoreType(string, arrayPerson, arrayPerson),
        replaceCoreType(string, arrayA, arrayA),

        replaceCoreType(blob, arrayType, arrayType),
        replaceCoreType(blob, arrayString, arrayString),
        replaceCoreType(blob, arrayBlob, arrayBlob),
        replaceCoreType(blob, arrayNothing, arrayNothing),
        replaceCoreType(blob, arrayPerson, arrayPerson),
        replaceCoreType(blob, arrayA, arrayA),

        replaceCoreType(nothing, arrayType, arrayType),
        replaceCoreType(nothing, arrayString, arrayString),
        replaceCoreType(nothing, arrayBlob, arrayBlob),
        replaceCoreType(nothing, arrayNothing, arrayNothing),
        replaceCoreType(nothing, arrayPerson, arrayPerson),
        replaceCoreType(nothing, arrayA, arrayA),

        replaceCoreType(person, arrayType, arrayType),
        replaceCoreType(person, arrayString, arrayString),
        replaceCoreType(person, arrayBlob, arrayBlob),
        replaceCoreType(person, arrayNothing, arrayNothing),
        replaceCoreType(person, arrayPerson, arrayPerson),
        replaceCoreType(person, arrayA, arrayA),

        replaceCoreType(a, arrayType, arrayType),
        replaceCoreType(a, arrayString, arrayString),
        replaceCoreType(a, arrayBlob, arrayBlob),
        replaceCoreType(a, arrayNothing, arrayNothing),
        replaceCoreType(a, arrayPerson, arrayPerson),
        replaceCoreType(a, arrayA, arrayA),

        //

        replaceCoreType(arrayType, type, arrayType),
        replaceCoreType(arrayType, string, arrayString),
        replaceCoreType(arrayType, blob, arrayBlob),
        replaceCoreType(arrayType, nothing, arrayNothing),
        replaceCoreType(arrayType, person, arrayPerson),
        replaceCoreType(arrayType, a, arrayA),

        replaceCoreType(arrayString, type, arrayType),
        replaceCoreType(arrayString, string, arrayString),
        replaceCoreType(arrayString, blob, arrayBlob),
        replaceCoreType(arrayString, nothing, arrayNothing),
        replaceCoreType(arrayString, person, arrayPerson),
        replaceCoreType(arrayString, a, arrayA),

        replaceCoreType(arrayBlob, type, arrayType),
        replaceCoreType(arrayBlob, string, arrayString),
        replaceCoreType(arrayBlob, blob, arrayBlob),
        replaceCoreType(arrayBlob, nothing, arrayNothing),
        replaceCoreType(arrayBlob, person, arrayPerson),
        replaceCoreType(arrayBlob, a, arrayA),

        replaceCoreType(arrayNothing, type, arrayType),
        replaceCoreType(arrayNothing, string, arrayString),
        replaceCoreType(arrayNothing, blob, arrayBlob),
        replaceCoreType(arrayNothing, nothing, arrayNothing),
        replaceCoreType(arrayNothing, person, arrayPerson),
        replaceCoreType(arrayNothing, a, arrayA),

        replaceCoreType(arrayPerson, type, arrayType),
        replaceCoreType(arrayPerson, string, arrayString),
        replaceCoreType(arrayPerson, blob, arrayBlob),
        replaceCoreType(arrayPerson, nothing, arrayNothing),
        replaceCoreType(arrayPerson, person, arrayPerson),
        replaceCoreType(arrayPerson, a, arrayA),

        replaceCoreType(arrayA, type, arrayType),
        replaceCoreType(arrayA, string, arrayString),
        replaceCoreType(arrayA, blob, arrayBlob),
        replaceCoreType(arrayA, nothing, arrayNothing),
        replaceCoreType(arrayA, person, arrayPerson),
        replaceCoreType(arrayA, a, arrayA),

        //

        replaceCoreType(arrayType, arrayType, array2Type),
        replaceCoreType(arrayType, arrayString, array2String),
        replaceCoreType(arrayType, arrayBlob, array2Blob),
        replaceCoreType(arrayType, arrayNothing, array2Nothing),
        replaceCoreType(arrayType, arrayPerson, array2Person),
        replaceCoreType(arrayType, arrayA, array2A),

        replaceCoreType(arrayString, arrayType, array2Type),
        replaceCoreType(arrayString, arrayString, array2String),
        replaceCoreType(arrayString, arrayBlob, array2Blob),
        replaceCoreType(arrayString, arrayNothing, array2Nothing),
        replaceCoreType(arrayString, arrayPerson, array2Person),
        replaceCoreType(arrayString, arrayA, array2A),

        replaceCoreType(arrayBlob, arrayType, array2Type),
        replaceCoreType(arrayBlob, arrayString, array2String),
        replaceCoreType(arrayBlob, arrayBlob, array2Blob),
        replaceCoreType(arrayBlob, arrayNothing, array2Nothing),
        replaceCoreType(arrayBlob, arrayPerson, array2Person),
        replaceCoreType(arrayBlob, arrayA, array2A),

        replaceCoreType(arrayNothing, arrayType, array2Type),
        replaceCoreType(arrayNothing, arrayString, array2String),
        replaceCoreType(arrayNothing, arrayBlob, array2Blob),
        replaceCoreType(arrayNothing, arrayNothing, array2Nothing),
        replaceCoreType(arrayNothing, arrayPerson, array2Person),
        replaceCoreType(arrayNothing, arrayA, array2A),

        replaceCoreType(arrayPerson, arrayType, array2Type),
        replaceCoreType(arrayPerson, arrayString, array2String),
        replaceCoreType(arrayPerson, arrayBlob, array2Blob),
        replaceCoreType(arrayPerson, arrayNothing, array2Nothing),
        replaceCoreType(arrayPerson, arrayPerson, array2Person),
        replaceCoreType(arrayPerson, arrayA, array2A),

        replaceCoreType(arrayA, arrayType, array2Type),
        replaceCoreType(arrayA, arrayString, array2String),
        replaceCoreType(arrayA, arrayBlob, array2Blob),
        replaceCoreType(arrayA, arrayNothing, array2Nothing),
        replaceCoreType(arrayA, arrayPerson, array2Person),
        replaceCoreType(arrayA, arrayA, array2A)));
  }

  private static Case replaceCoreType(Type type, Type coreType, Type expected) {
    return newCase(
        type.name() + ".replaceCoreType(" + coreType.name() + ") == " + expected.name(),
        () -> assertEquals(expected, type.replaceCoreType(coreType)));
  }

  @Quackery
  public static Suite core_depth() throws Exception {
    return suite("Type.coreDepth").addAll(asList(
        coreDepthIs(type, 0),
        coreDepthIs(string, 0),
        coreDepthIs(blob, 0),
        coreDepthIs(nothing, 0),
        coreDepthIs(person, 0),
        coreDepthIs(a, 0),

        coreDepthIs(arrayString, 1),
        coreDepthIs(arrayBlob, 1),
        coreDepthIs(arrayNothing, 1),
        coreDepthIs(arrayPerson, 1),
        coreDepthIs(arrayA, 1),

        coreDepthIs(array2String, 2),
        coreDepthIs(array2Blob, 2),
        coreDepthIs(array2Nothing, 2),
        coreDepthIs(array2Person, 2),
        coreDepthIs(array2A, 2)));
  }

  private static Case coreDepthIs(Type type, int depth) {
    return newCase(
        type.name() + ".coreDepth()",
        () -> assertEquals(depth, type.coreDepth()));
  }

  @Quackery
  public static Suite increase_core_depth_by() throws Exception {
    return suite("Type.increaseCoreDepthBy").addAll(asList(

        ///

        changeCoreDepthFailsFor(type, -2),
        changeCoreDepthFailsFor(string, -2),
        changeCoreDepthFailsFor(nothing, -2),
        changeCoreDepthFailsFor(person, -2),
        changeCoreDepthFailsFor(a, -2),

        changeCoreDepthFailsFor(type, -1),
        changeCoreDepthFailsFor(string, -1),
        changeCoreDepthFailsFor(nothing, -1),
        changeCoreDepthFailsFor(person, -1),
        changeCoreDepthFailsFor(a, -1),

        changeCoreDepthBy(type, 0, type),
        changeCoreDepthBy(string, 0, string),
        changeCoreDepthBy(nothing, 0, nothing),
        changeCoreDepthBy(person, 0, person),
        changeCoreDepthBy(a, 0, a),

        changeCoreDepthBy(type, 1, arrayType),
        changeCoreDepthBy(string, 1, arrayString),
        changeCoreDepthBy(nothing, 1, arrayNothing),
        changeCoreDepthBy(person, 1, arrayPerson),
        changeCoreDepthBy(a, 1, arrayA),

        changeCoreDepthBy(type, 2, array2Type),
        changeCoreDepthBy(string, 2, array2String),
        changeCoreDepthBy(nothing, 2, array2Nothing),
        changeCoreDepthBy(person, 2, array2Person),
        changeCoreDepthBy(a, 2, array2A),

        //

        changeCoreDepthFailsFor(arrayType, -2),
        changeCoreDepthFailsFor(arrayString, -2),
        changeCoreDepthFailsFor(arrayNothing, -2),
        changeCoreDepthFailsFor(arrayPerson, -2),
        changeCoreDepthFailsFor(arrayA, -2),

        changeCoreDepthBy(arrayString, -1, string),
        changeCoreDepthBy(arrayBlob, -1, blob),
        changeCoreDepthBy(arrayNothing, -1, nothing),
        changeCoreDepthBy(arrayPerson, -1, person),
        changeCoreDepthBy(arrayA, -1, a),

        changeCoreDepthBy(arrayString, 0, arrayString),
        changeCoreDepthBy(arrayBlob, 0, arrayBlob),
        changeCoreDepthBy(arrayNothing, 0, arrayNothing),
        changeCoreDepthBy(arrayPerson, 0, arrayPerson),
        changeCoreDepthBy(arrayA, 0, arrayA),

        changeCoreDepthBy(arrayString, 1, array2String),
        changeCoreDepthBy(arrayBlob, 1, array2Blob),
        changeCoreDepthBy(arrayNothing, 1, array2Nothing),
        changeCoreDepthBy(arrayPerson, 1, array2Person),
        changeCoreDepthBy(arrayA, 1, array2A),

        //

        changeCoreDepthFailsFor(array2String, -3),
        changeCoreDepthFailsFor(array2Blob, -3),
        changeCoreDepthFailsFor(array2Nothing, -3),
        changeCoreDepthFailsFor(array2Person, -3),
        changeCoreDepthFailsFor(array2A, -3),

        changeCoreDepthBy(array2String, -2, string),
        changeCoreDepthBy(array2Blob, -2, blob),
        changeCoreDepthBy(array2Nothing, -2, nothing),
        changeCoreDepthBy(array2Person, -2, person),
        changeCoreDepthBy(array2A, -2, a),

        changeCoreDepthBy(array2String, -1, arrayString),
        changeCoreDepthBy(array2Blob, -1, arrayBlob),
        changeCoreDepthBy(array2Nothing, -1, arrayNothing),
        changeCoreDepthBy(array2Person, -1, arrayPerson),
        changeCoreDepthBy(array2A, -1, arrayA),

        changeCoreDepthBy(array2String, 0, array2String),
        changeCoreDepthBy(array2Blob, 0, array2Blob),
        changeCoreDepthBy(array2Nothing, 0, array2Nothing),
        changeCoreDepthBy(array2Person, 0, array2Person),
        changeCoreDepthBy(array2A, 0, array2A)

    ));
  }

  private static Case changeCoreDepthBy(Type type, int delta, Type expected) {
    return newCase(
        type.name() + ".changeCoreDepthBy(" + delta + ") == " + expected.name(),
        () -> assertEquals(type.changeCoreDepthBy(delta), expected));
  }

  private static Case changeCoreDepthFailsFor(Type type, int delta) {
    return newCase(
        type.name() + ".changeCoreDepthBy(" + delta + ") throws IllegalArgumentException",
        () -> {
          try {
            type.changeCoreDepthBy(delta);
            fail();
          } catch (IllegalArgumentException e) {
            // expected
          }
        });
  }

  @Quackery
  public static Suite is_concrete() throws Exception {
    return suite("Type.isArray").addAll(asList(
        isNotGeneric(type),
        isNotGeneric(string),
        isNotGeneric(blob),
        isNotGeneric(nothing),
        isNotGeneric(person),
        isNotGeneric(arrayType),
        isNotGeneric(arrayString),
        isNotGeneric(arrayBlob),
        isNotGeneric(arrayNothing),
        isNotGeneric(arrayPerson),
        isNotGeneric(array2Type),
        isNotGeneric(array2String),
        isNotGeneric(array2Blob),
        isNotGeneric(array2Nothing),
        isNotGeneric(array2Person),

        isGeneric(a),
        isGeneric(arrayA),
        isGeneric(array2A),
        isGeneric(b),
        isGeneric(arrayB),
        isGeneric(array2B)));
  }

  private static Case isNotGeneric(Type type) {
    return newCase(type.isGeneric() + " is NOT Generic type", () -> assertFalse(type.isGeneric()));
  }

  private static Case isGeneric(Type type) {
    return newCase(type.isGeneric() + " is generic type", () -> assertTrue(type.isGeneric()));
  }

  @Quackery
  public static Suite is_array() throws Exception {
    return suite("Type.isArray").addAll(asList(
        isNotArrayType(type),
        isNotArrayType(string),
        isNotArrayType(blob),
        isNotArrayType(nothing),
        isNotArrayType(person),
        isNotArrayType(a),
        isArrayType(arrayType),
        isArrayType(arrayString),
        isArrayType(arrayBlob),
        isArrayType(arrayNothing),
        isArrayType(arrayPerson),
        isArrayType(arrayA),
        isArrayType(array2Type),
        isArrayType(array2String),
        isArrayType(array2Blob),
        isArrayType(array2Nothing),
        isArrayType(array2Person),
        isArrayType(array2A)));
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
        superTypeIs(null, nothing),
        superTypeIs(string, person),
        superTypeIs(null, a),

        superTypeIs(null, arrayType),
        superTypeIs(null, arrayString),
        superTypeIs(null, arrayBlob),
        superTypeIs(null, arrayNothing),
        superTypeIs(arrayString, arrayPerson),
        superTypeIs(null, arrayA),

        superTypeIs(null, array2Type),
        superTypeIs(null, array2String),
        superTypeIs(null, array2Blob),
        superTypeIs(null, array2Nothing),
        superTypeIs(array2String, array2Person),
        superTypeIs(null, array2A)));
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
        hierarchyTest(list(string, person)),
        hierarchyTest(list(nothing)),
        hierarchyTest(list(a)),
        hierarchyTest(list(arrayString)),
        hierarchyTest(list(arrayString, arrayPerson)),
        hierarchyTest(list(arrayNothing)),
        hierarchyTest(list(arrayA)),
        hierarchyTest(list(array2String)),
        hierarchyTest(list(array2String, array2Person)),
        hierarchyTest(list(array2Nothing)),
        hierarchyTest(list(array2A))));
  }

  private static Case hierarchyTest(List<Type> hierarchy) {
    Type root = hierarchy.get(hierarchy.size() - 1);
    return newCase(
        "Hierarchy of " + root.name() + " is " + hierarchy.toString(),
        () -> assertEquals(hierarchy, root.hierarchy()));
  }

  @Quackery
  public static Suite is_assignable_from() throws Exception {
    return suite("Type.isAssignableFrom").addAll(asList(
        allowedAssignment(type, type),
        illegalAssignment(type, string),
        illegalAssignment(type, blob),
        illegalAssignment(type, person),
        allowedAssignment(type, nothing),
        illegalAssignment(type, a),
        illegalAssignment(type, arrayType),
        illegalAssignment(type, arrayString),
        illegalAssignment(type, arrayBlob),
        illegalAssignment(type, arrayPerson),
        illegalAssignment(type, arrayNothing),
        illegalAssignment(type, arrayA),
        illegalAssignment(type, array2Type),
        illegalAssignment(type, array2String),
        illegalAssignment(type, array2Blob),
        illegalAssignment(type, array2Person),
        illegalAssignment(type, array2Nothing),
        illegalAssignment(type, array2A),

        illegalAssignment(string, type),
        allowedAssignment(string, string),
        illegalAssignment(string, blob),
        allowedAssignment(string, person),
        allowedAssignment(string, nothing),
        illegalAssignment(string, a),
        illegalAssignment(string, arrayType),
        illegalAssignment(string, arrayString),
        illegalAssignment(string, arrayBlob),
        illegalAssignment(string, arrayPerson),
        illegalAssignment(string, arrayNothing),
        illegalAssignment(string, arrayA),
        illegalAssignment(string, array2Type),
        illegalAssignment(string, array2String),
        illegalAssignment(string, array2Blob),
        illegalAssignment(string, array2Person),
        illegalAssignment(string, array2Nothing),
        illegalAssignment(string, array2A),

        illegalAssignment(blob, type),
        illegalAssignment(blob, string),
        allowedAssignment(blob, blob),
        illegalAssignment(blob, person),
        allowedAssignment(blob, nothing),
        illegalAssignment(blob, a),
        illegalAssignment(blob, arrayType),
        illegalAssignment(blob, arrayString),
        illegalAssignment(blob, arrayBlob),
        illegalAssignment(blob, arrayPerson),
        illegalAssignment(blob, arrayNothing),
        illegalAssignment(blob, arrayA),
        illegalAssignment(blob, array2Type),
        illegalAssignment(blob, array2String),
        illegalAssignment(blob, array2Blob),
        illegalAssignment(blob, array2Person),
        illegalAssignment(blob, array2Nothing),
        illegalAssignment(blob, array2A),

        illegalAssignment(person, type),
        illegalAssignment(person, string),
        illegalAssignment(person, blob),
        allowedAssignment(person, person),
        allowedAssignment(person, nothing),
        illegalAssignment(person, a),
        illegalAssignment(person, arrayType),
        illegalAssignment(person, arrayString),
        illegalAssignment(person, arrayBlob),
        illegalAssignment(person, arrayPerson),
        illegalAssignment(person, arrayNothing),
        illegalAssignment(person, arrayA),
        illegalAssignment(person, array2Type),
        illegalAssignment(person, array2String),
        illegalAssignment(person, array2Blob),
        illegalAssignment(person, array2Person),
        illegalAssignment(person, array2Nothing),
        illegalAssignment(person, array2A),

        illegalAssignment(nothing, type),
        illegalAssignment(nothing, string),
        illegalAssignment(nothing, blob),
        illegalAssignment(nothing, person),
        allowedAssignment(nothing, nothing),
        illegalAssignment(nothing, a),
        illegalAssignment(nothing, arrayType),
        illegalAssignment(nothing, arrayString),
        illegalAssignment(nothing, arrayBlob),
        illegalAssignment(nothing, arrayPerson),
        illegalAssignment(nothing, arrayNothing),
        illegalAssignment(nothing, arrayA),
        illegalAssignment(nothing, array2Type),
        illegalAssignment(nothing, array2String),
        illegalAssignment(nothing, array2Blob),
        illegalAssignment(nothing, array2Person),
        illegalAssignment(nothing, array2Nothing),
        illegalAssignment(nothing, array2A),

        illegalAssignment(a, type),
        illegalAssignment(a, string),
        illegalAssignment(a, blob),
        illegalAssignment(a, person),
        allowedAssignment(a, nothing),
        allowedAssignment(a, a),
        illegalAssignment(a, b),
        illegalAssignment(a, arrayType),
        illegalAssignment(a, arrayString),
        illegalAssignment(a, arrayBlob),
        illegalAssignment(a, arrayPerson),
        illegalAssignment(a, arrayNothing),
        illegalAssignment(a, arrayA),
        illegalAssignment(a, arrayB),
        illegalAssignment(a, array2Type),
        illegalAssignment(a, array2String),
        illegalAssignment(a, array2Blob),
        illegalAssignment(a, array2Person),
        illegalAssignment(a, array2Nothing),
        illegalAssignment(a, array2A),
        illegalAssignment(a, array2B),

        illegalAssignment(arrayType, type),
        illegalAssignment(arrayType, string),
        illegalAssignment(arrayType, blob),
        illegalAssignment(arrayType, person),
        allowedAssignment(arrayType, nothing),
        illegalAssignment(arrayType, a),
        allowedAssignment(arrayType, arrayType),
        illegalAssignment(arrayType, arrayString),
        illegalAssignment(arrayType, arrayBlob),
        illegalAssignment(arrayType, arrayPerson),
        allowedAssignment(arrayType, arrayNothing),
        illegalAssignment(arrayType, arrayA),
        illegalAssignment(arrayType, array2Type),
        illegalAssignment(arrayType, array2String),
        illegalAssignment(arrayType, array2Blob),
        illegalAssignment(arrayType, array2Person),
        illegalAssignment(arrayType, array2Nothing),
        illegalAssignment(arrayType, array2A),

        illegalAssignment(arrayString, type),
        illegalAssignment(arrayString, string),
        illegalAssignment(arrayString, blob),
        illegalAssignment(arrayString, person),
        allowedAssignment(arrayString, nothing),
        illegalAssignment(arrayString, a),
        illegalAssignment(arrayString, arrayType),
        allowedAssignment(arrayString, arrayString),
        illegalAssignment(arrayString, arrayBlob),
        allowedAssignment(arrayString, arrayPerson),
        allowedAssignment(arrayString, arrayNothing),
        illegalAssignment(arrayString, arrayA),
        illegalAssignment(arrayString, array2Type),
        illegalAssignment(arrayString, array2String),
        illegalAssignment(arrayString, array2Blob),
        illegalAssignment(arrayString, array2Person),
        illegalAssignment(arrayString, array2Nothing),
        illegalAssignment(arrayString, array2A),

        illegalAssignment(arrayBlob, type),
        illegalAssignment(arrayBlob, string),
        illegalAssignment(arrayBlob, blob),
        illegalAssignment(arrayBlob, person),
        allowedAssignment(arrayBlob, nothing),
        illegalAssignment(arrayBlob, a),
        illegalAssignment(arrayBlob, arrayType),
        illegalAssignment(arrayBlob, arrayString),
        allowedAssignment(arrayBlob, arrayBlob),
        illegalAssignment(arrayBlob, arrayPerson),
        allowedAssignment(arrayBlob, arrayNothing),
        illegalAssignment(arrayBlob, arrayA),
        illegalAssignment(arrayBlob, array2Type),
        illegalAssignment(arrayBlob, array2String),
        illegalAssignment(arrayBlob, array2Blob),
        illegalAssignment(arrayBlob, array2Person),
        illegalAssignment(arrayBlob, array2Nothing),
        illegalAssignment(arrayBlob, array2A),

        illegalAssignment(arrayPerson, type),
        illegalAssignment(arrayPerson, string),
        illegalAssignment(arrayPerson, blob),
        illegalAssignment(arrayPerson, person),
        allowedAssignment(arrayPerson, nothing),
        illegalAssignment(arrayPerson, a),
        illegalAssignment(arrayPerson, arrayType),
        illegalAssignment(arrayPerson, arrayString),
        illegalAssignment(arrayPerson, arrayBlob),
        allowedAssignment(arrayPerson, arrayPerson),
        allowedAssignment(arrayPerson, arrayNothing),
        illegalAssignment(arrayPerson, arrayA),
        illegalAssignment(arrayPerson, array2Type),
        illegalAssignment(arrayPerson, array2String),
        illegalAssignment(arrayPerson, array2Blob),
        illegalAssignment(arrayPerson, array2Person),
        illegalAssignment(arrayPerson, array2Nothing),
        illegalAssignment(arrayPerson, array2A),

        illegalAssignment(arrayNothing, type),
        illegalAssignment(arrayNothing, string),
        illegalAssignment(arrayNothing, blob),
        illegalAssignment(arrayNothing, person),
        allowedAssignment(arrayNothing, nothing),
        illegalAssignment(arrayNothing, a),
        illegalAssignment(arrayNothing, arrayType),
        illegalAssignment(arrayNothing, arrayString),
        illegalAssignment(arrayNothing, arrayBlob),
        illegalAssignment(arrayNothing, arrayPerson),
        allowedAssignment(arrayNothing, arrayNothing),
        illegalAssignment(arrayNothing, arrayA),
        illegalAssignment(arrayNothing, array2Type),
        illegalAssignment(arrayNothing, array2String),
        illegalAssignment(arrayNothing, array2Blob),
        illegalAssignment(arrayNothing, array2Person),
        illegalAssignment(arrayNothing, array2Nothing),
        illegalAssignment(arrayNothing, array2A),

        illegalAssignment(arrayA, type),
        illegalAssignment(arrayA, string),
        illegalAssignment(arrayA, blob),
        illegalAssignment(arrayA, person),
        allowedAssignment(arrayA, nothing),
        illegalAssignment(arrayA, a),
        illegalAssignment(arrayA, b),
        illegalAssignment(arrayA, arrayType),
        illegalAssignment(arrayA, arrayString),
        illegalAssignment(arrayA, arrayBlob),
        illegalAssignment(arrayA, arrayPerson),
        allowedAssignment(arrayA, arrayNothing),
        allowedAssignment(arrayA, arrayA),
        illegalAssignment(arrayA, arrayB),
        illegalAssignment(arrayA, array2Type),
        illegalAssignment(arrayA, array2String),
        illegalAssignment(arrayA, array2Blob),
        illegalAssignment(arrayA, array2Person),
        illegalAssignment(arrayA, array2Nothing),
        illegalAssignment(arrayA, array2A),
        illegalAssignment(arrayA, array2B),

        illegalAssignment(array2Type, type),
        illegalAssignment(array2Type, string),
        illegalAssignment(array2Type, blob),
        illegalAssignment(array2Type, person),
        allowedAssignment(array2Type, nothing),
        illegalAssignment(array2Type, a),
        illegalAssignment(array2Type, arrayType),
        illegalAssignment(array2Type, arrayString),
        illegalAssignment(array2Type, arrayBlob),
        illegalAssignment(array2Type, arrayPerson),
        allowedAssignment(array2Type, arrayNothing),
        illegalAssignment(array2Type, arrayA),
        allowedAssignment(array2Type, array2Type),
        illegalAssignment(array2Type, array2String),
        illegalAssignment(array2Type, array2Blob),
        illegalAssignment(array2Type, array2Person),
        allowedAssignment(array2Type, array2Nothing),
        illegalAssignment(array2Type, array2A),

        illegalAssignment(array2String, type),
        illegalAssignment(array2String, string),
        illegalAssignment(array2String, blob),
        illegalAssignment(array2String, person),
        allowedAssignment(array2String, nothing),
        illegalAssignment(array2String, a),
        illegalAssignment(array2String, arrayType),
        illegalAssignment(array2String, arrayString),
        illegalAssignment(array2String, arrayBlob),
        illegalAssignment(array2String, arrayPerson),
        allowedAssignment(array2String, arrayNothing),
        illegalAssignment(array2String, arrayA),
        illegalAssignment(array2String, array2Type),
        allowedAssignment(array2String, array2String),
        illegalAssignment(array2String, array2Blob),
        allowedAssignment(array2String, array2Person),
        allowedAssignment(array2String, array2Nothing),
        illegalAssignment(array2String, array2A),

        illegalAssignment(array2Blob, type),
        illegalAssignment(array2Blob, string),
        illegalAssignment(array2Blob, blob),
        illegalAssignment(array2Blob, person),
        allowedAssignment(array2Blob, nothing),
        illegalAssignment(array2Blob, a),
        illegalAssignment(array2Blob, arrayType),
        illegalAssignment(array2Blob, arrayString),
        illegalAssignment(array2Blob, arrayBlob),
        illegalAssignment(array2Blob, arrayPerson),
        allowedAssignment(array2Blob, arrayNothing),
        illegalAssignment(array2Blob, arrayA),
        illegalAssignment(array2Blob, array2Type),
        illegalAssignment(array2Blob, array2String),
        allowedAssignment(array2Blob, array2Blob),
        illegalAssignment(array2Blob, array2Person),
        allowedAssignment(array2Blob, array2Nothing),
        illegalAssignment(array2Blob, array2A),

        illegalAssignment(array2Person, type),
        illegalAssignment(array2Person, string),
        illegalAssignment(array2Person, blob),
        illegalAssignment(array2Person, person),
        allowedAssignment(array2Person, nothing),
        illegalAssignment(array2Person, a),
        illegalAssignment(array2Person, arrayType),
        illegalAssignment(array2Person, arrayString),
        illegalAssignment(array2Person, arrayBlob),
        illegalAssignment(array2Person, arrayPerson),
        allowedAssignment(array2Person, arrayNothing),
        illegalAssignment(array2Person, arrayA),
        illegalAssignment(array2Person, array2Type),
        illegalAssignment(array2Person, array2String),
        illegalAssignment(array2Person, array2Blob),
        allowedAssignment(array2Person, array2Person),
        allowedAssignment(array2Person, array2Nothing),
        illegalAssignment(array2Person, array2A),

        illegalAssignment(array2Nothing, type),
        illegalAssignment(array2Nothing, string),
        illegalAssignment(array2Nothing, blob),
        illegalAssignment(array2Nothing, person),
        allowedAssignment(array2Nothing, nothing),
        illegalAssignment(array2Nothing, a),
        illegalAssignment(array2Nothing, arrayType),
        illegalAssignment(array2Nothing, arrayString),
        illegalAssignment(array2Nothing, arrayBlob),
        illegalAssignment(array2Nothing, arrayPerson),
        allowedAssignment(array2Nothing, arrayNothing),
        illegalAssignment(array2Nothing, arrayA),
        illegalAssignment(array2Nothing, array2Type),
        illegalAssignment(array2Nothing, array2String),
        illegalAssignment(array2Nothing, array2Blob),
        illegalAssignment(array2Nothing, array2Person),
        allowedAssignment(array2Nothing, array2Nothing),
        illegalAssignment(array2Nothing, array2A),

        illegalAssignment(array2A, type),
        illegalAssignment(array2A, string),
        illegalAssignment(array2A, blob),
        illegalAssignment(array2A, person),
        allowedAssignment(array2A, nothing),
        illegalAssignment(array2A, a),
        illegalAssignment(array2A, b),
        illegalAssignment(array2A, arrayType),
        illegalAssignment(array2A, arrayString),
        illegalAssignment(array2A, arrayBlob),
        illegalAssignment(array2A, arrayPerson),
        allowedAssignment(array2A, arrayNothing),
        illegalAssignment(array2A, arrayA),
        illegalAssignment(array2A, arrayB),
        illegalAssignment(array2A, array2Type),
        illegalAssignment(array2A, array2String),
        illegalAssignment(array2A, array2Blob),
        illegalAssignment(array2A, array2Person),
        allowedAssignment(array2A, array2Nothing),
        allowedAssignment(array2A, array2A),
        illegalAssignment(array2A, array2B)));
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
  public static Suite is_arg_assignable_from() throws Exception {
    return suite("Type.isArgAssignableFrom").addAll(asList(
        allowedParameterAssignment(type, type),
        illegalParameterAssignment(type, string),
        illegalParameterAssignment(type, blob),
        illegalParameterAssignment(type, person),
        allowedParameterAssignment(type, nothing),
        illegalParameterAssignment(type, a),
        illegalParameterAssignment(type, arrayType),
        illegalParameterAssignment(type, arrayString),
        illegalParameterAssignment(type, arrayBlob),
        illegalParameterAssignment(type, arrayPerson),
        illegalParameterAssignment(type, arrayNothing),
        illegalParameterAssignment(type, arrayA),
        illegalParameterAssignment(type, array2Type),
        illegalParameterAssignment(type, array2String),
        illegalParameterAssignment(type, array2Blob),
        illegalParameterAssignment(type, array2Person),
        illegalParameterAssignment(type, array2Nothing),
        illegalParameterAssignment(type, array2A),

        illegalParameterAssignment(string, type),
        allowedParameterAssignment(string, string),
        illegalParameterAssignment(string, blob),
        allowedParameterAssignment(string, person),
        allowedParameterAssignment(string, nothing),
        illegalParameterAssignment(string, a),
        illegalParameterAssignment(string, arrayType),
        illegalParameterAssignment(string, arrayString),
        illegalParameterAssignment(string, arrayBlob),
        illegalParameterAssignment(string, arrayPerson),
        illegalParameterAssignment(string, arrayNothing),
        illegalParameterAssignment(string, arrayA),
        illegalParameterAssignment(string, array2Type),
        illegalParameterAssignment(string, array2String),
        illegalParameterAssignment(string, array2Blob),
        illegalParameterAssignment(string, array2Person),
        illegalParameterAssignment(string, array2Nothing),
        illegalParameterAssignment(string, array2A),

        illegalParameterAssignment(blob, type),
        illegalParameterAssignment(blob, string),
        allowedParameterAssignment(blob, blob),
        illegalParameterAssignment(blob, person),
        allowedParameterAssignment(blob, nothing),
        illegalParameterAssignment(blob, a),
        illegalParameterAssignment(blob, arrayType),
        illegalParameterAssignment(blob, arrayString),
        illegalParameterAssignment(blob, arrayBlob),
        illegalParameterAssignment(blob, arrayPerson),
        illegalParameterAssignment(blob, arrayNothing),
        illegalParameterAssignment(blob, arrayA),
        illegalParameterAssignment(blob, array2Type),
        illegalParameterAssignment(blob, array2String),
        illegalParameterAssignment(blob, array2Blob),
        illegalParameterAssignment(blob, array2Person),
        illegalParameterAssignment(blob, array2Nothing),
        illegalParameterAssignment(blob, array2A),

        illegalParameterAssignment(person, type),
        illegalParameterAssignment(person, string),
        illegalParameterAssignment(person, blob),
        allowedParameterAssignment(person, person),
        allowedParameterAssignment(person, nothing),
        illegalParameterAssignment(person, a),
        illegalParameterAssignment(person, arrayType),
        illegalParameterAssignment(person, arrayString),
        illegalParameterAssignment(person, arrayBlob),
        illegalParameterAssignment(person, arrayPerson),
        illegalParameterAssignment(person, arrayNothing),
        illegalParameterAssignment(person, arrayA),
        illegalParameterAssignment(person, array2Type),
        illegalParameterAssignment(person, array2String),
        illegalParameterAssignment(person, array2Blob),
        illegalParameterAssignment(person, array2Person),
        illegalParameterAssignment(person, array2Nothing),
        illegalParameterAssignment(person, array2A),

        illegalParameterAssignment(nothing, type),
        illegalParameterAssignment(nothing, string),
        illegalParameterAssignment(nothing, blob),
        illegalParameterAssignment(nothing, person),
        allowedParameterAssignment(nothing, nothing),
        illegalParameterAssignment(nothing, a),
        illegalParameterAssignment(nothing, arrayType),
        illegalParameterAssignment(nothing, arrayString),
        illegalParameterAssignment(nothing, arrayBlob),
        illegalParameterAssignment(nothing, arrayPerson),
        illegalParameterAssignment(nothing, arrayNothing),
        illegalParameterAssignment(nothing, arrayA),
        illegalParameterAssignment(nothing, array2Type),
        illegalParameterAssignment(nothing, array2String),
        illegalParameterAssignment(nothing, array2Blob),
        illegalParameterAssignment(nothing, array2Person),
        illegalParameterAssignment(nothing, array2Nothing),
        illegalParameterAssignment(nothing, array2A),

        allowedParameterAssignment(a, type),
        allowedParameterAssignment(a, string),
        allowedParameterAssignment(a, blob),
        allowedParameterAssignment(a, person),
        allowedParameterAssignment(a, nothing),
        allowedParameterAssignment(a, a),
        allowedParameterAssignment(a, b),
        allowedParameterAssignment(a, arrayType),
        allowedParameterAssignment(a, arrayString),
        allowedParameterAssignment(a, arrayBlob),
        allowedParameterAssignment(a, arrayPerson),
        allowedParameterAssignment(a, arrayNothing),
        allowedParameterAssignment(a, arrayA),
        allowedParameterAssignment(a, arrayB),
        allowedParameterAssignment(a, array2Type),
        allowedParameterAssignment(a, array2String),
        allowedParameterAssignment(a, array2Blob),
        allowedParameterAssignment(a, array2Person),
        allowedParameterAssignment(a, array2Nothing),
        allowedParameterAssignment(a, array2A),
        allowedParameterAssignment(a, array2B),

        illegalParameterAssignment(arrayType, type),
        illegalParameterAssignment(arrayType, string),
        illegalParameterAssignment(arrayType, blob),
        illegalParameterAssignment(arrayType, person),
        allowedParameterAssignment(arrayType, nothing),
        illegalParameterAssignment(arrayType, a),
        allowedParameterAssignment(arrayType, arrayType),
        illegalParameterAssignment(arrayType, arrayString),
        illegalParameterAssignment(arrayType, arrayBlob),
        illegalParameterAssignment(arrayType, arrayPerson),
        allowedParameterAssignment(arrayType, arrayNothing),
        illegalParameterAssignment(arrayType, arrayA),
        illegalParameterAssignment(arrayType, array2Type),
        illegalParameterAssignment(arrayType, array2String),
        illegalParameterAssignment(arrayType, array2Blob),
        illegalParameterAssignment(arrayType, array2Person),
        illegalParameterAssignment(arrayType, array2Nothing),
        illegalParameterAssignment(arrayType, array2A),

        illegalParameterAssignment(arrayString, type),
        illegalParameterAssignment(arrayString, string),
        illegalParameterAssignment(arrayString, blob),
        illegalParameterAssignment(arrayString, person),
        allowedParameterAssignment(arrayString, nothing),
        illegalParameterAssignment(arrayString, a),
        illegalParameterAssignment(arrayString, arrayType),
        allowedParameterAssignment(arrayString, arrayString),
        illegalParameterAssignment(arrayString, arrayBlob),
        allowedParameterAssignment(arrayString, arrayPerson),
        allowedParameterAssignment(arrayString, arrayNothing),
        illegalParameterAssignment(arrayString, arrayA),
        illegalParameterAssignment(arrayString, array2Type),
        illegalParameterAssignment(arrayString, array2String),
        illegalParameterAssignment(arrayString, array2Blob),
        illegalParameterAssignment(arrayString, array2Person),
        illegalParameterAssignment(arrayString, array2Nothing),
        illegalParameterAssignment(arrayString, array2A),

        illegalParameterAssignment(arrayBlob, type),
        illegalParameterAssignment(arrayBlob, string),
        illegalParameterAssignment(arrayBlob, blob),
        illegalParameterAssignment(arrayBlob, person),
        allowedParameterAssignment(arrayBlob, nothing),
        illegalParameterAssignment(arrayBlob, a),
        illegalParameterAssignment(arrayBlob, arrayType),
        illegalParameterAssignment(arrayBlob, arrayString),
        allowedParameterAssignment(arrayBlob, arrayBlob),
        illegalParameterAssignment(arrayBlob, arrayPerson),
        allowedParameterAssignment(arrayBlob, arrayNothing),
        illegalParameterAssignment(arrayBlob, arrayA),
        illegalParameterAssignment(arrayBlob, array2Type),
        illegalParameterAssignment(arrayBlob, array2String),
        illegalParameterAssignment(arrayBlob, array2Blob),
        illegalParameterAssignment(arrayBlob, array2Person),
        illegalParameterAssignment(arrayBlob, array2Nothing),
        illegalParameterAssignment(arrayBlob, array2A),

        illegalParameterAssignment(arrayPerson, type),
        illegalParameterAssignment(arrayPerson, string),
        illegalParameterAssignment(arrayPerson, blob),
        illegalParameterAssignment(arrayPerson, person),
        allowedParameterAssignment(arrayPerson, nothing),
        illegalParameterAssignment(arrayPerson, a),
        illegalParameterAssignment(arrayPerson, arrayType),
        illegalParameterAssignment(arrayPerson, arrayString),
        illegalParameterAssignment(arrayPerson, arrayBlob),
        allowedParameterAssignment(arrayPerson, arrayPerson),
        allowedParameterAssignment(arrayPerson, arrayNothing),
        illegalParameterAssignment(arrayPerson, arrayA),
        illegalParameterAssignment(arrayPerson, array2Type),
        illegalParameterAssignment(arrayPerson, array2String),
        illegalParameterAssignment(arrayPerson, array2Blob),
        illegalParameterAssignment(arrayPerson, array2Person),
        illegalParameterAssignment(arrayPerson, array2Nothing),
        illegalParameterAssignment(arrayPerson, array2A),

        illegalParameterAssignment(arrayNothing, type),
        illegalParameterAssignment(arrayNothing, string),
        illegalParameterAssignment(arrayNothing, blob),
        illegalParameterAssignment(arrayNothing, person),
        allowedParameterAssignment(arrayNothing, nothing),
        illegalParameterAssignment(arrayNothing, a),
        illegalParameterAssignment(arrayNothing, arrayType),
        illegalParameterAssignment(arrayNothing, arrayString),
        illegalParameterAssignment(arrayNothing, arrayBlob),
        illegalParameterAssignment(arrayNothing, arrayPerson),
        allowedParameterAssignment(arrayNothing, arrayNothing),
        illegalParameterAssignment(arrayNothing, arrayA),
        illegalParameterAssignment(arrayNothing, array2Type),
        illegalParameterAssignment(arrayNothing, array2String),
        illegalParameterAssignment(arrayNothing, array2Blob),
        illegalParameterAssignment(arrayNothing, array2Person),
        illegalParameterAssignment(arrayNothing, array2Nothing),
        illegalParameterAssignment(arrayNothing, array2A),

        illegalParameterAssignment(arrayA, type),
        illegalParameterAssignment(arrayA, string),
        illegalParameterAssignment(arrayA, blob),
        illegalParameterAssignment(arrayA, person),
        allowedParameterAssignment(arrayA, nothing),
        illegalParameterAssignment(arrayA, a),
        illegalParameterAssignment(arrayA, b),
        allowedParameterAssignment(arrayA, arrayType),
        allowedParameterAssignment(arrayA, arrayString),
        allowedParameterAssignment(arrayA, arrayBlob),
        allowedParameterAssignment(arrayA, arrayPerson),
        allowedParameterAssignment(arrayA, arrayNothing),
        allowedParameterAssignment(arrayA, arrayA),
        allowedParameterAssignment(arrayA, arrayB),
        allowedParameterAssignment(arrayA, array2Type),
        allowedParameterAssignment(arrayA, array2String),
        allowedParameterAssignment(arrayA, array2Blob),
        allowedParameterAssignment(arrayA, array2Person),
        allowedParameterAssignment(arrayA, array2Nothing),
        allowedParameterAssignment(arrayA, array2A),
        allowedParameterAssignment(arrayA, array2B),

        illegalParameterAssignment(array2Type, type),
        illegalParameterAssignment(array2Type, string),
        illegalParameterAssignment(array2Type, blob),
        illegalParameterAssignment(array2Type, person),
        allowedParameterAssignment(array2Type, nothing),
        illegalParameterAssignment(array2Type, a),
        illegalParameterAssignment(array2Type, arrayType),
        illegalParameterAssignment(array2Type, arrayString),
        illegalParameterAssignment(array2Type, arrayBlob),
        illegalParameterAssignment(array2Type, arrayPerson),
        allowedParameterAssignment(array2Type, arrayNothing),
        illegalParameterAssignment(array2Type, arrayA),
        allowedParameterAssignment(array2Type, array2Type),
        illegalParameterAssignment(array2Type, array2String),
        illegalParameterAssignment(array2Type, array2Blob),
        illegalParameterAssignment(array2Type, array2Person),
        allowedParameterAssignment(array2Type, array2Nothing),
        illegalParameterAssignment(array2Type, array2A),

        illegalParameterAssignment(array2String, type),
        illegalParameterAssignment(array2String, string),
        illegalParameterAssignment(array2String, blob),
        illegalParameterAssignment(array2String, person),
        allowedParameterAssignment(array2String, nothing),
        illegalParameterAssignment(array2String, a),
        illegalParameterAssignment(array2String, arrayType),
        illegalParameterAssignment(array2String, arrayString),
        illegalParameterAssignment(array2String, arrayBlob),
        illegalParameterAssignment(array2String, arrayPerson),
        allowedParameterAssignment(array2String, arrayNothing),
        illegalParameterAssignment(array2String, arrayA),
        illegalParameterAssignment(array2String, array2Type),
        allowedParameterAssignment(array2String, array2String),
        illegalParameterAssignment(array2String, array2Blob),
        allowedParameterAssignment(array2String, array2Person),
        allowedParameterAssignment(array2String, array2Nothing),
        illegalParameterAssignment(array2String, array2A),

        illegalParameterAssignment(array2Blob, type),
        illegalParameterAssignment(array2Blob, string),
        illegalParameterAssignment(array2Blob, blob),
        illegalParameterAssignment(array2Blob, person),
        allowedParameterAssignment(array2Blob, nothing),
        illegalParameterAssignment(array2Blob, a),
        illegalParameterAssignment(array2Blob, arrayType),
        illegalParameterAssignment(array2Blob, arrayString),
        illegalParameterAssignment(array2Blob, arrayBlob),
        illegalParameterAssignment(array2Blob, arrayPerson),
        allowedParameterAssignment(array2Blob, arrayNothing),
        illegalParameterAssignment(array2Blob, arrayA),
        illegalParameterAssignment(array2Blob, array2Type),
        illegalParameterAssignment(array2Blob, array2String),
        allowedParameterAssignment(array2Blob, array2Blob),
        illegalParameterAssignment(array2Blob, array2Person),
        allowedParameterAssignment(array2Blob, array2Nothing),
        illegalParameterAssignment(array2Blob, array2A),

        illegalParameterAssignment(array2Person, type),
        illegalParameterAssignment(array2Person, string),
        illegalParameterAssignment(array2Person, blob),
        illegalParameterAssignment(array2Person, person),
        allowedParameterAssignment(array2Person, nothing),
        illegalParameterAssignment(array2Person, a),
        illegalParameterAssignment(array2Person, arrayType),
        illegalParameterAssignment(array2Person, arrayString),
        illegalParameterAssignment(array2Person, arrayBlob),
        illegalParameterAssignment(array2Person, arrayPerson),
        allowedParameterAssignment(array2Person, arrayNothing),
        illegalParameterAssignment(array2Person, arrayA),
        illegalParameterAssignment(array2Person, array2Type),
        illegalParameterAssignment(array2Person, array2String),
        illegalParameterAssignment(array2Person, array2Blob),
        allowedParameterAssignment(array2Person, array2Person),
        allowedParameterAssignment(array2Person, array2Nothing),
        illegalParameterAssignment(array2Person, array2A),

        illegalParameterAssignment(array2Nothing, type),
        illegalParameterAssignment(array2Nothing, string),
        illegalParameterAssignment(array2Nothing, blob),
        illegalParameterAssignment(array2Nothing, person),
        allowedParameterAssignment(array2Nothing, nothing),
        illegalParameterAssignment(array2Nothing, a),
        illegalParameterAssignment(array2Nothing, arrayType),
        illegalParameterAssignment(array2Nothing, arrayString),
        illegalParameterAssignment(array2Nothing, arrayBlob),
        illegalParameterAssignment(array2Nothing, arrayPerson),
        allowedParameterAssignment(array2Nothing, arrayNothing),
        illegalParameterAssignment(array2Nothing, arrayA),
        illegalParameterAssignment(array2Nothing, array2Type),
        illegalParameterAssignment(array2Nothing, array2String),
        illegalParameterAssignment(array2Nothing, array2Blob),
        illegalParameterAssignment(array2Nothing, array2Person),
        allowedParameterAssignment(array2Nothing, array2Nothing),
        illegalParameterAssignment(array2Nothing, array2A),

        illegalParameterAssignment(array2A, type),
        illegalParameterAssignment(array2A, string),
        illegalParameterAssignment(array2A, blob),
        illegalParameterAssignment(array2A, person),
        allowedParameterAssignment(array2A, nothing),
        illegalParameterAssignment(array2A, a),
        illegalParameterAssignment(array2A, b),
        illegalParameterAssignment(array2A, arrayType),
        illegalParameterAssignment(array2A, arrayString),
        illegalParameterAssignment(array2A, arrayBlob),
        illegalParameterAssignment(array2A, arrayPerson),
        allowedParameterAssignment(array2A, arrayNothing),
        illegalParameterAssignment(array2A, arrayA),
        illegalParameterAssignment(array2A, arrayB),
        allowedParameterAssignment(array2A, array2Type),
        allowedParameterAssignment(array2A, array2String),
        allowedParameterAssignment(array2A, array2Blob),
        allowedParameterAssignment(array2A, array2Person),
        allowedParameterAssignment(array2A, array2Nothing),
        allowedParameterAssignment(array2A, array2A),
        allowedParameterAssignment(array2A, array2B)));
  }

  private static Case allowedParameterAssignment(Type destination, Type source) {
    return newCase(
        destination.name() + " parameter is assignable from " + source.name(),
        () -> assertTrue(destination.isParamAssignableFrom(source)));
  }

  private static Case illegalParameterAssignment(Type destination, Type source) {
    return newCase(
        destination.name() + " parameter is NOT assignable from " + source.name(),
        () -> assertFalse(destination.isParamAssignableFrom(source)));
  }

  @Quackery
  public static Suite common_super_type() throws Exception {
    return suite("Type.commonSuperType").addAll(asList(
        assertCommon(type, type, type),
        assertCommon(type, string, null),
        assertCommon(type, blob, null),
        assertCommon(type, nothing, type),
        assertCommon(type, a, null),
        assertCommon(type, arrayType, null),
        assertCommon(type, arrayString, null),
        assertCommon(type, arrayBlob, null),
        assertCommon(type, arrayNothing, null),
        assertCommon(type, arrayA, null),

        assertCommon(string, string, string),
        assertCommon(string, blob, null),
        assertCommon(string, nothing, string),
        assertCommon(string, a, null),
        assertCommon(string, arrayString, null),
        assertCommon(string, arrayType, null),
        assertCommon(string, arrayBlob, null),
        assertCommon(string, arrayNothing, null),
        assertCommon(string, arrayA, null),

        assertCommon(blob, blob, blob),
        assertCommon(blob, nothing, blob),
        assertCommon(blob, a, null),
        assertCommon(blob, arrayType, null),
        assertCommon(blob, arrayString, null),
        assertCommon(blob, arrayBlob, null),
        assertCommon(blob, arrayNothing, null),
        assertCommon(blob, arrayA, null),

        assertCommon(nothing, nothing, nothing),
        assertCommon(nothing, a, a),
        assertCommon(nothing, arrayType, arrayType),
        assertCommon(nothing, arrayString, arrayString),
        assertCommon(nothing, arrayBlob, arrayBlob),
        assertCommon(nothing, arrayNothing, arrayNothing),
        assertCommon(nothing, arrayA, arrayA),

        assertCommon(a, a, a),
        assertCommon(a, b, null),
        assertCommon(a, arrayType, null),
        assertCommon(a, arrayString, null),
        assertCommon(a, arrayBlob, null),
        assertCommon(a, arrayNothing, null),
        assertCommon(a, arrayA, null),
        assertCommon(a, arrayB, null),

        assertCommon(arrayType, arrayType, arrayType),
        assertCommon(arrayType, arrayString, null),
        assertCommon(arrayType, arrayBlob, null),
        assertCommon(arrayType, arrayNothing, arrayType),
        assertCommon(arrayType, arrayA, null),

        assertCommon(arrayString, arrayString, arrayString),
        assertCommon(arrayString, arrayBlob, null),
        assertCommon(arrayString, arrayNothing, arrayString),
        assertCommon(arrayString, nothing, arrayString),
        assertCommon(arrayString, arrayA, null),

        assertCommon(arrayBlob, arrayBlob, arrayBlob),
        assertCommon(arrayBlob, arrayNothing, arrayBlob),
        assertCommon(arrayBlob, nothing, arrayBlob),
        assertCommon(arrayBlob, arrayA, null),

        assertCommon(arrayNothing, arrayNothing, arrayNothing),
        assertCommon(arrayNothing, array2Nothing, array2Nothing),
        assertCommon(arrayNothing, arrayType, arrayType),
        assertCommon(arrayNothing, arrayString, arrayString),
        assertCommon(arrayNothing, arrayBlob, arrayBlob),
        assertCommon(arrayNothing, arrayA, arrayA),

        assertCommon(arrayA, arrayA, arrayA),
        assertCommon(arrayA, arrayB, null)));
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
  public static Suite actualCoreTypeWhenAssignedFrom() {
    return suite("GenericType.actualCoreTypeWhenAssignedFrom").addAll(asList(
        assertActualCoreType(a, type, type),
        assertActualCoreType(a, string, string),
        assertActualCoreType(a, blob, blob),
        assertActualCoreType(a, person, person),
        assertActualCoreType(a, nothing, nothing),
        assertActualCoreType(a, a, a),
        assertActualCoreType(a, b, b),

        assertActualCoreType(a, arrayType, arrayType),
        assertActualCoreType(a, arrayString, arrayString),
        assertActualCoreType(a, arrayBlob, arrayBlob),
        assertActualCoreType(a, arrayPerson, arrayPerson),
        assertActualCoreType(a, arrayNothing, arrayNothing),
        assertActualCoreType(a, arrayA, arrayA),
        assertActualCoreType(a, arrayB, arrayB),

        assertActualCoreType(a, array2Type, array2Type),
        assertActualCoreType(a, array2String, array2String),
        assertActualCoreType(a, array2Blob, array2Blob),
        assertActualCoreType(a, array2Person, array2Person),
        assertActualCoreType(a, array2Nothing, array2Nothing),
        assertActualCoreType(a, array2A, array2A),
        assertActualCoreType(a, array2B, array2B),

        failedActualCoreType(arrayA, type),
        failedActualCoreType(arrayA, string),
        failedActualCoreType(arrayA, blob),
        failedActualCoreType(arrayA, person),
        assertActualCoreType(arrayA, nothing, nothing),
        failedActualCoreType(arrayA, a),
        failedActualCoreType(arrayA, b),

        assertActualCoreType(arrayA, arrayType, type),
        assertActualCoreType(arrayA, arrayString, string),
        assertActualCoreType(arrayA, arrayBlob, blob),
        assertActualCoreType(arrayA, arrayPerson, person),
        assertActualCoreType(arrayA, arrayNothing, nothing),
        assertActualCoreType(arrayA, arrayA, a),
        assertActualCoreType(arrayA, arrayB, b),

        assertActualCoreType(arrayA, array2Type, arrayType),
        assertActualCoreType(arrayA, array2String, arrayString),
        assertActualCoreType(arrayA, array2Blob, arrayBlob),
        assertActualCoreType(arrayA, array2Person, arrayPerson),
        assertActualCoreType(arrayA, array2Nothing, arrayNothing),
        assertActualCoreType(arrayA, array2A, arrayA),
        assertActualCoreType(arrayA, array2B, arrayB),

        failedActualCoreType(array2A, type),
        failedActualCoreType(array2A, string),
        failedActualCoreType(array2A, blob),
        failedActualCoreType(array2A, person),
        assertActualCoreType(array2A, nothing, nothing),
        failedActualCoreType(array2A, a),
        failedActualCoreType(array2A, b),

        failedActualCoreType(array2A, arrayType),
        failedActualCoreType(array2A, arrayString),
        failedActualCoreType(array2A, arrayBlob),
        failedActualCoreType(array2A, arrayPerson),
        assertActualCoreType(array2A, arrayNothing, nothing),
        failedActualCoreType(array2A, arrayA),
        failedActualCoreType(array2A, arrayB),

        assertActualCoreType(array2A, array2Type, type),
        assertActualCoreType(array2A, array2String, string),
        assertActualCoreType(array2A, array2Blob, blob),
        assertActualCoreType(array2A, array2Person, person),
        assertActualCoreType(array2A, array2Nothing, nothing),
        assertActualCoreType(array2A, array2A, a),
        assertActualCoreType(array2A, array2B, b)));
  }

  private static Case assertActualCoreType(GenericType type, Type assigned, Type expected) {
    String expectedName = expected == null ? "null" : expected.name();
    return newCase(
        "Type " + type.name() + " when assigned from " + assigned.name()
            + " gets actual core type == " + expectedName,
        () -> assertEquals(expected, type.actualCoreTypeWhenAssignedFrom(assigned)));
  }

  private static Case failedActualCoreType(GenericType type, Type assigned) {
    return newCase(
        "actualCoreTypeWhenAssignedFrom " + type.name() + ", " + assigned.name()
            + " fails with IAE",
        () -> {
          when(() -> type.actualCoreTypeWhenAssignedFrom(assigned));
          thenThrown(IllegalArgumentException.class);
        });
  }

  @Quackery
  public static Suite array_element_types() {
    return suite("Type.elemType").addAll(asList(
        elementTypeOf(arrayType, type),
        elementTypeOf(arrayString, string),
        elementTypeOf(arrayBlob, blob),
        elementTypeOf(arrayPerson, person),
        elementTypeOf(arrayNothing, nothing),

        elementTypeOf(array2Type, arrayType),
        elementTypeOf(array2String, arrayString),
        elementTypeOf(array2Blob, arrayBlob),
        elementTypeOf(array2Person, arrayPerson),
        elementTypeOf(array2Nothing, arrayNothing)));
  }

  private static Case elementTypeOf(ConcreteArrayType arrayType, ConcreteType expected) {
    return newCase(
        arrayType.name() + ".elemType() == " + expected,
        () -> assertEquals(expected, arrayType.elemType()));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(type, type);
    tester.addEqualityGroup(string, string);
    tester.addEqualityGroup(blob, blob);
    tester.addEqualityGroup(nothing, nothing);
    tester.addEqualityGroup(person, person);
    tester.addEqualityGroup(a, a);

    tester.addEqualityGroup(arrayType, arrayType);
    tester.addEqualityGroup(arrayString, arrayString);
    tester.addEqualityGroup(arrayBlob, arrayBlob);
    tester.addEqualityGroup(arrayPerson, arrayPerson);
    tester.addEqualityGroup(arrayA, arrayA);

    tester.addEqualityGroup(array2Type, array2Type);
    tester.addEqualityGroup(array2String, array2String);
    tester.addEqualityGroup(array2Blob, array2Blob);
    tester.addEqualityGroup(array2Person, array2Person);
    tester.addEqualityGroup(array2A, array2A);
    tester.testEquals();
  }
}
