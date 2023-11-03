package dataBaseSQL;

import back.Module;
import back.Program;
import back.User;
import back.UserProgram;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static dataBaseSQL.Helper.EncodeStringDateToDate;
import static dataBaseSQL.Helper.hashPassword;

public class ProgramSQL {
    static Connection connection = ConnectionBDD.ConnectionBDD();

    public static List<Program> GetPrograms() {
        String sql = "SELECT * FROM program";
        List<Program> programs = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String name = resultSet.getString("Name");
                String description = resultSet.getString("Description");
                String jobIds = resultSet.getString("JobIds");
                Program program = new Program(id, name, description, jobIds);

                programs.add(program);
            }
        } catch (SQLException ex) {
            //Handle any errors
            System.out.println("SQLException : " +ex.getMessage());
            System.out.println("SQLState : " + ex.getSQLState());
            System.out.println("VendorError : " + ex.getErrorCode());
        }
        return programs;
    }

    public static List<UserProgram> GetProgramsByUserId(int userId) {
        String sql = "SELECT * FROM program " +
                "INNER JOIN user_program ON program.id = user_program.ProgramId " +
                "WHERE user_program.UserId = ?";
        List<UserProgram> userPrograms = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("Id");
                    String name = resultSet.getString("Name");
                    String description = resultSet.getString("Description");
                    String jobIds = resultSet.getString("JobIds");
                    Program program = new Program(id, name, description, jobIds);
                    boolean isValid = resultSet.getBoolean("IsValid");
                    Date endDateProgram = resultSet.getDate("EndDateProgram");
                    UserProgram userProgram = new UserProgram(null, program, isValid, endDateProgram);

                    userPrograms.add(userProgram);
                }
            }
        } catch (SQLException ex) {
            //Handle any errors
            System.out.println("SQLException : " +ex.getMessage());
            System.out.println("SQLState : " + ex.getSQLState());
            System.out.println("VendorError : " + ex.getErrorCode());
        }
        return userPrograms;
    }

    public static void AddProgram(String name, String description, String jobIds){
        String sql = "INSERT INTO program (`Name`, `Description`, `JobIds`) VALUES (?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, jobIds);
            preparedStatement.executeUpdate();
            System.out.println("Parcours créé avec succès");
        } catch (SQLException ex) {
            //Handle any errors
            System.out.println("SQLException : " +ex.getMessage());
            System.out.println("SQLState : " + ex.getSQLState());
            System.out.println("VendorError : " + ex.getErrorCode());
        }
    }

    //Modifier Parcoure
    public static void UpdateProgram(String name, String description, String jobIds, int id){
        String sql = "UPDATE `program` SET `Name`=?,`Description`=? WHERE Id=?";


        try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setString(1, name);
            prepareStatement.setString(2, description);
            prepareStatement.setString(3, jobIds);
            prepareStatement.setInt(4, id);
            prepareStatement.executeUpdate();
            System.out.println("Parcours modifier avec succès");
        }catch (SQLException ex){
            //Handle any errors
            System.out.println("SQLException : " +ex.getMessage());
            System.out.println("SQLState : " + ex.getSQLState());
            System.out.println("VendorError : " + ex.getErrorCode());
        }
    }

    //Supprimer Parcoure
    public static void DelateProgram(int id){
        String sql = "DELETE FROM `program` WHERE `id`=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Parcours supprimer avec succès");
        }catch (SQLException ex){
            System.out.println("SQLException : " +ex.getMessage());
            System.out.println("SQLState : " + ex.getSQLState());
            System.out.println("VendorError : " + ex.getErrorCode());
        }

    }

    public static Program GetProgramByIdForDisplay(int programId){
        String sql = "SELECT * FROM program WHERE Id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, programId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("Id");
                    String name = resultSet.getString("Name");
                    String description = resultSet.getString("Description");
                    String jobIds = resultSet.getString("JobIds");
                    Program program = new Program(id, name, description, jobIds);
                    List<Module> modules = ModuleSQL.GetModulesByProgramId(programId);
                    if (!modules.isEmpty()){
                        for (Module module: modules) {
                            program.addModule(module);
                        }
                    }
                    return program;
                }
            }
        } catch (SQLException ex) {
            //Handle any errors
            System.out.println("SQLException : " +ex.getMessage());
            System.out.println("SQLState : " + ex.getSQLState());
            System.out.println("VendorError : " + ex.getErrorCode());
        }
        return null;
    }
}