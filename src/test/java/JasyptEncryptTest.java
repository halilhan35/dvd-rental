import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptEncryptTest {

    public static void main(String[] args) {

        StandardPBEStringEncryptor encryptor =
                new StandardPBEStringEncryptor();

        encryptor.setPassword("MasterSifremAnkara2026");
        encryptor.setAlgorithm("PBEWithMD5AndDES");


        String encrypted = encryptor.encrypt("1234");

        System.out.println(encrypted);
    }
}