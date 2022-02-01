package BeansImplementation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.*;
import BeansInterfaces.IWallet;
import Utils.ErrorsEnum;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

/**
 * Session Bean implementation class Wallet
 */
@Stateless
@Local
public class WalletBean implements IWallet {
    private final Logger wallet_logger = LoggerFactory.getLogger(Ticket.class);

    @PersistenceContext(unitName = "busflix")
    EntityManager entityManager;

    @Override
    public List<Wallet> getAllWallets() {
        wallet_logger.info("Fetching all wallets");

        try
        {
            return entityManager.createQuery("SELECT w FROM Wallet w", Wallet.class).getResultList();
        }
        catch (Exception e)
        {
            wallet_logger.error("ERROR FETCHING ALL WALLETS.\n", e);
            throw e;
        }
    }

    @Override
    public Wallet getWalletByClientId(java.util.UUID ClientId) {
        wallet_logger.info("Fetching Wallet By Client Id: " + ClientId);
        Client client = entityManager.find(Client.class, ClientId);

        try
        {
            return entityManager.createQuery("SELECT w FROM Wallet w WHERE w.Id = (:id)", Wallet.class).
                    setParameter("id", client.getWallet().getId()).getSingleResult();
        }
        catch (NoResultException e)
        {
            wallet_logger.error("ERROR WHILE GETTING WALLET FOR CLIENT: " + ClientId + " \n", e);
            throw e;
        }
    }

    @Override
    public ErrorsEnum updateWalletByClientId(java.util.UUID ClientId, Float Balance) {
        wallet_logger.info("Updating Wallet of Client Id: " + ClientId + " with: " + Balance);

        try
        {

            Client client = entityManager.find(Client.class, ClientId);
            Wallet wallet = getWalletByClientId(client.getWallet().getId());

            wallet.setBalance(Balance);
            entityManager.merge(wallet);

            return ErrorsEnum.WALLET_UPDATED_SUCCESSFULLY;
        }
        catch(NoResultException e)
        {
            wallet_logger.error("ERROR WHILE UPDATING WALLET FROM CLIENT: " + ClientId, e);
            throw e;
        }

    }

    @Override
    public ErrorsEnum makeReimbursement(java.util.UUID ClientId, java.util.UUID TicketId){
        wallet_logger.info("Making Reimbursement for Client Id: " + ClientId + " of ticket Id: " + TicketId);

        try
        {

            Client client = entityManager.find(Client.class, ClientId);
            if(client != null)
            {
                Ticket ticket = entityManager.find(Ticket.class, TicketId);

                if (ticket != null) {
                    // HERE WE HAVE TO UPDATE CLIENT'S BALANCE, ADDING THE TICKET'S PRICE TO CLIENT'S CURRENT BALANCE
                    client.getWallet().setBalance(client.getWallet().getBalance() + ticket.getTrip().getPrice());

                    // BECAUSE IT'S A REIMBURSEMENT WE HAVE REMOVE THE TICKET ITSELF FROM THE CLIENTS'S LIST OF TICKETS
                    client.getTickets().remove(ticket);

                    // AND BECAUSE IT'S A REIMBURSEMENT, HE HAVE TO PUT AVAILABLE A THIS SEAT, BECAUSE NOW IT'S AVAILABLE TO SELL
                    ticket.getTrip().setCapacity(ticket.getTrip().getCapacity() + 1);

                    entityManager.merge(ticket);
                    entityManager.merge(ticket.getTrip());
                    entityManager.merge(client);

                    return ErrorsEnum.WALLET_REFUNDED_SUCCESSFULLY;
                }
                else
                {
                    return ErrorsEnum.TICKET_NOT_FOUND;
                }
            }
            else
            {
                return ErrorsEnum.CLIENT_NOT_FOUND;
            }
        }
        catch(Exception e)
        {
            wallet_logger.error("ERROR WHILE MAKING REIMBURSEMENT FOR CLIENT: " + ClientId + " of ticket Id: " + TicketId, e);
            throw e;
        }
    }

    @Override
    public void removeWallet(java.util.UUID Id){
        wallet_logger.info("Removing Wallet by Wallet Id: " + Id);

        try
        {
            TypedQuery<Wallet> wallet = entityManager.createQuery("SELECT w FROM Wallet w WHERE w.Id = (:id)", Wallet.class).
                    setParameter("id", Id);
            entityManager.remove(wallet.getSingleResult());
        }
        catch (NoResultException e)
        {
            wallet_logger.error("ERROR WHILE FETCHING WALLET: " + Id, e);
            throw e;
        }
    }
}
