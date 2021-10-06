package org.smoothbuild.lang.base.type;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.lang.base.type.Sides.Side;

import com.google.common.collect.ImmutableList;

@Singleton
public class TypeFactory {
  private static final AnyType ANY = new AnyType();
  private static final BlobType BLOB = new BlobType();
  private static final BoolType BOOL = new BoolType();
  private static final IntType INT = new IntType();
  private static final NothingType NOTHING = new NothingType();
  private static final StringType STRING = new StringType();

  private final SpecDb specDb;
  private final Sides sides;

  @Inject
  public TypeFactory(SpecDb specDb) {
    this.specDb = specDb;
    this.sides = new Sides(any(), nothing());
  }

  public AnyType any() {
    return ANY;
  }

  public ArrayType array(Type elemType) {
    return new ArrayType(elemType);
  }

  public BlobType blob() {
    return BLOB;
  }

  public BoolType bool() {
    return BOOL;
  }

  public IntType int_() {
    return INT;
  }

  public NothingType nothing() {
    return NOTHING;
  }

  public StringType string() {
    return STRING;
  }

  public StructType struct(String name, ImmutableList<ItemSignature> fields) {
    return new StructType(name, ImmutableList.copyOf(fields));
  }

  public FunctionType function(Type resultType, Iterable<ItemSignature> parameters) {
    return new FunctionType(resultType, ImmutableList.copyOf(parameters));
  }

  public Sides.Side upper() {
    return sides.upper();
  }

  public Sides.Side lower() {
    return sides.lower();
  }

  public Bounds unbounded() {
    return new Bounds(NOTHING, ANY);
  }

  public Bounds oneSideBound(Side side, Type type) {
    return side.dispatch(
        () -> new Bounds(type, ANY),
        () -> new Bounds(NOTHING, type)
    );
  }
}
