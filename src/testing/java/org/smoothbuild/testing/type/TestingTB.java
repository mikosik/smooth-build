package org.smoothbuild.testing.type;

import static org.smoothbuild.bytecode.type.val.VarSetB.toVarSetB;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.stream.Stream;

import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.AnyTB;
import org.smoothbuild.bytecode.type.val.BlobTB;
import org.smoothbuild.bytecode.type.val.BoolTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarSetB;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarSet;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingTB implements TestingT<TypeB> {
  public static final TestingTB INSTANCE = new TestingTB();

  private static final TestingContext CONTEXT = new TestingContext();
  public static final CatDb FACTORY = CONTEXT.catDb();

  public static final AnyTB ANY = FACTORY.any();
  public static final BlobTB BLOB = FACTORY.blob();
  public static final BoolTB BOOL = FACTORY.bool();
  public static final IntTB INT = FACTORY.int_();
  public static final NothingTB NOTHING = FACTORY.nothing();
  public static final StringTB STRING = FACTORY.string();
  public static final TupleTB TUPLE = FACTORY.tuple(list(STRING, INT));
  public static final VarB VAR_A = FACTORY.var("A");
  public static final VarB VAR_B = FACTORY.var("B");
  public static final VarB VAR_X = FACTORY.var("X");
  public static final VarB VAR_Y = FACTORY.var("Y");

  public static final FuncTB STRING_GETTER_FUNCTION = CONTEXT.funcTB(STRING, list());
  public static final FuncTB TUPLE_GETTER_FUNCTION = CONTEXT.funcTB(TUPLE, list());
  public static final FuncTB STRING_MAP_FUNCTION = CONTEXT.funcTB(STRING, list(STRING));
  public static final FuncTB PERSON_MAP_FUNCTION = CONTEXT.funcTB(TUPLE, list(TUPLE));
  public static final FuncTB IDENTITY_FUNCTION = CONTEXT.funcTB(VAR_A, list(VAR_A));
  public static final FuncTB ARRAY_HEAD_FUNCTION = CONTEXT.funcTB(VAR_A, list(FACTORY.array(VAR_A)));
  public static final FuncTB ARRAY_LENGTH_FUNCTION = CONTEXT.funcTB(STRING, list(FACTORY.array(VAR_A)));

  public static final ImmutableList<TypeB> FUNCTION_TYPES = list(
      STRING_GETTER_FUNCTION,
      TUPLE_GETTER_FUNCTION,
      STRING_MAP_FUNCTION,
      PERSON_MAP_FUNCTION,
      IDENTITY_FUNCTION,
      ARRAY_HEAD_FUNCTION,
      ARRAY_LENGTH_FUNCTION);

  private static final ImmutableList<TypeB> BASE_TYPES = CONTEXT.catDb().baseTs();
  private static final ImmutableList<TypeB> ELEMENTARY_TYPES = concat(BASE_TYPES, TUPLE);

  public static final ImmutableList<TypeB> ALL_TESTED_TYPES =
      ImmutableList.<TypeB>builder()
          .addAll(ELEMENTARY_TYPES)
          .addAll(FUNCTION_TYPES)
          .add(VAR_X)
          .build();

  @Override
  public TypingB typing() {
    return CONTEXT.typingB();
  }

  @Override
  public ImmutableList<TypeB> typesForBuildWideGraph() {
    return list(varA(), varB(), blob(), bool(), int_(), string(), tuple());
  }

  @Override
  public ImmutableList<TypeB> elementaryTypes() {
    return ELEMENTARY_TYPES;
  }

  @Override
  public ImmutableList<TypeB> allTestedTypes() {
    return ALL_TESTED_TYPES;
  }

  @Override
  public TypeB array(TypeB elemT) {
    return FACTORY.array(elemT);
  }

  @Override
  public TypeB func(VarSet<TypeB> tParams, TypeB resT, ImmutableList<TypeB> params) {
    return CONTEXT.funcTB((VarSetB)(Object) tParams, resT, params);
  }

  @Override
  public TypeB func(TypeB resT, ImmutableList<TypeB> params) {
    return CONTEXT.funcTB(resT, params);
  }

  @Override
  public TypeB any() {
    return ANY;
  }

  @Override
  public TypeB blob() {
    return BLOB;
  }

  @Override
  public TypeB bool() {
    return BOOL;
  }

  @Override
  public TypeB int_() {
    return INT;
  }

  @Override
  public TypeB nothing() {
    return NOTHING;
  }

  @Override
  public TypeB string() {
    return STRING;
  }

  @Override
  public boolean isStructSupported() {
    return false;
  }

  @Override
  public TypeB struct() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isTupleSupported() {
    return true;
  }

  @Override
  public TypeB tuple() {
    return TUPLE;
  }

  @Override
  public TypeB tuple(ImmutableList<TypeB> items) {
    return FACTORY.tuple(items);
  }

  @Override
  public TypeB varA() {
    return VAR_A;
  }

  @Override
  public TypeB varB() {
    return VAR_B;
  }

  @Override
  public TypeB varX() {
    return VAR_X;
  }

  @Override
  public TypeB varY() {
    return VAR_Y;
  }

  @Override
  public VarSet<TypeB> vs(Var... elements) {
    var varSetB = Stream.of(elements)
        .map(e -> (VarB) e)
        .collect(toVarSetB());
    return (VarSet<TypeB>)(Object) varSetB;
  }

  @Override
  public Sides<TypeB> oneSideBound(Side side, TypeB type) {
    return FACTORY.oneSideBound(side, type);
  }
}
