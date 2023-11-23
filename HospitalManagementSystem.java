package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="root";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner=new Scanner(System.in);
        try{

            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while (true){
                System.out.println("Hospital Management System !!");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. view Doctors");
                System.out.println("4. Boot Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your Choice...");

                int choice=scanner.nextInt();
                switch (choice){
                    case 1:
                        // Add Patients
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        // view Patients
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        // view  Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book  Appointments
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        System.out.println("Thank you using T");
                       return;
                    default:
                        System.out.println("Enter valid choice : ");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("Enter patient id: ");
        int patientId= scanner.nextInt();
        System.out.println("Enter Doctor id: ");
        int doctorId= scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD) : ");
        String appoitmentDate=scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailbility(doctorId,appoitmentDate,connection)){
                String appointmentQuery="insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                try {
                    PreparedStatement preparedStatement= connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appoitmentDate);
                    int rowsAffected=preparedStatement.executeUpdate();
                    if (rowsAffected>0){
                        System.out.println("Appointment Booked ...");
                    }else {
                        System.out.println("Failed to book Appointment..");
                    }

                }catch (SQLException e){
                    e.printStackTrace();
                }

            }else {
                System.out.println("doctor not available on this date !!");
            }
        }else {
            System.out.println("Either doctor or patient doesn't exist!!");
        }

    }

    public static  boolean checkDoctorAvailbility(int doctorId,String appointmentDate,Connection connection){
        String query ="select count(*) from appointments where doctor_id=? and appointment_date=?";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return  true;
                }else {
                    return false;
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
