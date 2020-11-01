package contacts;

import static java.lang.System.out;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MyUtils {
    // take an object, serialize it and save it to a file
    public static void serialize(Object obj, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }

    // take a string filename, deserialize the object present...
    // ...inside it and return that object.
    public static Object deserialize (String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

}

abstract class Record implements Serializable {

    private static final long serialVersionUID = 2L;
    // private boolean isPerson;
    private String number;
    private final LocalDateTime timeCreated;
    private LocalDateTime timeLastEdit;

    public abstract String getName();

    public abstract String[] fieldsChangeable();

    public abstract void changeField(String fieldName, String newValue);

    public abstract Object fieldValue(String fieldName);

    private final static String phoneRegex = "\\+?((\\([a-zA-Z0-9]+\\)([ -][a-zA-Z0-9]{2,})*)|([a-zA-Z0-9]+[ -]\\([a-zA-Z0-9]{2,}\\)([ -][a-zA-Z0-9]{2,})*)|([a-zA-Z0-9]+([ -][a-zA-Z0-9]{2,})*))";
    private final static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(phoneRegex);

    private static boolean PhoneValidityCheck(String number) {
        java.util.regex.Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public Record(String number, LocalDateTime timeCreated) {
        // this.setPerson(isPerson);
        this.setNumber(number);
        this.timeCreated = timeCreated;
        this.setTimeLastEdit(timeCreated);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (PhoneValidityCheck(number)) {
            this.number = number;
        } else {
            out.println("Wrong number format!");
            this.number = "[no number]";
        }
    }

    public LocalDateTime getTimeCreated() {
        return this.timeCreated;
    }

    // writing a setter for a final field is not allowed...
    // since we can assign them only once, so no use of...
    // a setter more than once. A final field can be...
    // initialized in a constructor though (or while declaring itself).
    // public void setTimeCreated(LocalDateTime timeCreated) {
    // this.timeCreated = timeCreated;
    // }

    public LocalDateTime getTimeLastEdit() {
        return this.timeLastEdit;
    }

    public void setTimeLastEdit(LocalDateTime timeLastEdit) {
        this.timeLastEdit = timeLastEdit;
    }

}

class Person extends Record {

    private static final long serialVersionUID = 3L;

    private String firstname;
    private String surname;
    private String dob;
    private String gender;

    private final String[] fieldsChangeable = new String[] {"name", "surname", "birth", "gender", "number"};

    public Person(String firstname, String surname, String dob, String gender, String phone) {
        super(phone, LocalDateTime.now());
        this.setFirstname(firstname);
        this.setSurname(surname);
        this.setDob(dob);
        this.setGender(gender);
    }

    @Override public String getName() {
        return this.getFirstname() + " " + this.getSurname();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public void changeField(String fieldName, String newValue) {
        switch (fieldName) {
            case "name":
                this.setFirstname(newValue);
                break;
            case "surname":
                this.setSurname(newValue);
                break;
            case "birth":
                this.setDob(newValue);
                break;
            case "gender":
                this.setGender(newValue);
                break;
            case "number":
                this.setNumber(newValue);
                break;
            default:
                break;
        }
        this.setTimeLastEdit(LocalDateTime.now());
    }

    @Override
    public String fieldValue(String fieldName) {
        switch(fieldName) {
            case "name":
                return "Name: ".concat(this.getFirstname());
            case "surname":
                return "Surname: ".concat(this.getSurname());
            case "birth":
                return "Birth date: ".concat(this.getDob());
            case "gender":
                return "Gender: ".concat(this.getGender());
            case "number":
                return "Number: ".concat(this.getNumber());
            default:
                return null;
        }
    }

    @Override
    public String[] fieldsChangeable() {
        return fieldsChangeable;
    }
}

class Organization extends Record {

    private static final long serialVersionUID = 4L;

    private String name;
    private String address;

    private final String[] fieldsChangeable = new String[] {"name", "address", "number"};

    public Organization(String name, String address, String phone) {
        super(phone, LocalDateTime.now());
        this.setName(name);
        this.setAddress(address);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] fieldsChangeable() {
        return fieldsChangeable;
    }

    @Override
    public void changeField(String fieldName, String newValue) {
        switch (fieldName) {
            case "name":
                this.setName(newValue);
                break;
            case "address":
                this.setAddress(newValue);
                break;
            case "number":
                this.setNumber(newValue);
                break;
            default:
                break;
        }
        this.setTimeLastEdit(LocalDateTime.now());
    }

    @Override
    public String fieldValue(String fieldName) {
        switch (fieldName) {
            case "name":
                return "Organization name: ".concat(this.getName());
            case "address":
                return "Address: ".concat(this.getAddress());
            case "number":
                return "Number: ".concat(this.getNumber());
            default:
                return null;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
public class Main {
    private static java.util.Scanner sc = new java.util.Scanner(System.in);
    private static void showNames(ArrayList<Record> list) {
        int index = 1;
        for (Record item : list) {
            out.println(index + ". " + item.getName());
            ++index;
        }
    }
    private static void showInfo(ArrayList<Record> list, int selection) {
        // indices starting from zero.
        int index = selection - 1;
        Record record = list.get(index);
        for (String field: record.fieldsChangeable()) {
            System.out.println(record.fieldValue(field));
        }
        out.println("Time created: " + list.get(index).getTimeCreated());
        out.println("Time last edit: " + list.get(index).getTimeLastEdit() + "\n");
    }
    private static void edit(ArrayList<Record> list, int selection) {
        // indices starting from zero
        int index = selection - 1;
        Record record = list.get(index);
        out.print("Select a field (");
        int i;
        for (i = 0; i < record.fieldsChangeable().length - 1; ++i) {
            out.print(record.fieldsChangeable()[i].concat(", "));
        }
        out.println(record.fieldsChangeable()[i].concat("): "));
        // store the field to be changed and its value...
        String fieldToChange = sc.next();
        out.println("Enter ".concat(fieldToChange).concat(": "));
        sc.nextLine();
        String newValue = sc.nextLine();       //// ******** WATCH FOR NEWLINE ERROR
        // the record should be modified using its new value...
        record.changeField(fieldToChange, newValue);
        out.println("Saved.");
        // save using serialization, if filename argument has been specified (check boolean variable " ... ")...

        // Re-display this record.
        showInfo(list, selection);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ArrayList<Record> recordList;
        String filename;
        boolean fileSave = false;
        // If filename is specified, save to it (using serialization)...
        // ...upon every modification of data.
        // RecordList creation and possible deserialization:---
        if (args.length == 0) {
            recordList = new ArrayList<Record>();
        } else {
            fileSave = true;
            filename = args[0];
            if ((new File(filename)).exists()) {
                recordList = (ArrayList<Record>) MyUtils.deserialize(filename);
            } else {
                // create new file
                File newFile = new File(filename);
                newFile.createNewFile();
                recordList = new ArrayList<Record>();
            }
        }

        boolean exit = false;

        while(!exit) {
            out.println("[menu] Enter action (add, list, search, count, exit): ");
            String mainOption = sc.next();
            switch (mainOption) {
                case "add":
                    out.println("Enter the type (person, organization):");
                    String type = sc.next();
                    if (type.equals("person")) {
                        out.println("Enter the name:");
                        String firstname = sc.next();
                        out.println("Enter the surname:");
                        String surname = sc.next();
                        out.println("Enter the birth date:");
                        sc.nextLine();
                        String dob = sc.nextLine();
                        if (dob.isEmpty()) {
                            out.println("Bad birth date!");
                            dob = "[no data]";
                        }
                        out.println("Enter the gender (M, F):");
                        String gender = sc.nextLine();
                        if (gender.isEmpty()) {
                            out.println("Bad gender!");
                            gender = "[no data]";
                        }
                        out.println("Enter the number:");
                        String phone = sc.nextLine();
                        recordList.add(new Person(firstname, surname, dob, gender, phone));
                        out.println("The record added.\n");
                    } else if (type.equals("organization")) {
                        out.println("Enter the organization name:");
                        sc.nextLine();  //sc.next() to sc.nextLine() requires an extra sc.nextLine() in between.
                        String name = sc.nextLine();
                        out.println("Enter the address:");
                        String address = sc.nextLine();
                        out.println("Enter the number:");
                        String phone = sc.nextLine();
                        recordList.add(new Organization(name, address, phone));
                        out.println("The record added.\n");
                    }
                    break;

                case "count":
                    out.println("The Phone book has " + recordList.size() + " records.\n");
                    break;

                case "search":
                    // enter search state...
                    boolean insideSearch = true;
                    do {
                        out.println("Enter search query: ");
                        String queryString = sc.next();
                        ArrayList<Record> results = search(recordList, queryString);
//                        out.println(results.isEmpty());
                        showNames(results); // IS NEWLINE GIVEN HERE?? IF NO, PUT A NEWLINE.
                                            // MAYBE IT IS GIVEN BY THE FOLLOWING WHILE BLOCK.
                        boolean optionalLoop = true;
                        while (optionalLoop) {
                            out.println("\n[search] Enter action ([number], back, again): ");
                            String selection = sc.next();
                            if (selection.equals("again")) {
                                break;
                            } else if (selection.equals("back")) {
                                insideSearch = false;
                                break;
                            } else {
                                try {
                                    int indexSelection = Integer.parseInt(selection);
                                    if (indexSelection > 0 && indexSelection <= results.size()) {
                                        showInfo(results, indexSelection);
                                        // Enter into record state...
                                        boolean insideRecord = true;
                                        while (insideRecord) {
                                            out.println("[record] Enter action (edit, delete, menu): ");
                                            switch (sc.next()) {
                                                case "edit":
                                                    edit(results, indexSelection);
                                                    // save function has been incorporated into edit method.
                                                    // All done here.
                                                    break;
                                                case "delete":
                                                    results.remove(indexSelection - 1);
                                                    /* TODO: save using serialization... */

                                                    out.println("The record deleted!\n");
                                                    break;
                                                case "menu":
                                                    insideRecord = false;
                                                    optionalLoop = false;
                                                    insideSearch = false;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    if (selection.equals("back")) {
                                        insideSearch = false;
                                    }
                                }
                            }
                        }
                    } while (insideSearch);
                    break;

                case "list":
                    if (recordList.size() == 0) {
                        out.println("No records to list!\n");
                    } else {
                        showNames(recordList);
                        // enter into list state...come out only if input is "back"...
                        boolean insideList = true;
                        while (insideList) {
                            out.println("\n[list] Enter action ([number], back): ");
                            String selection = sc.next();
                            try {
                                int indexSelection = Integer.parseInt(selection);
                                if (indexSelection > 0 && indexSelection <= recordList.size()) {
                                    showInfo(recordList, indexSelection);
                                    // Enter into record state...
                                    boolean insideRecord = true;
                                    while (insideRecord) {
                                        out.println("[record] Enter action (edit, delete, menu): ");
                                        switch (sc.next()) {
                                            case "edit":
                                                edit(recordList, indexSelection);
                                                // save function has been incorporated into edit method.
                                                // All done here.
                                                break;
                                            case "delete":
                                                recordList.remove(indexSelection - 1);
                                                /* TODO: save using serialization... */

                                                out.println("The record deleted!\n");
                                                break;
                                            case "menu":
                                                insideRecord = false;
                                                insideList = false;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                if (selection.equals("back")) {
                                    insideList = false;
                                }
                            }
                        }

                    }
                    break;

                case "exit":
                    exit = true;
            }
        }
    }

    private static ArrayList<Record> search(ArrayList<Record> recordList, String queryString) {
        ArrayList<Record> result = new ArrayList<>();

        Matcher matcher;
        int i;
        String queryRegex = ".*" + queryString + ".*";
        Pattern pattern = Pattern.compile(queryRegex, Pattern.CASE_INSENSITIVE);
        for (i = 0; i < recordList.size(); ++i) {
            matcher = pattern.matcher(recordList.get(i).getName() + recordList.get(i).getNumber());
//            out.print(queryRegex + "  ");
            if (matcher.matches()) {
//                out.print("  matches");
                result.add(recordList.get(i));
            }
        }
        return result;
    }
}
