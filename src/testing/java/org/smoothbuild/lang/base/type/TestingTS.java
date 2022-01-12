package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.VarBounds;
import org.smoothbuild.lang.base.type.impl.AnyTS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.BlobTS;
import org.smoothbuild.lang.base.type.impl.BoolTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.StringTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VarTS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTS implements TestingT<TypeS> {
  public static final TestingTS INSTANCE = new TestingTS();

  private static final TestingContext CONTEXT = new TestingContext();
  public static final TypeFactoryS FACTORY = CONTEXT.typeFactoryS();

  public static final ImmutableList<TypeS> BASE_TYPES = ImmutableList.copyOf(FACTORY.baseTs());
  public static final ImmutableList<TypeS> INFERABLE_BASE_TYPES = FACTORY.inferableBaseTs();

  public static final AnyTS ANY = FACTORY.any();
  public static final BlobTS BLOB = FACTORY.blob();
  public static final BoolTS BOOL = FACTORY.bool();
  public static final IntTS INT = FACTORY.int_();
  public static final NothingTS NOTHING = FACTORY.nothing();
  public static final StringTS STRING = FACTORY.string();
  public static final StructTS PERSON = struct("Person",
      nList(itemSigS("firstName", STRING), itemSigS("lastName", STRING)));
  public static final StructTS FLAG = struct("Flag", nList(itemSigS("flab", BOOL)));
  public static final StructTS DATA = struct("Data", nList(itemSigS("data", BLOB)));
  public static final VarTS A = var("A");
  public static final VarTS B = var("B");
  public static final VarTS C = var("C");
  public static final VarTS D = var("D");
  public static final VarTS X = var("X");
  public static final Side<TypeS> LOWER = FACTORY.lower();
  public static final Side<TypeS> UPPER = FACTORY.upper();

  public static final ImmutableList<TypeS> ELEMENTARY_TYPES = ImmutableList.<TypeS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final FuncTS STRING_GETTER_FUNCTION = f(STRING);
  public static final FuncTS PERSON_GETTER_FUNCTION = f(PERSON);
  public static final FuncTS STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final FuncTS PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final FuncTS IDENTITY_FUNCTION = f(A, A);
  public static final FuncTS ARRAY_HEAD_FUNCTION = f(A, a(A));
  public static final FuncTS ARRAY_LENGTH_FUNCTION = f(STRING, a(A));

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
          .add(X)
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

  public static VarTS var(String a) {
    return FACTORY.oVar(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return FACTORY.struct(name, fields);
  }

  @Override
  public Bounds<TypeS> oneSideBound(Side<TypeS> side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public static VarBounds<TypeS> vb(
      VarTS var1, Side<TypeS> side1, TypeS bound1,
      VarTS var2, Side<TypeS> side2, TypeS bound2) {
    return CONTEXT.vbS(var1, side1, bound1, var2, side2, bound2);
  }

  public static VarBounds<TypeS> vb(VarTS var, Side<TypeS> side, TypeS bound) {
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
    return list(a(), b(), blob(), bool(), int_(), struct(), string());
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
  public TypeS a() {
    return A;
  }

  @Override
  public TypeS b() {
    return B;
  }

  @Override
  public TypeS x() {
    return X;
  }

  @Override
  public Side<TypeS> lower() {
    return LOWER;
  }

  @Override
  public Side<TypeS> upper() {
    return UPPER;
  }
}
