package org.smoothbuild.compile.frontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compile.frontend.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

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
import org.smoothbuild.compile.frontend.lang.define.ItemSigS;
import org.smoothbuild.testing.TestExpressionS;

public class TypeSTest extends TestExpressionS {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(TypeFS.baseTs()).hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(TypeS type, String name) {
    assertThat(type.name()).isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(TypeS type, String name) {
    assertThat(type.q()).isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("to_string")
  public void to_string(TypeS type, String name) {
    assertThat(type.toString()).isEqualTo(name);
  }

  public static List<Arguments> names() {
    return name_or_to_string()
        .appendAll(list(
            arguments(structTS("MyStruct", nlist()), "MyStruct"),
            arguments(structTS("MyStruct", nlist(itemSigS(intTS(), "field"))), "MyStruct")));
  }

  public static List<Arguments> to_string() {
    return name_or_to_string()
        .appendAll(list(
            arguments(structTS("MyStruct", nlist()), "MyStruct()"),
            arguments(
                structTS("MyStruct", nlist(itemSigS(intTS(), "field"))), "MyStruct(Int field)")));
  }

  public static List<Arguments> name_or_to_string() {
    return list(
        arguments(blobTS(), "Blob"),
        arguments(boolTS(), "Bool"),
        arguments(intTS(), "Int"),
        arguments(stringTS(), "String"),
        arguments(varA(), "A"),
        arguments(tupleTS(), "()"),
        arguments(tupleTS(intTS()), "(Int)"),
        arguments(tupleTS(intTS(), boolTS()), "(Int,Bool)"),
        arguments(tupleTS(varA()), "(A)"),
        arguments(tupleTS(varA(), varB()), "(A,B)"),
        arguments(arrayTS(blobTS()), "[Blob]"),
        arguments(arrayTS(boolTS()), "[Bool]"),
        arguments(arrayTS(intTS()), "[Int]"),
        arguments(arrayTS(stringTS()), "[String]"),
        arguments(arrayTS(tupleTS()), "[()]"),
        arguments(arrayTS(tupleTS(intTS())), "[(Int)]"),
        arguments(arrayTS(tupleTS(intTS(), boolTS())), "[(Int,Bool)]"),
        arguments(arrayTS(tupleTS(varA())), "[(A)]"),
        arguments(arrayTS(tupleTS(varA(), varB())), "[(A,B)]"),
        arguments(arrayTS(structTS("MyStruct", nlist())), "[MyStruct]"),
        arguments(arrayTS(structTS("MyStruct", nlist(itemSigS(intTS(), "field")))), "[MyStruct]"),
        arguments(arrayTS(varA()), "[A]"),
        arguments(arrayTS(arrayTS(varA())), "[[A]]"),
        arguments(arrayTS(arrayTS(blobTS())), "[[Blob]]"),
        arguments(arrayTS(arrayTS(boolTS())), "[[Bool]]"),
        arguments(arrayTS(arrayTS(intTS())), "[[Int]]"),
        arguments(arrayTS(arrayTS(tupleTS())), "[[()]]"),
        arguments(arrayTS(arrayTS(tupleTS(intTS()))), "[[(Int)]]"),
        arguments(arrayTS(arrayTS(tupleTS(intTS(), boolTS()))), "[[(Int,Bool)]]"),
        arguments(arrayTS(arrayTS(tupleTS(varA()))), "[[(A)]]"),
        arguments(arrayTS(arrayTS(tupleTS(varA(), varB()))), "[[(A,B)]]"),
        arguments(arrayTS(arrayTS(structTS("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(
            arrayTS(arrayTS(structTS("MyStruct", nlist(itemSigS(intTS(), "filed"))))),
            "[[MyStruct]]"),
        arguments(arrayTS(arrayTS(stringTS())), "[[String]]"),
        arguments(funcTS(arrayTS(varA()), varA()), "([A])->A"),
        arguments(funcTS(arrayTS(varA()), stringTS()), "([A])->String"),
        arguments(funcTS(varA(), varA()), "(A)->A"),
        arguments(funcTS(stringTS()), "()->String"),
        arguments(funcTS(stringTS(), stringTS()), "(String)->String"),
        arguments(funcTS(tupleTS(intTS()), stringTS()), "((Int))->String"),
        arguments(interfaceTS(), "()"),
        arguments(interfaceTS(sigS(intTS(), "field1")), "(Int field1)"),
        arguments(
            interfaceTS(sigS(intTS(), "field1"), sigS(blobTS(), "field2")),
            "(Int field1,Blob field2)"));
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(TypeS type, VarSetS expected) {
    assertThat(type.vars()).isEqualTo(expected);
  }

  public static List<Arguments> vars_test_data() {
    return list(
        arguments(blobTS(), varSetS()),
        arguments(boolTS(), varSetS()),
        arguments(intTS(), varSetS()),
        arguments(stringTS(), varSetS()),
        arguments(tupleTS(intTS()), varSetS()),
        arguments(tupleTS(varA(), varB()), varSetS(varA(), varB())),
        arguments(arrayTS(intTS()), varSetS()),
        arguments(arrayTS(varA()), varSetS(varA())),
        arguments(funcTS(boolTS(), blobTS()), varSetS()),
        arguments(funcTS(boolTS(), varA()), varSetS(varA())),
        arguments(funcTS(varA(), blobTS()), varSetS(varA())),
        arguments(funcTS(varB(), varA()), varSetS(varA(), varB())),
        arguments(structTS(intTS()), varSetS()),
        arguments(structTS(intTS(), varA()), varSetS(varA())),
        arguments(structTS(varB(), varA()), varSetS(varA(), varB())),
        arguments(interfaceTS(intTS()), varSetS()),
        arguments(interfaceTS(intTS(), varA()), varSetS(varA())),
        arguments(interfaceTS(varB(), varA()), varSetS(varA(), varB())));
  }

  @ParameterizedTest
  @MethodSource("map_vars")
  public void map_vars(TypeS type, TypeS expected) {
    Function<VarS, TypeS> addPrefix = (VarS v) -> new VarS("prefix." + v.name());
    assertThat(type.mapVars(addPrefix)).isEqualTo(expected);
  }

  public static List<Arguments> map_vars() {
    return list(
        arguments(blobTS(), blobTS()),
        arguments(boolTS(), boolTS()),
        arguments(intTS(), intTS()),
        arguments(stringTS(), stringTS()),
        arguments(varS("A"), varS("prefix.A")),
        arguments(varS("pre.A"), varS("prefix.pre.A")),
        arguments(tupleTS(intTS()), tupleTS(intTS())),
        arguments(tupleTS(varA(), varB()), tupleTS(varS("prefix.A"), varS("prefix.B"))),
        arguments(tupleTS(tupleTS(varA())), tupleTS(tupleTS(varS("prefix.A")))),
        arguments(arrayTS(intTS()), arrayTS(intTS())),
        arguments(arrayTS(varS("A")), arrayTS(varS("prefix.A"))),
        arguments(arrayTS(varS("p.A")), arrayTS(varS("prefix.p.A"))),
        arguments(arrayTS(arrayTS(varS("A"))), arrayTS(arrayTS(varS("prefix.A")))),
        arguments(funcTS(boolTS(), blobTS()), funcTS(boolTS(), blobTS())),
        arguments(funcTS(boolTS(), varS("A")), funcTS(boolTS(), varS("prefix.A"))),
        arguments(funcTS(varS("A"), blobTS()), funcTS(varS("prefix.A"), blobTS())),
        arguments(funcTS(boolTS(), varS("p.A")), funcTS(boolTS(), varS("prefix.p.A"))),
        arguments(funcTS(varS("p.A"), blobTS()), funcTS(varS("prefix.p.A"), blobTS())),
        arguments(funcTS(funcTS(varS("A"))), funcTS(funcTS(varS("prefix.A")))),
        arguments(
            funcTS(funcTS(varS("A"), intTS()), intTS()),
            funcTS(funcTS(varS("prefix.A"), intTS()), intTS())),
        arguments(structTS("MyStruct", intTS()), structTS("MyStruct", intTS())),
        arguments(structTS(varA(), varB()), structTS(varS("prefix.A"), varS("prefix.B"))),
        arguments(
            structTS("S1", structTS("S2", varS("A"))),
            structTS("S1", structTS("S2", varS("prefix.A")))),
        arguments(interfaceTS(intTS()), interfaceTS(intTS())),
        arguments(interfaceTS(varA(), varB()), interfaceTS(varS("prefix.A"), varS("prefix.B"))),
        arguments(interfaceTS(interfaceTS(varS("A"))), interfaceTS(interfaceTS(varS("prefix.A")))));
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(TypeS type) {
      var array = arrayTS(type);
      assertThat(array.elem()).isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return list(
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
          arguments(arrayTS(varA())));
    }
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("func_result_cases")
    public void func_result(FuncTS type, TypeS expected) {
      assertThat(type.result()).isEqualTo(expected);
    }

    public static List<Arguments> func_result_cases() {
      return list(
          arguments(funcTS(intTS()), intTS()),
          arguments(funcTS(boolTS(), blobTS()), blobTS()),
          arguments(funcTS(boolTS(), intTS(), blobTS()), blobTS()));
    }

    @ParameterizedTest
    @MethodSource("func_params_cases")
    public void func_params(FuncTS type, Object expected) {
      assertThat(type.params()).isEqualTo(expected);
    }

    public static List<Arguments> func_params_cases() {
      return list(
          arguments(funcTS(intTS()), tupleTS()),
          arguments(funcTS(boolTS(), blobTS()), tupleTS(boolTS())),
          arguments(funcTS(boolTS(), intTS(), blobTS()), tupleTS(boolTS(), intTS())));
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
      assertThat(struct.name()).isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> structTS(name, nlist())).throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(StructTS struct, NList<ItemSigS> expected) {
      assertThat(struct.fields()).isEqualTo(expected);
    }

    public static List<Arguments> struct_fields_cases() {
      return list(
          arguments(structTS("Person", nlist()), nlist()),
          arguments(
              structTS("Person", nlist(itemSigS(stringTS(), "field"))),
              nlist(itemSigS(stringTS(), "field"))),
          arguments(
              structTS("Person", nlist(itemSigS(stringTS(), "field"), itemSigS(intTS(), "field2"))),
              nlist(itemSigS(stringTS(), "field"), itemSigS(intTS(), "field2"))));
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = list(
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
        funcTS(stringTS(), blobTS()),
        funcTS(blobTS(), blobTS()));

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
      assertThat(type.elements()).isEqualTo(expected);
    }

    public static List<Arguments> tuple_items_cases() {
      return list(
          arguments(tupleTS(), list()),
          arguments(tupleTS(boolTS()), list(boolTS())),
          arguments(tupleTS(boolTS(), intTS()), list(boolTS(), intTS())));
    }
  }

  @Nested
  class _temp_var {
    @Test
    public void is_older_than_1_1() {
      assertThat(new TempVarS("1").isOlderThan(new TempVarS("1"))).isFalse();
    }

    @Test
    public void is_older_than_1_2() {
      assertThat(new TempVarS("1").isOlderThan(new TempVarS("2"))).isTrue();
    }

    @Test
    public void is_older_than_1_10() {
      assertThat(new TempVarS("1").isOlderThan(new TempVarS("10"))).isTrue();
    }

    @Test
    public void is_older_than_2_1() {
      assertThat(new TempVarS("2").isOlderThan(new TempVarS("1"))).isFalse();
    }
  }
}
