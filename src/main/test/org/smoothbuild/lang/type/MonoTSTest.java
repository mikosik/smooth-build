package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.JoinTS.joinReduced;
import static org.smoothbuild.lang.type.MeetTS.meetReduced;
import static org.smoothbuild.lang.type.MergeTS.merge;
import static org.smoothbuild.lang.type.MergeTS.mergeReduced;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.Side.UPPER;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.A;
import static org.smoothbuild.testing.type.TestingTS.ANY;
import static org.smoothbuild.testing.type.TestingTS.B;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.C;
import static org.smoothbuild.testing.type.TestingTS.INFERABLE_BASE_TYPES;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.NOTHING;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.f;
import static org.smoothbuild.testing.type.TestingTS.join;
import static org.smoothbuild.testing.type.TestingTS.meet;
import static org.smoothbuild.testing.type.TestingTS.struct;
import static org.smoothbuild.testing.type.TestingTS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;

import com.google.common.testing.EqualsTester;

public class MonoTSTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(INFERABLE_BASE_TYPES)
        .hasSize(6);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(MonoTS type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(MonoTS type, String name) {
    assertThat(type.q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(MonoTS type, String name) {
    assertThat(type.toString())
        .isEqualTo(name);
  }

  public static List<Arguments> names() {
    return asList(
        arguments(ANY, "Any"),
        arguments(BLOB, "Blob"),
        arguments(BOOL, "Bool"),
        arguments(INT, "Int"),
        arguments(NOTHING, "Nothing"),
        arguments(STRING, "String"),
        arguments(struct("MyStruct", nlist()), "MyStruct"),
        arguments(A, "A"),

        arguments(join(BLOB, INT), "Blob ⊔ Int"),
        arguments(join(INT, BLOB), "Int ⊔ Blob"),
        arguments(meet(BLOB, INT), "Blob ⊓ Int"),
        arguments(meet(INT, BLOB), "Int ⊓ Blob"),

        arguments(a(ANY), "[Any]"),
        arguments(a(BLOB), "[Blob]"),
        arguments(a(BOOL), "[Bool]"),
        arguments(a(INT), "[Int]"),
        arguments(a(NOTHING), "[Nothing]"),
        arguments(a(STRING), "[String]"),
        arguments(a(struct("MyStruct", nlist())), "[MyStruct]"),
        arguments(a(A), "[A]"),

        arguments(a(a(A)), "[[A]]"),
        arguments(a(a(ANY)), "[[Any]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(INT)), "[[Int]]"),
        arguments(a(a(NOTHING)), "[[Nothing]]"),
        arguments(a(a(struct("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(a(a(STRING)), "[[String]]"),

        arguments(f(A, list(a(A))), "A([A])"),
        arguments(f(STRING, list(a(A))), "String([A])"),
        arguments(f(A, list(A)), "A(A)"),
        arguments(f(STRING, list()), "String()"),
        arguments(f(STRING, list(STRING)), "String(String)")
    );
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(MonoTS type, VarSetS expected) {
    assertThat(type.vars())
        .isEqualTo(expected);
  }

  public static List<Arguments> vars_test_data() {
    return List.of(
        arguments(ANY, varSetS()),
        arguments(BLOB, varSetS()),
        arguments(BOOL, varSetS()),
        arguments(INT, varSetS()),
        arguments(NOTHING, varSetS()),
        arguments(STRING, varSetS()),

        arguments(join(INT, BLOB), varSetS()),
        arguments(join(A, B), varSetS(A, B)),

        arguments(meet(INT, BLOB), varSetS()),
        arguments(meet(A, B), varSetS(A, B)),

        arguments(a(INT), varSetS()),
        arguments(a(A), varSetS(A)),

        arguments(f(BLOB, list(BOOL)), varSetS()),
        arguments(f(A, list(BOOL)), varSetS(A)),
        arguments(f(BLOB, list(A)), varSetS(A))
    );
  }

  @ParameterizedTest
  @MethodSource("map_vars")
  public void map_vars(MonoTS type, Function<VarS, VarS> varMapper, MonoTS expected) {
    assertThat(type.mapVars(varMapper))
        .isEqualTo(expected);
  }

  public static List<Arguments> map_vars() {
    Function<VarS, VarS> addPrefix = (VarS v) -> v.prefixed("prefix");
    return List.of(
        arguments(ANY, addPrefix, ANY),
        arguments(BLOB, addPrefix, BLOB),
        arguments(BOOL, addPrefix, BOOL),
        arguments(INT, addPrefix, INT),
        arguments(NOTHING, addPrefix, NOTHING),
        arguments(STRING, addPrefix, STRING),

        arguments(var("A"), addPrefix, var("prefix.A")),
        arguments(var("pre.A"), addPrefix, var("prefix.pre.A")),

        arguments(join(BLOB, INT), addPrefix, join(BLOB, INT)),
        arguments(join(var("A"), var("B")), addPrefix, join(var("prefix.A"), var("prefix.B"))),
        arguments(join(var("p.A"), var("p.B")), addPrefix, join(var("prefix.p.A"), var("prefix.p.B"))),

        arguments(meet(BLOB, INT), addPrefix, meet(BLOB, INT)),
        arguments(meet(var("A"), var("B")), addPrefix, meet(var("prefix.A"), var("prefix.B"))),
        arguments(meet(var("p.A"), var("p.B")), addPrefix, meet(var("prefix.p.A"), var("prefix.p.B"))),

        arguments(a(INT), addPrefix, a(INT)),
        arguments(a(var("A")), addPrefix, a(var("prefix.A"))),
        arguments(a(var("p.A")), addPrefix, a(var("prefix.p.A"))),

        arguments(f(BLOB, list(BOOL)), addPrefix, f(BLOB, list(BOOL))),
        arguments(f(var("A"), list(BOOL)), addPrefix, f(var("prefix.A"), list(BOOL))),
        arguments(f(BLOB, list(var("A"))), addPrefix, f(BLOB, list(var("prefix.A")))),
        arguments(f(var("p.A"), list(BOOL)), addPrefix, f(var("prefix.p.A"), list(BOOL))),
        arguments(f(BLOB, list(var("p.A"))), addPrefix, f(BLOB, list(var("prefix.p.A"))))
    );
  }

  @ParameterizedTest
  @MethodSource("includes")
  public void includes(MonoTS type, MonoTS included, boolean expected) {
    assertThat(type.includes(included))
        .isEqualTo(expected);
  }

  public static List<Arguments> includes() {
    return List.of(
        arguments(ANY, ANY, true),
        arguments(BLOB, BLOB, true),
        arguments(BOOL, BOOL, true),
        arguments(INT, INT, true),
        arguments(NOTHING, NOTHING, true),
        arguments(STRING, STRING, true),

        arguments(ANY, INT, false),
        arguments(BLOB, INT, false),
        arguments(BOOL, INT, false),
        arguments(INT, STRING, false),
        arguments(NOTHING, INT, false),
        arguments(STRING, INT, false),

        arguments(a(INT), INT, true),
        arguments(a(a(INT)), INT, true),
        arguments(a(INT), BLOB, false),
        arguments(a(a(INT)), BLOB, false),

        arguments(f(INT), INT, true),
        arguments(f(INT), BLOB, false),

        arguments(f(INT, BLOB), INT, true),
        arguments(f(INT, BLOB), BLOB, true),
        arguments(f(INT, BLOB), STRING, false),

        arguments(f(f(INT)), INT, true),
        arguments(f(INT, f(BLOB)), BLOB, true)
    );
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(MonoTS type) {
      ArrayTS array = a(type);
      assertThat(array.elem())
          .isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          arguments(ANY),
          arguments(BLOB),
          arguments(BOOL),
          arguments(f(STRING, list())),
          arguments(INT),
          arguments(NOTHING),
          arguments(STRING),
          arguments(struct("MyStruct", nlist())),
          arguments(A),

          arguments(a(ANY)),
          arguments(a(BLOB)),
          arguments(a(BOOL)),
          arguments(a(f(STRING, list()))),
          arguments(a(INT)),
          arguments(a(NOTHING)),
          arguments(a(STRING)),
          arguments(a(A))
      );
    }
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("func_result_cases")
    public void func_result(MonoFuncTS type, MonoTS expected) {
      assertThat(type.res())
          .isEqualTo(expected);
    }

    public static List<Arguments> func_result_cases() {
      return asList(
          arguments(f(INT, list()), INT),
          arguments(f(BLOB, list(BOOL)), BLOB),
          arguments(f(BLOB, list(BOOL, INT)), BLOB)
      );
    }

    @ParameterizedTest
    @MethodSource("func_params_cases")
    public void func_params(MonoFuncTS type, Object expected) {
      assertThat(type.params())
          .isEqualTo(expected);
    }

    public static List<Arguments> func_params_cases() {
      return asList(
          arguments(f(INT, list()), list()),
          arguments(f(BLOB, list(BOOL)), list(BOOL)),
          arguments(f(BLOB, list(BOOL, INT)), list(BOOL, INT))
      );
    }
  }

  @Nested
  class _join {
    @Test
    public void join_method_forbids_empty_set() {
      assertCall(() -> JoinTS.join(set()))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void joinReduced_method_forbids_empty_set() {
      assertCall(() -> joinReduced(set()))
          .throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("join_without_reduce")
    public void join_without_reduce(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(join(set(a, b)))
          .isEqualTo(expected);
      assertThat(join(set(b, a)))
          .isEqualTo(expected);
    }

    public static List<Arguments> join_without_reduce() {
      return List.of(
          arguments(NOTHING, ANY, join(NOTHING, ANY)),
          arguments(NOTHING, STRING, join(NOTHING, STRING)),
          arguments(NOTHING, a(STRING), join(NOTHING, a(STRING))),
          arguments(NOTHING, join(STRING, BOOL), join(NOTHING, join(STRING, BOOL))),
          arguments(NOTHING, NOTHING, join(NOTHING)),

          arguments(STRING, ANY, join(STRING, ANY)),
          arguments(STRING, a(STRING), join(STRING, a(STRING))),
          arguments(STRING, BOOL, join(STRING, BOOL)),
          arguments(STRING, STRING, join(STRING)),

          arguments(a(STRING), ANY, join(a(STRING), ANY)),
          arguments(a(STRING), a(STRING), join(a(STRING))),

          arguments(join(STRING, BOOL), ANY, join(join(STRING, BOOL), ANY)),
          arguments(join(STRING, BOOL), BOOL, join(join(STRING, BOOL), BOOL)),
          arguments(join(STRING, BOOL), STRING, join(join(STRING, BOOL), STRING)),

          arguments(ANY, ANY, join(ANY))
      );
    }

    @ParameterizedTest
    @MethodSource("join_reduced")
    public void join_reduced(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(joinReduced(set(a, b)))
          .isEqualTo(expected);
      assertThat(joinReduced(set(b, a)))
          .isEqualTo(expected);
    }

    public static List<Arguments> join_reduced() {
      return List.of(
          arguments(NOTHING, ANY, ANY),
          arguments(NOTHING, STRING, STRING),
          arguments(NOTHING, a(STRING), a(STRING)),
          arguments(NOTHING, join(STRING, BOOL), join(STRING, BOOL)),
          arguments(NOTHING, NOTHING, NOTHING),

          arguments(STRING, ANY, ANY),
          arguments(STRING, a(STRING), join(STRING, a(STRING))),
          arguments(STRING, BOOL, join(STRING, BOOL)),
          arguments(STRING, STRING, STRING),

          arguments(a(STRING), ANY, ANY),
          arguments(a(STRING), a(STRING), a(STRING)),

          arguments(join(STRING, BOOL), ANY, ANY),
          arguments(join(STRING, BOOL), BOOL, join(STRING, BOOL)),
          arguments(join(STRING, BOOL), STRING, join(STRING, BOOL)),

          arguments(ANY, ANY, ANY)
      );
    }
  }

  @Nested
  class _meet {
    @Test
    public void meet_method_forbids_empty_set() {
      assertCall(() -> MeetTS.meet(set()))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void meetReduced_method_forbids_empty_set() {
      assertCall(() -> MeetTS.meet(set()))
          .throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("meet_without_reduce")
    public void meet_without_reduce(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(meet(set(a, b)))
          .isEqualTo(expected);
      assertThat(meet(set(b, a)))
          .isEqualTo(expected);
    }

    public static List<Arguments> meet_without_reduce() {
      return List.of(
          arguments(NOTHING, ANY, meet(NOTHING, ANY)),
          arguments(NOTHING, STRING, meet(NOTHING, STRING)),
          arguments(NOTHING, a(STRING), meet(NOTHING, a(STRING))),
          arguments(NOTHING, meet(STRING, BOOL), meet(NOTHING, meet(STRING, BOOL))),
          arguments(NOTHING, NOTHING, meet(NOTHING)),

          arguments(STRING, ANY, meet(STRING, ANY)),
          arguments(STRING, a(STRING), meet(STRING, a(STRING))),
          arguments(STRING, BOOL, meet(STRING, BOOL)),
          arguments(STRING, STRING, meet(STRING)),

          arguments(a(STRING), ANY, meet(a(STRING), ANY)),
          arguments(a(STRING), a(STRING), meet(a(STRING))),

          arguments(meet(STRING, BOOL), ANY, meet(meet(STRING, BOOL), ANY)),
          arguments(meet(STRING, BOOL), BOOL, meet(meet(STRING, BOOL), BOOL)),
          arguments(meet(STRING, BOOL), STRING, meet(meet(STRING, BOOL), STRING)),

          arguments(ANY, ANY, meet(ANY))
      );
    }
    @ParameterizedTest
    @MethodSource("meet_reduced")
    public void meet_reduced(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(meetReduced(set(a, b)))
          .isEqualTo(expected);
      assertThat(meetReduced(set(b, a)))
          .isEqualTo(expected);
    }

    public static List<Arguments> meet_reduced() {
      return List.of(
          arguments(NOTHING, ANY, NOTHING),
          arguments(NOTHING, STRING, NOTHING),
          arguments(NOTHING, a(STRING), NOTHING),
          arguments(NOTHING, meet(STRING, BOOL), NOTHING),
          arguments(NOTHING, NOTHING, NOTHING),

          arguments(STRING, ANY, STRING),
          arguments(STRING, a(STRING), meet(STRING, a(STRING))),
          arguments(STRING, BOOL, meet(STRING, BOOL)),
          arguments(STRING, STRING, STRING),

          arguments(a(STRING), ANY, a(STRING)),
          arguments(a(STRING), a(STRING), a(STRING)),

          arguments(meet(STRING, BOOL), ANY, meet(STRING, BOOL)),
          arguments(meet(STRING, BOOL), BOOL, meet(STRING, BOOL)),
          arguments(meet(STRING, BOOL), STRING, meet(STRING, BOOL)),

          arguments(ANY, ANY, ANY)
      );
    }
  }

  @Nested
  class _merge {
    @ParameterizedTest
    @MethodSource("join_upper_without_reduce")
    public void merge_upper_without_reduce(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(merge(set(a, b), UPPER))
          .isEqualTo(expected);
      assertThat(merge(set(b, a), UPPER))
          .isEqualTo(expected);
    }

    public static List<Arguments> join_upper_without_reduce() {
      return _join.join_without_reduce();
    }

    @ParameterizedTest
    @MethodSource("join_lower_without_reduce")
    public void merge_lower_without_reduce(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(merge(set(a, b), LOWER))
          .isEqualTo(expected);
      assertThat(merge(set(b, a), LOWER))
          .isEqualTo(expected);
    }

    public static List<Arguments> join_lower_without_reduce() {
      return _meet.meet_without_reduce();
    }

    @ParameterizedTest
    @MethodSource("mergeReduced_upper")
    public void mergeReduced_upper(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(mergeReduced(set(a, b), UPPER))
          .isEqualTo(expected);
      assertThat(mergeReduced(set(b, a), UPPER))
          .isEqualTo(expected);
    }

    public static List<Arguments> mergeReduced_upper() {
      return _join.join_reduced();
    }

    @ParameterizedTest
    @MethodSource("mergeReduced_lower")
    public void mergeReduced_lower(MonoTS a, MonoTS b, MonoTS expected) {
      assertThat(mergeReduced(set(a, b), LOWER))
          .isEqualTo(expected);
      assertThat(mergeReduced(set(b, a), LOWER))
          .isEqualTo(expected);
    }

    public static List<Arguments> mergeReduced_lower() {
      return _meet.meet_reduced();
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      struct("MyStruct", nlist());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      struct("MyStruct", nlist(itemSigS(NOTHING, "fieldName")));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      struct("MyStruct", nlist(itemSigS(a(NOTHING), "fieldName")));
    }

    @Test
    public void struct_name() {
      var struct = struct("MyStruct", nlist());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> struct(name, nlist()))
          .throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(StructTS struct, NList<ItemSigS> expected) {
      assertThat(struct.fields())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_fields_cases() {
      return asList(
          arguments(struct("Person", nlist()), nlist()),
          arguments(struct("Person", nlist(itemSigS(STRING, "field"))),
              nlist(itemSigS(STRING, "field"))),
          arguments(struct("Person",
              nlist(itemSigS(STRING, "field"), itemSigS(INT, "field2"))),
              nlist(itemSigS(STRING, "field"), itemSigS(INT, "field2")))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<MonoTS> types = asList(
        ANY,
        BLOB,
        BOOL,
        INT,
        NOTHING,
        STRING,
        struct("MyStruct", nlist()),
        struct("MyStruct", nlist(itemSigS(INT, "field"))),
        A,
        B,
        C,

        f(BLOB, list()),
        f(STRING, list()),
        f(BLOB, list(STRING)),
        f(BLOB, list(BLOB))
    );

    for (MonoTS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.addEqualityGroup(join(BLOB, INT), join(INT, BLOB));
    equalsTester.addEqualityGroup(join(BLOB, STRING), join(STRING, BLOB));
    equalsTester.addEqualityGroup(meet(BLOB, INT), meet(INT, BLOB));
    equalsTester.addEqualityGroup(meet(BLOB, STRING), meet(STRING, BLOB));

    equalsTester.testEquals();
  }

  @Nested
  class _vars {
    @Test
    public void prefixed() {
      var var = var("A");
      assertThat(var.prefixed("abc"))
          .isEqualTo(var("abc.A"));
    }

    @Test
    public void prefixed_fails_when_prefix_contains_dot() {
      assertCall(() -> var("A").prefixed("abc."))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void unprefixed() {
      assertThat(var("pref.A").unprefixed())
          .isEqualTo(var("A"));
    }

    @Test
    public void remove_var_prefixes_fails_when_var_has_no_prefix() {
      var var = var("A");
      assertCall(() -> var.unprefixed())
          .throwsException(IllegalStateException.class);
    }

    @Test
    public void has_prefix_returns_false_for_not_prefixed_var() {
      assertThat(var("A").hasPrefix())
          .isFalse();
    }

    @Test
    public void has_prefix_returns_true_for_prefixed_var() {
      assertThat(var("pref.A").hasPrefix())
          .isTrue();
    }
  }
}
