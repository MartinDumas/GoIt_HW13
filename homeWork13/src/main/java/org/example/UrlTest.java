package org.example;



import java.net.URI;
import java.util.List;

public class UrlTest {

    public static final String DEFAULT_USERS = "https://jsonplaceholder.typicode.com";

    public static void main(String[] args) throws Exception {

        //  Task 1
        User user = new User();
        user.setId(1);
        user.setName("Bobo");
        user.setUsername("Bobojan");
        user.setEmail("bobo228@gmail.com");
        User userCreate = Utils.createUser(URI.create(DEFAULT_USERS), user);
        System.out.println(userCreate);
        System.out.println("--------------------------------------------------------");

        User user1 = new User();
        user1.setName("Bobo");
        user1.setUsername("Bobojan");
        user1.setEmail("bobo228@gmail.com");
        User updatedUser = Utils.updateUser(URI.create(DEFAULT_USERS), 2, user1);
        System.out.println("Updated user: " + updatedUser);
        System.out.println("--------------------------------------------------------");

        Utils.deleteUser(URI.create(DEFAULT_USERS), 6);
        System.out.println("--------------------------------------------------------");

        List<User> allUsers = Utils.getAllUsers(URI.create(DEFAULT_USERS));
        System.out.println("All users: " + allUsers.toString());
        System.out.println("--------------------------------------------------------");

        User userById = Utils.getUserById(URI.create(DEFAULT_USERS), 2);
        System.out.println("User by id: " + userById);
        System.out.println("--------------------------------------------------------");

        String userName = "Leopoldo_Corkery";
        User userByUsername = Utils.getUserByUsername(URI.create(DEFAULT_USERS), userName);
        System.out.println("User by username: " + userByUsername);
        System.out.println("--------------------------------------------------------");

        //  Task 2
        int userId = 1;
        int lastPostId = Utils.userLastPostFind(URI.create(DEFAULT_USERS), userId);
        String commentsJson = Utils.findLastPostComments(URI.create(DEFAULT_USERS), userId, lastPostId);
        Utils.writeCommentsToJson(commentsJson, userId, lastPostId);
        System.out.println("--------------------------------------------------------");

        //  Task 3
        Utils.findAndWriteTodos(URI.create(DEFAULT_USERS), 1);
    }
}
