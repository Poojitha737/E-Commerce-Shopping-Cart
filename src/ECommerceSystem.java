import java.util.*;
import java.sql.*;

abstract class Product {
    private int productID;
    private String name;
    private double price;
    private String category;

    public Product(int productID, String name, double price, String category) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public int getProductID() { return productID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public abstract double applyDiscount();
}

class ElectronicItem extends Product {
    public ElectronicItem(int id, String name, double price) {
        super(id, name, price, "Electronics");
    }
    public double applyDiscount() { return getPrice() * 0.90; }
}

class ClothingItem extends Product {
    public ClothingItem(int id, String name, double price) {
        super(id, name, price, "Clothing");
    }
    public double applyDiscount() { return getPrice() * 0.80; }
}

class Cart {
    private HashMap<Product, Integer> items = new HashMap<>();

    public void addItem(Product p, int quantity) {
        items.put(p, items.getOrDefault(p, 0) + quantity);
        System.out.println(quantity + " x " + p.getName() + " added to cart.");
    }

    public void viewCart() {
        if (items.isEmpty()) {
            System.out.println("\nCart is Empty!");
            return;
        }
        System.out.println("\n---------- YOUR CART ----------");
        for (Product p : items.keySet()) {
            System.out.println(p.getName() + " | Qty: " + items.get(p)
                    + " | Price after discount: " + p.applyDiscount());
        }
        System.out.println("--------------------------------");
    }

    public double getTotal() {
        double total = 0;
        for (Product p : items.keySet()) {
            total += p.applyDiscount() * items.get(p);
        }
        return total;
    }

    public boolean isEmpty() { return items.isEmpty(); }
}

class Order {
    private int orderID;
    private double amount;
    private java.util.Date date;

    public Order(int orderID, double amount) {
        this.orderID = orderID;
        this.amount = amount;
        this.date = new java.util.Date();
    }

    public int getOrderID() { return orderID; }
    public double getAmount() { return amount; }
    public java.util.Date getDate() { return date; }
}

public class ECommerceSystem {
    public static void saveOrderToDB(Order order) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ecommerce_db", "root", ""
            );
            String query = "INSERT INTO orders VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, order.getOrderID());
            ps.setDouble(2, order.getAmount());
            ps.setString(3, order.getDate().toString());
            ps.executeUpdate();
            System.out.println("\nOrder saved to database!");
            con.close();
        } catch (Exception e) {
            System.out.println("DB Error: " + e);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Product> products = new ArrayList<>();
        products.add(new ElectronicItem(1, "Laptop", 50000));
        products.add(new ElectronicItem(2, "Smartphone", 20000));
        products.add(new ClothingItem(3, "T-Shirt", 800));
        products.add(new ClothingItem(4, "Jeans", 1500));

        Cart cart = new Cart();
        int choice;

        while (true) {
            System.out.println("\n=========== ONLINE SHOPPING SYSTEM ===========");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Place Order");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n------ Available Products ------");
                    for (Product p : products) {
                        System.out.println(p.getProductID() + ". " + p.getName()
                                + " | Category: " + p.getCategory()
                                + " | Price: " + p.getPrice());
                    }
                    break;
                case 2:
                    System.out.print("Enter Product ID to add: ");
                    int id = sc.nextInt();
                    Product selected = null;
                    for (Product p : products) {
                        if (p.getProductID() == id) {
                            selected = p;
                            break;
                        }
                    }
                    if (selected == null) {
                        System.out.println("Invalid Product ID!");
                        break;
                    }
                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();
                    cart.addItem(selected, qty);
                    break;
                case 3:
                    cart.viewCart();
                    break;
                case 4:
                    if (cart.isEmpty()) {
                        System.out.println("\nCart is empty! Cannot place order.");
                        break;
                    }
                    double total = cart.getTotal();
                    System.out.println("\nTotal Bill After Discounts: Rs. " + total);
                    int orderID = new Random().nextInt(9000) + 1000;
                    Order order = new Order(orderID, total);
                    saveOrderToDB(order);
                    System.out.println("Order Placed Successfully!");
                    return;
                case 5:
                    System.out.println("Thank you for shopping!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
