package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.ANY;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.INFERABLE_BASE_TYPES;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.NOTHING;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.VAR_A;
import static org.smoothbuild.testing.type.TestingTS.VAR_B;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.f;
import static org.smoothbuild.testing.type.TestingTS.join;
import static org.smoothbuild.testing.type.TestingTS.meet;
import static org.smoothbuild.testing.type.TestingTS.struct;
import static org.smoothbuild.testing.type.TestingTS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;

import com.google.common.testing.EqualsTester;

public class TypeSTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(INFERABLE_BASE_TYPES)
        .hasSize(6);
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
        .isEqualTo("Type(`" + name + "`)");
  }

  public static List<Arguments> names() {
    return asList(
        arguments(ANY, "Any"),
        arguments(BLOB, "Blob"),
        arguments(BOOL, "Bool"),
        arguments(INT, "Int"),
        arguments(NOTHING, "Nothing"),
        arguments(STRING, "String"),
        arguments(struct("MyStruct", nList()), "MyStruct"),
        arguments(VAR_A, "A"),

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
        arguments(a(struct("MyStruct", nList())), "[MyStruct]"),
        arguments(a(VAR_A), "[A]"),

        arguments(a(a(VAR_A)), "[[A]]"),
        arguments(a(a(ANY)), "[[Any]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(INT)), "[[Int]]"),
        arguments(a(a(NOTHING)), "[[Nothing]]"),
        arguments(a(a(struct("MyStruct", nList()))), "[[MyStruct]]"),
        arguments(a(a(STRING)), "[[String]]"),

        arguments(f(VAR_A, list(a(VAR_A))), "A([A])"),
        arguments(f(STRING, list(a(VAR_A))), "String([A])"),
        arguments(f(VAR_A, list(VAR_A)), "A(A)"),
        arguments(f(STRING, list()), "String()"),
        arguments(f(STRING, list(STRING)), "String(String)")
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
        arguments(ANY, varSetS()),
        arguments(BLOB, varSetS()),
        arguments(BOOL, varSetS()),
        arguments(INT, varSetS()),
        arguments(NOTHING, varSetS()),
        arguments(STRING, varSetS()),

        arguments(join(INT, BLOB), varSetS()),
        arguments(join(VAR_A, VAR_B), varSetS(VAR_A, VAR_B)),

        arguments(meet(INT, BLOB), varSetS()),
        arguments(meet(VAR_A, VAR_B), varSetS(VAR_A, VAR_B)),

        arguments(a(INT), varSetS()),
        arguments(a(VAR_A), varSetS(VAR_A)),

        arguments(f(BLOB, list(BOOL)), varSetS()),
        arguments(f(VAR_A, list(BOOL)), varSetS(VAR_A)),
        arguments(f(BLOB, list(VAR_A)), varSetS(VAR_A))
    );
  }

  @ParameterizedTest
  @MethodSource("with_prefixed_vars")
  public void with_prefixed_vars(TypeS type, String prefix, TypeS typeWithPrefixedVars) {
    assertThat(type.withPrefixedVars(prefix))
        .isEqualTo(typeWithPrefixedVars);
  }

  public static List<Arguments> with_prefixed_vars() {
    return List.of(
        arguments(ANY, "prefix", ANY),
        arguments(BLOB, "prefix", BLOB),
        arguments(BOOL, "prefix", BOOL),
        arguments(INT, "prefix", INT),
        arguments(NOTHING, "prefix", NOTHING),
        arguments(STRING, "prefix", STRING),

        arguments(var("A"), "prefix", var("prefix.A")),
        arguments(var("pre.A"), "prefix", var("prefix.pre.A")),

        arguments(join(BLOB, INT), "prefix", join(BLOB, INT)),
        arguments(join(var("A"), var("B")), "prefix", join(var("prefix.A"), var("prefix.B"))),
        arguments(join(var("p.A"), var("p.B")), "prefix", join(var("prefix.p.A"), var("prefix.p.B"))),

        arguments(meet(BLOB, INT), "prefix", meet(BLOB, INT)),
        arguments(meet(var("A"), var("B")), "prefix", meet(var("prefix.A"), var("prefix.B"))),
        arguments(meet(var("p.A"), var("p.B")), "prefix", meet(var("prefix.p.A"), var("prefix.p.B"))),

        arguments(a(INT), "prefix", a(INT)),
        arguments(a(var("A")), "prefix", a(var("prefix.A"))),
        arguments(a(var("p.A")), "prefix", a(var("prefix.p.A"))),

        arguments(f(BLOB, list(BOOL)), "prefix", f(BLOB, list(BOOL))),
        arguments(f(var("A"), list(BOOL)), "prefix", f(var("prefix.A"), list(BOOL))),
        arguments(f(BLOB, list(var("A"))), "prefix", f(BLOB, list(var("prefix.A")))),
        arguments(f(var("p.A"), list(BOOL)), "prefix", f(var("prefix.p.A"), list(BOOL))),
        arguments(f(BLOB, list(var("p.A"))), "prefix", f(BLOB, list(var("prefix.p.A"))))
    );
  }

  @Test
  public void with_prefixed_vars_fails_when_prefix_contains_dot() {
    var var = var("A");
    assertCall(() -> var.withPrefixedVars("abc."))
        .throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("with_prefixed_vars")
  public void remove_var_prefixes(TypeS type, String prefix, TypeS typeWithPrefixedVars) {
    assertThat(typeWithPrefixedVars.removeVarPrefixes())
        .isEqualTo(type);
  }

  @Test
  public void remove_var_prefixes_fails_when_var_has_no_prefix() {
    var var = var("A");
    assertCall(() -> var.removeVarPrefixes())
        .throwsException(IllegalStateException.class);
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
          arguments(ANY),
          arguments(BLOB),
          arguments(BOOL),
          arguments(f(STRING, list())),
          arguments(INT),
          arguments(NOTHING),
          arguments(STRING),
          arguments(struct("MyStruct", nList())),
          arguments(VAR_A),

          arguments(a(ANY)),
          arguments(a(BLOB)),
          arguments(a(BOOL)),
          arguments(a(f(STRING, list()))),
          arguments(a(INT)),
          arguments(a(NOTHING)),
          arguments(a(STRING)),
          arguments(a(VAR_A))
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
          arguments(f(INT, list()), INT),
          arguments(f(BLOB, list(BOOL)), BLOB),
          arguments(f(BLOB, list(BOOL, INT)), BLOB)
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
          arguments(f(INT, list()), list()),
          arguments(f(BLOB, list(BOOL)), list(BOOL)),
          arguments(f(BLOB, list(BOOL, INT)), list(BOOL, INT))
      );
    }
  }

  @Nested
  class _join {
    @ParameterizedTest
    @MethodSource("join_of_test_cases")
    public void join_of(TypeS a, TypeS b, TypeS expected) {
      assertThat(join(a, b))
          .isEqualTo(expected);
      assertThat(join(b, a))
          .isEqualTo(expected);
    }

    public static List<Arguments> join_of_test_cases() {
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
    @ParameterizedTest
    @MethodSource("meet_of_test_cases")
    public void meet_of(TypeS a, TypeS b, TypeS expected) {
      assertThat(meet(a, b))
          .isEqualTo(expected);
      assertThat(meet(b, a))
          .isEqualTo(expected);
    }

    public static List<Arguments> meet_of_test_cases() {
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
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      struct("MyStruct", nList());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      struct("MyStruct", nList(itemSigS(NOTHING, "fieldName")));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      struct("MyStruct", nList(itemSigS(a(NOTHING), "fieldName")));
    }

    @Test
    public void struct_name() {
      var struct = struct("MyStruct", nList());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> struct(name, nList()))
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
          arguments(struct("Person", nList()), nList()),
          arguments(struct("Person", nList(itemSigS(STRING, "field"))),
              nList(itemSigS(STRING, "field"))),
          arguments(struct("Person",
              nList(itemSigS(STRING, "field"), itemSigS(INT, "field2"))),
              nList(itemSigS(STRING, "field"), itemSigS(INT, "field2")))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = asList(
        ANY,
        BLOB,
        BOOL,
        INT,
        NOTHING,
        STRING,
        struct("MyStruct", nList()),
        struct("MyStruct", nList(itemSigS(INT, "field"))),
        VAR_A,
        var("B"),
        var("C"),

        f(BLOB, list()),
        f(STRING, list()),
        f(BLOB, list(STRING)),
        f(BLOB, list(BLOB))
    );

    for (TypeS type : types) {
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
}
