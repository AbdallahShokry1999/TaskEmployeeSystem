package org.example.demo;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.example.model.Department;
import org.example.services.DepartmentService;
import org.example.services.UsersServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@WebServlet(name = "DepartmentServlet", urlPatterns = "/api/departments")
public class DepartmentServlet extends HttpServlet {
    private final DepartmentService dService = new DepartmentService();
    private final UsersServices usersService = new UsersServices();
    private final Gson gson = new Gson();

    // Handle GET requests to fetch department information
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            handleUnauthorizedAccess(response);
            return;
        }

        String department_IdString = request.getParameter("department_id");
        if (department_IdString != null) {
            if ("show all".equalsIgnoreCase(department_IdString)) {
                // Retrieve all departments
                getAllDepartments(request, response);
            } else if (!department_IdString.isEmpty()) {
                // Retrieve specific department by ID
                Long departmentId = Long.parseLong(department_IdString);
                getSingleDepartmentById(request, response, departmentId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid department ID");
            }
        }
    }

    // Helper method to handle unauthorized access
    private void handleUnauthorizedAccess(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized access");
    }

    // Helper method to fetch all departments
    private void getAllDepartments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        List<Department> departmentList = dService.getAllDepartments();
        String json = gson.toJson(departmentList);
        response.getWriter().write(json);
    }

    // Helper method to fetch a single department by ID
    private void getSingleDepartmentById(HttpServletRequest request, HttpServletResponse response, long dId) throws ServletException, IOException {
        try {
            Department department = dService.getDepartmentById(dId);
            if (department != null) {
                // Convert department object to JSON and send as response
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(gson.toJson(department));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Department not found.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Department ID format.");
        }
    }

    // Handle POST requests to add a new department
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            handleUnauthorizedAccess(response);
            return;
        }

        // Add new department
        BufferedReader reader = request.getReader();
        Department department = gson.fromJson(reader, Department.class);
        dService.addDepartment(department);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Department added successfully!");
    }

    // Handle PUT requests to update an existing department
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            handleUnauthorizedAccess(response);
            return;
        }

        // Update department
        BufferedReader reader = request.getReader();
        Department updatedDepartment = gson.fromJson(reader, Department.class);
        dService.updateDepartment(updatedDepartment);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Department updated successfully!");
    }

    // Handle DELETE requests to delete a department
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            handleUnauthorizedAccess(response);
            return;
        }

        String departmentIdString = request.getParameter("department_id");
        if (departmentIdString != null && !departmentIdString.isEmpty()) {
            Long departmentId = Long.parseLong(departmentIdString);
            dService.deleteDepartment(departmentId);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Department deleted successfully!");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Department ID parameter is required.");
        }
    }

    // Helper method to check if user is authenticated as admin
    private boolean isUserAuthenticated(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            // Extract username and password from the Authorization header
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];

            // Check if the provided username and password match an admin in the database
            return isValidAdmin(username, password);
        }
        return false; // Authorization header missing or invalid
    }

    // Helper method to validate admin credentials against the database
    private boolean isValidAdmin(String username, String password) {
        return usersService.isValidUser(username, password);
    }
}
