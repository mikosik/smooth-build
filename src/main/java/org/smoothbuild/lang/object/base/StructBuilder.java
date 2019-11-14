package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.StructType;

public class StructBuilder {
  private final StructType type;
  private final ObjectsDb objectsDb;
  private final Map<String, SObject> fields;

  public StructBuilder(StructType type, ObjectsDb objectsDb) {
    this.type = type;
    this.objectsDb = objectsDb;
    this.fields = new HashMap<>();
  }

  public StructBuilder set(String name, SObject object) {
    checkArgument(type.fields().containsKey(name), name);
    checkArgument(type.fields().get(name).type().equals(object.type()));
    fields.put(name, object);
    return this;
  }

  public Struct build() {
    Set<String> fieldNames = type.fields().keySet();
    List<String> unspecifiedNames = fieldNames
        .stream()
        .filter(e -> !fields.containsKey(e))
        .collect(toImmutableList());
    if (0 < unspecifiedNames.size()) {
      throw new IllegalStateException(
          "Field " + unspecifiedNames.get(0) + " hasn't been specified.");
    }
    List<SObject> objects = fieldNames
        .stream()
        .map(fields::get)
        .collect(toImmutableList());
    return wrapException(() -> objectsDb.newStructSObject(type, objects));
  }
}
