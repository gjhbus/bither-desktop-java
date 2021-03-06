/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.runnable;

import net.bither.bitherj.core.Address;
import net.bither.bitherj.core.Tx;
import net.bither.bitherj.crypto.SecureCharSequence;
import net.bither.bitherj.exception.PasswordException;
import net.bither.bitherj.exception.TxBuilderException;
import net.bither.bitherj.utils.Utils;
import net.bither.utils.LocaliserUtils;


public class CompleteTransactionRunnable extends BaseRunnable {
    private Address wallet;

    private SecureCharSequence password;
    private long amount;
    private String toAddress;

    private String changeAddress;
    private boolean toSign = false;

    static {
        for (TxBuilderException.TxBuilderErrorType type : TxBuilderException.TxBuilderErrorType
                .values()) {
            String format = LocaliserUtils.getString("send.failed");
            switch (type) {
                case TxNotEnoughMoney:
                    format = LocaliserUtils.getString("send.failed.missing.btc");
                    break;
                case TxDustOut:
                    format = LocaliserUtils.getString("send.failed.dust.out.put");
                    break;
                case TxWaitConfirm:
                    format = LocaliserUtils.getString("send.failed.pendding");
                    break;
            }
            type.registerFormatString(format);
        }
    }

    public CompleteTransactionRunnable(Address a, long amount, String toAddress, String changeAddress,
                                       SecureCharSequence password) throws Exception {
        this.amount = amount;
        this.toAddress = toAddress;
        this.password = password;
        this.changeAddress = changeAddress;
        if (password == null || password.length() == 0) {

            wallet = a;
            toSign = false;
        } else {

            if (a.hasPrivKey()) {
                wallet = a;
            } else {
                throw new Exception("address not with private key");
            }
            toSign = true;
        }
        if (!Utils.isEmpty(changeAddress)) {
            this.changeAddress = changeAddress;
        } else {
            this.changeAddress = wallet.getAddress();
        }
    }

    @Override
    public void run() {
        prepare();
        try {
            Tx tx = wallet.buildTx(amount, toAddress, changeAddress);
            if (tx == null) {
                error(0, LocaliserUtils.getString("send.failed"));
                return;
            }
            if (tx.amountSentToAddress(toAddress) <= 0) {
                error(0, LocaliserUtils.getString("send.failed.amount.is.less"));
                return;
            }
            if (toSign) {
                wallet.signTx(tx, password);
                password.wipe();

                if (!tx.verifySignatures()) {
                    error(0, getMessageFromException(null));
                    return;
                }

            }
            success(tx);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = getMessageFromException(e);
            error(0, msg);
        }
    }

    public String getMessageFromException(Exception e) {
        if (e != null && e instanceof TxBuilderException) {
            return e.getMessage();
        } else if (e != null && e instanceof PasswordException) {
            return LocaliserUtils.getString("password.wrong");
        } else {
            return LocaliserUtils.getString("send.failed");
        }
    }
}
