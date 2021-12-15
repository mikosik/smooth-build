package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.AnyTH;
import org.smoothbuild.db.object.type.val.BlobTH;
import org.smoothbuild.db.object.type.val.BoolTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IntTH;
import org.smoothbuild.db.object.type.val.NothingTH;
import org.smoothbuild.db.object.type.val.StringTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.db.object.type.val.VarH;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingTH implements TestingT<TypeH> {
  public static final TestingTH INSTANCE = new TestingTH();

  private static final TestingContext CONTEXT = new TestingContext();
  public static final CatDb FACTORY = CONTEXT.catDb();

  private static final ImmutableList<TypeH> BASE_TYPES = CONTEXT.catDb().baseTs();
  private static final ImmutableList<TypeH> ELEMENTARY_TYPES = BASE_TYPES;

  public static final AnyTH ANY = FACTORY.any();
  public static final BlobTH BLOB = FACTORY.blob();
  public static final BoolTH BOOL = FACTORY.bool();
  public static final IntTH INT = FACTORY.int_();
  public static final NothingTH NOTHING = FACTORY.nothing();
  public static final StringTH STRING = FACTORY.string();
  public static final TupleTH TUPLE = FACTORY.tuple(list(STRING, INT));
  public static final VarH A = FACTORY.var("A");
  public static final VarH B = FACTORY.var("B");
  public static final VarH X = FACTORY.var("X");
  public static final Side<TypeH> LOWER = FACTORY.lower();
  public static final Side<TypeH> UPPER = FACTORY.upper();

  public static final FuncTH STRING_GETTER_FUNCTION = FACTORY.func(STRING, list());
  public static final FuncTH TUPLE_GETTER_FUNCTION = FACTORY.func(TUPLE, list());
  public static final FuncTH STRING_MAP_FUNCTION = FACTORY.func(STRING, list(STRING));
  public static final FuncTH PERSON_MAP_FUNCTION = FACTORY.func(TUPLE, list(TUPLE));
  public static final FuncTH IDENTITY_FUNCTION = FACTORY.func(A, list(A));
  public static final FuncTH ARRAY_HEAD_FUNCTION = FACTORY.func(A, list(FACTORY.array(A)));
  public static final FuncTH ARRAY_LENGTH_FUNCTION = FACTORY.func(STRING, list(FACTORY.array(A)));

  public static final ImmutableList<TypeH> FUNCTION_TYPES = list(
      STRING_GETTER_FUNCTION,
      TUPLE_GETTER_FUNCTION,
      STRING_MAP_FUNCTION,
      PERSON_MAP_FUNCTION,
      IDENTITY_FUNCTION,
      ARRAY_HEAD_FUNCTION,
      ARRAY_LENGTH_FUNCTION);

  public static final ImmutableList<TypeH> ALL_TESTED_TYPES =
      ImmutableList.<TypeH>builder()
          .addAll(ELEMENTARY_TYPES)
          .addAll(FUNCTION_TYPES)
          .add(X)
          .build();

  @Override
  public TypingH typing() {
    return CONTEXT.typingH();
  }

  @Override
  public ImmutableList<TypeH> typesForBuildWideGraph() {
    return list(a(), b(), blob(), bool(), int_(), struct(), string());
  }

  @Override
  public ImmutableList<TypeH> elementaryTypes() {
    return ELEMENTARY_TYPES;
  }

  @Override
  public ImmutableList<TypeH> allTestedTypes() {
    return ALL_TESTED_TYPES;
  }

  @Override
  public TypeH array(TypeH elemT) {
    return FACTORY.array(elemT);
  }

  @Override
  public TypeH func(TypeH resT, ImmutableList<TypeH> params) {
    return FACTORY.func(resT, params);
  }

  @Override
  public TypeH any() {
    return ANY;
  }

  @Override
  public TypeH blob() {
    return BLOB;
  }

  @Override
  public TypeH bool() {
    return BOOL;
  }

  @Override
  public TypeH int_() {
    return INT;
  }

  @Override
  public TypeH nothing() {
    return NOTHING;
  }

  @Override
  public TypeH string() {
    return STRING;
  }

  @Override
  public TypeH struct() {
    return TUPLE;
  }

  @Override
  public TypeH a() {
    return A;
  }

  @Override
  public TypeH b() {
    return B;
  }

  @Override
  public TypeH x() {
    return X;
  }

  @Override
  public Side<TypeH> lower() {
    return LOWER;
  }

  @Override
  public Side<TypeH> upper() {
    return UPPER;
  }

  @Override
  public Bounds<TypeH> oneSideBound(Side<TypeH> side, TypeH type) {
    return FACTORY.oneSideBound(side, type);
  }
}
