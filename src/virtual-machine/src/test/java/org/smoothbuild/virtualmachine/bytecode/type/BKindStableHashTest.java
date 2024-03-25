package org.smoothbuild.virtualmachine.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BKindStableHashTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("hash_is_stable_cases")
  public void hash_is_stable(BKind kind, String hash) {
    assertThat(kind.hash()).isEqualTo(Hash.decode(hash));
  }

  public static List<Arguments> hash_is_stable_cases() throws BytecodeException {
    var t = new TestingVirtualMachine();
    return List.of(
        arguments(
            t.bBlobType(), "1406e05881e299367766d313e26c05564ec91bf721d31726bd6e46e60689539a"),
        arguments(
            t.bBoolType(), "9c12cfdc04c74584d787ac3d23772132c18524bc7ab28dec4219b8fc5b425f70"),
        arguments(
            t.bLambdaKind(), "6bc8397b7832905afad31d92d07c1d21f1302196b142d0b004549aec72962c07"),
        arguments(
            t.bFuncType(), "2efc8dc079b8693ac751fc181888b64bb53404b0c1aba4c54f9dafbe2e1ec254"),
        arguments(t.bIfKind(), "40899d393a16038b9b21d71dc422370e69c623d07e98b88b2c37b232506188a1"),
        arguments(t.bIntType(), "1cc3adea40ebfd94433ac004777d68150cce9db4c771bc7de1b297a7b795bbba"),
        arguments(t.bMapKind(), "21f8080bd94cf983b9082f0600b7eee7bbd93079ac0e736a966bfafb06089475"),
        arguments(
            t.bNativeFuncKind(),
            "338de86e29bcefc852824d0885e1aeab93a87a86018176359c947693448a3bce"),
        arguments(
            t.bStringType(), "c942a06c127c2c18022677e888020afb174208d299354f3ecfedb124a1f3fa45"),
        arguments(
            t.bTupleType(t.bBlobType()),
            "e6831749c04819dc01d5e7ae2c9c4e85eb54fcf3a2866270e679c908e9529efa"),
        arguments(
            t.bArrayType(t.bBlobType()),
            "8d0c2024fc9bedc6905b582e4f0f815eb16f1573f021332d7957ec12dfd1ba05"),
        arguments(
            t.bArrayType(t.bBoolType()),
            "d2f329dc9ff111fadbf744f3baa9ddd497ba6254cb04811bd765698164e8bf68"),
        arguments(
            t.bArrayType(t.bFuncType()),
            "9b57a68d69b3fe00d196de001f50b545c8ef53386fb42c98227b86b3ab966532"),
        arguments(
            t.bArrayType(t.bIntType()),
            "5a627db2a53ab5c02ea2aa798b79c6e4717c14afa433f39daf1acc45bd8e8c90"),
        arguments(
            t.bArrayType(t.bStringType()),
            "61e7ec0f483b43991e647a5dc0d365044a5f6ac299846a273b0b1ba94d615972"),
        arguments(
            t.bArrayType(t.bTupleType(t.bBlobType())),
            "3a9e7ba7be251f82ab23dd6cc6a439c4191484ce7f1727bf8698e39e61d98472"),
        arguments(
            t.bCallKind(t.bIntType()),
            "c23f38bb9870794f9873ee82daec1111bf5e6232b564a7b789ae4c75d608fa54"),
        arguments(
            t.bCombineKind(), "c41cd9d13e32154103a31db858ba533bbcb38fe2c2ddac7c5e4a0be50201083b"),
        arguments(
            t.bCombineKind(t.bIntType()),
            "801b0b192a0b269ada637c6433141a798950ee9750bccf45bb6328af2efdc365"),
        arguments(
            t.bOrderKind(t.bIntType()),
            "39ecc95bb839bb4fb1594d9707352a4801883c700003dce9f377cca0b6bfe367"),
        arguments(
            t.bPickKind(t.bIntType()),
            "54d87c95031d4493b38e2fc3dc714e47df9995d34cddb5e7a7d2c2ec75c0b433"),
        arguments(
            t.bSelectKind(t.bIntType()),
            "7d0bf9c43c4674d53c1bdfa7a8783ab8c9d608c46b90bfd9d99208e101c86677"),
        arguments(
            t.bReferenceKind(t.bIntType()),
            "caf484ac9e60b0b662b1aee490192288de4e44832518aaff3b78ebe732098560"));
  }
}