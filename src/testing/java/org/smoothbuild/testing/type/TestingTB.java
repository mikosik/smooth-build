package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.cnst.AnyTB;
import org.smoothbuild.bytecode.type.cnst.BlobTB;
import org.smoothbuild.bytecode.type.cnst.BoolTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.IntTB;
import org.smoothbuild.bytecode.type.cnst.NothingTB;
import org.smoothbuild.bytecode.type.cnst.StringTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingTB {
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

  public static final FuncTB STRING_GETTER_FUNCTION = CONTEXT.funcTB(STRING, list());
  public static final FuncTB TUPLE_GETTER_FUNCTION = CONTEXT.funcTB(TUPLE, list());
  public static final FuncTB STRING_MAP_FUNCTION = CONTEXT.funcTB(STRING, list(STRING));
  public static final FuncTB PERSON_MAP_FUNCTION = CONTEXT.funcTB(TUPLE, list(TUPLE));

  public static final ImmutableList<TypeB> FUNCTION_TYPES = list(
      STRING_GETTER_FUNCTION,
      TUPLE_GETTER_FUNCTION,
      STRING_MAP_FUNCTION,
      PERSON_MAP_FUNCTION
  );

  private static final ImmutableList<TypeB> BASE_TYPES = CONTEXT.catDb().baseTs();
  private static final ImmutableList<TypeB> ELEMENTARY_TYPES = concat(BASE_TYPES, TUPLE);

  public TypeB array(TypeB elemT) {
    return FACTORY.array(elemT);
  }

  public TypeB func(TypeB resT, ImmutableList<TypeB> params) {
    return CONTEXT.funcTB(resT, params);
  }

  public TypeB any() {
    return ANY;
  }

  public TypeB blob() {
    return BLOB;
  }

  public TypeB bool() {
    return BOOL;
  }

  public TypeB int_() {
    return INT;
  }

  public TypeB nothing() {
    return NOTHING;
  }

  public TypeB string() {
    return STRING;
  }

  public TypeB tuple() {
    return TUPLE;
  }

  public TypeB tuple(ImmutableList<TypeB> items) {
    return FACTORY.tuple(items);
  }
}
