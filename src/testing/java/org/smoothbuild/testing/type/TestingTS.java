package org.smoothbuild.testing.type;

import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.impl.FuncTS.calculateFuncVars;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.VarBoundsS;
import org.smoothbuild.lang.type.impl.AnyTS;
import org.smoothbuild.lang.type.impl.ArrayTS;
import org.smoothbuild.lang.type.impl.BlobTS;
import org.smoothbuild.lang.type.impl.BoolTS;
import org.smoothbuild.lang.type.impl.FuncTS;
import org.smoothbuild.lang.type.impl.IntTS;
import org.smoothbuild.lang.type.impl.NothingTS;
import org.smoothbuild.lang.type.impl.StringTS;
import org.smoothbuild.lang.type.impl.StructTS;
import org.smoothbuild.lang.type.impl.TypeS;
import org.smoothbuild.lang.type.impl.TypeSF;
import org.smoothbuild.lang.type.impl.TypingS;
import org.smoothbuild.lang.type.impl.VarS;
import org.smoothbuild.lang.type.impl.VarSetS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTS {
  public static final TestingTS INSTANCE = new TestingTS();

  private static final TestingContext CONTEXT = new TestingContext();
  public static final TypeSF FACTORY = CONTEXT.typeFS();

  public static final ImmutableList<TypeS> BASE_TYPES = ImmutableList.copyOf(FACTORY.baseTs());
  public static final ImmutableList<TypeS> INFERABLE_BASE_TYPES = FACTORY.inferableBaseTs();

  public static final AnyTS ANY = FACTORY.any();
  public static final BlobTS BLOB = FACTORY.blob();
  public static final BoolTS BOOL = FACTORY.bool();
  public static final IntTS INT = FACTORY.int_();
  public static final NothingTS NOTHING = FACTORY.nothing();
  public static final StringTS STRING = FACTORY.string();
  public static final StructTS PERSON = struct("Person",
      nList(itemSigS(STRING, "firstName"), itemSigS(STRING, "lastName")));
  public static final StructTS FLAG = struct("Flag", nList(itemSigS(BOOL, "flab")));
  public static final StructTS DATA = struct("Data", nList(itemSigS(BLOB, "data")));
  public static final VarS VAR_A = var("A");
  public static final VarS VAR_B = var("B");
  public static final VarS VAR_X = var("X");
  public static final VarS VAR_Y = var("Y");

  public static final ImmutableList<TypeS> ELEMENTARY_TYPES = ImmutableList.<TypeS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FuncTS STRING_GETTER_FUNCTION = f(STRING);
  public static final FuncTS PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FuncTS STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FuncTS PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FuncTS IDENTITY_FUNCTION = f(VAR_A, VAR_A);
  public static final FuncTS ARRAY_HEAD_FUNCTION = f(VAR_A, a(VAR_A));
  public static final FuncTS ARRAY_LENGTH_FUNCTION = f(STRING, a(VAR_A));

  public static final ImmutableList<TypeS> FUNCTION_TYPES =
      list(
          STRING_GETTER_FUNCTION,
          PERSON_GETTER_FUNCTION,
          STRING_MAP_FUNCTION,
          PERSON_MAP_FUNCTION,
          IDENTITY_FUNCTION,
          ARRAY_HEAD_FUNCTION,
          ARRAY_LENGTH_FUNCTION);

  public static final ImmutableList<TypeS> ALL_TESTED_TYPES =
      ImmutableList.<TypeS>builder()
          .addAll(ELEMENTARY_TYPES)
          .addAll(FUNCTION_TYPES)
          .add(VAR_X)
          .build();

  public static ArrayTS a(TypeS elemT) {
    return FACTORY.array(elemT);
  }

  public static FuncTS f(TypeS resT) {
    return f(resT, list());
  }

  public static FuncTS f(TypeS resT, TypeS... paramTs) {
    return f(resT, list(paramTs));
  }

  public static FuncTS f(TypeS resT, ImmutableList<TypeS> paramTs) {
    var tParams = calculateFuncVars(resT, paramTs);
    return f(tParams, resT, paramTs);
  }

  public static FuncTS f(VarSetS tParams, TypeS resT, ImmutableList<TypeS> paramTs) {
    return FACTORY.func(tParams, resT, paramTs);
  }

  public static VarS var(String a) {
    return FACTORY.var(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return FACTORY.struct(name, fields);
  }

  public Sides<TypeS> oneSideBound(Side side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public TypingS typing() {
    return CONTEXT.typingS();
  }

  public ImmutableList<TypeS> typesForBuildWideGraph() {
    return list(varA(), varB(), blob(), bool(), int_(), struct(), string());
  }

  public ImmutableList<TypeS> elementaryTypes() {
    return ELEMENTARY_TYPES;
  }

  public ImmutableList<TypeS> allTestedTypes() {
    return ALL_TESTED_TYPES;
  }

  public TypeS array(TypeS elemT) {
    return a(elemT);
  }

  public TypeS func(TypeS resT, ImmutableList<TypeS> params) {
    return TestingTS.f(resT, params);
  }

  public TypeS func(VarSetS tParams, TypeS resT, ImmutableList<TypeS> params) {
    return TestingTS.f(tParams, resT, params);
  }

  public TypeS any() {
    return ANY;
  }

  public TypeS blob() {
    return BLOB;
  }

  public TypeS bool() {
    return BOOL;
  }

  public TypeS int_() {
    return INT;
  }

  public TypeS nothing() {
    return NOTHING;
  }

  public TypeS string() {
    return STRING;
  }

  public boolean isStructSupported() {
    return true;
  }

  public TypeS struct() {
    return PERSON;
  }

  public boolean isTupleSupported() {
    return false;
  }

  public TypeS tuple() {
    throw new UnsupportedOperationException();
  }

  public TypeS tuple(ImmutableList<TypeS> items) {
    throw new UnsupportedOperationException();
  }

  public VarS varA() {
    return VAR_A;
  }

  public VarS varB() {
    return VAR_B;
  }

  public VarS varX() {
    return VAR_X;
  }

  public VarS varY() {
    return VAR_Y;
  }
}
