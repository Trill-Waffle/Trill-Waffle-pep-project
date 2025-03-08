package Service;

import DAO.AccountDAO;
import Model.Account;

import java.util.*;

public class AccountService {
    private AccountDAO accountDAO;


    public AccountService(){
        accountDAO = new AccountDAO();
    }


    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;

    }

    public Account addAccount(Account account) {
        return this.accountDAO.insertAccount(account);
    }

    public List<Account> getAllAccounts(){
        return this.accountDAO.getAllAccounts();

    }
}
