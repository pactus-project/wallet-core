// SPDX-License-Identifier: Apache-2.0
//
// Copyright © 2017 Trust Wallet.

package com.trustwallet.core.app.blockchains.pactus

import com.google.protobuf.ByteString
import com.trustwallet.core.app.utils.toHexByteArray
import org.junit.Assert.assertEquals
import org.junit.Test
import wallet.core.jni.PrivateKey
import wallet.core.java.AnySigner
import wallet.core.jni.CoinType
import wallet.core.jni.CoinType.PACTUS
import wallet.core.jni.proto.Pactus
import wallet.core.jni.proto.Pactus.SigningOutput
import com.trustwallet.core.app.utils.Numeric
import org.junit.Assert.assertArrayEquals

class TestPactusSigner {

    init {
        System.loadLibrary("TrustWalletCore")
    }

    @Test
    fun testPactusTransactionSigning() {
        val signingInput = Pactus.SigningInput.newBuilder()
        signingInput.apply {
            privateKey = ByteString.copyFrom(
                PrivateKey("4e51f1f3721f644ac7a193be7f5e7b8c2abaa3467871daf4eacb5d3af080e5d6".toHexByteArray()).data())
            transaction = Pactus.TransactionMessage.newBuilder().apply {
                lockTime = 2335524
                fee = 10000000
                memo = "wallet-core"
                transfer = Pactus.TransferPayload.newBuilder().apply {
                    sender = "pc1rwzvr8rstdqypr80ag3t6hqrtnss9nwymcxy3lr"
                    receiver = "pc1r0g22ufzn8qtw0742dmfglnw73e260hep0k3yra"
                    amount = 200000000
                }.build()
            }.build()
        }

        val output = AnySigner.sign(signingInput.build(), PACTUS, SigningOutput.parser())

        assertEquals(
            "0x1b6b7226f7935a15f05371d1a1fefead585a89704ce464b7cc1d453d299d235f",
            Numeric.toHexString(output.transactionId.toByteArray())
        )

        assertEquals(
            "0x8dea6d79ee7ec60f66433f189ed9b3c50b2ad6fa004e26790ee736693eda850695794161374b22c696dabb98e93f6ca9300b22f3b904921fbf560bb72145f4fa",
            Numeric.toHexString(output.signature.toByteArray())
        )

        assertEquals(
            "0x000124a3230080ade2040b77616c6c65742d636f726501037098338e0b6808119dfd4457ab806b9c2059b89b037a14ae24533816e7faaa6ed28fcdde8e55a7df218084af5f4ed8fee3d8992e82660dd05bbe8608fc56ceabffdeeee61e3213b9b49d33a0fc8dea6d79ee7ec60f66433f189ed9b3c50b2ad6fa004e26790ee736693eda850695794161374b22c696dabb98e93f6ca9300b22f3b904921fbf560bb72145f4fa",
            Numeric.toHexString(output.signedTransactionData.toByteArray())
        )
    }
}
