package com.example.perspikyliator.mypracticeproject.interfaces;

import com.example.perspikyliator.mypracticeproject.model.BankArray;

public interface CallbackBankInfoLoader {
    void onBankSuccess(BankArray bankArray);
    void onBankFailure(String errorMessage);
}
