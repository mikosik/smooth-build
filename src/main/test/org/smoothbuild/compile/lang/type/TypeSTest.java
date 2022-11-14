package org.smoothbuild.compile.lang.type;

import static com.google.common.base.Predicates.alwaysFalse;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.compile.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.testing.TestContext.arrayTS;
import static org.smoothbuild.testing.TestContext.blobTS;
import static org.smoothbuild.testing.TestContext.boolTS;
import static org.smoothbuild.testing.TestContext.funcTS;
import static org.smoothbuild.testing.TestContext.intTS;
import static org.smoothbuild.testing.TestContext.stringTS;
import static org.smoothbuild.testing.TestContext.structTS;
import static org.smoothbuild.testing.TestContext.tupleTS;
import static org.smoothbuild.testing.TestContext.varA;
import static org.smoothbuild.testing.TestContext.varB;
import static org.smoothbuild.testing.TestContext.varC;
import static org.smoothbuild.testing.TestContext.varS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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
        arguments(blobTS(), "Blob"),
        arguments(boolTS(), "Bool"),
        arguments(intTS(), "Int"),
        arguments(stringTS(), "String"),
        arguments(varA(), "A"),

        arguments(tupleTS(), "{}"),
        arguments(tupleTS(intTS()), "{Int}"),
        arguments(tupleTS(intTS(), boolTS()), "{Int,Bool}"),
        arguments(tupleTS(varA()), "{A}"),
        arguments(tupleTS(varA(), varB()), "{A,B}"),

        arguments(structTS("MyStruct", nlist()), "MyStruct"),
        arguments(structTS("MyStruct", nlist(itemSigS(intTS()))), "MyStruct"),

        arguments(arrayTS(blobTS()), "[Blob]"),
        arguments(arrayTS(boolTS()), "[Bool]"),
        arguments(arrayTS(intTS()), "[Int]"),
        arguments(arrayTS(stringTS()), "[String]"),
        arguments(arrayTS(tupleTS()), "[{}]"),
        arguments(arrayTS(tupleTS(intTS())), "[{Int}]"),
        arguments(arrayTS(tupleTS(intTS(), boolTS())), "[{Int,Bool}]"),
        arguments(arrayTS(tupleTS(varA())), "[{A}]"),
        arguments(arrayTS(tupleTS(varA(), varB())), "[{A,B}]"),
        arguments(arrayTS(structTS("MyStruct", nlist())), "[MyStruct]"),
        arguments(arrayTS(structTS("MyStruct", nlist(itemSigS(intTS())))), "[MyStruct]"),
        arguments(arrayTS(varA()), "[A]"),


        arguments(arrayTS(arrayTS(varA())), "[[A]]"),
        arguments(arrayTS(arrayTS(blobTS())), "[[Blob]]"),
        arguments(arrayTS(arrayTS(boolTS())), "[[Bool]]"),
        arguments(arrayTS(arrayTS(intTS())), "[[Int]]"),
        arguments(arrayTS(tupleTS()), "[{}]"),
        arguments(arrayTS(arrayTS(tupleTS(intTS()))), "[[{Int}]]"),
        arguments(arrayTS(arrayTS(tupleTS(intTS(), boolTS()))), "[[{Int,Bool}]]"),
        arguments(arrayTS(arrayTS(tupleTS(varA()))), "[[{A}]]"),
        arguments(arrayTS(arrayTS(tupleTS(varA(), varB()))), "[[{A,B}]]"),
        arguments(arrayTS(arrayTS(structTS("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(arrayTS(arrayTS(structTS("MyStruct", nlist(itemSigS(intTS()))))), "[[MyStruct]]"),
        arguments(arrayTS(arrayTS(stringTS())), "[[String]]"),

        arguments(funcTS(varA(), arrayTS(varA())), "([A])->A"),
        arguments(funcTS(stringTS(), arrayTS(varA())), "([A])->String"),
        arguments(funcTS(varA(), varA()), "(A)->A"),
        arguments(funcTS(stringTS()), "()->String"),
        arguments(funcTS(stringTS(), stringTS()), "(String)->String"),
        arguments(funcTS(stringTS(), tupleTS(intTS())), "({Int})->String")
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
        arguments(blobTS(), varSetS()),
        arguments(boolTS(), varSetS()),
        arguments(intTS(), varSetS()),
        arguments(stringTS(), varSetS()),

        arguments(tupleTS(intTS()), varSetS()),
        arguments(tupleTS(varA(), varB()), varSetS(varA(), varB())),
        arguments(arrayTS(intTS()), varSetS()),
        arguments(arrayTS(varA()), varSetS(varA())),

        arguments(funcTS(blobTS(), boolTS()), varSetS()),
        arguments(funcTS(varA(), boolTS()), varSetS(varA())),
        arguments(funcTS(blobTS(), varA()), varSetS(varA())),
        arguments(funcTS(varA(), varB()), varSetS(varA(), varB()))
    );
  }

  @ParameterizedTest
  @MethodSource("rename_vars")
  public void rename_vars(TypeS type, Predicate<VarS> predicate, TypeS expected) {
    assertThat(type.renameVars(predicate))
        .isEqualTo(expected);
  }

  public static List<Arguments> rename_vars() {
    Predicate<VarS> matchC = (VarS v) -> v.name().equals("C");
    return List.of(
        arguments(blobTS(), alwaysTrue(), blobTS()),
        arguments(blobTS(), alwaysFalse(), blobTS()),
        arguments(boolTS(), alwaysTrue(), boolTS()),
        arguments(boolTS(), alwaysFalse(), boolTS()),
        arguments(intTS(), alwaysTrue(), intTS()),
        arguments(intTS(), alwaysFalse(), intTS()),
        arguments(stringTS(), alwaysTrue(), stringTS()),
        arguments(stringTS(), alwaysFalse(), stringTS()),

        arguments(varB(), alwaysTrue(), varA()),
        arguments(varB(), alwaysFalse(), varB()),


        arguments(tupleTS(intTS()), alwaysTrue(), tupleTS(intTS())),
        arguments(tupleTS(intTS()), alwaysFalse(), tupleTS(intTS())),
        arguments(tupleTS(varB(), varC()), alwaysTrue(), tupleTS(varA(), varB())),
        arguments(tupleTS(varB(), varC()), alwaysFalse(), tupleTS(varB(), varC())),
        arguments(tupleTS(varB(), varC()), matchC, tupleTS(varB(), varA())),

        arguments(arrayTS(intTS()), alwaysTrue(), arrayTS(intTS())),
        arguments(arrayTS(intTS()), alwaysFalse(), arrayTS(intTS())),
        arguments(arrayTS(varB()), alwaysTrue(), arrayTS(varA())),
        arguments(arrayTS(varB()), alwaysFalse(), arrayTS(varB())),

        arguments(funcTS(blobTS(), boolTS()), alwaysTrue(), funcTS(blobTS(), boolTS())),
        arguments(funcTS(blobTS(), boolTS()), alwaysFalse(), funcTS(blobTS(), boolTS())),
        arguments(funcTS(varB(), varC()), alwaysTrue(), funcTS(varA(), varB())),
        arguments(funcTS(varB(), varC()), alwaysFalse(), funcTS(varB(), varC())),
        arguments(funcTS(varB(), varC()), matchC, funcTS(varB(), varA()))
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
        arguments(blobTS(), addPrefix, blobTS()),
        arguments(boolTS(), addPrefix, boolTS()),
        arguments(intTS(), addPrefix, intTS()),
        arguments(stringTS(), addPrefix, stringTS()),

        arguments(varS("A"), addPrefix, varS("prefix.A")),
        arguments(varS("pre.A"), addPrefix, varS("prefix.pre.A")),

        arguments(tupleTS(intTS()), addPrefix, tupleTS(intTS())),
        arguments(tupleTS(varA(), varB()), addPrefix, tupleTS(varS("prefix.A"), varS("prefix.B"))),

        arguments(arrayTS(intTS()), addPrefix, arrayTS(intTS())),
        arguments(arrayTS(varS("A")), addPrefix, arrayTS(varS("prefix.A"))),
        arguments(arrayTS(varS("p.A")), addPrefix, arrayTS(varS("prefix.p.A"))),

        arguments(funcTS(blobTS(), boolTS()), addPrefix, funcTS(blobTS(), boolTS())),
        arguments(funcTS(varS("A"), boolTS()), addPrefix, funcTS(varS("prefix.A"), boolTS())),
        arguments(funcTS(blobTS(), varS("A")), addPrefix, funcTS(blobTS(), varS("prefix.A"))),
        arguments(funcTS(varS("p.A"), boolTS()), addPrefix, funcTS(varS("prefix.p.A"), boolTS())),
        arguments(funcTS(blobTS(), varS("p.A")), addPrefix, funcTS(blobTS(), varS("prefix.p.A")))
    );
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(TypeS type) {
      var array = arrayTS(type);
      assertThat(array.elem())
          .isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          arguments(blobTS()),
          arguments(boolTS()),
          arguments(funcTS(stringTS())),
          arguments(intTS()),
          arguments(stringTS()),
          arguments(structTS("MyStruct", nlist())),
          arguments(varA()),

          arguments(arrayTS(blobTS())),
          arguments(arrayTS(boolTS())),
          arguments(arrayTS(funcTS(stringTS()))),
          arguments(arrayTS(intTS())),
          arguments(arrayTS(stringTS())),
          arguments(arrayTS(varA()))
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
          arguments(funcTS(intTS()), intTS()),
          arguments(funcTS(blobTS(), boolTS()), blobTS()),
          arguments(funcTS(blobTS(), boolTS(), intTS()), blobTS())
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
          arguments(funcTS(intTS()), tupleTS()),
          arguments(funcTS(blobTS(), boolTS()), tupleTS(boolTS())),
          arguments(funcTS(blobTS(), boolTS(), intTS()), tupleTS(boolTS(), intTS()))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      structTS("MyStruct", nlist());
    }

    @Test
    public void struct_name() {
      var struct = structTS("MyStruct", nlist());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> structTS(name, nlist()))
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
          arguments(structTS("Person", nlist()), nlist()),
          arguments(structTS("Person", nlist(itemSigS(stringTS(), "field"))),
              nlist(itemSigS(stringTS(), "field"))),
          arguments(
              structTS("Person", nlist(itemSigS(stringTS(), "field"), itemSigS(intTS(), "field2"))),
              nlist(itemSigS(stringTS(), "field"), itemSigS(intTS(), "field2")))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = asList(
        blobTS(),
        boolTS(),
        intTS(),
        stringTS(),
        tupleTS(),
        tupleTS(intTS(), boolTS()),
        structTS("MyStruct", nlist()),
        structTS("MyStruct", nlist(itemSigS(intTS(), "field"))),
        varA(),
        varB(),
        varC(),

        funcTS(blobTS()),
        funcTS(stringTS()),
        funcTS(blobTS(), stringTS()),
        funcTS(blobTS(), blobTS())
    );

    for (TypeS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(tupleTS(type), tupleTS(type));
      equalsTester.addEqualityGroup(arrayTS(type), arrayTS(type));
      equalsTester.addEqualityGroup(arrayTS(arrayTS(type)), arrayTS(arrayTS(type)));
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
          arguments(tupleTS(), list()),
          arguments(tupleTS(boolTS()), list(boolTS())),
          arguments(tupleTS(boolTS(), intTS()), list(boolTS(), intTS()))
      );
    }
  }
}
