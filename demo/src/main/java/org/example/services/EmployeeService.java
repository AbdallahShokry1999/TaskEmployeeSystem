package org.example.services;

import org.example.model.Employee;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

// Service class for managing Employee entities using Hibernate.
public class EmployeeService {

    // Retrieves an employee from the database based on the given ID.
    public Employee getEmployeeById(Long employeeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Employee.class, employeeId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieves all employees from the database.
    public List<Employee> getAllEmployees() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Adds a new employee to the database.
    public void addEmployee(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(employee);
            tx.commit();
            System.out.println("Successfully added employee");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to add employee");
        }
    }

    // Deletes an employee from the database based on the given ID.
    public void deleteEmployeeById(Long employeeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Load the Employee entity by ID
            Employee employee = session.load(Employee.class, employeeId);
            if (employee != null) {
                session.delete(employee);
                tx.commit();
            } else {
                // Handle case when employee with given ID is not found
                throw new IllegalArgumentException("Employee with ID " + employeeId + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Updates an existing employee in the database.
    public void updateEmployee(Employee updatedEmployee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(updatedEmployee);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
