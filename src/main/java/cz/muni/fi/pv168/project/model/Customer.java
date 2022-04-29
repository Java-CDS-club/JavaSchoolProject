package cz.muni.fi.pv168.project.model;

import java.util.Objects;

public class Customer {

    private Long id;

    private String name;

    private String dic;

    private String phone;

    private String mail;

    private int taskCount;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer(String name, String dic, String phone, String mail, int taskCount) {
        this.name = name;
        this.dic = dic;
        this.phone = phone;
        this.mail = mail;
        this.taskCount = taskCount;
    }

    public Customer(String name, String dic, String phone, String mail) {
        this(name, dic, phone, mail, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public static Customer create() {
        return new Customer( "", "", "", "", 0);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer that = (Customer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
}
