package app.entities;

public class User
{
    private int userid;
    private String eMail;
    private String passWord;
    private int balance;
    private String role;

    public User(int userid, String eMail, String passWord, int balance, String role)
    {
        this.userid = userid;
        this.eMail = eMail;
        this.passWord = passWord;
        this.balance = balance;
        this.role = role;
    }

    public int getUserid()
    {
        return userid;
    }

    public void setUserid(int userid)
    {
        this.userid = userid;
    }

    public String geteMail()
    {
        return eMail;
    }

    public void seteMail(String eMail)
    {
        this.eMail = eMail;
    }

    public String getPassWord()
    {
        return passWord;
    }

    public void setPassWord(String passWord)
    {
        this.passWord = passWord;
    }

    public int getBalance()
    {
        return balance;
    }

    public void setBalance(int balance)
    {
        this.balance = balance;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "userid=" + userid +
                ", eMail='" + eMail + '\'' +
                ", passWord='" + passWord + '\'' +
                ", balance=" + balance +
                ", role='" + role + '\'' +
                '}';
    }
}
