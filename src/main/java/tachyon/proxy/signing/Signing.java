package tachyon.proxy.signing;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class Signing {
    private static PrivateKey privateKey;

    public static void init()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream inputStream = Signing.class.getResourceAsStream("/tachyon.key");
        byte[] encodedKey = new byte[inputStream.available()];
        inputStream.read(encodedKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        privateKey = keyFactory.generatePrivate(keySpec);
    }

    public static String encode(byte[] data)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        byte[] encodedData = data;
        Signature sig = Signature.getInstance("SHA512withECDSA");
        sig.initSign(privateKey);
        sig.update(encodedData);

        byte[] base64 = Base64.getEncoder().encode(sig.sign());
        return new String(base64);
    }
}
