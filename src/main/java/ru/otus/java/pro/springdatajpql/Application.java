package ru.otus.java.pro.springdatajpql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.otus.java.pro.springdatajpql.entity.Address;
import ru.otus.java.pro.springdatajpql.entity.Client;
import ru.otus.java.pro.springdatajpql.entity.Phone;

import java.util.Arrays;
import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        Properties properties = new Properties();
        try {
            properties.load(Application.class.getClassLoader().getResourceAsStream("hibernate.properties"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        configuration.setProperties(properties);

        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Address.class);
        configuration.addAnnotatedClass(Phone.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Address address = new Address();
            address.setStreet("123 Main St");

            Phone phone1 = new Phone();
            phone1.setNumber("123-456-7890");

            Phone phone2 = new Phone();
            phone2.setNumber("098-765-4321");

            Client client = new Client();
            client.setName("John Doe");
            client.setAddress(address);
            client.setPhones(Arrays.asList(phone1, phone2));

            phone1.setClient(client);
            phone2.setClient(client);

            session.save(client);

            session.getTransaction().commit();
        }

        sessionFactory.close();
    }
}