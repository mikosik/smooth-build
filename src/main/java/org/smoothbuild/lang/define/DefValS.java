package org.smoothbuild.lang.define;

import java.util.Objects;

import org.smoothbuild.lang.obj.ObjS;
import org.smoothbuild.lang.type.TypeS;

/**
 * Defined value (one that has a body).
 *
 * This class is immutable.
 */
public final class DefValS extends ValS {
  private final ObjS body;

  public DefValS(TypeS type, ModPath modPath, String name, ObjS body, Loc loc) {
    super(type, modPath, name, loc);
    this.body = body;
  }

  public ObjS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefValS that
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.body().equals(that.body())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), body(), loc());
  }

  @Override
  public String toString() {
    return "DefVal(`" + type().name() + " " + name() + " = " + body + "`)";
  }
}


