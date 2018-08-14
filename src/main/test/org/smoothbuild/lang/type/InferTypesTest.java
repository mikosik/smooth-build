package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.type.InferTypes.inferActualCoreTypes;
import static org.smoothbuild.lang.type.TestingTypes.a;
import static org.smoothbuild.lang.type.TestingTypes.array2A;
import static org.smoothbuild.lang.type.TestingTypes.array2B;
import static org.smoothbuild.lang.type.TestingTypes.array2Nothing;
import static org.smoothbuild.lang.type.TestingTypes.array2Person;
import static org.smoothbuild.lang.type.TestingTypes.array2String;
import static org.smoothbuild.lang.type.TestingTypes.arrayA;
import static org.smoothbuild.lang.type.TestingTypes.arrayB;
import static org.smoothbuild.lang.type.TestingTypes.arrayNothing;
import static org.smoothbuild.lang.type.TestingTypes.arrayPerson;
import static org.smoothbuild.lang.type.TestingTypes.arrayString;
import static org.smoothbuild.lang.type.TestingTypes.b;
import static org.smoothbuild.lang.type.TestingTypes.nothing;
import static org.smoothbuild.lang.type.TestingTypes.personType;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;

import com.google.common.collect.ImmutableMap;

@RunWith(QuackeryRunner.class)
public class InferTypesTest {
  @Quackery
  public static Suite infer_actual_core_types() {
    return suite("infer_actual_core_types").addAll(asList(
        // a <- string
        assertInferActualCoreTypes(
            list(a),
            list(string),
            map(a, string)),
        assertInferActualCoreTypes(
            list(a),
            list(arrayString),
            map(a, arrayString)),
        assertInferActualCoreTypes(
            list(a),
            list(array2String),
            map(a, array2String)),

        failsInferActualCoreTypes(
            list(arrayA),
            list(string)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(arrayString),
            map(a, string)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(array2String),
            map(a, arrayString)),

        failsInferActualCoreTypes(
            list(array2A),
            list(string)),
        failsInferActualCoreTypes(
            list(array2A),
            list(arrayString)),
        assertInferActualCoreTypes(
            list(array2A),
            list(array2String),
            map(a, string)),

        // a <- struct (Person)
        assertInferActualCoreTypes(
            list(a),
            list(personType),
            map(a, personType)),
        assertInferActualCoreTypes(
            list(a),
            list(arrayPerson),
            map(a, arrayPerson)),
        assertInferActualCoreTypes(
            list(a),
            list(array2Person),
            map(a, array2Person)),

        failsInferActualCoreTypes(
            list(arrayA),
            list(personType)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(arrayPerson),
            map(a, personType)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(array2Person),
            map(a, arrayPerson)),

        failsInferActualCoreTypes(
            list(array2A),
            list(personType)),
        failsInferActualCoreTypes(
            list(array2A),
            list(arrayPerson)),
        assertInferActualCoreTypes(
            list(array2A),
            list(array2Person),
            map(a, personType)),

        // a <- Nothing

        assertInferActualCoreTypes(
            list(a),
            list(nothing),
            map(a, nothing)),
        assertInferActualCoreTypes(
            list(a),
            list(arrayNothing),
            map(a, arrayNothing)),
        assertInferActualCoreTypes(
            list(a),
            list(array2Nothing),
            map(a, array2Nothing)),

        assertInferActualCoreTypes(
            list(arrayA),
            list(nothing),
            map(a, nothing)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(arrayNothing),
            map(a, nothing)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(array2Nothing),
            map(a, arrayNothing)),

        assertInferActualCoreTypes(
            list(array2A),
            list(nothing),
            map(a, nothing)),
        assertInferActualCoreTypes(
            list(array2A),
            list(arrayNothing),
            map(a, nothing)),
        assertInferActualCoreTypes(
            list(array2A),
            list(array2Nothing),
            map(a, nothing)),

        // a <- b

        assertInferActualCoreTypes(
            list(a),
            list(b),
            map(a, b)),
        assertInferActualCoreTypes(
            list(a),
            list(arrayB),
            map(a, arrayB)),
        assertInferActualCoreTypes(
            list(a),
            list(array2B),
            map(a, array2B)),

        failsInferActualCoreTypes(
            list(arrayA),
            list(b)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(arrayB),
            map(a, b)),
        assertInferActualCoreTypes(
            list(arrayA),
            list(array2B),
            map(a, arrayB)),

        failsInferActualCoreTypes(
            list(array2A),
            list(b)),
        failsInferActualCoreTypes(
            list(array2A),
            list(arrayB)),
        assertInferActualCoreTypes(
            list(array2A),
            list(array2B),
            map(a, b)),

        // a <- String, struct (Person); with conversions

        assertInferActualCoreTypes(
            list(a, a),
            list(personType, string),
            map(a, string)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(personType, arrayString),
            map(a, string)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(string, arrayPerson),
            map(a, string)),

        // a <- Nothing, String; with conversions

        assertInferActualCoreTypes(
            list(a, a),
            list(nothing, string),
            map(a, string)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(string, arrayNothing),
            map(a, string)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(nothing, arrayString),
            map(a, string)),
        assertInferActualCoreTypes(
            list(arrayA, arrayA),
            list(arrayString, arrayNothing),
            map(a, string)),
        assertInferActualCoreTypes(
            list(a, a),
            list(arrayString, nothing),
            map(a, arrayString)),

        // a <- Nothing, String; with conversions

        assertInferActualCoreTypes(
            list(a, a),
            list(nothing, personType),
            map(a, personType)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(personType, arrayNothing),
            map(a, personType)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(nothing, arrayPerson),
            map(a, personType)),
        assertInferActualCoreTypes(
            list(arrayA, arrayA),
            list(arrayPerson, arrayNothing),
            map(a, personType)),
        assertInferActualCoreTypes(
            list(a, a),
            list(arrayPerson, nothing),
            map(a, arrayPerson)),
        assertInferActualCoreTypes(
            list(arrayA, arrayA),
            list(arrayNothing, array2String),
            map(a, arrayString)),

        // a <- Nothing, a; with conversions

        assertInferActualCoreTypes(
            list(a, a),
            list(nothing, a),
            map(a, a)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(a, arrayNothing),
            map(a, a)),
        assertInferActualCoreTypes(
            list(a, arrayA),
            list(nothing, arrayA),
            map(a, a)),
        assertInferActualCoreTypes(
            list(arrayA, arrayA),
            list(arrayA, arrayNothing),
            map(a, a)),
        assertInferActualCoreTypes(
            list(a, a),
            list(arrayA, nothing),
            map(a, arrayA)),
        assertInferActualCoreTypes(
            list(arrayA, arrayA),
            list(arrayNothing, array2A),
            map(a, arrayA))));
  }

  private static Case assertInferActualCoreTypes(List<GenericType> types, List<Type> actualTypes,
      Map<GenericType, Type> expected) {
    String typesString = commaSeparatedList(types);
    String actualStrings = commaSeparatedList(actualTypes);
    return newCase(
        "types=(" + typesString + "), actual=(" + actualStrings + ") " + expected,
        () -> assertEquals(expected, inferActualCoreTypes(types, actualTypes)));
  }

  private static Case failsInferActualCoreTypes(List<GenericType> types, List<Type> actualTypes) {
    String typesString = commaSeparatedList(types);
    String actualStrings = commaSeparatedList(actualTypes);
    return newCase(
        "types=(" + typesString + "), actual=(" + actualStrings + ") fails with IAE",
        () -> {
          when(() -> inferActualCoreTypes(types, actualTypes));
          thenThrown(IllegalArgumentException.class);
        });
  }

  private static String commaSeparatedList(List<? extends Type> params) {
    return params
        .stream()
        .map(Type::name)
        .collect(joining(","));
  }

  public static Map<GenericType, Type> map(GenericType type, Type actual) {
    return ImmutableMap.of(type, actual);
  }
}
