import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class Transaction {
	int transaction_id;
	int coin_id;
	String sender_signature;
	String sender_id;
	String receiver_id;
	String type;
	String hash;
	String previousHash;
	int coin_amount;

	public Transaction(int transaction_id, int coin_id, String sender_id, String receiver_id, String type) {
		this.coin_amount = 1;
		this.coin_id = coin_id; 
		this.transaction_id = transaction_id;
		this.sender_id = sender_id;
		this.receiver_id = receiver_id;
		this.type = type;
		this.hash = computeHash();
	}
	
	public String computeHash() {

		String dataToHash = this.toString();

		MessageDigest message_digest;
		String hashed_message = null;

		try {
			message_digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = message_digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			hashed_message = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hashed_message;
	}

	@Override
	public String toString() {
		return "Transaction [transaction_id=" + Integer.toString(transaction_id) + ", coin_id="
				+ Integer.toString(coin_id) + ", sender_id=" + sender_id + ", receiver_id="
				+ receiver_id + ", type=" + type + ", Hash=" + hash
				+ ", previousHash=" + previousHash + ", coin_amount=" + Integer.toString(coin_amount)+"]";
	}	

	public String getSender_id() {
		return sender_id;
	}

	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}

	public int getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(int transaction_id) {
		this.transaction_id = transaction_id;
	}

	public int getCoin_id() {
		return coin_id;
	}

	public void setCoin_id(int coin_id) {
		this.coin_id = coin_id;
	}

	public String getReceiver_id() {
		return receiver_id;
	}

	public void setReceiver_id(String receiver_id) {
		this.receiver_id = receiver_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public int getCoin_amount() {
		return coin_amount;
	}

	public void setCoin_amount(int coin_amount) {
		this.coin_amount = coin_amount;
	}

	public String getSender_signature() {
		return sender_signature;
	}
	
	public void setSender_signature(String sender_signature) {
		this.sender_signature = sender_signature;
	}

}
