package BeansInterfaces;

import Utils.ErrorsEnum;
import uc.mei.Entities.Client;
import uc.mei.Entities.CompanyManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ICm {

    public CompanyManager getCmByEmail(String Email);
    public ErrorsEnum login(String Email, String Password);
    public void setCm(String username, String email, String password);
    public ErrorsEnum createTrip(String dp, String arr, String org, String dst,  String cpct,String price);
    public List<Client> getTop5Clients();
    public float getReveneu(LocalDateTime date);
}
