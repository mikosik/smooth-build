package org.smoothbuild.compile.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.A;
import static org.smoothbuild.testing.type.TestingTS.B;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.C;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.struct;
import static org.smoothbuild.testing.type.TestingTS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.testing.type.TestingTS;
import org.smoothbuild.util.collect.NList;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;

public class TypeSTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(TypeFS.baseTs())
        .hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(TypeS type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(TypeS type, String name) {
    assertThat(type.q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(TypeS type, String name) {
    assertThat(type.toString())
        .isEqualTo(name);
  }

  public static List<Arguments> names() {
    return asList(
        arguments(BLOB, "Blob"),
        arguments(BOOL, "Bool"),
        arguments(INT, "Int"),
        arguments(STRING, "String"),
        arguments(struct("MyStruct", nlist()), "MyStruct"),
        arguments(A, "A"),

        arguments(a(BLOB), "[Blob]"),
        arguments(a(BOOL), "[Bool]"),
        arguments(a(INT), "[Int]"),
        arguments(a(STRING), "[String]"),
        arguments(a(struct("MyStruct", nlist())), "[MyStruct]"),
        arguments(a(A), "[A]"),

        arguments(a(a(A)), "[[A]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(INT)), "[[Int]]"),
        arguments(a(a(struct("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(a(a(STRING)), "[[String]]"),

        arguments(TestingTS.f(A, list(a(A))), "A([A])"),
        arguments(TestingTS.f(STRING, list(a(A))), "String([A])"),
        arguments(TestingTS.f(A, list(A)), "A(A)"),
        arguments(TestingTS.f(STRING, list()), "String()"),
        arguments(TestingTS.f(STRING, list(STRING)), "String(String)")
    );
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(TypeS type, VarSetS expected) {
    assertThat(type.vars())
        .isEqualTo(expected);
  }

  public static List<Arguments> vars_test_data() {
    return List.of(
        arguments(BLOB, VarSetS.varSetS()),
        arguments(BOOL, VarSetS.varSetS()),
        arguments(INT, VarSetS.varSetS()),
        arguments(STRING, VarSetS.varSetS()),

        arguments(a(INT), VarSetS.varSetS()),
        arguments(a(A), VarSetS.varSetS(A)),

        arguments(TestingTS.f(BLOB, list(BOOL)), VarSetS.varSetS()),
        arguments(TestingTS.f(A, list(BOOL)), VarSetS.varSetS(A)),
        arguments(TestingTS.f(BLOB, list(A)), VarSetS.varSetS(A))
    );
  }

  @ParameterizedTest
  @MethodSource("map_vars")
  public void map_vars(TypeS type, Function<VarS, TypeS> varMapper, TypeS expected) {
    assertThat(type.mapVars(varMapper))
        .isEqualTo(expected);
  }

  public static List<Arguments> map_vars() {
    Function<VarS, VarS> addPrefix = (VarS v) -> v.prefixed("prefix");
    return List.of(
        arguments(BLOB, addPrefix, BLOB),
        arguments(BOOL, addPrefix, BOOL),
        arguments(INT, addPrefix, INT),
        arguments(STRING, addPrefix, STRING),

        arguments(var("A"), addPrefix, var("prefix.A")),
        arguments(var("pre.A"), addPrefix, var("prefix.pre.A")),

        arguments(a(INT), addPrefix, a(INT)),
        arguments(a(var("A")), addPrefix, a(var("prefix.A"))),
        arguments(a(var("p.A")), addPrefix, a(var("prefix.p.A"))),

        arguments(TestingTS.f(BLOB, list(BOOL)), addPrefix, TestingTS.f(BLOB, list(BOOL))),
        arguments(TestingTS.f(var("A"), list(BOOL)), addPrefix, TestingTS.f(var("prefix.A"), list(BOOL))),
        arguments(TestingTS.f(BLOB, list(var("A"))), addPrefix, TestingTS.f(BLOB, list(var("prefix.A")))),
        arguments(
            TestingTS.f(var("p.A"), list(BOOL)), addPrefix, TestingTS.f(var("prefix.p.A"), list(BOOL))),
        arguments(TestingTS.f(BLOB, list(var("p.A"))), addPrefix, TestingTS.f(BLOB, list(var("prefix.p.A"))))
    );
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(TypeS type) {
      ArrayTS array = a(type);
      assertThat(array.elem())
          .isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          Arguments.arguments(BLOB),
          Arguments.arguments(BOOL),
          arguments(TestingTS.f(STRING, list())),
          Arguments.arguments(INT),
          Arguments.arguments(STRING),
          Arguments.arguments(struct("MyStruct", nlist())),
          Arguments.arguments(A),

          Arguments.arguments(a(BLOB)),
          Arguments.arguments(a(BOOL)),
          Arguments.arguments(a(TestingTS.f(STRING, list()))),
          Arguments.arguments(a(INT)),
          Arguments.arguments(a(STRING)),
          Arguments.arguments(a(A))
      );
    }
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("func_result_cases")
    public void func_result(FuncTS type, TypeS expected) {
      assertThat(type.res())
          .isEqualTo(expected);
    }

    public static List<Arguments> func_result_cases() {
      return asList(
          arguments(TestingTS.f(INT, list()), INT),
          arguments(TestingTS.f(BLOB, list(BOOL)), BLOB),
          arguments(TestingTS.f(BLOB, list(BOOL, INT)), BLOB)
      );
    }

    @ParameterizedTest
    @MethodSource("func_params_cases")
    public void func_params(FuncTS type, Object expected) {
      assertThat(type.params())
          .isEqualTo(expected);
    }

    public static List<Arguments> func_params_cases() {
      return asList(
          arguments(TestingTS.f(INT, list()), list()),
          arguments(TestingTS.f(BLOB, list(BOOL)), list(BOOL)),
          arguments(TestingTS.f(BLOB, list(BOOL, INT)), list(BOOL, INT))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      struct("MyStruct", nlist());
    }

    @Test
    public void struct_name() {
      var struct = struct("MyStruct", nlist());
      Truth.assertThat(struct.name())
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
          arguments(struct("Person", NList.nlist(ItemSigS.itemSigS(STRING, "field"))),
              NList.nlist(ItemSigS.itemSigS(STRING, "field"))),
          arguments(struct("Person",
              nlist(ItemSigS.itemSigS(STRING, "field"), ItemSigS.itemSigS(INT, "field2"))),
              nlist(ItemSigS.itemSigS(STRING, "field"), ItemSigS.itemSigS(INT, "field2")))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = asList(
        BLOB,
        BOOL,
        INT,
        STRING,
        struct("MyStruct", nlist()),
        struct("MyStruct", NList.nlist(ItemSigS.itemSigS(INT, "field"))),
        A,
        B,
        C,

        TestingTS.f(BLOB, list()),
        TestingTS.f(STRING, list()),
        TestingTS.f(BLOB, list(STRING)),
        TestingTS.f(BLOB, list(BLOB))
    );

    for (TypeS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.testEquals();
  }

  @Nested
  class _vars {
    @Test
    public void prefixed() {
      var var = var("A");
      Truth.assertThat(var.prefixed("abc"))
          .isEqualTo(var("abc.A"));
    }

    @Test
    public void prefixed_fails_when_prefix_contains_dot() {
      assertCall(() -> var("A").prefixed("abc."))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void unprefixed() {
      Truth.assertThat(var("pref.A").unprefixed())
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
      Truth.assertThat(var("A").hasPrefix())
          .isFalse();
    }

    @Test
    public void has_prefix_returns_true_for_prefixed_var() {
      Truth.assertThat(var("pref.A").hasPrefix())
          .isTrue();
    }
  }
}
