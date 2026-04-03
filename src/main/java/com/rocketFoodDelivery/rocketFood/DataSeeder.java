package com.rocketFoodDelivery.rocketFood;

import com.github.javafaker.Faker;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("null")
@Component
@RequiredArgsConstructor
public class DataSeeder {

   private final UserRepository userRepository;
   private final RestaurantRepository restaurantRepository;
   private final ProductOrderRepository productOrderRepository;
   private final ProductRepository productRepository;
   private final OrderStatusRepository orderStatusRepository;
   private final OrderRepository orderRepository;
   private final EmployeeRepository employeeRepository;
   private final CustomerRepository customerRepository;
   private final AddressRepository addressRepository;
   private final CourierStatusRepository courierStatusRepository;
   private final CourierRepository courierRepository;
   private final Faker faker = new Faker();

   @PostConstruct
   @SuppressWarnings("null")
   public void seedData() {
       System.out.println("Starting database seeding...");
       
       seedUsers();
       seedAddresses();
       seedOrderStatuses();
       seedCourierStatuses();
       seedRestaurants();
       seedEmployees();
       seedCustomers();
       seedCouriers();
       seedProducts();
       seedOrdersAndProductOrders();
       
       System.out.println("✓ Database seeding process completed!");
   }

   private void seedUsers() {
       if (userRepository.count() > 0) {
           System.out.println("⚠ Users already exist. Skipping user seeding.");
           return;
       }
       
       // Create 30 users for all entities (restaurants, employees, customers, couriers)
       List<UserEntity> users = new ArrayList<>();
       
       // User #1 (index 0) - both@gmail.com - will be customer #1 and courier #1
       users.add(UserEntity.builder()
               .name(faker.name().fullName())
               .email("both@gmail.com")
               .password("password")
               .build());
       
       // User #2 (index 1) - customer@gmail.com - will be customer #2
       users.add(UserEntity.builder()
               .name(faker.name().fullName())
               .email("customer@gmail.com")
               .password("password")
               .build());
       
       // User #3 (index 2) - courier@gmail.com - will be courier #2
       users.add(UserEntity.builder()
               .name(faker.name().fullName())
               .email("courier@gmail.com")
               .password("password")
               .build());
       
       // Create remaining users (index 3-29)
       for (int i = 3; i < 30; i++) {
           users.add(UserEntity.builder()
                   .name(faker.name().fullName())
                   .email("user" + i + "@" + faker.internet().domainName())
                   .password("password" + i)
                   .build());
       }
       
       userRepository.saveAll(users);
       System.out.println("✓ Seeded " + users.size() + " users");
   }

   private void seedAddresses() {
       if (addressRepository.count() > 0) {
           System.out.println("⚠ Addresses already exist. Skipping address seeding.");
           return;
       }
       
       List<Address> addresses = new ArrayList<>();
       for (int i = 0; i < 30; i++) {
           addresses.add(Address.builder()
                   .streetAddress(faker.address().streetAddress())
                   .city(faker.address().city())
                   .postalCode(faker.address().zipCode())
                   .build());
       }
       
       addressRepository.saveAll(addresses);
       System.out.println("✓ Seeded " + addresses.size() + " addresses");
   }

   private void seedOrderStatuses() {
       if (orderStatusRepository.count() > 0) {
           System.out.println("⚠ Order statuses already exist. Skipping order status seeding.");
           return;
       }
       
       List<OrderStatus> orderStatuses = new ArrayList<>();
       for (String statusName : Arrays.asList("pending", "in progress", "delivered")) {
           orderStatuses.add(OrderStatus.builder().name(statusName).build());
       }
       
       orderStatusRepository.saveAll(orderStatuses);
       System.out.println("✓ Seeded " + orderStatuses.size() + " order statuses");
   }

   private void seedCourierStatuses() {
       if (courierStatusRepository.count() > 0) {
           System.out.println("⚠ Courier statuses already exist. Skipping courier status seeding.");
           return;
       }
       
       List<CourierStatus> courierStatuses = new ArrayList<>();
       for (String statusName : Arrays.asList("available", "busy", "on-break", "offline", "delivering")) {
           courierStatuses.add(CourierStatus.builder().name(statusName).build());
       }
       
       courierStatusRepository.saveAll(courierStatuses);
       System.out.println("✓ Seeded " + courierStatuses.size() + " courier statuses");
   }

