package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.type.TestingObjTypes.ANY;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_ANY;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_BLOB;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_BOOL;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_INT;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_LAMBDA;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_STR;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY2_VARIABLE;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_ANY;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_BLOB;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_BOOL;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_INT;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_LAMBDA;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_NOTHING;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_STR;
import static org.smoothbuild.db.object.type.TestingObjTypes.ARRAY_VARIABLE;
import static org.smoothbuild.db.object.type.TestingObjTypes.BLOB;
import static org.smoothbuild.db.object.type.TestingObjTypes.BOOL;
import static org.smoothbuild.db.object.type.TestingObjTypes.CALL;
import static org.smoothbuild.db.object.type.TestingObjTypes.CONST;
import static org.smoothbuild.db.object.type.TestingObjTypes.CONSTRUCT;
import static org.smoothbuild.db.object.type.TestingObjTypes.INT;
import static org.smoothbuild.db.object.type.TestingObjTypes.LAMBDA;
import static org.smoothbuild.db.object.type.TestingObjTypes.NOTHING;
import static org.smoothbuild.db.object.type.TestingObjTypes.OBJECT_TYPE_DB;
import static org.smoothbuild.db.object.type.TestingObjTypes.ORDER;
import static org.smoothbuild.db.object.type.TestingObjTypes.PERSON;
import static org.smoothbuild.db.object.type.TestingObjTypes.PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingObjTypes.REF;
import static org.smoothbuild.db.object.type.TestingObjTypes.SELECT;
import static org.smoothbuild.db.object.type.TestingObjTypes.STR;
import static org.smoothbuild.db.object.type.TestingObjTypes.VARIABLE;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.ConstructOType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

/**
 * Most types are tested in TypeTest. Here we test only types which are not Types from
 * TypeFactory perspective.
 */
