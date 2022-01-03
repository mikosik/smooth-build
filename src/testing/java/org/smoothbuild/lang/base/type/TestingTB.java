package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

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
import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
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
  public static final VarTB A = FACTORY.var("A");
  public static final VarTB B = FACTORY.var("B");
  public static final VarTB X = FACTORY.var("X");
  public static final Side<TypeB> LOWER = FACTORY.lower();
  public static final Side<TypeB> UPPER = FACTORY.upper();

  public static final FuncTB STRING_GETTER_FUNCTION = FACTORY.func(STRING, list());
  public static final FuncTB TUPLE_GETTER_FUNCTION = FACTORY.func(TUPLE, list());
  public static final FuncTB STRING_MAP_FUNCTION = FACTORY.func(STRING, list(STRING));
  public static final FuncTB PERSON_MAP_FUNCTION = FACTORY.func(TUPLE, list(TUPLE));
  public static final FuncTB IDENTITY_FUNCTION = FACTORY.func(A, list(A));
  public static final FuncTB ARRAY_HEAD_FUNCTION = FACTORY.func(A, list(FACTORY.array(A)));
  public static final FuncTB ARRAY_LENGTH_FUNCTION = FACTORY.func(STRING, list(FACTORY.array(A)));

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
          .add(X)
          .build();

  @Override
  public TypingB typing() {
    return CONTEXT.typingB();
  }

  @Override
  public ImmutableList<TypeB> typesForBuildWideGraph() {
    return list(a(), b(), blob(), bool(), int_(), string(), tuple());
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
  public TypeB func(TypeB resT, ImmutableList<TypeB> params) {
    return FACTORY.func(resT, params);
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
  public TypeB a() {
    return A;
  }

  @Override
  public TypeB b() {
    return B;
  }

  @Override
  public TypeB x() {
    return X;
  }

  @Override
  public Side<TypeB> lower() {
    return LOWER;
  }

  @Override
  public Side<TypeB> upper() {
    return UPPER;
  }

  @Override
  public Bounds<TypeB> oneSideBound(Side<TypeB> side, TypeB type) {
    return FACTORY.oneSideBound(side, type);
  }
}
