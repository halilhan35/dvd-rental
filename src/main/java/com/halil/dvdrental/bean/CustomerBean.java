package com.halil.dvdrental.bean;

import com.halil.dvdrental.dto.CustomerDTO;
import com.halil.dvdrental.service.CustomerService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
@RequiredArgsConstructor
public class CustomerBean implements Serializable {

    private final CustomerService customerService;

    private List<CustomerDTO> customerList;

    private CustomerDTO selectedCustomerDTO = new CustomerDTO();

    public void prepareNew() {
        selectedCustomerDTO = new CustomerDTO();
    }

    public void prepareEdit(CustomerDTO customer) {
        selectedCustomerDTO =
                customerService.getCustomerDTOById(customer.getCustomerId());
    }

    public void save() {

        System.out.println(">>> CUSTOMER SAVE ÇAĞRILDI");

        System.out.println(
                ">>> Customer: "
                        + selectedCustomerDTO.getFirstName()
                        + " "
                        + selectedCustomerDTO.getLastName()
        );

        CustomerDTO saved =
                customerService.saveCustomerDTO(selectedCustomerDTO);

        System.out.println(">>> SERVICE SONUCU: " + saved);

        if (saved != null) {
            customerList = customerService.getAllCustomerDTOs();
            selectedCustomerDTO = new CustomerDTO();

            System.out.println(">>> CUSTOMER SAVE BAŞARILI");
        }
    }

    public void delete() {

        boolean deleted =
                customerService.deleteCustomer(
                        selectedCustomerDTO.getCustomerId()
                );

        if (deleted) {

            customerList = customerService.getAllCustomerDTOs();
            selectedCustomerDTO = new CustomerDTO();

        } else {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Müşteri silinemedi",
                            "Bu müşterinin kiralama kayıtları olduğu için silinemez."
                    )
            );
        }
    }

    public List<CustomerDTO> getCustomerList() {

        if (customerList == null) {
            customerList = customerService.getAllCustomerDTOs();
        }

        return customerList;
    }
}