   private void seedRestaurants() {
       if (restaurantRepository.count() > 0) {
           System.out.println("⚠ Restaurants already exist. Skipping restaurant seeding.");
           return;
       }
       
       List<Restaurant> restaurants = new ArrayList<>();
       List<UserEntity> users = userRepository.findAll();
       List<Address> addresses = addressRepository.findAll();
       
       // Create 8 restaurants using users[0-7] and addresses[0-7]
       for (int i = 0; i < 8; i++) {
           restaurants.add(Restaurant.builder()
                   .userEntity(users.get(i))
                   .address(addresses.get(i))
                   .name(faker.company().name() + " Restaurant")
                   .phone("+1-555-" + String.format("%04d", 1000 + i))
                   .email("restaurant" + i + "@" + faker.internet().domainName())
                   .priceRange(ThreadLocalRandom.current().nextInt(1, 4))
                   .build());
       }
       
       restaurantRepository.saveAll(restaurants);
       System.out.println("✓ Seeded " + restaurants.size() + " restaurants");
   }

   private void seedEmployees() {
       if (employeeRepository.count() > 0) {
           System.out.println("⚠ Employees already exist. Skipping employee seeding.");
           return;
       }
       
       List<Employee> employees = new ArrayList<>();
       List<UserEntity> users = userRepository.findAll();
       List<Address> addresses = addressRepository.findAll();
       
       // Create 5 employees using users[8-12] and addresses[8-12]
       for (int i = 0; i < 5; i++) {
           employees.add(Employee.builder()
                   .userEntity(users.get(8 + i))
                   .address(addresses.get(8 + i))
                   .phone("+1-555-" + String.format("%04d", 2000 + i))
                   .email("employee" + i + "@" + faker.internet().domainName())
                   .build());
       }
       
       employeeRepository.saveAll(employees);
       System.out.println("✓ Seeded " + employees.size() + " employees");
   }

   private void seedCustomers() {
       if (customerRepository.count() > 0) {
           System.out.println("⚠ Customers already exist. Skipping customer seeding.");
           return;
       }
       
       List<Customer> customers = new ArrayList<>();
       List<UserEntity> users = userRepository.findAll();
       List<Address> addresses = addressRepository.findAll();
       
       // Customer #1 - user id 1 (index 0) - both@gmail.com
       customers.add(Customer.builder()
               .userEntity(users.get(0))
               .address(addresses.get(13))
               .phone("+1-555-3000")
               .email(users.get(0).getEmail())
               .active(true)
               .build());
       
       // Customer #2 - user id 2 (index 1) - customer@gmail.com
       customers.add(Customer.builder()
               .userEntity(users.get(1))
               .address(addresses.get(14))
               .phone("+1-555-3001")
               .email(users.get(1).getEmail())
               .active(true)
               .build());
       
       // Create remaining 6 customers using users[15-20] and addresses[15-20]
       for (int i = 2; i < 8; i++) {
           customers.add(Customer.builder()
                   .userEntity(users.get(13 + i))
                   .address(addresses.get(13 + i))
                   .phone("+1-555-" + String.format("%04d", 3000 + i))
                   .email("customer" + i + "@" + faker.internet().domainName())
                   .active(true)
                   .build());
       }
       
       customerRepository.saveAll(customers);
       System.out.println("✓ Seeded " + customers.size() + " customers");
   }

