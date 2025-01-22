package ru.otus.java.pro.springcontext;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.otus.java.pro.springcontext.service.Cart;

import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "ru.otus.java.pro.springcontext")
public class ShoppingCartApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ShoppingCartApplication.class);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add product to cart");
            System.out.println("2. Remove product from cart");
            System.out.println("3. View cart");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            Cart cart = context.getBean(Cart.class);

            switch (option) {
                case 1:
                    System.out.print("Enter product id to add: ");
                    int addId = scanner.nextInt();
                    cart.addProductById(addId);
                    break;
                case 2:
                    System.out.print("Enter product id to remove: ");
                    int removeId = scanner.nextInt();
                    cart.removeProductById(removeId);
                    break;
                case 3:
                    System.out.println("Cart items: " + cart.getItems());
                    break;
                case 4:
                    context.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }
}