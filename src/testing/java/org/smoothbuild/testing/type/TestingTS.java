package org.smoothbuild.testing.type;

import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.Typing;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.lang.type.impl.AnyTS;
import org.smoothbuild.lang.type.impl.ArrayTS;
import org.smoothbuild.lang.type.impl.BlobTS;
import org.smoothbuild.lang.type.impl.BoolTS;
import org.smoothbuild.lang.type.impl.ClosedVarTS;
import org.smoothbuild.lang.type.impl.FuncTS;
import org.smoothbuild.lang.type.impl.IntTS;
import org.smoothbuild.lang.type.impl.NothingTS;
import org.smoothbuild.lang.type.impl.OpenVarTS;
import org.smoothbuild.lang.type.impl.StringTS;
import org.smoothbuild.lang.type.impl.StructTS;
import org.smoothbuild.lang.type.impl.TypeS;
import org.smoothbuild.lang.type.impl.TypeSF;
import org.smoothbuild.lang.type.impl.VarTS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTS implements TestingT<TypeS> {
  public static final TestingTS INSTANCE = new TestingTS();

  private static final TestingContext CONTEXT = new TestingContext();
  public static final TypeSF FACTORY = CONTEXT.typeSF();

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
  public static final OpenVarTS OPEN_A = oVar("A");
  public static final OpenVarTS OPEN_B = oVar("B");
  public static final OpenVarTS OPEN_X = oVar("X");
  public static final ClosedVarTS CLOSED_A = cVar("A");
  public static final ClosedVarTS CLOSED_B = cVar("B");
  public static final ClosedVarTS CLOSED_X = cVar("X");

  public static final ImmutableList<TypeS> ELEMENTARY_TYPES = ImmutableList.<TypeS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FuncTS STRING_GETTER_FUNCTION = f(STRING);
  public static final FuncTS PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FuncTS STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FuncTS PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FuncTS IDENTITY_FUNCTION = f(OPEN_A, OPEN_A);
  public static final FuncTS ARRAY_HEAD_FUNCTION = f(OPEN_A, a(OPEN_A));
  public static final FuncTS ARRAY_LENGTH_FUNCTION = f(STRING, a(OPEN_A));

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
          .add(OPEN_X)
          .build();

  public static ArrayTS a(TypeS elemT) {
    return FACTORY.array(elemT);
  }

  public static FuncTS f(TypeS resT) {
    return FACTORY.func(resT, list());
  }

  public static FuncTS f(TypeS resT, TypeS... paramTs) {
    return f(resT, list(paramTs));
  }

  public static FuncTS f(TypeS resT, ImmutableList<TypeS> paramTs) {
    return FACTORY.func(resT, paramTs);
  }

  public static OpenVarTS oVar(String a) {
    return FACTORY.oVar(a);
  }

  public static ClosedVarTS cVar(String a) {
    return FACTORY.cVar(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return FACTORY.struct(name, fields);
  }

  @Override
  public Sides<TypeS> oneSideBound(Side side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public static VarBounds<TypeS> vb(
      VarTS var1, Side side1, TypeS bound1,
      VarTS var2, Side side2, TypeS bound2) {
    return CONTEXT.vbS(var1, side1, bound1, var2, side2, bound2);
  }

  public static VarBounds<TypeS> vb(VarTS var, Side side, TypeS bound) {
    return CONTEXT.vbS(var, side, bound);
  }

  public static VarBounds<TypeS> vb() {
    return CONTEXT.vbS();
  }

  @Override
  public Typing<TypeS> typing() {
    return CONTEXT.typingS();
  }

  @Override
  public ImmutableList<TypeS> typesForBuildWideGraph() {
    return list(oa(), ob(), blob(), bool(), int_(), struct(), string());
  }

  @Override
  public ImmutableList<TypeS> elementaryTypes() {
    return ELEMENTARY_TYPES;
  }

  @Override
  public ImmutableList<TypeS> allTestedTypes() {
    return ALL_TESTED_TYPES;
  }

  @Override
  public TypeS array(TypeS elemT) {
    return a(elemT);
  }

  @Override
  public TypeS func(TypeS resT, ImmutableList<TypeS> params) {
    return TestingTS.f(resT, params);
  }

  @Override
  public TypeS any() {
    return ANY;
  }

  @Override
  public TypeS blob() {
    return BLOB;
  }

  @Override
  public TypeS bool() {
    return BOOL;
  }

  @Override
  public TypeS int_() {
    return INT;
  }

  @Override
  public TypeS nothing() {
    return NOTHING;
  }

  @Override
  public TypeS string() {
    return STRING;
  }

  @Override
  public boolean isStructSupported() {
    return true;
  }

  @Override
  public TypeS struct() {
    return PERSON;
  }

  @Override
  public boolean isTupleSupported() {
    return false;
  }

  @Override
  public TypeS tuple() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TypeS tuple(ImmutableList<TypeS> items) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TypeS oa() {
    return OPEN_A;
  }

  @Override
  public TypeS ob() {
    return OPEN_B;
  }

  @Override
  public TypeS ox() {
    return OPEN_X;
  }

  @Override
  public TypeS ca() {
    return CLOSED_A;
  }

  @Override
  public TypeS cb() {
    return CLOSED_B;
  }

  @Override
  public TypeS cx() {
    return CLOSED_X;
  }
}
