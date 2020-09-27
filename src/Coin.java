
public class Coin {
	int coin_ID;
	String last_transaction_hash;
	
	@Override
	public String toString() {
		return "Coin [coin_ID=" + coin_ID + ", last_transaction_hash="
				+ last_transaction_hash + "]";
	}

	public int getCoin_ID() {
		return coin_ID;
	}

	public void setCoin_ID(int coin_ID) {
		this.coin_ID = coin_ID;
	}

	public String getLast_transaction_hash() {
		return last_transaction_hash;
	}

	public void setLast_transaction_hash(String last_transaction_hash) {
		this.last_transaction_hash = last_transaction_hash;
	}

	public Coin(int coin_ID)
	{
		this.coin_ID = coin_ID;
	}
}
