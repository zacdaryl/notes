# Android对称、非对称加解密

本篇记录下Android平台下对称加密AES、非对称加密RSA算法的具体实现，以前只是知道，或者调用别人写好的方法，这次实现了一遍，记录下，加深下印象，也为需要的朋友节省点时间。

## AES

先简单了解下AES，这里摘录下维基百科对 [高级加密标准]([https://zh.wikipedia.org/wiki/%E9%AB%98%E7%BA%A7%E5%8A%A0%E5%AF%86%E6%A0%87%E5%87%86](https://zh.wikipedia.org/wiki/高级加密标准)) 的说明:

> **高级加密标准**（英语：**A**dvanced **E**ncryption **S**tandard，[缩写](https://zh.wikipedia.org/wiki/缩写)：AES），在[密码学](https://zh.wikipedia.org/wiki/密码学)中又称**Rijndael加密法**，是[美国联邦政府](https://zh.wikipedia.org/wiki/美国联邦政府)采用的一种[区块加密](https://zh.wikipedia.org/wiki/區塊加密)标准。这个标准用来替代原先的[DES](https://zh.wikipedia.org/wiki/DES)，已经被多方分析且广为全世界所使用。经过五年的甄选流程，高级加密标准由[美国国家标准与技术研究院](https://zh.wikipedia.org/wiki/美国国家标准与技术研究院)（NIST）于2001年11月26日发布于FIPS PUB 197，并在2002年5月26日成为有效的标准。2006年，高级加密标准已然成为[对称密钥加密](https://zh.wikipedia.org/wiki/对称密钥加密)中最流行的[算法](https://zh.wikipedia.org/wiki/演算法)之一。
>
> 该算法为[比利时](https://zh.wikipedia.org/wiki/比利时)密码学家Joan Daemen和Vincent Rijmen所设计，结合两位作者的名字，以Rijndael为名投稿高级加密标准的甄选流程。（Rijndael的发音近于"Rhine doll"）

实现AES加密算法：

1. 创建Cipher对象

    `val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")`

2. 根据传入的密钥key（建议16位长），创建SecretKeySpec对象

   `val keySpec = SecretKeySpec(key.toByteArray(), "AES")`

3. 初始化cipher

   `cipher.init(Cipher.ENCRYPT_MODE, keySpec)`

4. 对明文进行加密，并将结果转换为Base64串

   ```kotlin
   val cipherByteArray = cipher.doFinal(plainText.toByteArray())
   String(Base64.encode(cipherByteArray, Base64.NO_WRAP))
   ```

虽然就这简单四步，但在第二步是查了不少资料，因为不确定init传入的Key对象具体该怎么生成。

AES解密算法实现是一样的

```kotlin
val keySpec = SecretKeySpec(key.toByteArray(), "AES")
val cipher = Cipher.getInstance(transformation)
cipher.init(Cipher.DECRYPT_MODE, keySpec)
val base64DecodeArray = Base64.decode(cipherText, Base64.NO_WRAP)
val plainByteArray = cipher.doFinal(base64DecodeArray)
```



## RSA

还是先了解下 [百科]([https://zh.wikipedia.org/wiki/RSA%E5%8A%A0%E5%AF%86%E6%BC%94%E7%AE%97%E6%B3%95](https://zh.wikipedia.org/wiki/RSA加密演算法))

> **RSA加密算法**是一种[非对称加密算法](https://zh.wikipedia.org/wiki/非对称加密演算法)，在[公开密钥加密](https://zh.wikipedia.org/wiki/公开密钥加密)和[电子商业](https://zh.wikipedia.org/wiki/电子商业)中被广泛使用。RSA是1977年由[罗纳德·李维斯特](https://zh.wikipedia.org/wiki/罗纳德·李维斯特)（Ron Rivest）、[阿迪·萨莫尔](https://zh.wikipedia.org/wiki/阿迪·萨莫尔)（Adi Shamir）和[伦纳德·阿德曼](https://zh.wikipedia.org/wiki/伦纳德·阿德曼)（Leonard Adleman）一起提出的。当时他们三人都在[麻省理工学院](https://zh.wikipedia.org/wiki/麻省理工学院)工作。RSA就是他们三人姓氏开头字母拼在一起组成的。[[1\]](https://zh.wikipedia.org/wiki/RSA加密演算法#cite_note-1)
>
> 1973年，在英国政府通讯总部工作的数学家克利福德·柯克斯（Clifford Cocks）在一个内部文件中提出了一个与之等效的算法，但该算法被列入机密，直到1997年才得到公开。[[2\]](https://zh.wikipedia.org/wiki/RSA加密演算法#cite_note-2)
>
> 对极大整数做[因数分解](https://zh.wikipedia.org/wiki/因数分解)的难度决定了RSA算法的可靠性。换言之，对一极大整数做因数分解愈困难，RSA算法愈可靠。假如有人找到一种快速因数分解的算法的话，那么用RSA加密的信息的可靠性就会极度下降。但找到这样的算法的可能性是非常小的。今天只有短的RSA钥匙才可能被强力方式破解。到当前为止，世界上还没有任何可靠的攻击RSA算法的方式。只要其钥匙的长度足够长，用RSA加密的信息实际上是不能被破解的。
>
> 1983年9月12日麻省理工学院在[美国](https://zh.wikipedia.org/wiki/美国)为RSA算法申请了[专利](https://zh.wikipedia.org/wiki/专利)。[[3\]](https://zh.wikipedia.org/wiki/RSA加密演算法#cite_note-3)这个专利于2000年9月21日失效。[[4\]](https://zh.wikipedia.org/wiki/RSA加密演算法#cite_note-4)由于该算法在申请专利前就已经被发表了[[5\]](https://zh.wikipedia.org/wiki/RSA加密演算法#cite_note-SIAM-5)，在世界上大多数其它地区这个专利权不被承认。

由于实现RSA算法也是用Cipher对象，具体实现大同小异，就不分步说明了

```kotlin
 // 公钥加密
fun encrypt(publicKey: String, plainText: String): String {
    val byteArray = Base64.decode(publicKey, Base64.NO_WRAP)
    val keyFactory = KeyFactory.getInstance(ALGORITHM)
    val keySpec = X509EncodedKeySpec(byteArray)
    val pubKey = keyFactory.generatePublic(keySpec) as PublicKey

    val cipher = Cipher.getInstance(transformation)
    cipher.init(Cipher.ENCRYPT_MODE, pubKey)

    val cipherByteArray = cipher.doFinal(plainText.toByteArray())
    val base64 = Base64.encode(cipherByteArray, Base64.NO_WRAP)

    return String(base64)
}

// 私钥解密
fun decrypt(privateKey: String, cipherText: String): String {
    val base64Buffer = Base64.decode(privateKey, Base64.NO_WRAP)
    val keySpec = PKCS8EncodedKeySpec(base64Buffer)
    val keyFactory = KeyFactory.getInstance(ALGORITHM)
    val priKey = keyFactory.generatePrivate(keySpec) as PrivateKey

    val cipher = Cipher.getInstance(transformation)
    cipher.init(Cipher.DECRYPT_MODE, priKey)

    val rawByteArray = Base64.decode(cipherText.toByteArray(), Base64.NO_WRAP)
    val plainBuffer = cipher.doFinal(rawByteArray)
    return String(plainBuffer)
}

 //生成公私钥对
fun generateKeyPair(): Pair<String, String> {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    val keypair = keyPairGenerator.genKeyPair()

    val private = Base64.getEncoder().encodeToString(keypair.private.encoded)
    val public = Base64.getEncoder().encodeToString(keypair.public.encoded)

    return Pair(private, public)
}
```