public class TypeOTest {
  @ParameterizedTest
  @MethodSource("names")
  public void name(TypeO type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(TypeO type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(TypeO type, String name) {
    assertThat(type.toString())
        .isEqualTo("Type(`" + name + "`)");
  }

  public static Stream<Arguments> names() {
    TestingContext tc = new TestingContext();
    return Stream.of(
        arguments(PERSON_TUPLE, "{String,String}"),
        arguments(tc.callOT(tc.intOT()), "CALL:Int"),
        arguments(tc.constOT(tc.intOT()), "CONST:Int"),
        arguments(tc.nativeMethodOT(), "NATIVE_METHOD"),
        arguments(tc.orderOT(tc.stringOT()), "ORDER:[String]"),
        arguments(tc.constructOT(list(tc.stringOT(), tc.intOT())), "CONSTRUCT:{String,Int}"),
        arguments(tc.selectOT(tc.intOT()), "SELECT:Int"),
        arguments(tc.refOT(tc.intOT()), "REF:Int"),

        arguments(ARRAY_PERSON_TUPLE, "[{String,String}]"),
        arguments(ARRAY2_PERSON_TUPLE, "[[{String,String}]]")
    );
  }

  @ParameterizedTest
  @MethodSource("jType_test_data")
  public void jType(TypeO type, Class<?> expected) {
    assertThat(type.jType())
        .isEqualTo(expected);
  }

  public static List<Arguments> jType_test_data() {
    return list(
        arguments(ANY, null),
        arguments(BLOB, Blob.class),
        arguments(BOOL, Bool.class),
        arguments(LAMBDA, Lambda.class),
        arguments(INT, Int.class),
        arguments(NOTHING, null),
        arguments(PERSON, Struc_.class),
        arguments(STR, Str.class),
        arguments(VARIABLE, null),

        arguments(ARRAY_ANY, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_LAMBDA, Array.class),
        arguments(ARRAY_INT, Array.class),
        arguments(ARRAY_NOTHING, Array.class),
        arguments(ARRAY_PERSON_TUPLE, Array.class),
        arguments(ARRAY_STR, Array.class),
        arguments(ARRAY_VARIABLE, Array.class),

        arguments(CALL, Call.class),
        arguments(CONST, Const.class),
        arguments(ORDER, Order.class),
        arguments(CONSTRUCT, Construct.class),
        arguments(SELECT, Select.class),
        arguments(REF, Ref.class)
    );
  }

  @ParameterizedTest
  @MethodSource("array_element_cases")
  public void array_element(ArrayOType type, TypeO expected) {
    assertThat(type.element())
        .isEqualTo(expected);
  }

  public static List<Arguments> array_element_cases() {
    return list(
        arguments(ARRAY_PERSON_TUPLE, PERSON_TUPLE),
        arguments(ARRAY2_PERSON_TUPLE, ARRAY_PERSON_TUPLE));
  }

  @ParameterizedTest
  @MethodSource("tuple_items_cases")
  public void tuple_item(TupleOType type, List<TypeO> expected) {
    assertThat(type.items())
        .isEqualTo(expected);
  }

  public static List<Arguments> tuple_items_cases() {
    return list(
        arguments(tupleType(), list()),
        arguments(tupleType(STR), list(STR)),
        arguments(tupleType(STR, INT), list(STR, INT)),
        arguments(tupleType(STR, INT, BLOB), list(STR, INT, BLOB))
    );
  }

  @Nested
  class _evaluation_type {
    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeV type) {
      assertThat(OBJECT_TYPE_DB.order(type).evaluationType())
          .isEqualTo(OBJECT_TYPE_DB.array(type));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeV type) {
      assertThat(OBJECT_TYPE_DB.call(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void const_(TypeV type) {
      assertThat(OBJECT_TYPE_DB.const_(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("construct_cases")
    public void construct(ConstructOType type, TupleOType expected) {
      assertThat(type.evaluationType())
          .isEqualTo(expected);
    }

    public static List<Arguments> construct_cases() {
      return list(
          arguments(constructType(), tupleType()),
          arguments(constructType(STR), tupleType(STR)),
          arguments(constructType(STR, INT), tupleType(STR, INT)),
          arguments(constructType(STR, INT, BLOB), tupleType(STR, INT, BLOB))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeV type) {
      assertThat(OBJECT_TYPE_DB.ref(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeV type) {
      assertThat(OBJECT_TYPE_DB.select(type).evaluationType())
          .isEqualTo(type);
    }

    public static ImmutableList<TypeO> types() {
      return TestingObjTypes.VAL_TYPES_TO_TEST;
    }
  }

  private static TupleOType tupleType(TypeV... items) {
    return tupleType(list(items));
  }

  private static TupleOType tupleType(ImmutableList<TypeV> items) {
    return OBJECT_TYPE_DB.tuple(items);
  }

  private static ConstructOType constructType(TypeV... items) {
    return OBJECT_TYPE_DB.construct(tupleType(list(items)));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(ANY, ANY);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(LAMBDA, LAMBDA);
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(STR, STR);
    tester.addEqualityGroup(PERSON_TUPLE, PERSON_TUPLE);
    tester.addEqualityGroup(VARIABLE, VARIABLE);

    tester.addEqualityGroup(ARRAY_ANY, ARRAY_ANY);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_LAMBDA, ARRAY_LAMBDA);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_NOTHING, ARRAY_NOTHING);
    tester.addEqualityGroup(ARRAY_STR, ARRAY_STR);
    tester.addEqualityGroup(ARRAY_PERSON_TUPLE, ARRAY_PERSON_TUPLE);
    tester.addEqualityGroup(ARRAY_VARIABLE, ARRAY_VARIABLE);

    tester.addEqualityGroup(ARRAY2_VARIABLE, ARRAY2_VARIABLE);
    tester.addEqualityGroup(ARRAY2_ANY, ARRAY2_ANY);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_LAMBDA, ARRAY2_LAMBDA);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_NOTHING, ARRAY2_NOTHING);
    tester.addEqualityGroup(ARRAY2_STR, ARRAY2_STR);
    tester.addEqualityGroup(ARRAY2_PERSON_TUPLE, ARRAY2_PERSON_TUPLE);

    tester.addEqualityGroup(CALL, CALL);
    tester.addEqualityGroup(CONST, CONST);
    tester.addEqualityGroup(ORDER, ORDER);
    tester.addEqualityGroup(CONSTRUCT, CONSTRUCT);
    tester.addEqualityGroup(SELECT, SELECT);
    tester.addEqualityGroup(REF, REF);

    tester.testEquals();
  }
}
