package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.data.validation.CustomerValidator;
import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CustomerDialog extends EntityDialog<Customer> {

    private static final I18N I18N = new I18N(CustomerDialog.class);

    private final JTextField customerName = new JTextField(20);
    private final JTextField customerDic = new JTextField(20);
    private final JTextField customerPhone = new JTextField(20);
    private final JTextField customerMail = new JTextField(20);

    private final Customer customer;

    public CustomerDialog(Customer customer, String title) {
        super(title);
        this.customer = customer;
        setValues();
        addFields();
    }

    private void setValues() {
        customerName.setText(customer.getName());
        customerDic.setText(customer.getDic());
        customerPhone.setText(customer.getPhone());
        customerMail.setText(customer.getMail());
    }

    private void addFields() {
        add(I18N.getString("name"), customerName, true);
        add(I18N.getString("dic"), customerDic, true);
        add(I18N.getString("phone"), customerPhone);
        add(I18N.getString("mail"), customerMail);
    }

    @Override
    Customer getEntity() {
        customer.setName(customerName.getText());
        customer.setDic(customerDic.getText());
        customer.setPhone(customerPhone.getText());
        customer.setMail(customerMail.getText());
        return customer;
    }

    @Override
    void fieldsListener(JButton ok) {
        DocumentListener docListener = new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkForText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkForText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkForText();
            }

            private void checkForText() {
                String dic = customerDic.getText();
                String name = customerName.getText();
                String phone = customerPhone.getText().replaceAll("[+ ()-]", "");
                String mail = customerMail.getText();

                ok.setEnabled(CustomerValidator.validate(dic, name, phone, mail));
            }
        };
        customerDic.getDocument().addDocumentListener(docListener);
        customerName.getDocument().addDocumentListener(docListener);
        customerPhone.getDocument().addDocumentListener(docListener);
        customerMail.getDocument().addDocumentListener(docListener);
    }

}
