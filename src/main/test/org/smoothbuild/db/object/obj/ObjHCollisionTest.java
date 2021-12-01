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
        defFuncH(defFuncHT(intHT(), list()), intH(0)),
        defFuncH(defFuncHT(intHT(), list()), intH(1)),
        defFuncH(defFuncHT(stringHT(), list()), stringH("abc")),
        defFuncH(defFuncHT(intHT(), list(intHT())), intH(0)),
        defFuncH(defFuncHT(intHT(), list(stringHT())), intH(0)),
        natFuncH(natFuncHT(intHT(), list()), blob1, stringH("binary name")),
        natFuncH(natFuncHT(intHT(), list()), blob1, stringH("binary name 2")),
        natFuncH(natFuncHT(intHT(), list()), blob2, stringH("binary name")),
        natFuncH(natFuncHT(intHT(), list(intHT())), blob1, stringH("binary name")),
        natFuncH(natFuncHT(stringHT(), list()), blob1, stringH("binary name")),
        ifFuncH(),
        mapFuncH(),
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
