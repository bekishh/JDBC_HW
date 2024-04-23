package org.example.dao.daoImpl;

import org.example.config.Config;
import org.example.dao.JobDao;
import org.example.model.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDaoImpl implements JobDao {
    private final Connection connection = Config.getConnection();

    @Override
    public void createJobTable() {
        String sql = "create table jobs(" +
                "id serial primary key," +
                "position varchar," +
                "profession varchar," +
                "description varchar," +
                "experience int" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addJob(Job job) {
        String sql = "insert into jobs(position, profession, description, experience)" +
                "values (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, job.getPosition());
            preparedStatement.setString(2, job.getProfession());
            preparedStatement.setString(3, job.getDescription());
            preparedStatement.setInt(4, job.getExperience());
            preparedStatement.executeUpdate();
            System.out.println(job.getProfession() + " is successfully saved!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Job getJobById(Long jobId) {
        String sql = "select * from jobs where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, jobId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String position = resultSet.getString("position");
                String profession = resultSet.getString("profession");
                String description = resultSet.getString("description");
                int experience = resultSet.getInt("experience");
                return new Job(position, profession, description, experience);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Job> sortByExperience(String ascOrDesc) {
        List<Job> sortedJobs = new ArrayList<>();
        String sql = "select * from jobs order by experience ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ascOrDesc);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sortedJobs.add(new Job(
                        resultSet.getLong("id"),
                        resultSet.getString("position"),
                        resultSet.getString("profession"),
                        resultSet.getString("description"),
                        resultSet.getInt("experience")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sortedJobs;
    }

    @Override
    public Job getJobByEmployeeId(Long employeeId) {
        String sql = "select j from employees e join jobs j on j.id = e.jobId where e.id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            return new Job(
                    resultSet.getLong("id"),
                    resultSet.getString("position"),
                    resultSet.getString("profession"),
                    resultSet.getString("description"),
                    resultSet.getInt("experience")
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteDescriptionColumn() {
        String sql = "alter table jobs drop column description";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Column description successfully deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
