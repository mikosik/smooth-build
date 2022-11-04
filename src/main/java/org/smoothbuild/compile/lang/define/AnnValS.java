package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class AnnValS extends ValS {
  private final AnnS ann;

  public AnnValS(AnnS ann, TypeS type, String name, Loc loc) {
    super(type, name, loc);
    this.ann = ann;
  }

  public AnnS ann() {
    return ann;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnnValS that
        && this.ann().equals(that.ann())
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(ann(), type(), name(), loc());
  }


  @Override
  public String toString() {
    var fieldsString = ann().toString() + "\n" + valFieldsToString();
    return "AnnVal(\n" + indent(fieldsString) + "\n)";
  }
}


