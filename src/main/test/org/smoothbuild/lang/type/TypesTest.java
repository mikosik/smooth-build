package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.type.TestingTypes.array;
import static org.smoothbuild.lang.type.TestingTypes.array2;
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

        typeNameIs(array(type), "[Type]"),
        typeNameIs(array(string), "[String]"),
        typeNameIs(array(blob), "[Blob]"),
        typeNameIs(array(nothing), "[Nothing]"),
        typeNameIs(array(personType), "[Person]"),

        typeNameIs(array2(type), "[[Type]]"),
        typeNameIs(array2(string), "[[String]]"),
        typeNameIs(array2(blob), "[[Blob]]"),
        typeNameIs(array2(nothing), "[[Nothing]]"),
        typeNameIs(array2(personType), "[[Person]]")));
  }

  private static Case typeNameIs(ConcreteType type, String expected) {
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

        typeToStringIs(array(type), "Type(\"[Type]\")"),
        typeToStringIs(array(string), "Type(\"[String]\")"),
        typeToStringIs(array(blob), "Type(\"[Blob]\")"),
        typeToStringIs(array(nothing), "Type(\"[Nothing]\")"),
        typeToStringIs(array(personType), "Type(\"[Person]\")"),

        typeToStringIs(array2(type), "Type(\"[[Type]]\")"),
        typeToStringIs(array2(string), "Type(\"[[String]]\")"),
        typeToStringIs(array2(blob), "Type(\"[[Blob]]\")"),
        typeToStringIs(array2(nothing), "Type(\"[[Nothing]]\")"),
        typeToStringIs(array2(personType), "Type(\"[[Person]]\")")));
  }

  private static Case typeToStringIs(ConcreteType type, String expectedPrefix) {
    return newCase(
        type.name() + ".toString()",
        () -> assertEquals(expectedPrefix + ":" + type.hash(), type.toString()));
  }

  @Quackery
  public static Suite jType() {
    return suite("Type.jType").addAll(asList(
        jTypeIs(type, ConcreteType.class),
        jTypeIs(string, SString.class),
        jTypeIs(blob, Blob.class),
        jTypeIs(nothing, Nothing.class),
        jTypeIs(array(type), Array.class),
        jTypeIs(array(string), Array.class),
        jTypeIs(array(blob), Array.class),
        jTypeIs(array(nothing), Array.class)));
  }

  private static Case jTypeIs(ConcreteType type, Class<?> expected) {
    return newCase(
        type.name() + ".jType",
        () -> assertEquals(expected, type.jType()));
  }

  @Quackery
  public static Suite core_type() throws Exception {
    return suite("Type.jType").addAll(asList(
        coreTypeIs(type, type),
        coreTypeIs(string, string),
        coreTypeIs(blob, blob),
        coreTypeIs(nothing, nothing),
        coreTypeIs(personType, personType),

        coreTypeIs(array(string), string),
        coreTypeIs(array(blob), blob),
        coreTypeIs(array(nothing), nothing),
        coreTypeIs(array(personType), personType),

        coreTypeIs(array2(string), string),
        coreTypeIs(array2(blob), blob),
        coreTypeIs(array2(nothing), nothing),
        coreTypeIs(array2(personType), personType)));
  }

  private static Case coreTypeIs(ConcreteType type, ConcreteType expected) {
    return newCase(
        type.name() + ".coreType()",
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

        coreDepthIs(array(string), 1),
        coreDepthIs(array(blob), 1),
        coreDepthIs(array(nothing), 1),
        coreDepthIs(array(personType), 1),

        coreDepthIs(array2(string), 2),
        coreDepthIs(array2(blob), 2),
        coreDepthIs(array2(nothing), 2),
        coreDepthIs(array2(personType), 2)));
  }

  private static Case coreDepthIs(ConcreteType type, int depth) {
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
        isNotArrayType(nothing),
        isNotArrayType(personType),
        isArrayType(array(type)),
        isArrayType(array(string)),
        isArrayType(array(blob)),
        isArrayType(array(nothing)),
        isArrayType(array(personType)),
        isArrayType(array2(type)),
        isArrayType(array2(string)),
        isArrayType(array2(blob)),
        isArrayType(array2(nothing)),
        isArrayType(array2(personType))));
  }

  private static Case isArrayType(ConcreteType type) {
    return newCase(type.name() + " is array type", () -> assertTrue(type.isArray()));
  }

  private static Case isNotArrayType(ConcreteType type) {
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

        superTypeIs(null, array(type)),
        superTypeIs(null, array(string)),
        superTypeIs(null, array(blob)),
        superTypeIs(null, array(nothing)),
        superTypeIs(array(string), array(personType)),

        superTypeIs(null, array2(type)),
        superTypeIs(null, array2(string)),
        superTypeIs(null, array2(blob)),
        superTypeIs(null, array2(nothing)),
        superTypeIs(array2(string), array2(personType))));
  }

  private static Case superTypeIs(Object expected, ConcreteType type) {
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
        hierarchyTest(list(array(string))),
        hierarchyTest(list(array(string), array(personType))),
        hierarchyTest(list(array(nothing))),
        hierarchyTest(list(array2(string))),
        hierarchyTest(list(array2(string), array2(personType))),
        hierarchyTest(list(array2(nothing)))));
  }

  private static Case hierarchyTest(List<ConcreteType> hierarchy) {
    ConcreteType root = hierarchy.get(hierarchy.size() - 1);
    return newCase(
        "Hierarchy of " + root.name() + " is " + hierarchy.toString(),
        () -> assertEquals(hierarchy, root.hierarchy()));
  }

  @Quackery
  public static Suite is_assignable_from() throws Exception {
    return suite("Type.isAssignableFrom").addAll(asList(
        allowedAssignment(type, type),
        allowedAssignment(type, nothing),
        illegalAssignment(type, array(type)),
        illegalAssignment(type, array2(type)),
        illegalAssignment(type, array(nothing)),
        illegalAssignment(type, array2(nothing)),
        illegalAssignment(type, string),
        illegalAssignment(type, array(string)),
        illegalAssignment(type, array2(string)),
        illegalAssignment(type, blob),
        illegalAssignment(type, array(blob)),
        illegalAssignment(type, array2(blob)),
        illegalAssignment(type, personType),
        illegalAssignment(type, array(personType)),
        illegalAssignment(type, array2(personType)),

        allowedAssignment(array(type), array(type)),
        allowedAssignment(array(type), nothing),
        allowedAssignment(array(type), array(nothing)),
        illegalAssignment(array(type), type),
        illegalAssignment(array(type), array2(nothing)),
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

        allowedAssignment(array2(type), array2(type)),
        allowedAssignment(array2(type), nothing),
        allowedAssignment(array2(type), array(nothing)),
        allowedAssignment(array2(type), array2(nothing)),
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
        allowedAssignment(string, nothing),
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
        illegalAssignment(string, array(nothing)),
        illegalAssignment(string, array2(nothing)),

        allowedAssignment(array(string), array(string)),
        allowedAssignment(array(string), array(personType)),
        allowedAssignment(array(string), nothing),
        allowedAssignment(array(string), array(nothing)),
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
        illegalAssignment(array(string), array2(nothing)),

        allowedAssignment(array2(string), array2(string)),
        allowedAssignment(array2(string), array2(personType)),
        allowedAssignment(array2(string), nothing),
        allowedAssignment(array2(string), array(nothing)),
        allowedAssignment(array2(string), array2(nothing)),
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
        allowedAssignment(blob, nothing),
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
        illegalAssignment(blob, array(nothing)),
        illegalAssignment(blob, array2(nothing)),

        allowedAssignment(array(blob), array(blob)),
        allowedAssignment(array(blob), nothing),
        allowedAssignment(array(blob), array(nothing)),
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
        illegalAssignment(array(blob), array2(nothing)),

        allowedAssignment(array2(blob), array2(blob)),
        allowedAssignment(array2(blob), nothing),
        allowedAssignment(array2(blob), array(nothing)),
        allowedAssignment(array2(blob), array2(nothing)),
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
        allowedAssignment(personType, nothing),
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
        illegalAssignment(personType, array(nothing)),
        illegalAssignment(personType, array2(nothing)),

        allowedAssignment(array(personType), array(personType)),
        allowedAssignment(array(personType), nothing),
        allowedAssignment(array(personType), array(nothing)),
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
        illegalAssignment(array(personType), array2(nothing)),

        allowedAssignment(array2(personType), array2(personType)),
        allowedAssignment(array2(personType), nothing),
        allowedAssignment(array2(personType), array(nothing)),
        allowedAssignment(array2(personType), array2(nothing)),
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
        illegalAssignment(array2(personType), array(personType))));
  }

  private static Case allowedAssignment(ConcreteType destination, ConcreteType source) {
    return newCase(
        destination.name() + " is assignable from " + source.name(),
        () -> assertTrue(destination.isAssignableFrom(source)));
  }

  private static Case illegalAssignment(ConcreteType destination, ConcreteType source) {
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
        assertCommon(type, array(type), null),
        assertCommon(type, array(string), null),
        assertCommon(type, array(blob), null),
        assertCommon(type, array(nothing), null),

        assertCommon(string, string, string),
        assertCommon(string, blob, null),
        assertCommon(string, nothing, string),
        assertCommon(string, array(string), null),
        assertCommon(string, array(type), null),
        assertCommon(string, array(blob), null),
        assertCommon(string, array(nothing), null),

        assertCommon(blob, blob, blob),
        assertCommon(blob, nothing, blob),
        assertCommon(blob, array(type), null),
        assertCommon(blob, array(string), null),
        assertCommon(blob, array(blob), null),
        assertCommon(blob, array(nothing), null),

        assertCommon(nothing, nothing, nothing),
        assertCommon(nothing, array(type), array(type)),
        assertCommon(nothing, array(string), array(string)),
        assertCommon(nothing, array(blob), array(blob)),
        assertCommon(nothing, array(nothing), array(nothing)),

        assertCommon(array(type), array(type), array(type)),
        assertCommon(array(type), array(string), null),
        assertCommon(array(type), array(blob), null),
        assertCommon(array(type), array(nothing), array(type)),
        assertCommon(array(string), array(string), array(string)),
        assertCommon(array(string), array(blob), null),
        assertCommon(array(string), array(nothing), array(string)),
        assertCommon(array(string), nothing, array(string)),

        assertCommon(array(blob), array(blob), array(blob)),
        assertCommon(array(blob), array(nothing), array(blob)),
        assertCommon(array(blob), nothing, array(blob)),

        assertCommon(array(nothing), array(nothing), array(nothing)),
        assertCommon(array(nothing), array2(nothing), array2(nothing)),
        assertCommon(array(nothing), array(type), array(type)),
        assertCommon(array(nothing), array(string), array(string)),
        assertCommon(array(nothing), array(blob), array(blob))));
  }

  private static Case assertCommon(ConcreteType type1, ConcreteType type2, ConcreteType expected) {
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
        elementTypeOf(array(nothing), nothing),

        elementTypeOf(array2(type), array(type)),
        elementTypeOf(array2(string), array(string)),
        elementTypeOf(array2(blob), array(blob)),
        elementTypeOf(array2(personType), array(personType)),
        elementTypeOf(array2(nothing), array(nothing))));
  }

  private static Case elementTypeOf(ArrayType arrayType, ConcreteType expected) {
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

    tester.addEqualityGroup(array(type), array(type));
    tester.addEqualityGroup(array(string), array(string));
    tester.addEqualityGroup(array(blob), array(blob));
    tester.addEqualityGroup(array(personType), array(personType));

    tester.addEqualityGroup(array2(type), array2(type));
    tester.addEqualityGroup(array2(string), array2(string));
    tester.addEqualityGroup(array2(blob), array2(blob));
    tester.addEqualityGroup(array2(personType), array2(personType));
    tester.testEquals();
  }
}
