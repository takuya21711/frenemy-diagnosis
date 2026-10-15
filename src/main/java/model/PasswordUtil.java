package model;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * パスワードのハッシュ化（PBKDF2WithHmacSHA256）
 * DBには "salt:hash"（どちらもBase64）の形で保存する
 */
public final class PasswordUtil {
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 100_000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;

	private PasswordUtil() {
	}

	public static String hash(String password) {
		byte[] salt = new byte[SALT_LENGTH];
		new SecureRandom().nextBytes(salt);
		Base64.Encoder enc = Base64.getEncoder();
		return enc.encodeToString(salt) + ":" + enc.encodeToString(pbkdf2(password, salt));
	}

	public static boolean verify(String password, String stored) {
		if (stored == null) {
			return false;
		}
		String[] parts = stored.split(":");
		if (parts.length != 2) {
			return false;
		}
		Base64.Decoder dec = Base64.getDecoder();
		byte[] salt = dec.decode(parts[0]);
		byte[] expected = dec.decode(parts[1]);
		// 比較にかかる時間で推測されないように MessageDigest.isEqual を使う
		return MessageDigest.isEqual(expected, pbkdf2(password, salt));
	}

	private static byte[] pbkdf2(String password, byte[] salt) {
		try {
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("パスワードをハッシュ化できませんでした", e);
		}
	}
}
