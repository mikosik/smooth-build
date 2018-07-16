package org.smoothbuild.lang.type;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.type.TestingTypes.a;
import static org.smoothbuild.lang.type.TestingTypes.array;
import static org.smoothbuild.lang.type.TestingTypes.array2;
import static org.smoothbuild.lang.type.TestingTypes.array3;
import static org.smoothbuild.lang.type.TestingTypes.array4;
import static org.smoothbuild.lang.type.TestingTypes.b;
import static org.smoothbuild.lang.type.TestingTypes.blob;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.lang.type.TypeUtils.actualCoreType;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TypeUtilsTest {

  @Quackery
  public static Suite actual_core_type() throws Exception {
    return suite("actualCoreType").addAll(asList(

        // generic 0

        assertActualCoreType(a, string, string),
        assertActualCoreType(a, blob, blob),
        assertActualCoreType(a, a, a),
        assertActualCoreType(a, b, b),

        assertActualCoreType(a, array(string), array(string)),
        assertActualCoreType(a, array(blob), array(blob)),
        assertActualCoreType(a, array(a), array(a)),
        assertActualCoreType(a, array(b), array(b)),

        assertActualCoreType(a, array2(string), array2(string)),
        assertActualCoreType(a, array2(blob), array2(blob)),
        assertActualCoreType(a, array2(a), array2(a)),
        assertActualCoreType(a, array2(b), array2(b)),

        // string 0

        assertActualCoreTypeFails(string, string),
        assertActualCoreTypeFails(string, a),
        assertActualCoreTypeFails(string, blob),

        assertActualCoreTypeFails(string, array(string)),
        assertActualCoreTypeFails(string, array(a)),
        assertActualCoreTypeFails(string, array(blob)),

        assertActualCoreTypeFails(string, array2(string)),
        assertActualCoreTypeFails(string, array2(a)),
        assertActualCoreTypeFails(string, array2(blob)),

        // generic 1

        assertActualCoreTypeFails(array(a), string),
        assertActualCoreTypeFails(array(a), blob),
        assertActualCoreTypeFails(array(a), a),
        assertActualCoreTypeFails(array(a), b),

        assertActualCoreType(array(a), array(string), string),
        assertActualCoreType(array(a), array(blob), blob),
        assertActualCoreType(array(a), array(a), a),
        assertActualCoreType(array(a), array(b), b),

        assertActualCoreType(array(a), array2(string), array(string)),
        assertActualCoreType(array(a), array2(blob), array(blob)),
        assertActualCoreType(array(a), array2(a), array(a)),
        assertActualCoreType(array(a), array2(b), array(b)),

        assertActualCoreType(array(a), array3(string), array2(string)),
        assertActualCoreType(array(a), array3(blob), array2(blob)),
        assertActualCoreType(array(a), array3(a), array2(a)),
        assertActualCoreType(array(a), array3(b), array2(b)),

        // string 1

        assertActualCoreTypeFails(array(string), string),
        assertActualCoreTypeFails(array(string), blob),
        assertActualCoreTypeFails(array(string), a),
        assertActualCoreTypeFails(array(string), b),

        assertActualCoreTypeFails(array(string), array(string)),
        assertActualCoreTypeFails(array(string), array(blob)),
        assertActualCoreTypeFails(array(string), array(a)),
        assertActualCoreTypeFails(array(string), array(b)),

        assertActualCoreTypeFails(array(string), array2(string)),
        assertActualCoreTypeFails(array(string), array2(blob)),
        assertActualCoreTypeFails(array(string), array2(a)),
        assertActualCoreTypeFails(array(string), array2(b)),

        assertActualCoreTypeFails(array(string), array3(string)),
        assertActualCoreTypeFails(array(string), array3(blob)),
        assertActualCoreTypeFails(array(string), array3(a)),
        assertActualCoreTypeFails(array(string), array3(b)),

        // generic 2

        assertActualCoreTypeFails(array2(a), string),
        assertActualCoreTypeFails(array2(a), blob),
        assertActualCoreTypeFails(array2(a), a),
        assertActualCoreTypeFails(array2(a), b),

        assertActualCoreTypeFails(array2(a), array(string)),
        assertActualCoreTypeFails(array2(a), array(blob)),
        assertActualCoreTypeFails(array2(a), array(a)),
        assertActualCoreTypeFails(array2(a), array(b)),

        assertActualCoreType(array2(a), array2(string), string),
        assertActualCoreType(array2(a), array2(blob), blob),
        assertActualCoreType(array2(a), array2(a), a),
        assertActualCoreType(array2(a), array2(b), b),

        assertActualCoreType(array2(a), array3(string), array(string)),
        assertActualCoreType(array2(a), array3(blob), array(blob)),
        assertActualCoreType(array2(a), array3(a), array(a)),
        assertActualCoreType(array2(a), array3(b), array(b)),

        assertActualCoreType(array2(a), array4(string), array2(string)),
        assertActualCoreType(array2(a), array4(blob), array2(blob)),
        assertActualCoreType(array2(a), array4(a), array2(a)),
        assertActualCoreType(array2(a), array4(b), array2(b)),

        // string 2

        assertActualCoreTypeFails(array2(string), string),
        assertActualCoreTypeFails(array2(string), blob),
        assertActualCoreTypeFails(array2(string), a),
        assertActualCoreTypeFails(array2(string), b),

        assertActualCoreTypeFails(array2(string), array(string)),
        assertActualCoreTypeFails(array2(string), array(blob)),
        assertActualCoreTypeFails(array2(string), array(a)),
        assertActualCoreTypeFails(array2(string), array(b)),

        assertActualCoreTypeFails(array2(string), array2(string)),
        assertActualCoreTypeFails(array2(string), array2(blob)),
        assertActualCoreTypeFails(array2(string), array2(a)),
        assertActualCoreTypeFails(array2(string), array2(b)),

        assertActualCoreTypeFails(array2(string), array3(string)),
        assertActualCoreTypeFails(array2(string), array3(blob)),
        assertActualCoreTypeFails(array2(string), array3(a)),
        assertActualCoreTypeFails(array2(string), array3(b)),

        assertActualCoreTypeFails(array2(string), array4(string)),
        assertActualCoreTypeFails(array2(string), array4(blob)),
        assertActualCoreTypeFails(array2(string), array4(a)),
        assertActualCoreTypeFails(array2(string), array4(b))));
  }

  private static Case assertActualCoreType(Type generic, Type actual, Type expected) {
    String name =
        "actualCoreType(" + generic.name() + "," + actual.name() + ") == " + expected.name();
    return newCase(name, () -> assertEquals(expected, actualCoreType(generic, actual)));
  }

  private static Case assertActualCoreTypeFails(Type generic, Type actual) {
    String name = "actualCoreType(" + generic.name() + "," + actual.name()
        + ") throws IllegalArgumentException";
    return newCase(name, () -> {
      try {
        actualCoreType(generic, actual);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // expected
      }
    });
  }
}
