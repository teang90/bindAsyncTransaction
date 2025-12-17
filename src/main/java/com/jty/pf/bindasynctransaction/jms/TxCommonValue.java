package com.jty.pf.bindasynctransaction.jms;

import lombok.Getter;

@Getter
public enum TxCommonValue {
    JMS_HEADER_TX_KEY("txId")
    ;
    private final String value;

    TxCommonValue(String value) {
        this.value = value;
    }
}
