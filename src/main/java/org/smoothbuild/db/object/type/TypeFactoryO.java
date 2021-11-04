package org.smoothbuild.db.object.type;

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
import org.smoothbuild.lang.base.type.api.BaseType;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public interface TypeFactoryO extends TypeFactory {
  @Override
  public ImmutableSet<BaseType> inferableBaseTypes();

  @Override
  public ImmutableSet<BaseType> baseTypes();

  @Override
  public Bounds unbounded();

  @Override
  public Bounds oneSideBound(Side side, Type type);

  @Override
  public Side upper();

  @Override
  public Side lower();

  @Override
  public AnyOType any();

  @Override
  public ArrayOType array(Type elemType);

  @Override
  public BlobOType blob();

  @Override
  public BoolOType bool();

  @Override
  public LambdaOType function(Type result, ImmutableList<? extends Type> parameters);

  @Override
  public IntOType int_();

  @Override
  public NothingOType nothing();

  @Override
  public StringOType string();

  @Override
  public StructOType struct(String name, NamedList<? extends Type> fields);

  @Override
  public VariableOType variable(String name);
}
