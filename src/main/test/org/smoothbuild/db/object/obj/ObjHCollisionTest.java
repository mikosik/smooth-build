package org.smoothbuild.db.object.obj;

import static okio.ByteString.encodeUtf8;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjHCollisionTest extends TestingContext {
  @Test
  public void collisions() {
    HashMap<Hash, ObjH> map = new HashMap<>();
    for (ObjH obj : objectsH()) {
      Hash hash = obj.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two ObjH " + obj + " and " + map.get(hash) + ".");
      }
      map.put(hash, obj);
    }
  }

  private List<ObjH> objectsH() {
    BlobH blob1 = blobH(encodeUtf8("abc"));
    BlobH blob2 = blobH(encodeUtf8("def"));
    BlobH blobEmpty = blobH(ByteString.EMPTY);
    return List.of(
        blobEmpty,
        blob1,
        blob2,
        boolH(false),
        boolH(true),
        funcH(funcTH(intTH(), list()), intH(0)),
        funcH(funcTH(intTH(), list()), intH(1)),
        funcH(funcTH(stringTH(), list()), stringH("abc")),
        funcH(funcTH(intTH(), list(intTH())), intH(0)),
        funcH(funcTH(intTH(), list(stringTH())), intH(0)),
        invokeH(invokeCH(intTH(), list()), blob1, stringH("binary name")),
        invokeH(invokeCH(intTH(), list()), blob1, stringH("binary name 2")),
        invokeH(invokeCH(intTH(), list()), blob2, stringH("binary name")),
        invokeH(invokeCH(intTH(), list(intTH())), blob1, stringH("binary name")),
        invokeH(invokeCH(stringTH(), list()), blob1, stringH("binary name")),
        ifH(boolH(false), intH(1), intH(2)),
        ifH(boolH(false), intH(1), intH(3)),
        ifH(boolH(false), intH(3), intH(2)),
        ifH(boolH(true), intH(1), intH(2)),
        mapH(arrayH(intH(1)), funcH(funcTH(intTH(), list(intTH())), paramRefH(intTH(), 0))),
        mapH(arrayH(intH(1)), funcH(funcTH(intTH(), list(intTH())), intH(3))),
        mapH(arrayH(intH(2)), funcH(funcTH(intTH(), list(intTH())), paramRefH(intTH(), 0))),
        intH(0),
        intH(1),
        stringH("abc"),
        stringH("def"),
        tupleH(list()),
        tupleH(list(intH(0))),
        tupleH(list(intH(1))),
        tupleH(list(stringH("abc"))),
        tupleH(list(stringH("def")))
    );
  }
}
