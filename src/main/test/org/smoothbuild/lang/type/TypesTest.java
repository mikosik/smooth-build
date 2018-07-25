package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import static org.smoothbuild.lang.type.TestingTypes.personType;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.lang.type.TestingTypes.type;
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
        typeNameIs(nothing, "Nothing"),
        typeNameIs(personType, "Person"),
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
  public static Suite to_string() {
    return suite("Type.toString").addAll(asList(
        typeToStringIs(type, "Type(\"Type\")"),
        typeToStringIs(string, "Type(\"String\")"),
        typeToStringIs(blob, "Type(\"Blob\")"),
        typeToStringIs(nothing, "Type(\"Nothing\")"),
        typeToStringIs(personType, "Type(\"Person\")"),
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
        coreTypeIs(personType, personType),
        coreTypeIs(a, a),

        coreTypeIs(arrayString, string),
        coreTypeIs(arrayBlob, blob),
        coreTypeIs(arrayNothing, nothing),
        coreTypeIs(arrayPerson, personType),
        coreTypeIs(arrayA, a),

        coreTypeIs(array2String, string),
        coreTypeIs(array2Blob, blob),
        coreTypeIs(array2Nothing, nothing),
        coreTypeIs(array2Person, personType),
        coreTypeIs(array2A, a)));
  }

  private static Case coreTypeIs(Type type, Type expected) {
    return newCase(
        type.name() + ".coreType() == " + expected.name(),
        () -> assertEquals(expected, type.coreType()));
  }

  @Quackery
  public static Suite core_depth() throws Exception {
    return suite("Type.coreDepth").addAll(asList(
        coreDepthIs(type, 0),
        coreDepthIs(string, 0),
        coreDepthIs(blob, 0),
        coreDepthIs(nothing, 0),
        coreDepthIs(personType, 0),
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
  public static Suite is_concrete() throws Exception {
    return suite("Type.isArray").addAll(asList(
        isNotGeneric(type),
        isNotGeneric(string),
        isNotGeneric(blob),
        isNotGeneric(nothing),
        isNotGeneric(personType),
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
        isNotArrayType(personType),
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
        superTypeIs(string, personType),
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
        hierarchyTest(list(string, personType)),
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
        illegalAssignment(type, personType),
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
        allowedAssignment(string, personType),
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
        illegalAssignment(blob, personType),
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

        illegalAssignment(personType, type),
        illegalAssignment(personType, string),
        illegalAssignment(personType, blob),
        allowedAssignment(personType, personType),
        allowedAssignment(personType, nothing),
        illegalAssignment(personType, a),
        illegalAssignment(personType, arrayType),
        illegalAssignment(personType, arrayString),
        illegalAssignment(personType, arrayBlob),
        illegalAssignment(personType, arrayPerson),
        illegalAssignment(personType, arrayNothing),
        illegalAssignment(personType, arrayA),
        illegalAssignment(personType, array2Type),
        illegalAssignment(personType, array2String),
        illegalAssignment(personType, array2Blob),
        illegalAssignment(personType, array2Person),
        illegalAssignment(personType, array2Nothing),
        illegalAssignment(personType, array2A),

        illegalAssignment(nothing, type),
        illegalAssignment(nothing, string),
        illegalAssignment(nothing, blob),
        illegalAssignment(nothing, personType),
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
        illegalAssignment(a, personType),
        illegalAssignment(a, nothing),
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
        illegalAssignment(arrayType, personType),
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
        illegalAssignment(arrayString, personType),
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
        illegalAssignment(arrayBlob, personType),
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
        illegalAssignment(arrayPerson, personType),
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
        illegalAssignment(arrayNothing, personType),
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
        illegalAssignment(arrayA, personType),
        illegalAssignment(arrayA, nothing),
        illegalAssignment(arrayA, a),
        illegalAssignment(arrayA, b),
        illegalAssignment(arrayA, arrayType),
        illegalAssignment(arrayA, arrayString),
        illegalAssignment(arrayA, arrayBlob),
        illegalAssignment(arrayA, arrayPerson),
        illegalAssignment(arrayA, arrayNothing),
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
        illegalAssignment(array2Type, personType),
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
        illegalAssignment(array2String, personType),
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
        illegalAssignment(array2Blob, personType),
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
        illegalAssignment(array2Person, personType),
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
        illegalAssignment(array2Nothing, personType),
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
        illegalAssignment(array2A, personType),
        illegalAssignment(array2A, nothing),
        illegalAssignment(array2A, a),
        illegalAssignment(array2A, b),
        illegalAssignment(array2A, arrayType),
        illegalAssignment(array2A, arrayString),
        illegalAssignment(array2A, arrayBlob),
        illegalAssignment(array2A, arrayPerson),
        illegalAssignment(array2A, arrayNothing),
        illegalAssignment(array2A, arrayA),
        illegalAssignment(array2A, arrayB),
        illegalAssignment(array2A, array2Type),
        illegalAssignment(array2A, array2String),
        illegalAssignment(array2A, array2Blob),
        illegalAssignment(array2A, array2Person),
        illegalAssignment(array2A, array2Nothing),
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
        assertCommon(nothing, a, null),
        assertCommon(nothing, arrayType, arrayType),
        assertCommon(nothing, arrayString, arrayString),
        assertCommon(nothing, arrayBlob, arrayBlob),
        assertCommon(nothing, arrayNothing, arrayNothing),
        assertCommon(nothing, arrayA, null),

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
        assertCommon(arrayNothing, arrayA, null),

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
    return suite("GenericType.inferActualCoreTypeWhenAssignedFrom").addAll(asList(
        actualCoreType(a, type, type),
        actualCoreType(a, string, string),
        actualCoreType(a, blob, blob),
        actualCoreType(a, personType, personType),
        actualCoreType(a, nothing, nothing),
        actualCoreType(a, a, a),
        actualCoreType(a, b, b),

        actualCoreType(a, arrayType, arrayType),
        actualCoreType(a, arrayString, arrayString),
        actualCoreType(a, arrayBlob, arrayBlob),
        actualCoreType(a, arrayPerson, arrayPerson),
        actualCoreType(a, arrayNothing, arrayNothing),
        actualCoreType(a, arrayA, arrayA),
        actualCoreType(a, arrayB, arrayB),

        actualCoreType(a, array2Type, array2Type),
        actualCoreType(a, array2String, array2String),
        actualCoreType(a, array2Blob, array2Blob),
        actualCoreType(a, array2Person, array2Person),
        actualCoreType(a, array2Nothing, array2Nothing),
        actualCoreType(a, array2A, array2A),
        actualCoreType(a, array2B, array2B),

        actualCoreType(arrayA, type, null),
        actualCoreType(arrayA, string, null),
        actualCoreType(arrayA, blob, null),
        actualCoreType(arrayA, personType, null),
        actualCoreType(arrayA, nothing, null),
        actualCoreType(arrayA, a, null),
        actualCoreType(arrayA, b, null),

        actualCoreType(arrayA, arrayType, type),
        actualCoreType(arrayA, arrayString, string),
        actualCoreType(arrayA, arrayBlob, blob),
        actualCoreType(arrayA, arrayPerson, personType),
        actualCoreType(arrayA, arrayNothing, nothing),
        actualCoreType(arrayA, arrayA, a),
        actualCoreType(arrayA, arrayB, b),

        actualCoreType(arrayA, array2Type, arrayType),
        actualCoreType(arrayA, array2String, arrayString),
        actualCoreType(arrayA, array2Blob, arrayBlob),
        actualCoreType(arrayA, array2Person, arrayPerson),
        actualCoreType(arrayA, array2Nothing, arrayNothing),
        actualCoreType(arrayA, array2A, arrayA),
        actualCoreType(arrayA, array2B, arrayB),

        actualCoreType(array2A, type, null),
        actualCoreType(array2A, string, null),
        actualCoreType(array2A, blob, null),
        actualCoreType(array2A, personType, null),
        actualCoreType(array2A, nothing, null),
        actualCoreType(array2A, a, null),
        actualCoreType(array2A, b, null),

        actualCoreType(array2A, arrayType, null),
        actualCoreType(array2A, arrayString, null),
        actualCoreType(array2A, arrayBlob, null),
        actualCoreType(array2A, arrayPerson, null),
        actualCoreType(array2A, arrayNothing, null),
        actualCoreType(array2A, arrayA, null),
        actualCoreType(array2A, arrayB, null),

        actualCoreType(array2A, array2Type, type),
        actualCoreType(array2A, array2String, string),
        actualCoreType(array2A, array2Blob, blob),
        actualCoreType(array2A, array2Person, personType),
        actualCoreType(array2A, array2Nothing, nothing),
        actualCoreType(array2A, array2A, a),
        actualCoreType(array2A, array2B, b)));
  }

  private static Case actualCoreType(GenericType type, Type assigned, Type expected) {
    String expectedName = expected == null ? "null" : expected.name();
    return newCase(
        "Type " + type.name() + " when assigned from " + assigned.name()
            + " gets actual core type == " + expectedName,
        () -> assertEquals(expected, type.actualCoreTypeWhenAssignedFrom(assigned)));
  }

  @Quackery
  public static Suite array_element_types() {
    return suite("Type.elemType").addAll(asList(
        elementTypeOf(arrayType, type),
        elementTypeOf(arrayString, string),
        elementTypeOf(arrayBlob, blob),
        elementTypeOf(arrayPerson, personType),
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
    tester.addEqualityGroup(personType, personType);
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
