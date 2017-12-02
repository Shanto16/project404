package mcgyvers.mobitrip.dataModels;

/**
 * Created by edson on 09/09/17.
 *
 * member class
 *
 */

public class Member {

    private String name;
    private String phone;
    private String amount;
    private String expense;

    public Member(){}

    public Member(String name, String phone, String amount) {
        this.name = name;
        this.phone = phone;
        this.amount = amount;
        this.expense = "0";
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

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }
}
