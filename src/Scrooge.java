import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;


public class Scrooge {

	ArrayList <Transaction> accumulator;
	ArrayList <User> user_network;
	KeyPair scrooge_keypair; 
	LinkedList<Block> blockChain;
	int transaction_id_counter;

	PrintStream out;
	PrintStream console; 

	JFrame frame;
	private JTextField txtPressSpacebarTo;

	public Scrooge() {

		// INITIALIZATION
		
		blockChain = new LinkedList<>();

		// Generate key-pair for Scrooge to sign his transactions
		try {
			scrooge_keypair = generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}	

		try {
			out = new PrintStream(new FileOutputStream("Output.txt"));
			console = System.out;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void initialize() {

		// Create Terminate Window
		initializeWindow();

		// Create 1000 create coin transactions (10 coins for each of the 100 users), without transferring to the users yet 
		// Each create coin transaction is signed by Scrooge
		ArrayList<Coin> coins = new ArrayList<>();
		int coin_id_counter = 0;
		accumulator = new ArrayList<>();
		for(int i = 0; i<1000; i++){ 
			Coin coin = new Coin(coin_id_counter++);
			Transaction transaction = createScroogeTransaction(transaction_id_counter++, coin.coin_ID, "Scrooge", null, "Create");

			//Set the last_transaction_hash attribute of this coin to be the hash of the newly created transaction
			coin.setLast_transaction_hash(transaction.getHash());

			coins.add(coin);
			accumulator.add(transaction);

			if(accumulator.size()==10) {
				String previous_hash = null;
				if(!blockChain.isEmpty()) {
					previous_hash = blockChain.get(blockChain.size()-1).getHash();
				}
				Block b = new Block(blockChain.size(), previous_hash, accumulator);
				blockChain.add(b);
				accumulator = new ArrayList<>();
			}			
		}

		// Create the 100 users of the network
		user_network = new ArrayList<>();
		for(int i = 0; i<100; i++){ 
			User u = createUser(i);
			user_network.add(u);
		}

		// Transfer 10 coins to each of the 100 users using "Transfer" transactions signed by Scrooge
		for(int i = 0; i<100; i++) { //looping over every block & user 
			User currentUser = user_network.get(i);
			Block currentBlock = blockChain.get(i);

			for(int j = 0; j<10; j++) { //looping over every transaction
				Transaction prevTransaction = currentBlock.getTransactions().get(j);
				Coin coin = coins.get((i*10)+j);
				Transaction transaction = createScroogeTransaction(transaction_id_counter++, coin.getCoin_ID(), "Scrooge", i+"", "Transfer");

				//Set the previous hash of the newly created transaction to be that of the corresponding CreateCoin transaction
				transaction.setPreviousHash(prevTransaction.getHash());

				//Set the last_transaction_hash attribute of this coin to be the hash of the newly created transaction
				coins.get((i*10)+j).setLast_transaction_hash(transaction.getHash());

				//Add this coin to the user's array list of coins
				currentUser.coins.add(coin);

				//Increment the total coins of this user by 1
				currentUser.setTotal_coins(currentUser.getTotal_coins()+1);

				accumulator.add(transaction);

				if(accumulator.size()==10) {
					String previous_hash = blockChain.get(blockChain.size()-1).hash;
					Block b = new Block(blockChain.size(), previous_hash, accumulator);
					blockChain.add(b);
					accumulator = new ArrayList<>();
				}
			}
		}

		for(int i = 0; i<user_network.size(); i++) {
			System.setOut(console);
			System.out.println("User: " + user_network.get(i).getUser_ID());
			System.out.println("Public Key: " + user_network.get(i).getKey_pair().getPublic());
			System.out.println("Amount of Coins: " + user_network.get(i).total_coins);
			System.out.println();
			System.setOut(out);
			System.out.println("User: " + user_network.get(i).getUser_ID());
			System.out.println("Public Key: " + user_network.get(i).getKey_pair().getPublic());
			System.out.println("Amount of Coins: " + user_network.get(i).total_coins);
			System.out.println();

		}
	}

	public void initializeWindow() {

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		txtPressSpacebarTo = new JTextField();
		txtPressSpacebarTo.setEditable(false);
		txtPressSpacebarTo.setFont(new Font("Gadugi", Font.BOLD, 15));
		txtPressSpacebarTo.setBackground(UIManager.getColor("Panel.background"));
		txtPressSpacebarTo.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
		txtPressSpacebarTo.setHorizontalAlignment(SwingConstants.CENTER);
		txtPressSpacebarTo.setText("Press SPACEBAR to terminate program execution");
		frame.getContentPane().add(txtPressSpacebarTo, BorderLayout.CENTER);
		txtPressSpacebarTo.setColumns(10);
		txtPressSpacebarTo.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_SPACE){
					scroogeSignBlock(blockChain.getLast());
					System.setOut(console);
					System.out.println("Blockchain after signing last block: ");
					System.out.println(blockChain);
					System.out.println();
					System.setOut(out);
					System.out.println("Blockchain after signing last block: ");
					System.out.println(blockChain);
					System.out.println();
					System.exit(0);
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		frame.setVisible(true);
	}

	public void startSimulation() {

		Random rand_int = new Random();

		// Simulate random transactions from and to random users in the network
		while(true) {

			// Randomly select two users to be sender & receiver
			int sender_index = rand_int.nextInt(user_network.size());
			User random_sender = user_network.get(sender_index);
			while(random_sender.getTotal_coins()==0) {
				sender_index = rand_int.nextInt(user_network.size());
				random_sender = user_network.get(sender_index);
			}

			int rec_index = rand_int.nextInt(user_network.size());
			while(rec_index==sender_index) {
				rec_index = rand_int.nextInt(user_network.size());
			}

			// Randomly select amount of coins to be sent in the transaction
			int random_amount = rand_int.nextInt(random_sender.getTotal_coins());

			// Randomly select the coins to be sent in the transaction + Create, verify & add n transactions to accumulator
			ArrayList<Coin> transaction_coins = new ArrayList<>();
			for (int i = 0; i<random_amount; i++) {

				if(random_sender.getTotal_coins()==0) 
					break;

				int random_coin_index = rand_int.nextInt(random_sender.getTotal_coins());
				Coin sender_coin = random_sender.getCoins().get(random_coin_index); 
				while(transaction_coins.contains(sender_coin)) {
					random_coin_index = rand_int.nextInt(random_sender.getTotal_coins());
					sender_coin = random_sender.getCoins().get(random_coin_index); 
				}
				transaction_coins.add(sender_coin);

				// Create transaction
				Transaction trans = random_sender.createTransaction(transaction_id_counter++, sender_coin.getCoin_ID(), sender_index+"", rec_index+"", "Transfer");

				// Set previous hash of created transaction
				trans.setPreviousHash(sender_coin.getLast_transaction_hash());

				// Prevent double-spending by making sure no other transaction
				// in the accumulator has this previous hash as its own previous hash
				boolean isValid = true;
				String prev_hash = trans.getPreviousHash();
				for(Transaction t: accumulator) {
					if(t.previousHash.equals(prev_hash)) {
						isValid = false;
					}
				}

				// Sign the transaction
				String transaction_string = trans.toString();
				String signature;
				try {
					signature = random_sender.sign(transaction_string, random_sender.getKey_pair().getPrivate());
					trans.setSender_signature(signature);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				// 1) Verify signature of sender
				try {
					isValid = isValid & verify(trans.toString(), trans.sender_signature, random_sender.getKey_pair().getPublic());
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 2) Verify coin ownership (receiver of previous transaction is sender of this transaction)				
				for(Block b : blockChain) {
					ArrayList<Transaction> block_trans = b.getTransactions();
					for (Transaction t : block_trans) {
						String t_hash = t.getHash();
						if(prev_hash.equals(t_hash)) {
							String rec_id = t.receiver_id;
							if(!rec_id.equals(sender_index+"")) {
								isValid= isValid & false;
							}
						}
					}	
				}

				// Add verified transaction to accumulator
				if(isValid) {
					accumulator.add(trans);
					System.setOut(console);
					System.out.println("Block under construction: ");
					System.out.println(accumulator);
					System.out.println("New transaction added: ");
					System.out.println(trans);
					System.out.println();
					System.setOut(out);
					System.out.println("Block under construction: ");
					System.out.println(accumulator);
					System.out.println("New transaction added: ");
					System.out.println(trans);
					System.out.println();


					if(accumulator.size()==10) {
						// Remove all the coins in these 10 transactions from their senders' coins list
						// and add them to their receivers' coins list.
						for(Transaction t1: accumulator) {

							String sender_id = t1.getSender_id();
							int coin_id = t1.getCoin_id();

							for(User u1 : user_network) {
								String u_id = u1.getUser_ID()+"";
								if(u_id.equals(sender_id)) {
									int coinIndex = 0;
									for(Coin c1 : u1.getCoins()) {
										if(c1.getCoin_ID()==coin_id) {
											coinIndex = u1.getCoins().indexOf(c1);
										}
									}

									// Get the coin used in this transaction
									Coin c2 = u1.getCoins().get(coinIndex);

									// Set the last_transaction_hash attribute of this coin to be the hash of the newly created transaction
									c2.setLast_transaction_hash(t1.getHash());

									// Add coin to receiver's list
									String receiver_id = t1.getReceiver_id();
									for(User u2: user_network) {
										String u2_id = u2.getUser_ID()+"";
										if(u2_id.equals(receiver_id)) {
											u2.getCoins().add(c2);
											u2.setTotal_coins(u2.getTotal_coins()+1);
										}
									}
									// Remove coin from sender's list
									u1.getCoins().remove(c2);
									u1.setTotal_coins(u1.getTotal_coins()-1);
								}
							}
						}

						// Set the previous hash of the new block, create it & add to blockchain
						String previous_hash = blockChain.get(blockChain.size()-1).hash;
						Block b = new Block(blockChain.size(), previous_hash, accumulator);
						blockChain.add(b);
						System.setOut(console);
						System.out.println("Blockchain after adding new block: ");
						System.out.println(blockChain);
						System.out.println();
						System.setOut(out);
						System.out.println("Blockchain after adding new block: ");
						System.out.println(blockChain);
						System.out.println();
						accumulator = new ArrayList<>();
					}
				}
			}			

			// Wait for 5 seconds
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}
	


	public KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();

		return pair;
	}


	public Transaction createScroogeTransaction(int transaction_id, int coin_id, String sender_id, String receiver_id, String type) {

		// Create the transaction
		Transaction t = new Transaction(transaction_id, coin_id, sender_id, receiver_id, type);

		// Sign it 
		String transaction_string = t.toString();
		try {
			String scrooge_signature = sign(transaction_string, scrooge_keypair.getPrivate());
			t.setSender_signature(scrooge_signature);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}

	public void scroogeSignBlock(Block b) {
		try {
			String signature = sign(b.getHash(),scrooge_keypair.getPrivate());
			b.setScroogeSignature(signature);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

		byte[] signature = privateSignature.sign();

		return Base64.getEncoder().encodeToString(signature);
	}


	public boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);

		return publicSignature.verify(signatureBytes);
	}

	private User createUser(int user_ID) {
		User u = new User(user_ID);
		return u;
	}

	public static void main (String [] args) {
		Scrooge s = new Scrooge();
		s.initialize();
		s.startSimulation();
	}

}

