package org.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.example.model.Employee;
import org.example.services.EmployeeService;
import org.example.services.UsersServices;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "EmployeeServlet", urlPatterns = "/api/employees")
public class HelloServlet extends HttpServlet {
    private final EmployeeService employeeService = new EmployeeService();
    private final UsersServices usersService = new UsersServices(); // Assuming you have a UsersService class
    private final Gson gson = new Gson();

    // Handle GET requests
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idString = request.getParameter("id");
        if (idString != null && !idString.isEmpty()) {
            getSingleEmployeeById(request, response);
        } else {

            getAllEmployees(request, response);
        }
    }

    // Method to get a single employee by ID
    private void getSingleEmployeeById(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long employeeId = Long.parseLong(request.getParameter("id"));
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (!isUserAuthenticatedEmployee(request,employeeId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        try {

            if (employee != null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(employee));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Employee not found.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid employee ID format.");
        }
    }

    // Method to get all employees
    private void getAllEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        response.setContentType("application/json");
        List<Employee> employees = employeeService.getAllEmployees();
        String json = gson.toJson(employees);
        response.getWriter().write(json);
    }

    // Handle POST requests
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        BufferedReader reader = request.getReader();
        Employee employee = gson.fromJson(reader, Employee.class);
        employeeService.addEmployee(employee);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Employee added successfully!");
    }

    // Handle PUT requests
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        BufferedReader reader = request.getReader();
        Employee updatedEmployee = gson.fromJson(reader, Employee.class);
        employeeService.updateEmployee(updatedEmployee);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Employee updated successfully!");
    }

    // Handle DELETE requests
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isUserAuthenticated(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access");
            return;
        }
        String idString = request.getParameter("id");
        if (idString != null && !idString.isEmpty()) {
            try {
                Long employeeId = Long.parseLong(idString);
                employeeService.deleteEmployeeById(employeeId);
                response.getWriter().write("Employee with ID " + employeeId + " deleted successfully!");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid employee ID format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Employee ID parameter is required.");
        }
    }

    // Helper method to check user authentication
    private boolean isUserAuthenticated(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];
            return isValidAdmin(username, password);
        }
        return false; // Authorization header missing or invalid
    }
    private boolean isUserAuthenticatedEmployee(HttpServletRequest request,long id) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];
            boolean isEqual = username.equals(Long.toString(id));
            boolean isEqual2 = password.equals(Long.toString(id));
            boolean u;
            if (isEqual&&isEqual2){
                                u = true;
            }else
                u=false;
            return isValidEmployee(username, password,u);
        }
        return false; // Authorization header missing or invalid
    }

    // Helper method to validate admin credentials using database check
    private boolean isValidAdmin(String username, String password) {
        return usersService.isValidUser(username, password);
    }
    private boolean isValidEmployee(String username, String password, Boolean isUser) {
        return employeeService.isValidUser(username, password, isUser);
    }
}
