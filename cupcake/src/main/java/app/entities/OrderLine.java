package app.entities;

public class OrderLine
{
    private int orderLineId;
    private int quantity;
    private int toppingId;
    private int bottomId;
    private int orderId;


    public OrderLine(int orderLineId,int quantity, int toppingId, int bottomId, int orderId)
    {
        this.orderLineId = orderLineId;
        this.toppingId = toppingId;
        this.bottomId = bottomId;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public int getOrderLineId()
    {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId)
    {
        this.orderLineId = orderLineId;
    }

    public int getToppingId()
    {
        return toppingId;
    }

    public void setToppingId(int toppingId)
    {
        this.toppingId = toppingId;
    }

    public int getBottomId()
    {
        return bottomId;
    }

    public void setBottomId(int bottomId)
    {
        this.bottomId = bottomId;
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    @Override
    public String toString()
    {
        return "OrderLine{" +
                "orderLineId=" + orderLineId +
                ", toppingId=" + toppingId +
                ", bottomId=" + bottomId +
                ", orderId=" + orderId +
                ", quantity=" + quantity +
                '}';
    }
}
