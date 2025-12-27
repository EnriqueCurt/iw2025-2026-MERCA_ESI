package com.example.iw20252026merca_esi.util;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.security.*;
import java.util.Base64;

public class VapidKeyGenerator {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("prime256v1");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
        keyPairGenerator.initialize(parameterSpec);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
        byte[] privateKeyBytes = privateKey.getD().toByteArray();

        String publicKeyBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKeyBytes);
        String privateKeyBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(privateKeyBytes);

        System.out.println("=== CLAVES VAPID GENERADAS ===");
        System.out.println("\nPublic Key:");
        System.out.println(publicKeyBase64);
        System.out.println("\nPrivate Key:");
        System.out.println(privateKeyBase64);
        System.out.println("\n=== Copia estas claves a tu application.properties ===");
    }
}