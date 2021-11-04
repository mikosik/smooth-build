package org.smoothbuild.db.object.type;

import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.val.AnyOType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.BlobOType;
import org.smoothbuild.db.object.type.val.BoolOType;
import org.smoothbuild.db.object.type.val.IntOType;
import org.smoothbuild.db.object.type.val.LambdaOType;
import org.smoothbuild.db.object.type.val.NothingOType;
import org.smoothbuild.db.object.type.val.StringOType;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.db.object.type.val.VariableOType;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public interface TypeFactoryO extends TypeFactory<TypeV> {
  @Override
  public Bounds unbounded();

  @Override
  public Bounds oneSideBound(Side side, TypeV type);

  @Override
  public Side upper();

  @Override
  public Side lower();

  @Override
  public AnyOType any();

  @Override
  public ArrayOType array(TypeV elemType);

  @Override
  public BlobOType blob();

  @Override
  public BoolOType bool();

  @Override
  public LambdaOType function(TypeV result, ImmutableList<? extends TypeV> parameters);

  @Override
  public IntOType int_();

  @Override
  public NothingOType nothing();

  @Override
  public StringOType string();

  @Override
  public StructOType struct(String name, NamedList<? extends TypeV> fields);

  @Override
  public VariableOType variable(String name);
}
