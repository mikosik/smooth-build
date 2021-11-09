package org.smoothbuild.db.object.type;

import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.val.AnyTypeO;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.db.object.type.val.BlobTypeO;
import org.smoothbuild.db.object.type.val.BoolTypeO;
import org.smoothbuild.db.object.type.val.IntTypeO;
import org.smoothbuild.db.object.type.val.FunctionTypeO;
import org.smoothbuild.db.object.type.val.NothingTypeO;
import org.smoothbuild.db.object.type.val.StringTypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;
import org.smoothbuild.db.object.type.val.VariableO;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TypeFactory;

import com.google.common.collect.ImmutableList;

public interface TypeFactoryO extends TypeFactory<TypeV> {
  @Override
  public Bounds<TypeV> unbounded();

  @Override
  public Bounds<TypeV> oneSideBound(Side<TypeV> side, TypeV type);

  @Override
  public Side<TypeV> upper();

  @Override
  public Side<TypeV> lower();

  @Override
  public AnyTypeO any();

  @Override
  public ArrayTypeO array(TypeV elemType);

  @Override
  public BlobTypeO blob();

  @Override
  public BoolTypeO bool();

  @Override
  public FunctionTypeO function(TypeV result, ImmutableList<TypeV> parameters);

  @Override
  public IntTypeO int_();

  @Override
  public NothingTypeO nothing();

  @Override
  public StringTypeO string();

  @Override
  public VariableO variable(String name);

  public TupleTypeO tuple(ImmutableList<TypeV> items);
}
