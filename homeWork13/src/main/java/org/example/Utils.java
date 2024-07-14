package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Utils {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public static User createUser(URI uri, User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type", "application/json")
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST test: " + response.statusCode());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User updateUser(URI uri, int id, User user1) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users/" + id))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type", "application/json")
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("PUT test: " + response.statusCode());
        return GSON.fromJson(response.body(), User.class);
    }

    public static void deleteUser(URI uri, int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("DELETE test: " + response.statusCode());
    }

    public static List<User> getAllUsers(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users"))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET all users test: " + response.statusCode());
        Type userListType = new TypeToken<List<User>>(){}.getType();
        return GSON.fromJson(response.body(), userListType);
    }

    public static User getUserById(URI uri, int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users/" + id))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET by id test: " + response.statusCode());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User getUserByUsername(URI uri, String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users?username=" + username))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET by username test: " + response.statusCode());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        return GSON.fromJson(jsonArray.get(0), User.class);
    }

    public static int userLastPostFind(URI uri, int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users/" + userId + "/posts"))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        int lastPostId = -1;
        for (String line : responseBody.split("\n")) {
            String[] parts = line.trim().split(",");
            for (String part : parts) {
                if (part.contains("\"id\":")) {
                    int postId = Integer.parseInt(part.trim().split(":")[1].trim());
                    if (postId > lastPostId) {
                        lastPostId = postId;
                    }
                    break;
                }
            }
        }
        System.out.println("Last post finded.");
        return lastPostId;
    }

    public static String findLastPostComments(URI uri, int userId, int postId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/posts/" + postId + "/comments?userId=" + userId))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("All comments finded.");
        return response.body();
    }

    public static void writeCommentsToJson(String commentsJson, int userId, int postId) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File("user-" + userId + "-post-" + postId + "-comments.json");
        FileWriter writer = new FileWriter(file);

        JsonArray commentsArray = JsonParser.parseString(commentsJson).getAsJsonArray();
        gson.toJson(commentsArray, writer);

        System.out.println("File writed.");
        writer.close();
    }

    public static void findAndWriteTodos(URI uri, int userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users/" + userId + "/todos"))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray todos = new JSONArray(response.body());
        JSONArray completedTodos = new JSONArray();
        for (int i = 0; i < todos.length(); i++) {
            JSONObject todo = todos.getJSONObject(i);
            if (!todo.getBoolean("completed")) {
                completedTodos.put(todo);
            }
        }
        System.out.println("Uncomplited todos find.");
        FileWriter writer = new FileWriter("user-" + userId + "-uncompleted-todos.json");
        new GsonBuilder().setPrettyPrinting().create().toJson(completedTodos, writer);
        writer.close();
        System.out.println("File writed");
    }
}
