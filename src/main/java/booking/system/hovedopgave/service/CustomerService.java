package booking.system.hovedopgave.service;

import booking.system.hovedopgave.model.Customer;
import booking.system.hovedopgave.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public ArrayList<Customer> getAllCustomers() {
        return new ArrayList<>(customerRepository.findAll());
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public Customer saveCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new RuntimeException("A customer with this email already exists: " + customer.getEmail());
        }
        return customerRepository.save(customer);
    }
}

