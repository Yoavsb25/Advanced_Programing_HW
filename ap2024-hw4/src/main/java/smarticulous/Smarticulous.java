package smarticulous;

import smarticulous.db.Exercise;
import smarticulous.db.Submission;
import smarticulous.db.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Smarticulous class, implementing a grading system.
 */
public class Smarticulous {

    /**
     * The connection to the underlying DB.
     * <p>
     * null if the db has not yet been opened.
     */
    Connection db;

    /**
     * Open the {@link Smarticulous} SQLite database.
     * <p>
     * This should open the database, creating a new one if necessary, and set the {@link #db} field
     * to the new connection.
     * <p>
     * The open method should make sure the database contains the following tables, creating them if necessary:
     *
     * <table>
     *   <caption><em>Table name: <strong>User</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>UserId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Username</td><td>Text</td></tr>
     *   <tr><td>Firstname</td><td>Text</td></tr>
     *   <tr><td>Lastname</td><td>Text</td></tr>
     *   <tr><td>Password</td><td>Text</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Exercise</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>DueDate</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Question</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>Desc</td><td>Text</td></tr>
     *   <tr><td>Points</td><td>Integer</td></tr>
     * </table>
     * In this table the combination of ExerciseId and QuestionId together comprise the primary key.
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Submission</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>UserId</td><td>Integer</td></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>SubmissionTime</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>QuestionGrade</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Grade</td><td>Real</td></tr>
     * </table>
     * In this table the combination of SubmissionId and QuestionId together comprise the primary key.
     *
     * @param dburl The JDBC url of the database to open (will be of the form "jdbc:sqlite:...")
     * @return the new connection
     * @throws SQLException
     */
    public Connection openDB(String dburl) throws SQLException {
        // assign db to the connection
        db = DriverManager.getConnection(dburl);

        // create the tables if they don't already exists
        try (Statement st = db.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS User (UserId INTEGER PRIMARY KEY, Username TEXT UNIQUE, Firstname TEXT, Lastname TEXT, Password TEXT)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Exercise (ExerciseId INTEGER PRIMARY KEY, Name TEXT, DueDate INTEGER)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Question (ExerciseId INTEGER, QuestionId INTEGER, Name TEXT, Desc TEXT, Points INTEGER, PRIMARY KEY (ExerciseId, QuestionId))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Submission (SubmissionId INTEGER PRIMARY KEY, UserId INTEGER, ExerciseId INTEGER, SubmissionTime INTEGER)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS QuestionGrade (SubmissionId INTEGER, QuestionId INTEGER, Grade REAL, PRIMARY KEY (SubmissionId, QuestionId))");
        }
        return db;
    }


    /**
     * Close the DB if it is open.
     *
     * @throws SQLException
     */
    public void closeDB() throws SQLException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    // =========== User Management =============
    public int getUserId(String username) throws SQLException{
        //check if there is user with this username
        PreparedStatement firstCheck = db.prepareStatement("SELECT UserId FROM User WHERE username=?");
        firstCheck.setString(1, username);
        try (ResultSet res = firstCheck.executeQuery()) {
            if (res.next()) {
                //user found, return userid
                return res.getInt("UserId");
            } else
                return -1;
        }
    }


    /**
     * Add a user to the database / modify an existing user.
     * <p>
     * Add the user to the database if they don't exist. If a user with user.username does exist,
     * update their password and firstname/lastname in the database.
     *
     * @param user
     * @param password
     * @return the userid.
     * @throws SQLException
     */
    public int addOrUpdateUser(User user, String password) throws SQLException {
        // filter the table by username
        PreparedStatement filterByUsername = db.prepareStatement("SELECT * FROM User WHERE username=?");
        filterByUsername.setString(1, user.username);

        try (ResultSet res = filterByUsername.executeQuery()) {
            // username found
            if (res.next()) {
                // update user password and firstname/lastname in the database
                PreparedStatement updatePassword = db.prepareStatement("UPDATE User SET Password=?, Firstname=?, Lastname=? WHERE username=?");
                updatePassword.setString(1, password);
                updatePassword.setString(2, user.firstname);
                updatePassword.setString(3, user.lastname);
                updatePassword.setString(4, user.username);
                updatePassword.executeUpdate();

            //username not found
            } else {
                // Add the user to the database
                PreparedStatement addUser = db.prepareStatement("INSERT INTO User (Username, Firstname, Lastname, Password) VALUES (?,?,?,?)");
                addUser.setString(1, user.username);
                addUser.setString(2, user.firstname);
                addUser.setString(3, user.lastname);
                addUser.setString(4, password);
                addUser.executeUpdate();
            }
        }
        return filterByUsername.executeQuery().getInt("UserId");
    }


