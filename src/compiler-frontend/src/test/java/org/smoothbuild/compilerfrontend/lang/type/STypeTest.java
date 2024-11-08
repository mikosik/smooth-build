package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.define.SItemSig.itemSigS;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import com.google.common.testing.EqualsTester;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class STypeTest extends FrontendCompilerTestContext {
  @Test
  void verify_all_base_types_are_tested() {
    assertThat(STypes.baseTypes()).hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(SType type, String name) {
    assertThat(type.name()).isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(SType type, String name) {
    assertThat(type.q()).isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("to_string")
  public void to_string(SType type, String name) {
    assertThat(type.toString()).isEqualTo(name);
  }

  public static List<Arguments> names() {
    return new STypeTest().names_non_static();
  }

  public List<Arguments> names_non_static() {
    return name_or_to_string()
        .appendAll(list(
            arguments(sStructType("MyStruct", nlist()), "MyStruct"),
            arguments(sStructType("MyStruct", nlist(itemSigS(sIntType(), "field"))), "MyStruct")));
  }

  public static List<Arguments> to_string() {
    return new STypeTest().to_string_non_static();
  }

  public List<Arguments> to_string_non_static() {
    return name_or_to_string()
        .appendAll(list(
            arguments(sStructType("MyStruct", nlist()), "MyStruct{}"),
            arguments(
                sStructType("MyStruct", nlist(itemSigS(sIntType(), "field"))),
                "MyStruct{Int field}")));
  }

  public List<Arguments> name_or_to_string() {
    return list(
        arguments(sBlobType(), "Blob"),
        arguments(sBoolType(), "Bool"),
        arguments(sIntType(), "Int"),
        arguments(sStringType(), "String"),
        arguments(varA(), "A"),
        arguments(sTupleType(), "{}"),
        arguments(sTupleType(sIntType()), "{Int}"),
        arguments(sTupleType(sIntType(), sBoolType()), "{Int,Bool}"),
        arguments(sTupleType(varA()), "{A}"),
        arguments(sTupleType(varA(), varB()), "{A,B}"),
        arguments(sArrayType(sBlobType()), "[Blob]"),
        arguments(sArrayType(sBoolType()), "[Bool]"),
        arguments(sArrayType(sIntType()), "[Int]"),
        arguments(sArrayType(sStringType()), "[String]"),
        arguments(sArrayType(sTupleType()), "[{}]"),
        arguments(sArrayType(sTupleType(sIntType())), "[{Int}]"),
        arguments(sArrayType(sTupleType(sIntType(), sBoolType())), "[{Int,Bool}]"),
        arguments(sArrayType(sTupleType(varA())), "[{A}]"),
        arguments(sArrayType(sTupleType(varA(), varB())), "[{A,B}]"),
        arguments(sArrayType(sStructType("MyStruct", nlist())), "[MyStruct]"),
        arguments(
            sArrayType(sStructType("MyStruct", nlist(itemSigS(sIntType(), "field")))),
            "[MyStruct]"),
        arguments(sArrayType(varA()), "[A]"),
        arguments(sArrayType(sArrayType(varA())), "[[A]]"),
        arguments(sArrayType(sArrayType(sBlobType())), "[[Blob]]"),
        arguments(sArrayType(sArrayType(sBoolType())), "[[Bool]]"),
        arguments(sArrayType(sArrayType(sIntType())), "[[Int]]"),
        arguments(sArrayType(sArrayType(sTupleType())), "[[{}]]"),
        arguments(sArrayType(sArrayType(sTupleType(sIntType()))), "[[{Int}]]"),
        arguments(sArrayType(sArrayType(sTupleType(sIntType(), sBoolType()))), "[[{Int,Bool}]]"),
        arguments(sArrayType(sArrayType(sTupleType(varA()))), "[[{A}]]"),
        arguments(sArrayType(sArrayType(sTupleType(varA(), varB()))), "[[{A,B}]]"),
        arguments(sArrayType(sArrayType(sStructType("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(
            sArrayType(sArrayType(sStructType("MyStruct", nlist(itemSigS(sIntType(), "filed"))))),
            "[[MyStruct]]"),
        arguments(sArrayType(sArrayType(sStringType())), "[[String]]"),
        arguments(sFuncType(sArrayType(varA()), varA()), "([A])->A"),
        arguments(sFuncType(sArrayType(varA()), sStringType()), "([A])->String"),
        arguments(sFuncType(varA(), varA()), "(A)->A"),
        arguments(sFuncType(sStringType()), "()->String"),
        arguments(sFuncType(sStringType(), sStringType()), "(String)->String"),
        arguments(sFuncType(sTupleType(sIntType()), sStringType()), "({Int})->String"),
        arguments(sInterfaceType(), "{}"),
        arguments(sInterfaceType(sSig(sIntType(), "field1")), "{Int field1}"),
        arguments(
            sInterfaceType(sSig(sIntType(), "field1"), sSig(sBlobType(), "field2")),
            "{Int field1,Blob field2}"));
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(SType type, SVarSet expected) {
    assertThat(type.vars()).isEqualTo(expected);
  }

  public static List<Arguments> vars_test_data() {
    return new STypeTest().vars_test_data_non_static();
  }

  public List<Arguments> vars_test_data_non_static() {
    return list(
        arguments(sBlobType(), varSetS()),
        arguments(sBoolType(), varSetS()),
        arguments(sIntType(), varSetS()),
        arguments(sStringType(), varSetS()),
        arguments(sTupleType(sIntType()), varSetS()),
        arguments(sTupleType(varA(), varB()), varSetS(varA(), varB())),
        arguments(sArrayType(sIntType()), varSetS()),
        arguments(sArrayType(varA()), varSetS(varA())),
        arguments(sFuncType(sBoolType(), sBlobType()), varSetS()),
        arguments(sFuncType(sBoolType(), varA()), varSetS(varA())),
        arguments(sFuncType(varA(), sBlobType()), varSetS(varA())),
        arguments(sFuncType(varB(), varA()), varSetS(varA(), varB())),
        arguments(sStructType(sIntType()), varSetS()),
        arguments(sStructType(sIntType(), varA()), varSetS(varA())),
        arguments(sStructType(varB(), varA()), varSetS(varA(), varB())),
        arguments(sInterfaceType(sIntType()), varSetS()),
        arguments(sInterfaceType(sIntType(), varA()), varSetS(varA())),
        arguments(sInterfaceType(varB(), varA()), varSetS(varA(), varB())));
  }

  @ParameterizedTest
  @MethodSource("map_vars")
  public void map_vars(SType type, SType expected) {
    Function<SVar, SType> addPrefix = (SVar v) -> new SVar("prefix." + v.name());
    assertThat(type.mapVars(addPrefix)).isEqualTo(expected);
  }

  public static List<Arguments> map_vars() {
    return new STypeTest().map_vars_non_static();
  }

  public List<Arguments> map_vars_non_static() {
    return list(
        arguments(sBlobType(), sBlobType()),
        arguments(sBoolType(), sBoolType()),
        arguments(sIntType(), sIntType()),
        arguments(sStringType(), sStringType()),
        arguments(sVar("A"), sVar("prefix.A")),
        arguments(sVar("pre.A"), sVar("prefix.pre.A")),
        arguments(sTupleType(sIntType()), sTupleType(sIntType())),
        arguments(sTupleType(varA(), varB()), sTupleType(sVar("prefix.A"), sVar("prefix.B"))),
        arguments(sTupleType(sTupleType(varA())), sTupleType(sTupleType(sVar("prefix.A")))),
        arguments(sArrayType(sIntType()), sArrayType(sIntType())),
        arguments(sArrayType(sVar("A")), sArrayType(sVar("prefix.A"))),
        arguments(sArrayType(sVar("p.A")), sArrayType(sVar("prefix.p.A"))),
        arguments(sArrayType(sArrayType(sVar("A"))), sArrayType(sArrayType(sVar("prefix.A")))),
        arguments(sFuncType(sBoolType(), sBlobType()), sFuncType(sBoolType(), sBlobType())),
        arguments(sFuncType(sBoolType(), sVar("A")), sFuncType(sBoolType(), sVar("prefix.A"))),
        arguments(sFuncType(sVar("A"), sBlobType()), sFuncType(sVar("prefix.A"), sBlobType())),
        arguments(sFuncType(sBoolType(), sVar("p.A")), sFuncType(sBoolType(), sVar("prefix.p.A"))),
        arguments(sFuncType(sVar("p.A"), sBlobType()), sFuncType(sVar("prefix.p.A"), sBlobType())),
        arguments(sFuncType(sFuncType(sVar("A"))), sFuncType(sFuncType(sVar("prefix.A")))),
        arguments(
            sFuncType(sFuncType(sVar("A"), sIntType()), sIntType()),
            sFuncType(sFuncType(sVar("prefix.A"), sIntType()), sIntType())),
        arguments(sStructType("MyStruct", sIntType()), sStructType("MyStruct", sIntType())),
        arguments(sStructType(varA(), varB()), sStructType(sVar("prefix.A"), sVar("prefix.B"))),
        arguments(
            sStructType("S1", sStructType("S2", sVar("A"))),
            sStructType("S1", sStructType("S2", sVar("prefix.A")))),
        arguments(sInterfaceType(sIntType()), sInterfaceType(sIntType())),
        arguments(
            sInterfaceType(varA(), varB()), sInterfaceType(sVar("prefix.A"), sVar("prefix.B"))),
        arguments(
            sInterfaceType(sInterfaceType(sVar("A"))),
            sInterfaceType(sInterfaceType(sVar("prefix.A")))));
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(SType type) {
      var array = sArrayType(type);
      assertThat(array.elem()).isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return new STypeTest().elemType_test_data_non_static();
    }
  }

  public List<Arguments> elemType_test_data_non_static() {
    return list(
        arguments(sBlobType()),
        arguments(sBoolType()),
        arguments(sFuncType(sStringType())),
        arguments(sIntType()),
        arguments(sStringType()),
        arguments(sStructType("MyStruct", nlist())),
        arguments(varA()),
        arguments(sArrayType(sBlobType())),
        arguments(sArrayType(sBoolType())),
        arguments(sArrayType(sFuncType(sStringType()))),
        arguments(sArrayType(sIntType())),
        arguments(sArrayType(sStringType())),
        arguments(sArrayType(varA())));
  }

  @Nested
  public class _func {
    @ParameterizedTest
    @MethodSource("func_result_cases")
    public void func_result(SFuncType type, SType expected) {
      assertThat(type.result()).isEqualTo(expected);
    }

    public static List<Arguments> func_result_cases() {
      return new STypeTest().func_result_cases_non_static();
    }

    @ParameterizedTest
    @MethodSource("func_params_cases")
    public void func_params(SFuncType type, Object expected) {
      assertThat(type.params()).isEqualTo(expected);
    }

    public static List<Arguments> func_params_cases() {
      return new STypeTest().func_params_cases_non_static();
    }
  }

  public List<Arguments> func_params_cases_non_static() {
    return list(
        arguments(sFuncType(sIntType()), sTupleType()),
        arguments(sFuncType(sBoolType(), sBlobType()), sTupleType(sBoolType())),
        arguments(
            sFuncType(sBoolType(), sIntType(), sBlobType()), sTupleType(sBoolType(), sIntType())));
  }

  public List<Arguments> func_result_cases_non_static() {
    return list(
        arguments(sFuncType(sIntType()), sIntType()),
        arguments(sFuncType(sBoolType(), sBlobType()), sBlobType()),
        arguments(sFuncType(sBoolType(), sIntType(), sBlobType()), sBlobType()));
  }

  @Nested
  class _struct {
    @Test
    void without_fields_can_be_created() {
      sStructType("MyStruct", nlist());
    }

    @Test
    void struct_name() {
      var struct = sStructType("MyStruct", nlist());
      assertThat(struct.name()).isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> sStructType(name, nlist())).throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(SStructType struct, NList<SItemSig> expected) {
      assertThat(struct.fields()).isEqualTo(expected);
    }

    public static List<Arguments> struct_fields_cases() {
      return new STypeTest().struct_fields_cases_non_static();
    }
  }

  public List<Arguments> struct_fields_cases_non_static() {
    return list(
        arguments(sStructType("Person", nlist()), nlist()),
        arguments(
            sStructType("Person", nlist(itemSigS(sStringType(), "field"))),
            nlist(itemSigS(sStringType(), "field"))),
        arguments(
            sStructType(
                "Person", nlist(itemSigS(sStringType(), "field"), itemSigS(sIntType(), "field2"))),
            nlist(itemSigS(sStringType(), "field"), itemSigS(sIntType(), "field2"))));
  }

  @Test
  void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<SType> types = list(
        sBlobType(),
        sBoolType(),
        sIntType(),
        sStringType(),
        sTupleType(),
        sTupleType(sIntType(), sBoolType()),
        sStructType("MyStruct", nlist()),
        sStructType("MyStruct", nlist(itemSigS(sIntType(), "field"))),
        varA(),
        varB(),
        varC(),
        sFuncType(sBlobType()),
        sFuncType(sStringType()),
        sFuncType(sStringType(), sBlobType()),
        sFuncType(sBlobType(), sBlobType()));

    for (SType type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(sTupleType(type), sTupleType(type));
      equalsTester.addEqualityGroup(sArrayType(type), sArrayType(type));
      equalsTester.addEqualityGroup(sArrayType(sArrayType(type)), sArrayType(sArrayType(type)));
    }
    equalsTester.testEquals();
  }

  @Nested
  class _tuple {
    @ParameterizedTest
    @MethodSource("tuple_items_cases")
    public void func_params(STupleType type, Object expected) {
      assertThat(type.elements()).isEqualTo(expected);
    }

    public static List<Arguments> tuple_items_cases() {
      return new STypeTest().tuple_items_cases();
    }
  }

  public List<Arguments> tuple_items_cases() {
    return list(
        arguments(sTupleType(), list()),
        arguments(sTupleType(sBoolType()), list(sBoolType())),
        arguments(sTupleType(sBoolType(), sIntType()), list(sBoolType(), sIntType())));
  }

  @Nested
  class _temp_var {
    @Test
    void is_older_than_1_1() {
      assertThat(new STempVar("1").isOlderThan(new STempVar("1"))).isFalse();
    }

    @Test
    void is_older_than_1_2() {
      assertThat(new STempVar("1").isOlderThan(new STempVar("2"))).isTrue();
    }

    @Test
    void is_older_than_1_10() {
      assertThat(new STempVar("1").isOlderThan(new STempVar("10"))).isTrue();
    }

    @Test
    void is_older_than_2_1() {
      assertThat(new STempVar("2").isOlderThan(new STempVar("1"))).isFalse();
    }
  }
}
