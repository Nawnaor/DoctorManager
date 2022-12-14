package project.kyawmyoag.doctormanager;

public class InventoryItem {
    private String itemName;
    private String itemName2;
    private String date;
    private int total_available, sold, profit;

    public InventoryItem(){

    }

    public InventoryItem(String itemName, String itemName2,String date, int total_available, int sold, int profit)
    {
        this.itemName=itemName;
        this.itemName2=itemName2;
        this.date = date;
        this.total_available=total_available;
        this.sold=sold;
        this.profit =profit;
    }


    public InventoryItem(String itemName,String itemName2,String date, int total_available,int sold)
    {
        this.itemName=itemName;
        this.itemName2=itemName2;
        this.date = date;
        this.total_available=total_available;
        this.sold=sold;
        this.profit = (this.total_available-this.sold);
    }

    public String getItemName() { return itemName; }
    public String getItemName2() { return itemName2; }
    public String getdate() {return date;}

    public int getTotal_available() {
        return total_available;
    }

    public int getSold() {
        return sold;
    }

    public int getProfit() {
        return profit;
    }

}

