package app.entities;

public class Bottom
{
    private int bottomId;
    private int price;
    private String type;

    public Bottom(int bottomId, int price, String type)
    {
        this.bottomId = bottomId;
        this.price = price;
        this.type = type;
    }

    public int getBottomId()
    {
        return bottomId;
    }

    public void setBottomId(int bottomId)
    {
        this.bottomId = bottomId;
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

    @Override
    public String toString()
    {
        return "Bottom{" +
                "bottomId=" + bottomId +
                ", price=" + price +
                ", type='" + type + '\'' +
                '}';
    }
}
