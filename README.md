# CampusFlow — College Event Management System

A Java Swing desktop working model based on the uploaded 7-slide project PPT.

## Included features
- Dashboard with event, registration and available-seat counts
- Create, edit, delete and search events
- Student registration with required-field, email, duplicate and capacity validation
- Participant search and CSV export
- Data saved between runs in `data/events.csv` and `data/registrations.csv`
- Sample events included for a quick demonstration

## Requirements
- JDK 17 or newer (JDK 21/25 also works)
- VS Code with the **Extension Pack for Java** (recommended)

## Run in VS Code (beginner steps)
1. Extract this ZIP to a normal folder, e.g. `Desktop/CollegeEventManagementSystem`.
2. Open VS Code.
3. Choose **File → Open Folder** and select the extracted `CollegeEventManagementSystem` folder.
4. Install **Extension Pack for Java** if VS Code asks.
5. Open `src/Main.java`.
6. Click **Run** above `main`, or use Terminal → New Terminal and run:

   **Windows**
   ```powershell
   javac -d out src\Main.java
   java -cp out Main
   ```

   **macOS/Linux**
   ```bash
   javac -d out src/Main.java
   java -cp out Main
   ```

7. The app opens as a desktop window. Try creating an event, registering a student, and opening Participants.
8. Close and reopen the app: records remain saved in the `data` folder.

## Suggested demo (about 2 minutes)
1. Show the Dashboard counts.
2. Open Manage Events and add a new event (date must be `YYYY-MM-DD`).
3. Open Student Registration, fill all fields, select the event and register.
4. Open Participants and search by student name or registration number.
5. Export the participant list to CSV.
6. Explain that the app validates required fields, duplicate sign-ups and capacity.

## Technology note
The PPT proposes Java + Swing + JDBC + MySQL. This version is a self-contained, runnable desktop prototype using Java Swing and local CSV files so it works immediately without installing/configuring a database. JDBC/MySQL is not connected in this version. If your teacher requires a live MySQL database, the persistence layer should be switched to JDBC and the MySQL Connector/J driver configured.

## Troubleshooting
- `javac is not recognized`: install a JDK and ensure its `bin` folder is on PATH, then restart VS Code.
- Window does not appear: run `java -cp out Main` in the terminal and read any error shown.
- Data reset needed: close the app and remove the CSV files in the `data` folder (this deletes saved demo records).
