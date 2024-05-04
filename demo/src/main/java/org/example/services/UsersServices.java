package org.example.services;

import org.example.model.Users;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.example.util.HibernateUtil;

// Service class for validating user credentials using Hibernate.
public class UsersServices {

    // Method to validate a user based on username and password.
    public boolean isValidUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Hibernate query to check if a user with the given username and password exists.
            String hql = "FROM Users WHERE username = :username AND password = :password";
            Query<Users> query = session.createQuery(hql, Users.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            // Return true if a user is found, false otherwise.
            return query.uniqueResult() != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
