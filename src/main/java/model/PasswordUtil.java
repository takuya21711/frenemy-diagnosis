package model;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * パスワードのハッシュ化・検証ユーティリティ。
 *
 * <p>アルゴリズム: PBKDF2WithHmacSHA256 / {@value #ITERATIONS} iterations /
 * key length {@value #KEY_LENGTH} bits / salt {@value #SALT_LENGTH} bytes。
 *
 * <p>DB 保存形式は {@code "<Base64 salt>:<Base64 hash>"} の 1 列。
 * 検証は {@link java.security.MessageDigest#isEqual} でタイミング攻撃を回避する。
 */
public final class PasswordUtil {
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 100_000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;

	private PasswordUtil() {
	}

	/**
	 * 平文パスワードを DB 保存用のハッシュ文字列（{@code "salt:hash"}, Base64）に変換する。
	 * ソルトは毎回ランダム生成される。
	 */
	public static String hash(String password) {
		byte[] salt = new byte[SALT_LENGTH];
		new SecureRandom().nextBytes(salt);
		Base64.Encoder enc = Base64.getEncoder();
		return enc.encodeToString(salt) + ":" + enc.encodeToString(pbkdf2(password, salt));
	}

	/**
	 * 平文パスワードが {@code stored}（{@link #hash(String)} の返り値）と一致するか判定する。
	 * フォーマット不正・{@code null} はすべて不一致扱い（例外にしない）。
	 */
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
