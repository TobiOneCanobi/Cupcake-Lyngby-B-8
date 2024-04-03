package app.entities;

public class Topping
{
    private int toppingId;
    private int price;
    private String type;

    public Topping(int toppingId, int price, String type)
    {
        this.toppingId = toppingId;
        this.price = price;
        this.type = type;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getToppingId()
    {
        return toppingId;
    }

    public void setToppingId(int toppingId)
    {
        this.toppingId = toppingId;
    }

    @Override
    public String toString()
    {
        return "Topping{" +
                "toppingId=" + toppingId +
                ", price=" + price +
                ", type='" + type + '\'' +
                '}';
    }
}
