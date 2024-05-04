package org.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.example.model.Projects;
import org.example.services.ProjectService;
import org.example.services.UsersServices;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ProjectServlet", urlPatterns = "/api/projects")
public class ProjectServlet extends HttpServlet {
    private final ProjectService projectService = new ProjectService();
    private final UsersServices usersService = new UsersServices(); // Assuming you have a UsersService class
    private final Gson gson = new Gson();

    // Handle GET requests
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        String projectIdString = request.getParameter("project_id");
        if (projectIdString != null) {
            if ("show all".equalsIgnoreCase(projectIdString)) {
                getAllProjects(request, response); // Get all Projects
            } else if (!projectIdString.isEmpty()) {
                // Handle specific project ID logic here
                Long projectId = Long.parseLong(projectIdString);
                getSingleProjectById(request, response, projectId);
            }
            else {
                // Handle other cases (e.g., invalid input)
                // Return an error message or redirect to an error page
                response.getWriter().write("Invalid project ID");
            }
        }
    }

    // Method to get a single project by ID
    private void getSingleProjectById(HttpServletRequest request, HttpServletResponse response, long projectId) throws ServletException, IOException {
        try {
            Projects project = projectService.getProjectById(projectId);
            if (project != null) {
                // Convert project object to JSON and send as response
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(gson.toJson(project));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Project not found.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Project ID format.");
        }
    }

    // Method to get all projects
    private void getAllProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        List<Projects> projectsList = projectService.getAllProjects();
        String json = gson.toJson(projectsList);
        response.getWriter().write(json);
    }

    // Handle POST requests
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Add new project
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        BufferedReader reader = request.getReader();
        Projects project = gson.fromJson(reader, Projects.class);
        projectService.addProject(project);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Project added successfully!");
    }

    // Handle PUT requests
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Update project
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        BufferedReader reader = request.getReader();
        Projects updatedProject = gson.fromJson(reader, Projects.class);
        projectService.updateProject(updatedProject);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Project updated successfully!");
    }

    // Handle DELETE requests
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        String projectIdString = request.getParameter("project_id");
        if (projectIdString != null && !projectIdString.isEmpty()) {
            try {
                Long projectId = Long.parseLong(projectIdString);
                projectService.deleteProject(projectId);
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Project deleted successfully!");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid project ID format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Project ID parameter is required.");
        }
    }

    // Helper method to check user authentication
    private boolean isUserAuthenticated(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            // Extract username and password from the Authorization header
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];

            // Check if the provided username and password match the admin credentials
            return isValidAdmin(username, password);
        }
        return false; // Authorization header missing or invalid
    }

    // Helper method to validate admin credentials using database check
    private boolean isValidAdmin(String username, String password) {
        // Replace this with actual database logic using UsersService or any DAO pattern
        return usersService.isValidUser(username, password);
    }
}
