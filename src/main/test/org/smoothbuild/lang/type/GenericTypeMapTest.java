package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
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
import static org.smoothbuild.lang.type.TestingTypes.blob;
import static org.smoothbuild.lang.type.TestingTypes.nothing;
import static org.smoothbuild.lang.type.TestingTypes.person;
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
public class GenericTypeMapTest {
  @Quackery
  public static Suite infer_actual_core_types() {
    return suite("infer_from").addAll(asList(
        // concrete types
        assertInferFrom(
            list(string),
            list(string),
            blob, blob),

        // a <- string
        assertInferFrom(
            list(a),
            list(string),
            a, string),
        assertInferFrom(
            list(a),
            list(arrayString),
            a, arrayString),
        assertInferFrom(
            list(a),
            list(array2String),
            a, array2String),

        failsInferFrom(
            list(arrayA),
            list(string)),
        assertInferFrom(
            list(arrayA),
            list(arrayString),
            a, string),
        assertInferFrom(
            list(arrayA),
            list(array2String),
            a, arrayString),

        failsInferFrom(
            list(array2A),
            list(string)),
        failsInferFrom(
            list(array2A),
            list(arrayString)),
        assertInferFrom(
            list(array2A),
            list(array2String),
            a, string),

        // a <- struct (Person)
        assertInferFrom(
            list(a),
            list(person),
            a, person),
        assertInferFrom(
            list(a),
            list(arrayPerson),
            a, arrayPerson),
        assertInferFrom(
            list(a),
            list(array2Person),
            a, array2Person),

        failsInferFrom(
            list(arrayA),
            list(person)),
        assertInferFrom(
            list(arrayA),
            list(arrayPerson),
            a, person),
        assertInferFrom(
            list(arrayA),
            list(array2Person),
            a, arrayPerson),

        failsInferFrom(
            list(array2A),
            list(person)),
        failsInferFrom(
            list(array2A),
            list(arrayPerson)),
        assertInferFrom(
            list(array2A),
            list(array2Person),
            a, person),

        // a <- Nothing

        assertInferFrom(
            list(a),
            list(nothing),
            a, nothing),
        assertInferFrom(
            list(a),
            list(arrayNothing),
            a, arrayNothing),
        assertInferFrom(
            list(a),
            list(array2Nothing),
            a, array2Nothing),

        assertInferFrom(
            list(arrayA),
            list(nothing),
            a, nothing),
        assertInferFrom(
            list(arrayA),
            list(arrayNothing),
            a, nothing),
        assertInferFrom(
            list(arrayA),
            list(array2Nothing),
            a, arrayNothing),

        assertInferFrom(
            list(array2A),
            list(nothing),
            a, nothing),
        assertInferFrom(
            list(array2A),
            list(arrayNothing),
            a, nothing),
        assertInferFrom(
            list(array2A),
            list(array2Nothing),
            a, nothing),

        // a <- b

        assertInferFrom(
            list(a),
            list(b),
            a, b),
        assertInferFrom(
            list(a),
            list(arrayB),
            a, arrayB),
        assertInferFrom(
            list(a),
            list(array2B),
            a, array2B),

        failsInferFrom(
            list(arrayA),
            list(b)),
        assertInferFrom(
            list(arrayA),
            list(arrayB),
            a, b),
        assertInferFrom(
            list(arrayA),
            list(array2B),
            a, arrayB),

        failsInferFrom(
            list(array2A),
            list(b)),
        failsInferFrom(
            list(array2A),
            list(arrayB)),
        assertInferFrom(
            list(array2A),
            list(array2B),
            a, b),

        // a <- String, struct (Person); with conversions

        assertInferFrom(
            list(a, a),
            list(person, string),
            a, string),
        assertInferFrom(
            list(a, arrayA),
            list(person, arrayString),
            a, string),
        assertInferFrom(
            list(a, arrayA),
            list(string, arrayPerson),
            a, string),

        // a <- Nothing, String; with conversions

        assertInferFrom(
            list(a, a),
            list(nothing, string),
            a, string),
        assertInferFrom(
            list(a, arrayA),
            list(string, arrayNothing),
            a, string),
        assertInferFrom(
            list(a, arrayA),
            list(nothing, arrayString),
            a, string),
        assertInferFrom(
            list(arrayA, arrayA),
            list(arrayString, arrayNothing),
            a, string),
        assertInferFrom(
            list(a, a),
            list(arrayString, nothing),
            a, arrayString),

        // a <- Nothing, String; with conversions

        assertInferFrom(
            list(a, a),
            list(nothing, person),
            a, person),
        assertInferFrom(
            list(a, arrayA),
            list(person, arrayNothing),
            a, person),
        assertInferFrom(
            list(a, arrayA),
            list(nothing, arrayPerson),
            a, person),
        assertInferFrom(
            list(arrayA, arrayA),
            list(arrayPerson, arrayNothing),
            a, person),
        assertInferFrom(
            list(a, a),
            list(arrayPerson, nothing),
            a, arrayPerson),
        assertInferFrom(
            list(arrayA, arrayA),
            list(arrayNothing, array2String),
            a, arrayString),

        // a <- Nothing, a; with conversions

        assertInferFrom(
            list(a, a),
            list(nothing, a),
            a, a),
        assertInferFrom(
            list(a, arrayA),
            list(a, arrayNothing),
            a, a),
        assertInferFrom(
            list(a, arrayA),
            list(nothing, arrayA),
            a, a),
        assertInferFrom(
            list(arrayA, arrayA),
            list(arrayA, arrayNothing),
            a, a),
        assertInferFrom(
            list(a, a),
            list(arrayA, nothing),
            a, arrayA),
        assertInferFrom(
            list(arrayA, arrayA),
            list(arrayNothing, array2A),
            a, arrayA)));
  }

  private static Case assertInferFrom(List<Type> types, List<Type> actualTypes,
      Type type, Type expected) {
    String typesString = commaSeparatedList(types);
    String actualStrings = commaSeparatedList(actualTypes);
    return newCase(
        "types=(" + typesString + "), actual=(" + actualStrings + ") " + expected,
        () -> assertEquals(expected, GenericTypeMap.inferFrom(types, actualTypes).applyTo(type)));
  }

  private static Case failsInferFrom(List<Type> types, List<Type> actualTypes) {
    String typesString = commaSeparatedList(types);
    String actualStrings = commaSeparatedList(actualTypes);
    return newCase(
        "types=(" + typesString + "), actual=(" + actualStrings + ") fails with IAE",
        () -> {
          when(() -> GenericTypeMap.inferFrom(types, actualTypes));
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
