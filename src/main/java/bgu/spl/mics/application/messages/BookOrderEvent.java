package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String bookName;
    private int orderTick;
    private int orderId;

    public BookOrderEvent(Customer customer , String book , int tick,int orderId){
        this.bookName = book;
        this.customer = customer;
        this.orderTick = tick;
        this.orderId = orderId;
    }


    public Customer getCustomer() {
        return customer;
    }

    public String getBookName() {
        return bookName;
    }

    public int getOrderTick() {
        return orderTick;
    }

    public int getOrderId() {
        return orderId;
    }
}
