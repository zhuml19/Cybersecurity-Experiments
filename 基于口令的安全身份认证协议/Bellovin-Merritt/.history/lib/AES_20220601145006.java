import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AES extends Algorithm {
    Key key;

    public AES() {
        super("AES");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        key = keyGenerator.generateKey();
    }

    public void setKey(byte[] key) {
        this.key = new SecretKeySpec(key, "AES");
    }

    public byte[] getKey() {
        return key.getEncoded();
    }
}