   private void seedCouriers() {
       if (courierRepository.count() > 0) {
           System.out.println("⚠ Couriers already exist. Skipping courier seeding.");
           return;
       }
       
       List<Courier> couriers = new ArrayList<>();
       List<UserEntity> users = userRepository.findAll();
       List<Address> addresses = addressRepository.findAll();
       List<CourierStatus> courierStatuses = courierStatusRepository.findAll();
       
       // Courier #1 - user id 1 (index 0) - both@gmail.com
       couriers.add(Courier.builder()
               .userEntity(users.get(0))
               .address(addresses.get(21))
               .phone("+1-555-4000")
               .email(users.get(0).getEmail())
               .courierStatus(courierStatuses.get(0))
               .active(true)
               .build());
       
       // Courier #2 - user id 3 (index 2) - courier@gmail.com
       couriers.add(Courier.builder()
               .userEntity(users.get(2))
               .address(addresses.get(22))
               .phone("+1-555-4001")
               .email(users.get(2).getEmail())
               .courierStatus(courierStatuses.get(1 % courierStatuses.size()))
               .active(true)
               .build());
       
       // Create remaining 6 couriers using users[23-28] and addresses[23-28]
       for (int i = 2; i < 8; i++) {
           couriers.add(Courier.builder()
                   .userEntity(users.get(21 + i))
                   .address(addresses.get(21 + i))
                   .phone("+1-555-" + String.format("%04d", 4000 + i))
                   .email("courier" + i + "@" + faker.internet().domainName())
                   .courierStatus(courierStatuses.get(i % courierStatuses.size()))
                   .active(true)
                   .build());
       }
       
       courierRepository.saveAll(couriers);
       System.out.println("✓ Seeded " + couriers.size() + " couriers");
   }

   private void seedProducts() {
       if (productRepository.count() > 0) {
           System.out.println("⚠ Products already exist. Skipping product seeding.");
           return;
       }
       
       List<Product> products = new ArrayList<>();
       List<Restaurant> restaurants = restaurantRepository.findAll();
       Random random = new Random();
       
       // Create 5-7 products per restaurant
       for (Restaurant restaurant : restaurants) {
           int numProducts = random.nextInt(3) + 5;
           for (int j = 0; j < numProducts; j++) {
               products.add(Product.builder()
                       .restaurant(restaurant)
                       .name(faker.food().dish())
                       .description(faker.food().ingredient() + " with " + faker.food().spice())
                       .cost(random.nextInt(20) + 5)
                       .build());
           }
       }
       
       productRepository.saveAll(products);
       System.out.println("✓ Seeded " + products.size() + " products across " + restaurants.size() + " restaurants");
   }

   @SuppressWarnings("all")
   private void seedOrdersAndProductOrders() {
       if (orderRepository.count() > 0) {
           System.out.println("⚠ Orders already exist. Skipping order and product order seeding.");
           return;
       }
       
       Random random = new Random();
       List<Restaurant> restaurants = restaurantRepository.findAll();
       List<Customer> customers = customerRepository.findAll();
       List<OrderStatus> orderStatuses = orderStatusRepository.findAll();
       List<Courier> couriers = courierRepository.findAll();
       
       int totalProductOrders = 0;
       
       for (int i = 0; i < 10; i++) {
           // Randomly assign courier (50% chance)
           Courier courier = random.nextBoolean() ? couriers.get(random.nextInt(couriers.size())) : null;
           
           // Order status logic: courier assigned = "in progress" or "delivered", no courier = "pending"
           OrderStatus orderStatus = courier != null ? orderStatuses.get(random.nextInt(2) + 1) : orderStatuses.get(0);
           
           @SuppressWarnings("null")
           Order order = Order.builder()
                   .restaurant(restaurants.get(random.nextInt(restaurants.size())))
                   .customer(customers.get(random.nextInt(customers.size())))
                   .orderStatus(orderStatus)
                   .courier(courier)
                   .restaurantRating(random.nextInt(5) + 1)
                   .build();
           orderRepository.save(order);
           
           // Add 2-4 product orders per order
           List<Product> restaurantProducts = productRepository.findByRestaurantId(order.getRestaurant().getId());
           int numProductOrders = random.nextInt(3) + 2;
           
           for (int j = 0; j < numProductOrders && j < restaurantProducts.size(); j++) {
               Product product = restaurantProducts.get(j);
               try {
                   productOrderRepository.save(ProductOrder.builder()
                           .product(product)
                           .order(order)
                           .productQuantity(random.nextInt(3) + 1)
                           .productUnitCost(product.getCost())
                           .build());
                   totalProductOrders++;
               } catch (Exception e) {
                   // Skip duplicate or validation errors
               }
           }
       }
       
       System.out.println("✓ Seeded 10 orders with " + totalProductOrders + " product orders");
   }
}
