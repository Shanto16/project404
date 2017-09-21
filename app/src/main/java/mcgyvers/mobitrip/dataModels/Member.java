package mcgyvers.mobitrip.dataModels;

/**
 * Created by edson on 09/09/17.
 */

public class Member {

    private String name;
    private String phone;
    private String amount;

    public Member(){}

    public Member(String name, String phone, String amount) {
        this.name = name;
        this.phone = phone;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
