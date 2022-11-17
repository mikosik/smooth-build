package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class AnnValueS extends NamedValueS {
  private final AnnS ann;

  public AnnValueS(AnnS ann, SchemaS schema, String name, Loc loc) {
    super(schema, name, loc);
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
    return object instanceof AnnValueS that
        && this.ann().equals(that.ann())
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(ann(), schema(), name(), loc());
  }


  @Override
  public String toString() {
    var fieldsString = ann().toString() + "\n" + fieldsToString();
    return "AnnVal(\n" + indent(fieldsString) + "\n)";
  }
}


