package app.entities;

public class User
{
    private int userid;
    private String email;
    private String password;
    private int balance;
    private String role;

    public User(int userid, String email, String password, int balance, String role)
    {
        this.userid = userid;
        this.email = email;
        this.password = password;
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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                ", role='" + role + '\'' +
                '}';
    }
}
