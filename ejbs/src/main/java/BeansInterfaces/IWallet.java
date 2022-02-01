package BeansInterfaces;

import uc.mei.Entities.Wallet;
import Utils.ErrorsEnum;

import java.util.List;

public interface IWallet {
    List<Wallet> getAllWallets();
    Wallet getWalletByClientId(java.util.UUID ClientId);
    ErrorsEnum updateWalletByClientId(java.util.UUID ClientId, Float Balance);
    ErrorsEnum makeReimbursement(java.util.UUID Client, java.util.UUID TicketId);
    void removeWallet(java.util.UUID Id);
}
