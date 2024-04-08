package app.entities;

public class ShoppingCartLine
{
    private int quantity = 0;

    private Bottom bottom;

    private Topping topping;

    private int total = 0;

    public ShoppingCartLine(int quantity, Bottom bottom, Topping topping)
    {
        this.quantity = quantity;
        this.bottom = bottom;
        this.topping = topping;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public int getTotal()
    {
        total = ((topping.getPrice()+bottom.getPrice())*quantity);
        return total;
    }

    public Bottom getBottom()
    {
        return bottom;
    }

    public Topping getTopping()
    {
        return topping;
    }

    @Override
    public String toString()
    {
        return "ShoppingCartLine{" +
                "quantity=" + quantity +
                ", bottom=" + bottom +
                ", topping=" + topping +
                '}';
    }
}
