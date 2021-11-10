package org.smoothbuild.db.object.type;

import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.AnyTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.db.object.type.val.VariableH;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TypeFactory;

import com.google.common.collect.ImmutableList;

public interface TypeFactoryH extends TypeFactory<TypeHV> {
  @Override
  public Bounds<TypeHV> unbounded();

  @Override
  public Bounds<TypeHV> oneSideBound(Side<TypeHV> side, TypeHV type);

  @Override
  public Side<TypeHV> upper();

  @Override
  public Side<TypeHV> lower();

  @Override
  public AnyTypeH any();

  @Override
  public ArrayTypeH array(TypeHV elemType);

  @Override
  public BlobTypeH blob();

  @Override
  public BoolTypeH bool();

  @Override
  public FunctionTypeH function(TypeHV result, ImmutableList<TypeHV> parameters);

  @Override
  public IntTypeH int_();

  @Override
  public NothingTypeH nothing();

  @Override
  public StringTypeH string();

  public TupleTypeH tuple(ImmutableList<TypeHV> items);

  @Override
  public VariableH variable(String name);
}
