package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.cnst.BlobTB;
import org.smoothbuild.bytecode.type.cnst.BoolTB;
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

  public static final BlobTB BLOB = FACTORY.blob();
  public static final BoolTB BOOL = FACTORY.bool();
  public static final IntTB INT = FACTORY.int_();
  public static final NothingTB NOTHING = FACTORY.nothing();
  public static final StringTB STRING = FACTORY.string();
  public static final TupleTB TUPLE = FACTORY.tuple(list(STRING, INT));

  public TypeB array(TypeB elemT) {
    return FACTORY.array(elemT);
  }

  public TypeB func(TypeB resT, ImmutableList<TypeB> params) {
    return CONTEXT.funcTB(resT, params);
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
