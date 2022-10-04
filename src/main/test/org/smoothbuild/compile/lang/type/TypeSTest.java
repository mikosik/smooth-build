package org.smoothbuild.compile.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.compile.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.testing.TestContext.varA;
import static org.smoothbuild.testing.TestContext.varB;
import static org.smoothbuild.testing.TestContext.varC;
import static org.smoothbuild.testing.TestContext.varS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.f;
import static org.smoothbuild.testing.type.TestingTS.struct;
import static org.smoothbuild.testing.type.TestingTS.tuple;
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
import org.smoothbuild.util.collect.NList;

import com.google.common.testing.EqualsTester;

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
        arguments(varA(), "A"),

        arguments(tuple(), "{}"),
        arguments(tuple(INT), "{Int}"),
        arguments(tuple(INT, BOOL), "{Int,Bool}"),
        arguments(tuple(varA()), "{A}"),
        arguments(tuple(varA(), varB()), "{A,B}"),

        arguments(struct("MyStruct", nlist()), "MyStruct"),
        arguments(struct("MyStruct", nlist(itemSigS(INT))), "MyStruct"),

        arguments(a(BLOB), "[Blob]"),
        arguments(a(BOOL), "[Bool]"),
        arguments(a(INT), "[Int]"),
        arguments(a(STRING), "[String]"),
        arguments(a(tuple()), "[{}]"),
        arguments(a(tuple(INT)), "[{Int}]"),
        arguments(a(tuple(INT, BOOL)), "[{Int,Bool}]"),
        arguments(a(tuple(varA())), "[{A}]"),
        arguments(a(tuple(varA(), varB())), "[{A,B}]"),
        arguments(a(struct("MyStruct", nlist())), "[MyStruct]"),
        arguments(a(struct("MyStruct", nlist(itemSigS(INT)))), "[MyStruct]"),
        arguments(a(varA()), "[A]"),


        arguments(a(a(varA())), "[[A]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(INT)), "[[Int]]"),
        arguments(a(tuple()), "[{}]"),
        arguments(a(a(tuple(INT))), "[[{Int}]]"),
        arguments(a(a(tuple(INT, BOOL))), "[[{Int,Bool}]]"),
        arguments(a(a(tuple(varA()))), "[[{A}]]"),
        arguments(a(a(tuple(varA(), varB()))), "[[{A,B}]]"),
        arguments(a(a(struct("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(a(a(struct("MyStruct", nlist(itemSigS(INT))))), "[[MyStruct]]"),
        arguments(a(a(STRING)), "[[String]]"),

        arguments(f(varA(), a(varA())), "A([A])"),
        arguments(f(STRING, a(varA())), "String([A])"),
        arguments(f(varA(), varA()), "A(A)"),
        arguments(f(STRING), "String()"),
        arguments(f(STRING, STRING), "String(String)"),
        arguments(f(STRING, tuple(INT)), "String({Int})")
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
        arguments(BLOB, varSetS()),
        arguments(BOOL, varSetS()),
        arguments(INT, varSetS()),
        arguments(STRING, varSetS()),

        arguments(tuple(INT), varSetS()),
        arguments(tuple(varA(), varB()), varSetS(varA(), varB())),
        arguments(a(INT), varSetS()),
        arguments(a(varA()), varSetS(varA())),

        arguments(f(BLOB, BOOL), varSetS()),
        arguments(f(varA(), BOOL), varSetS(varA())),
        arguments(f(BLOB, varA()), varSetS(varA())),
        arguments(f(varA(), varB()), varSetS(varA(), varB()))
    );
  }

  @ParameterizedTest
  @MethodSource("map_vars")
  public void map_vars(TypeS type, Function<VarS, TypeS> varMapper, TypeS expected) {
    assertThat(type.mapVars(varMapper))
        .isEqualTo(expected);
  }

  public static List<Arguments> map_vars() {
    Function<VarS, VarS> addPrefix = (VarS v) -> new VarS("prefix." + v.name());
    return List.of(
        arguments(BLOB, addPrefix, BLOB),
        arguments(BOOL, addPrefix, BOOL),
        arguments(INT, addPrefix, INT),
        arguments(STRING, addPrefix, STRING),

        arguments(varS("A"), addPrefix, varS("prefix.A")),
        arguments(varS("pre.A"), addPrefix, varS("prefix.pre.A")),

        arguments(tuple(INT), addPrefix, tuple(INT)),
        arguments(tuple(varA(), varB()), addPrefix, tuple(varS("prefix.A"), varS("prefix.B"))),

        arguments(a(INT), addPrefix, a(INT)),
        arguments(a(varS("A")), addPrefix, a(varS("prefix.A"))),
        arguments(a(varS("p.A")), addPrefix, a(varS("prefix.p.A"))),

        arguments(f(BLOB, BOOL), addPrefix, f(BLOB, BOOL)),
        arguments(f(varS("A"), BOOL), addPrefix, f(varS("prefix.A"), BOOL)),
        arguments(f(BLOB, varS("A")), addPrefix, f(BLOB, varS("prefix.A"))),
        arguments(f(varS("p.A"), BOOL), addPrefix, f(varS("prefix.p.A"), BOOL)),
        arguments(f(BLOB, varS("p.A")), addPrefix, f(BLOB, varS("prefix.p.A")))
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
          arguments(BLOB),
          arguments(BOOL),
          arguments(f(STRING)),
          arguments(INT),
          arguments(STRING),
          arguments(struct("MyStruct", nlist())),
          arguments(varA()),

          arguments(a(BLOB)),
          arguments(a(BOOL)),
          arguments(a(f(STRING))),
          arguments(a(INT)),
          arguments(a(STRING)),
          arguments(a(varA()))
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
          arguments(f(INT), INT),
          arguments(f(BLOB, BOOL), BLOB),
          arguments(f(BLOB, BOOL, INT), BLOB)
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
          arguments(f(INT), tuple()),
          arguments(f(BLOB, BOOL), tuple(BOOL)),
          arguments(f(BLOB, BOOL, INT), tuple(BOOL, INT))
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
    List<TypeS> types = asList(
        BLOB,
        BOOL,
        INT,
        STRING,
        tuple(),
        tuple(INT, BOOL),
        struct("MyStruct", nlist()),
        struct("MyStruct", nlist(itemSigS(INT, "field"))),
        varA(),
        varB(),
        varC(),

        f(BLOB),
        f(STRING),
        f(BLOB, STRING),
        f(BLOB, BLOB)
    );

    for (TypeS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(tuple(type), tuple(type));
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.testEquals();
  }

  @Nested
  class _tuple {
    @ParameterizedTest
    @MethodSource("tuple_items_cases")
    public void func_params(TupleTS type, Object expected) {
      assertThat(type.items())
          .isEqualTo(expected);
    }

    public static List<Arguments> tuple_items_cases() {
      return asList(
          arguments(tuple(), list()),
          arguments(tuple(BOOL), list(BOOL)),
          arguments(tuple(BOOL, INT), list(BOOL, INT))
      );
    }
  }
}
