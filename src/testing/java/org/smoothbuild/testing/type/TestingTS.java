package org.smoothbuild.testing.type;

import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

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
  public static final VarS VAR_C = var("C");
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
    return FACTORY.func(resT, paramTs);
  }

  public static VarS var(String a) {
    return FACTORY.var(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return FACTORY.struct(name, fields);
  }

  public Bounds<TypeS> oneSideBound(Side side, TypeS type) {
    return FACTORY.oneSideBound(side, type);
  }

  public Bounds<TypeS> bounds() {
    return bounds(nothing(), any());
  }

  public Bounds<TypeS> bounds(TypeS lower, TypeS upper) {
    return new Bounds<>(lower, upper);
  }

  public TypingS typing() {
    return CONTEXT.typingS();
  }

  public ImmutableList<TypeS> typesForBuildWideGraph() {
    return list(varA(), varB(), blob(), bool(), int_(), struct(), string());
  }

  public ImmutableList<TypeS> baseTypes() {
    return BASE_TYPES;
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

  public TypeS join(List<TypeS> elems) {
    return switch (elems.size()) {
      case 0 -> throw new IllegalArgumentException();
      case 1 -> elems.get(0);
      default -> {
        TypeS result = nothing();
        for (TypeS elem : elems) {
          result = JoinTS.join(result, elem);
        }
        yield  result;
      }
    };
  }

  public TypeS join(TypeS a, TypeS b) {
    return JoinTS.join(a, b);
  }

  public TypeS meet(List<TypeS> elems) {
    return switch (elems.size()) {
      case 0 -> throw new IllegalArgumentException();
      case 1 -> elems.get(0);
      default -> {
        TypeS result = any();
        for (TypeS elem : elems) {
          result = MeetTS.meet(result, elem);
        }
        yield  result;
      }
    };
  }

  public TypeS meet(TypeS a, TypeS b) {
    return MeetTS.meet(a, b);
  }

  public TypeS func(TypeS resT, ImmutableList<TypeS> params) {
    return TestingTS.f(resT, params);
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

  public VarS var0() {
    return var("_0");
  }

  public VarS var1() {
    return var("_1");
  }

  public VarS varA() {
    return VAR_A;
  }

  public VarS varB() {
    return VAR_B;
  }

  public VarS varC() {
    return VAR_C;
  }

  public VarS varX() {
    return VAR_X;
  }

  public VarS varY() {
    return VAR_Y;
  }

  public TypeSF factory() {
    return FACTORY;
  }
}
