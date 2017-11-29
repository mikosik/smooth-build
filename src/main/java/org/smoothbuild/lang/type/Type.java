package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.Types.NOTHING;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

/**
 * Type in smooth language.
 */
public class Type {
  private final String name;
  private final Class<? extends Value> jType;

  protected Type(String name, Class<? extends Value> jType) {
    this.name = name;
    this.jType = jType;
  }

  public String name() {
    return name;
  }

  public Class<? extends Value> jType() {
    return jType;
  }

  public Value newValue(HashCode hash, HashedDb hashedDb) {
    try {
      return jType.getConstructor(HashCode.class, HashedDb.class).newInstance(hash, hashedDb);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      throw new UnsupportedOperationException("Can't create value for type " + toString() + ".", e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof HashedDbException) {
        throw (HashedDbException) e.getCause();
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public Type coreType() {
    return this;
  }

  public int coreDepth() {
    return 0;
  }

  public Type directConvertibleTo() {
    return null;
  }

  public boolean isAssignableFrom(Type type) {
    if (type.equals(NOTHING)) {
      return true;
    }
    if (this.equals(type)) {
      return true;
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).directConvertibleTo());
    }
    if (this instanceof ArrayType && type instanceof ArrayType) {
      return ((ArrayType) this).elemType().isAssignableFrom(((ArrayType) type).elemType());
    }
    return false;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (!Type.class.equals(object.getClass())) {
      return false;
    }
    Type that = (Type) object;
    return this.name.equals(that.name) && this.jType.equals(that.jType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, jType);
  }

  @Override
  public String toString() {
    return name;
  }
}
