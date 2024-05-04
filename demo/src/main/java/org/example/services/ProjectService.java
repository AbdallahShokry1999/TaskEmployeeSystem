package org.example.services;

import org.example.model.Projects;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.example.util.HibernateUtil;

import java.util.List;

// Service class for managing Projects using Hibernate.
public class ProjectService {

    // Adds a new project to the database.
    public void addProject(Projects projects) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(projects);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Retrieves a project from the database based on the given ID.
    public Projects getProjectById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Projects.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieves all projects from the database.
    public List<Projects> getAllProjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Projects";
            Query<Projects> query = session.createQuery(hql, Projects.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Updates an existing project in the database.
    public void updateProject(Projects projects) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(projects);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Deletes a project from the database based on the given ID.
    public void deleteProject(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Projects projects = session.get(Projects.class, id);
            if (projects != null) {
                session.delete(projects);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
