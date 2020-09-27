import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class Block {
	int block_ID;
	String previousHash;
	String hash;
	ArrayList<Transaction> transactions;
	String scroogeSignature;

	public Block(int block_ID, String previousHash, ArrayList<Transaction> transactions) {

		this.block_ID = block_ID;
		this.previousHash = previousHash;
		this.transactions = transactions;
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

	public String toString() {

		// Convert the accumulator of transactions to a string
		String stringTransactions = "";
		for(int i = 0; i<transactions.size(); i++) {
			stringTransactions += transactions.get(i).toString()+"\n";
		}

		return "Block [block_ID=" + Integer.toString(block_ID) + ", Hash=" + hash + ", previousHash=" + previousHash
				+ ", transactions=" + stringTransactions + ", scroogeSignature=" + scroogeSignature + "]";
	}
	
	public String getScroogeSignature() {
		return scroogeSignature;
	}

	public void setScroogeSignature(String scroogeSignature) {
		this.scroogeSignature = scroogeSignature;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getBlock_ID() {
		return block_ID;
	}

	public void setBlock_ID(int block_ID) {
		this.block_ID = block_ID;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}

}
