package metabase.model.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * REST client for interacting with the Metabase API. Provides methods to manage
 * dashboards, cards, users, groups, and collections. Handles authentication
 * using API key and session-based requests.
 *
 * @author sergi
 */
public class restClientMetaBase {

    private static String URL = "http://localhost:3000/api/";
    private static String API_KEY = "mb_Jrwc2cVoT1JkWn8O+0qz1k5v/m8l/WDjgpZOchjc3xA=";
    private static String USERNAME = "sergipanec@gmail.com";
    private static String PASSWORD = "odoo1234";

    /**
     * Initializes and configures an HTTP client with default parameters.
     *
     * @return HttpClient configured with HTTP/1.1, normal redirects, and
     * 20-second connection timeout
     * @throws RuntimeException if an error occurs during initialization
     */
    public HttpClient initClient() {

        try {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar HttpClient", e);
        }
    }

    /**
     * Tests authentication with the Metabase API by making a request to the
     * permissions/group endpoint. Prints the status code and response body to
     * console.
     */
    public void tryAuth() {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "permissions/group"))
                    .header("x-api-key", this.API_KEY)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Lists information for a specific dashboard from Metabase. Makes a GET
     * request to /dashboard/1 endpoint. Prints the status code and dashboard
     * information to console.
     */
    public void listDashboards() {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard/1"))
                    .header("x-api-key", this.API_KEY)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("DASHBOARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Lists all available cards in Metabase. Makes a GET request to /card
     * endpoint with 'all' filter. Prints the status code and card information
     * to console.
     */
    public void listCards() {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "card/?f=all"))
                    .header("x-api-key", this.API_KEY)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Creates a new card in Metabase with the provided data. Useful for
     * creating cards with simple SQL queries (e.g., sales per day/month, active
     * users).
     *
     * @param data JSON string containing the card configuration and query
     * information
     */
    public void createCardInfo(String data) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "card/"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(data))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Creates a new user in Metabase.
     *
     * @param first_name user's first name
     * @param last_name user's last name
     * @param email user's email address
     * @param id group ID to assign the user to
     * @param is_group_manager whether the user should be a group manager
     */
    public void createUser(String first_name, String last_name, String email, Integer id, Boolean is_group_manager) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "user/"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(userData(first_name, last_name, email, id, is_group_manager)))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

    }

    /**
     * Generates JSON formatted user data for user creation requests.
     *
     * @param first_name user's first name
     * @param last_name user's last name
     * @param email user's email address
     * @param id group ID
     * @param is_group_manager whether the user is a group manager
     * @return JSON string with user data
     */
    private String userData(String first_name, String last_name, String email, Integer id, Boolean is_group_manager) {
        return "{\n"
                + "  \"email\": \"" + email + "\",\n"
                + "  \"first_name\": \"" + first_name + "\",\n"
                + "  \"last_name\": \"" + last_name + "\",\n"
                + "  \"login_attributes\": {\n"
                + "    \"propertyName*\": \"anything\"\n"
                + "  },\n"
                + "  \"source\": \"admin\",\n"
                + "  \"user_group_memberships\": [\n"
                + "    {\n"
                + "      \"id\": " + id + ",\n"
                + "      \"is_group_manager\": " + is_group_manager + "\n"
                + "    }\n"
                + "  ]\n"
                + "}";
    }

    /**
     * Creates a new permission group in Metabase.
     *
     * @param name name of the group
     * @param is_tenant_group whether this is a tenant group
     */
    public void createGroup(String name, Boolean is_tenant_group) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "permissions/group"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(groupData(name, is_tenant_group)))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

    }

    /**
     * Generates JSON formatted group data for group creation requests.
     *
     * @param name group name
     * @param is_tenant_group whether this is a tenant group
     * @return JSON string with group data
     */
    private String groupData(String name, Boolean is_tenant_group) {
        return "{\n"
                + "  \"is_tenant_group\": " + is_tenant_group.toString() + ",\n"
                + "  \"name\": \"" + name + "\"\n"
                + "}";

    }

    /**
     * Adds an existing user to a permission group.
     *
     * @param group_id ID of the group
     * @param is_group_manager whether the user should be a manager of this
     * group
     * @param user_id ID of the user to add
     */
    public void addUserToGroup(Integer group_id, Boolean is_group_manager, Integer user_id) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "permissions/membership"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(userToGroupInfo(group_id, is_group_manager, user_id)))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Generates JSON formatted data for adding a user to a group.
     *
     * @param group_id ID of the group
     * @param is_group_manager whether the user is a group manager
     * @param user_id ID of the user
     * @return JSON string with membership data
     */
    private String userToGroupInfo(Integer group_id, Boolean is_group_manager, Integer user_id) {
        return "{\n"
                + "  \"group_id\": " + group_id.toString() + ",\n"
                + "  \"is_group_manager\": " + is_group_manager.toString() + ",\n"
                + "  \"user_id\": " + user_id.toString() + "\n"
                + "}";
    }

    /**
     * Obtains a session ID by authenticating with username and password. Parses
     * the session ID from the API response.
     *
     * @param json JSON string containing username and password credentials
     * @return session ID string extracted from the response
     */
    private String getSessionId(String json) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response = null;
        String id;
        String regex[];
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "session/"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

        regex = response.body().toString().split(":");
        id = regex[1];
        id = id.substring(1, id.length() - 2);
        System.out.println("Id: " + id);

        return id;
    }

    /**
     * Creates a new collection in Metabase. Requires session authentication.
     * Creates a test collection with predefined values (authority_level:
     * official, parent_id: 1). Prints the status code and response body to
     * console.
     */
    public void postCollection() {
        String sessionJson = "{"
                + "\"username\": \"" + this.USERNAME + "\","
                + "\"password\": \"" + this.PASSWORD + "\""
                + "}";
        String collectionJson = "{"
                + "\"authority_level\": \"official\","
                + "\"description\": \"This is a test\","
                + "\"name\": \"test\","
                + "\"namespace\": \"test\","
                + "\"parent_id\": 1"
                + "}";

        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "collection"))
                    .header("x-api-key", this.API_KEY)
                    .header("X-Metabase-Session", getSessionId(sessionJson))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(collectionJson))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("POST Body: " + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Creates a new dashboard in Metabase. Requires session authentication.
     * Creates a test dashboard named "Dashboard de Proba" in collection ID 1.
     * Prints the status code and response body to console.
     */
    public void createDashBoard() {
        String sessionJson = "{"
                + "\"username\": \"" + this.USERNAME + "\","
                + "\"password\": \"" + this.PASSWORD + "\""
                + "}";
        String dashboardJson = "{"
                + "  \"name\": \"Dashboard de Proba\","
                + "  \"description\": \"Aixo es un dashboard de proba\","
                + "  \"collection_id\": 1"
                + "}";
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard"))
                    .header("x-api-key", this.API_KEY)
                    .header("X-Metabase-Session", getSessionId(sessionJson))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(dashboardJson))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("POST Body: " + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    /**
     * Makes a dashboard publicly accessible by generating a public link.
     *
     * @param dashboard_id ID of the dashboard to make public
     * @return UUID string of the public link
     */
    public String makeDashBoardPublic(String dashboard_id) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response = null;
        String regex[];
        String uuid;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard/" + dashboard_id + "/public_link"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString("{}"))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

        regex = response.body().toString().split(":");
        uuid = regex[1];
        uuid = uuid.substring(1, uuid.length() - 2);
        return uuid;
    }

    /**
     * Deletes the public link of a dashboard, making it private again.
     *
     * @param dashboard_id ID of the dashboard whose public link should be
     * deleted
     */
    public void deleteDashBoardPublicLink(String dashboard_id) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard/" + dashboard_id + "/public_link"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("DELETE Status Code: " + response.statusCode());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

    }
}
