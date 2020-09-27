import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class User {
	int user_ID;
	KeyPair key_pair;
	int total_coins;
	ArrayList<Coin> coins;

	public User(int user_ID) {

		// Generate key-pair for the user to sign the transactions to be sent
		try {
			key_pair = generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		this.user_ID = user_ID;
		this.coins = new ArrayList<>();		
	}	

	
	@Override
	public String toString() {
		return "User [user_ID=" + user_ID + ", total_coins=" + total_coins + ", coins=" + coins + "]";
	}

	public int getUser_ID() {
		return user_ID;
	}

	public void setUser_ID(int user_ID) {
		this.user_ID = user_ID;
	}

	public KeyPair getKey_pair() {
		return key_pair;
	}

	public void setKey_pair(KeyPair key_pair) {
		this.key_pair = key_pair;
	}

	public int getTotal_coins() {
		return total_coins;
	}

	public void setTotal_coins(int total_coins) {
		this.total_coins = total_coins;
	}


	public ArrayList<Coin> getCoins() {
		return coins;
	}


	public void setCoins(ArrayList<Coin> coins) {
		this.coins = coins;
	}


	public KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();

		return pair;
	}	

	public Transaction createTransaction(int transaction_id, int coin_id, String sender_id, String receiver_id, String type) {

		Transaction t = new Transaction(transaction_id, coin_id, sender_id, receiver_id, type);
		return t;
	}


	public String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

		byte[] signature = privateSignature.sign();

		return Base64.getEncoder().encodeToString(signature);
	}

}
