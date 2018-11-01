package com.all.write.util;

import com.all.write.api.Block;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.Base64;

public class StringUtil {
	
	//Applies Sha256 to a string and returns the result. 
	public static String applySha256(byte[] input){
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input);

			return Base64.getEncoder().encodeToString(hash);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, byte [] input) {
		Signature dsa;
		byte[] output;
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input;
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	//Verifies a String signature 
	public static boolean verifyECDSASig(PublicKey publicKey, byte[] data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data);
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
//	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
//		int count = transactions.size();
//
//		List<String> previousTreeLayer = new ArrayList<String>();
//		for(Transaction transaction : transactions) {
//			previousTreeLayer.add(transaction.transactionId);
//		}
//		List<String> treeLayer = previousTreeLayer;
//
//		while(count > 1) {
//			treeLayer = new ArrayList<String>();
//			for(int i=1; i < previousTreeLayer.size(); i+=2) {
//				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
//			}
//			count = treeLayer.size();
//			previousTreeLayer = treeLayer;
//		}
//
//		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
//		return merkleRoot;
//	}

	public static String getBase64Encoded(PublicKey publicKey) {
		if (publicKey != null) {
			return Base64.getEncoder().encodeToString(publicKey.getEncoded());
		}

		return null;
	}

	public static String getHashOfBlock(Block block) {
		if (block == null) {
			return null;
		}

		return applySha256(getBlockBytes(block));
	}

	public static byte [] getBlockBytes(Block block) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(block);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (Exception e) {
        }

        return bytes;
    }

    public static String getAuthorPublicKey(Block block) {
		if (block == null) {
			return null;
		}

		if(block.getType() == Block.Type.SEND_FILE
				|| block.getType() == Block.Type.SEND_KEY
				|| block.getType() == Block.Type.GENESIS) {
				return block.getSender();
		} else if (block.getType() == Block.Type.GET_FILE){
			return block.getReceiver();
		} else {
			return null;
		}
	}
}
