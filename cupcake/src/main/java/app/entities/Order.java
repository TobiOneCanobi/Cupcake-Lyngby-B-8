package app.entities;

public class Order
{
    private int orderId;
    private int userId;
    private String userEmail;
    private String toppingType;
    private String bottomType;
    private int quantity;
    private int totalPrice;
    private int toppingPrice;
    private int bottomPrice;

    public Order(int orderId, int userId)
    {
        this.orderId = orderId;
        this.userId = userId;
    }

    public Order(int orderId, String userEmail, String toppingType, String bottomType, int quantity, int totalPrice)
    {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.toppingType = toppingType;
        this.bottomType = bottomType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Order(String toppingType, String bottomType, int quantity, int toppingPrice, int bottomPrice, int totalPrice)
    {
        this.toppingType = toppingType;
        this.bottomType = bottomType;
        this.quantity = quantity;
        this.toppingPrice = toppingPrice;
        this.bottomPrice = bottomPrice;
        this.totalPrice = totalPrice;
    }

    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    public String getToppingType()
    {
        return toppingType;
    }

    public void setToppingType(String toppingType)
    {
        this.toppingType = toppingType;
    }

    public String getBottomType()
    {
        return bottomType;
    }

    public void setBottomType(String bottomType)
    {
        this.bottomType = bottomType;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public int getToppingPrice()
    {
        return toppingPrice;
    }

    public void setToppingPrice(int toppingPrice)
    {
        this.toppingPrice = toppingPrice;
    }

    public int getBottomPrice()
    {
        return bottomPrice;
    }

    public void setBottomPrice(int bottomPrice)
    {
        this.bottomPrice = bottomPrice;
    }

    @Override
    public String toString()
    {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", toppingType='" + toppingType + '\'' +
                ", bottomType='" + bottomType + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", toppingPrice=" + toppingPrice +
                ", bottomPrice=" + bottomPrice +
                '}';
    }
}
