package ru.otus.java.pro.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;
import java.util.Scanner;

public class Application {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            Properties properties = new Properties();
            properties.load(Application.class.getClassLoader().getResourceAsStream("hibernate.properties"));
            configuration.setProperties(properties);

            configuration.addAnnotatedClass(Customer.class);
            configuration.addAnnotatedClass(Product.class);
            configuration.addAnnotatedClass(Purchase.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(String[] args) {
        populateTestData();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("1. View products by customer");
                System.out.println("2. View customers by product");
                System.out.println("3. Delete product");
                System.out.println("4. Delete customer");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1 -> viewProductsByCustomer(scanner);
                    case 2 -> viewCustomersByProduct(scanner);
                    case 3 -> deleteProduct(scanner);
                    case 4 -> deleteCustomer(scanner);
                    case 5 -> {
                        sessionFactory.close();
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            }
        }
    }

    private static void populateTestData() {
        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                Customer customer1 = new Customer();
                customer1.setName("John Doe");
                session.save(customer1);

                Customer customer2 = new Customer();
                customer2.setName("Jane Smith");
                session.save(customer2);

                Product product1 = new Product();
                product1.setName("Laptop");
                product1.setPrice(1000.0);
                session.save(product1);

                Product product2 = new Product();
                product2.setName("Smartphone");
                product2.setPrice(500.0);
                session.save(product2);

                Purchase purchase1 = new Purchase();
                purchase1.setCustomer(customer1);
                purchase1.setProduct(product1);
                session.save(purchase1);

                Purchase purchase2 = new Purchase();
                purchase2.setCustomer(customer1);
                purchase2.setProduct(product2);
                session.save(purchase2);

                Purchase purchase3 = new Purchase();
                purchase3.setCustomer(customer2);
                purchase3.setProduct(product1);
                session.save(purchase3);

                session.getTransaction().commit();
            } catch (Exception ex) {
                session.getTransaction().rollback();
                throw ex;
            }
        }
    }

    private static void viewProductsByCustomer(Scanner scanner) {
        System.out.print("Enter customer ID: ");
        Long customerId = scanner.nextLong();
        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.get(Customer.class, customerId);
            if (customer != null) {
                customer.getPurchases().size();
                System.out.println("Products bought by " + customer.getName() + ":");
                customer.getPurchases().forEach(purchase -> System.out.println(purchase.getProduct().getName()));
            } else {
                System.out.println("Customer not found.");
            }
        }
    }

    private static void viewCustomersByProduct(Scanner scanner) {
        System.out.print("Enter product ID: ");
        Long productId = scanner.nextLong();
        try (Session session = sessionFactory.openSession()) {
            Product product = session.get(Product.class, productId);
            if (product != null) {
                product.getPurchases().size();
                System.out.println("Customers who bought " + product.getName() + ":");
                product.getPurchases().forEach(purchase -> System.out.println(purchase.getCustomer().getName()));
            } else {
                System.out.println("Product not found.");
            }
        }
    }

    private static void deleteProduct(Scanner scanner) {
        System.out.print("Enter product ID: ");
        Long productId = scanner.nextLong();
        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                Product product = session.get(Product.class, productId);
                if (product != null) {
                    session.delete(product);
                    session.getTransaction().commit();
                    System.out.println("Product deleted.");
                } else {
                    System.out.println("Product not found.");
                }
            } catch (Exception ex) {
                session.getTransaction().rollback();
                throw ex;
            }
        }
    }

    private static void deleteCustomer(Scanner scanner) {
        System.out.print("Enter customer ID: ");
        Long customerId = scanner.nextLong();
        try (Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                Customer customer = session.get(Customer.class, customerId);
                if (customer != null) {
                    session.delete(customer);
                    session.getTransaction().commit();
                    System.out.println("Customer deleted.");
                } else {
                    System.out.println("Customer not found.");
                }
            } catch (Exception ex) {
                session.getTransaction().rollback();
                throw ex;
            }
        }
    }
}