    /**
     * Verify a user's login credentials.
     *
     * @param username
     * @param password
     * @return true if the user exists in the database and the password matches; false otherwise.
     * @throws SQLException
     * <p>
     * Note: this is totally insecure. For real-life password checking, it's important to store only
     * a password hash
     * @see <a href="https://crackstation.net/hashing-security.htm">How to Hash Passwords Properly</a>
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        // filter by username
        PreparedStatement findUsername = db.prepareStatement("SELECT Password FROM User WHERE username=?");
        findUsername.setString(1, username);

        try (ResultSet res = findUsername.executeQuery()) {
            // username found
            if (res.next()) {
                //get password
                String getPassword = res.getString("Password");
                return getPassword.equals(password);
            }
            return false;
        }
    }

    // =========== Exercise Management =============

    /**
     * Add an exercise to the database.
     *
     * @param exercise
     * @return the new exercise id, or -1 if an exercise with this id already existed in the database.
     * @throws SQLException
     */
    public int addExercise(Exercise exercise) throws SQLException {
        // check if the exercise with the given id already exists
        PreparedStatement filterByExercise = db.prepareStatement("SELECT * FROM Exercise WHERE ExerciseId=?");
        filterByExercise.setInt(1, exercise.id);

        try (ResultSet res = filterByExercise.executeQuery()) {
            if (!res.next()) {
                // if the exercise doesn't exist, insert it into the Exercise table
                PreparedStatement insertExercise = db.prepareStatement("INSERT INTO Exercise (Name, DueDate) VALUES (?,?)");
                insertExercise.setString(1, exercise.name);
                long milliseconds = exercise.dueDate.getTime();
                int intValue = (int) milliseconds;
                insertExercise.setInt(2, intValue);
                insertExercise.executeUpdate();
                //insert questions associated with the exercise into the Question table
                for (int i = 0; i < exercise.questions.size(); i++){
                    PreparedStatement insertQuestions = db.prepareStatement("INSERT INTO Question (ExerciseId, Name, Desc, Points) VALUES(?,?,?,?)");
                    insertQuestions.setInt(1,exercise.id);
                    insertQuestions.setString(2,exercise.questions.get(i).name);
                    insertQuestions.setString(3,exercise.questions.get(i).desc);
                    insertQuestions.setInt(4,exercise.questions.get(i).points);
                    insertQuestions.executeUpdate();
                }
                // retrieve the ExerciseId of the newly inserted exercise
                PreparedStatement check = db.prepareStatement("SELECT ExerciseId FROM Exercise WHERE Name=?");
                check.setString(1, exercise.name);
                try (ResultSet resultSet = check.executeQuery()) {
                    if (res.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        }
        // if the exercise with the given id already exists, return -1
        return -1;
    }


    /**
     * Return a list of all the exercises in the database.
     * <p>
     * The list should be sorted by exercise id.
     *
     * @return list of all exercises.
     * @throws SQLException
     */
    public List<Exercise> loadExercises() throws SQLException {
        // retrieve the total number of exercises in the Exercise table
        Statement st = db.createStatement();
        ResultSet getLength = st.executeQuery("SELECT COUNT(*) FROM Exercise");
        int length = getLength.getInt(1);
        ArrayList<Exercise> exerciseList = new ArrayList<>(length);

        // fetch all exercises and their associated questions from the Exercise and Question tables
        try (ResultSet res = st.executeQuery("SELECT * FROM Exercise ORDER BY ExerciseId")) {
            while (res.next()) {
                // create Exercise object for the current row
                int intDate = res.getInt("DueDate");
                Date date = new Date(intDate);
                Exercise currExercise = new Exercise(res.getInt("ExerciseId"), res.getString("Name"), date);

                // Retrieve questions associated with the current exercise from the Question table
                PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM Question WHERE ExerciseId=?");
                int currId =  res.getInt("ExerciseId");
                preparedStatement.setInt(1, currId);
                ResultSet resultSet = preparedStatement.executeQuery();

                // add questions to the current Exercise object
                while (resultSet.next()) {
                    Exercise.Question currquestion = currExercise.new Question(resultSet.getString("Name"), resultSet.getString("Desc"), resultSet.getInt("Points"));
                    currExercise.questions.add(currquestion);
                }
                exerciseList.add(currExercise);
            }
        }
         return exerciseList;
    }

    // ========== Submission Storage ===============

    /**
     * Store a submission in the database.
     * The id field of the submission will be ignored if it is -1.
     * <p>
     * Return -1 if the corresponding user doesn't exist in the database.
     *
     * @param submission
     * @return the submission id.
     * @throws SQLException
     */
    public int storeSubmission(Submission submission) throws SQLException {
        // retrieve the user id using the username
        int userId = getUserId(submission.user.username);

        if (userId != -1) {
            PreparedStatement storeSubmission;

            // check if it's a new submission or an update to an existing one
            if (submission.id == -1) {
                // insert a new submission
                storeSubmission = db.prepareStatement("INSERT INTO Submission (UserId, ExerciseId, SubmissionTime) VALUES (?,?,?)");
                storeSubmission.setInt(1, userId);
                storeSubmission.setInt(2, submission.exercise.id);
                storeSubmission.setLong(3, submission.submissionTime.getTime());
            } else {
                // update an existing submission
                storeSubmission = db.prepareStatement("UPDATE Submission SET ExerciseId=?, SubmissionTime=? WHERE SubmissionId=?");
                storeSubmission.setInt(1, submission.exercise.id);
                storeSubmission.setLong(2, submission.submissionTime.getTime());
                storeSubmission.setInt(3, submission.id);
            }

            // execute the update or insert operation
            int rowsAffected = storeSubmission.executeUpdate();

            // check if the operation was successful
            if (rowsAffected > 0) {
                // if it's an insert, retrieve the generated keys (auto-incremented ID)
                ResultSet generatedKeys = storeSubmission.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        // Return -1 if the submission couldn't be stored
        return -1;
    }


    // ============= Submission Query ===============


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the latest submission for the given exercise by the given user.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getLastSubmission(User, Exercise)}
     *
     * @return
     */
    PreparedStatement getLastSubmissionGradesStatement() throws SQLException {
        String sql = "SELECT sg.SubmissionId, qg.QuestionId, qg.Grade, sg.SubmissionTime " +
                "FROM Submission sg " +
                "INNER JOIN QuestionGrade qg ON sg.SubmissionId = qg.SubmissionId " +
                "WHERE sg.UserId = (SELECT UserId FROM User WHERE username = ?) " +
                "AND sg.ExerciseId = ? " +
                "ORDER BY sg.SubmissionTime DESC, qg.QuestionId " +
                "LIMIT ?";

        return db.prepareStatement(sql);
    }


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the <i>best</i> submission for the given exercise by the given user.
     * The best submission is the one whose point total is maximal.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getBestSubmission(User, Exercise)}
     *
     */
    PreparedStatement getBestSubmissionGradesStatement() throws SQLException {
        // TODO: Implement
        return null;
    }

    /**
     * Return a submission for the given exercise by the given user that satisfies
     * some condition (as defined by an SQL prepared statement).
     * <p>
     * The prepared statement should accept the user name as parameter 1, the exercise id as parameter 2 and a limit on the
     * number of rows returned as parameter 3, and return a row for each question corresponding to the submission, sorted by questionId.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @param stmt
     * @return
     * @throws SQLException
     */
    Submission getSubmission(User user, Exercise exercise, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, user.username);
        stmt.setInt(2, exercise.id);
        stmt.setInt(3, exercise.questions.size());

        ResultSet res = stmt.executeQuery();

        boolean hasNext = res.next();
        if (!hasNext)
            return null;

        int sid = res.getInt("SubmissionId");
        Date submissionTime = new Date(res.getLong("SubmissionTime"));

        float[] grades = new float[exercise.questions.size()];

        for (int i = 0; hasNext; ++i, hasNext = res.next()) {
            grades[i] = res.getFloat("Grade");
        }

        return new Submission(sid, user, exercise, submissionTime, (float[]) grades);
    }

    /**
     * Return the latest submission for the given exercise by the given user.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     * @throws SQLException
     */
    public Submission getLastSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getLastSubmissionGradesStatement());
    }


    /**
     * Return the submission with the highest total grade
     *
     * @param user the user for which we retrieve the best submission
     * @param exercise the exercise for which we retrieve the best submission
     * @return
     * @throws SQLException
     */
    public Submission getBestSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getBestSubmissionGradesStatement());
    }
}
