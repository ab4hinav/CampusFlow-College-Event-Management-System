import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * College Event Management System
 * Java Swing desktop prototype with persistent CSV storage (no external libraries required).
 */
public class Main extends JFrame {
    private static final Color NAVY = new Color(25, 43, 68);
    private static final Color TEAL = new Color(37, 132, 132);
    private static final Color BG = new Color(242, 246, 249);
    private static final Color MUTED = new Color(100, 115, 130);
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path EVENTS_FILE = DATA_DIR.resolve("events.csv");
    private static final Path REG_FILE = DATA_DIR.resolve("registrations.csv");

    private final List<Event> events = new ArrayList<>();
    private final List<Registration> registrations = new ArrayList<>();
    private DefaultTableModel eventModel, participantModel, registrationModel;
    private JTable eventTable, participantTable, registrationTable;
    private JTextField searchField;
    private JComboBox<String> eventChoice;
    private JTextField studentName, studentId, studentEmail, studentDepartment;
    private JLabel eventsCount, registrationsCount, capacityCount;
    private JTextField titleField, dateField, timeField, venueField, capacityField;
    private JTextArea descriptionField;

    public Main() {
        super("CampusFlow | College Event Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 700));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        loadData();
        buildUI();
        refreshAll();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 4));
        brand.setOpaque(false);
        JLabel name = new JLabel("CAMPUSFLOW");
        name.setFont(new Font("SansSerif", Font.BOLD, 23));
        name.setForeground(Color.WHITE);
        JLabel sub = new JLabel("COLLEGE EVENT MANAGEMENT SYSTEM  •  JAVA SWING");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(190, 211, 222));
        brand.add(name); brand.add(sub);
        JLabel badge = new JLabel("  SRMIST  |  SEMESTER PROJECT  ");
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setOpaque(true); badge.setBackground(TEAL);
        badge.setBorder(new EmptyBorder(10, 12, 10, 12));
        header.add(brand, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setBackground(BG);
        tabs.addTab("  Dashboard  ", dashboardPanel());
        tabs.addTab("  Manage Events  ", eventsPanel());
        tabs.addTab("  Student Registration  ", registrationPanel());
        tabs.addTab("  Participants  ", participantsPanel());
        add(tabs, BorderLayout.CENTER);

        JLabel footer = new JLabel("  Centralize  →  Organize  →  Manage       •       Data is saved locally in the data folder");
        footer.setForeground(MUTED);
        footer.setBorder(new EmptyBorder(9, 14, 9, 14));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(18, 18));
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        p.setBackground(BG);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Event Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 27)); title.setForeground(NAVY);
        JLabel hint = new JLabel("A simple workspace for organizers and students");
        hint.setForeground(MUTED);
        JPanel labels = new JPanel(new GridLayout(2,1,0,5)); labels.setOpaque(false);
        labels.add(title); labels.add(hint);
        top.add(labels, BorderLayout.WEST);
        JButton add = primaryButton("+ Create Event");
        add.addActionListener(e -> showCreateEventDialog());
        top.add(add, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1,3,16,0)); cards.setOpaque(false);
        eventsCount = new JLabel("0"); registrationsCount = new JLabel("0"); capacityCount = new JLabel("0");
        cards.add(statCard("TOTAL EVENTS", eventsCount, "Events currently listed"));
        cards.add(statCard("REGISTRATIONS", registrationsCount, "Student sign-ups recorded"));
        cards.add(statCard("AVAILABLE SEATS", capacityCount, "Across all listed events"));
        JPanel center = new JPanel(new BorderLayout(0,12)); center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        JPanel welcome = new JPanel(new BorderLayout(16,12));
        welcome.setBackground(Color.WHITE); welcome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224,232,238)), new EmptyBorder(24,24,24,24)));
        JPanel text = new JPanel(new GridLayout(0,1,0,10)); text.setOpaque(false);
        JLabel h = new JLabel("Everything in one place.");
        h.setFont(new Font("SansSerif", Font.BOLD, 22)); h.setForeground(NAVY);
        JLabel d = new JLabel("<html>Manage college events, register students and review participant records.<br>Use the tabs above to demonstrate each module.</html>");
        d.setForeground(MUTED); d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        text.add(h); text.add(d);
        welcome.add(text, BorderLayout.CENTER);
        JPanel quick = new JPanel(new GridLayout(3,1,0,10)); quick.setOpaque(false);
        quick.add(quickItem("01", "Create and maintain events"));
        quick.add(quickItem("02", "Register students with validation"));
        quick.add(quickItem("03", "Search and view participant records"));
        welcome.add(quick, BorderLayout.SOUTH);
        center.add(welcome, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel statCard(String label, JLabel value, String detail) {
        JPanel c = new JPanel(new BorderLayout(0,8)); c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222,231,237)), new EmptyBorder(18,20,18,20)));
        JLabel l = new JLabel(label); l.setFont(new Font("SansSerif", Font.BOLD, 11)); l.setForeground(TEAL);
        value.setFont(new Font("SansSerif", Font.BOLD, 32)); value.setForeground(NAVY);
        JLabel d = new JLabel(detail); d.setFont(new Font("SansSerif", Font.PLAIN, 12)); d.setForeground(MUTED);
        JPanel mid = new JPanel(new BorderLayout()); mid.setOpaque(false); mid.add(value, BorderLayout.WEST);
        c.add(l, BorderLayout.NORTH); c.add(mid, BorderLayout.CENTER); c.add(d, BorderLayout.SOUTH);
        return c;
    }
    private JPanel quickItem(String n, String text) {
        JPanel p = new JPanel(new BorderLayout(12,0)); p.setOpaque(false);
        JLabel number = new JLabel(n); number.setForeground(TEAL); number.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel label = new JLabel(text); label.setForeground(NAVY); label.setFont(new Font("SansSerif", Font.BOLD, 13));
        p.add(number, BorderLayout.WEST); p.add(label, BorderLayout.CENTER); return p;
    }

    private JPanel eventsPanel() {
        JPanel root = panelBase();
        JPanel top = new JPanel(new BorderLayout(10,0)); top.setOpaque(false);
        JPanel title = sectionTitle("Manage Events", "Create, update, search and remove college events");
        top.add(title, BorderLayout.WEST);
        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); search.setOpaque(false);
        searchField = new JTextField(18); searchField.setPreferredSize(new Dimension(190, 36));
        searchField.setToolTipText("Search title, date or venue");
        JButton find = secondaryButton("Search");
        find.addActionListener(e -> refreshEventTable(searchField.getText().trim()));
        JButton clear = secondaryButton("Clear");
        clear.addActionListener(e -> { searchField.setText(""); refreshEventTable(""); });
        search.add(searchField); search.add(find); search.add(clear); top.add(search, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Event Title","Date","Time","Venue","Capacity","Description"};
        eventModel = model(cols); eventTable = new JTable(eventModel); styleTable(eventTable);
        root.add(new JScrollPane(eventTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); actions.setOpaque(false);
        JButton create = primaryButton("+ Add Event"); create.addActionListener(e -> showCreateEventDialog());
        JButton edit = secondaryButton("Edit Selected"); edit.addActionListener(e -> editSelectedEvent());
        JButton delete = dangerButton("Delete Selected"); delete.addActionListener(e -> deleteSelectedEvent());
        actions.add(create); actions.add(edit); actions.add(delete);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel registrationPanel() {
        JPanel root = panelBase();
        root.add(sectionTitle("Student Registration", "Register for an event — required fields are checked"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222,231,237)), new EmptyBorder(22,24,22,24)));
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(9,8,9,8); g.fill = GridBagConstraints.HORIZONTAL;
        studentName = new JTextField(22); studentId = new JTextField(22); studentEmail = new JTextField(22); studentDepartment = new JTextField(22);
        eventChoice = new JComboBox<>();
        addFormRow(form,g,0,"Student full name",studentName);
        addFormRow(form,g,1,"Registration number",studentId);
        addFormRow(form,g,2,"College email",studentEmail);
        addFormRow(form,g,3,"Department / section",studentDepartment);
        addFormRow(form,g,4,"Select event",eventChoice);
        JButton register = primaryButton("Complete Registration");
        register.addActionListener(e -> registerStudent());
        g.gridx=1; g.gridy=5; g.weightx=1; form.add(register,g);
        JLabel note = new JLabel("<html><b>Note:</b> Duplicate registrations are blocked and event capacity is checked.</html>");
        note.setForeground(MUTED); note.setBorder(new EmptyBorder(10,2,0,2));
        JPanel holder = new JPanel(new BorderLayout(0,12)); holder.setOpaque(false); holder.add(form,BorderLayout.NORTH); holder.add(note,BorderLayout.CENTER);
        root.add(holder,BorderLayout.CENTER);
        return root;
    }

    private JPanel participantsPanel() {
        JPanel root = panelBase();
        root.add(sectionTitle("Participant Records", "Review registrations and use search to find students"), BorderLayout.NORTH);
        String[] cols = {"Student Name","Registration No.","Email","Department","Event","Registered On"};
        registrationModel = model(cols); registrationTable = new JTable(registrationModel); styleTable(registrationTable);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); searchBar.setOpaque(false);
        JTextField query = new JTextField(22); query.setPreferredSize(new Dimension(220,36));
        JButton search = secondaryButton("Search Participants");
        JButton showAll = secondaryButton("Show All");
        search.addActionListener(e -> refreshRegistrationTable(query.getText().trim()));
        showAll.addActionListener(e -> { query.setText(""); refreshRegistrationTable(""); });
        searchBar.add(query); searchBar.add(search); searchBar.add(showAll);
        JPanel center = new JPanel(new BorderLayout(0,12)); center.setOpaque(false);
        center.add(searchBar,BorderLayout.NORTH); center.add(new JScrollPane(registrationTable),BorderLayout.CENTER);
        root.add(center,BorderLayout.CENTER);
        JButton export = primaryButton("Export Participant CSV");
        export.addActionListener(e -> exportParticipants());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT)); bottom.setOpaque(false); bottom.add(export);
        root.add(bottom,BorderLayout.SOUTH);
        return root;
    }

    private JPanel panelBase() {
        JPanel p = new JPanel(new BorderLayout(0,18)); p.setBackground(BG);
        p.setBorder(new EmptyBorder(24,28,24,28)); return p;
    }
    private JPanel sectionTitle(String title, String subtitle) {
        JPanel p = new JPanel(new GridLayout(2,1,0,5)); p.setOpaque(false);
        JLabel t = new JLabel(title); t.setFont(new Font("SansSerif",Font.BOLD,24)); t.setForeground(NAVY);
        JLabel s = new JLabel(subtitle); s.setFont(new Font("SansSerif",Font.PLAIN,13)); s.setForeground(MUTED);
        p.add(t); p.add(s); return p;
    }
    private void addFormRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx=0; g.gridy=row; g.weightx=0; JLabel l = new JLabel(label); l.setForeground(NAVY); l.setFont(new Font("SansSerif",Font.BOLD,13)); p.add(l,g);
        g.gridx=1; g.weightx=1; field.setPreferredSize(new Dimension(300,36)); p.add(field,g);
    }
    private DefaultTableModel model(String[] columns) {
        return new DefaultTableModel(columns,0) { public boolean isCellEditable(int r,int c){return false;} };
    }
    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(NAVY);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(210, 235, 233));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(220, 228, 234));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        // Force visible column titles even when Windows uses native table-header styling.
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.TableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value == null ? "" : value.toString());
                label.setOpaque(true);
                label.setBackground(NAVY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 12));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(220, 228, 234)),
                        new EmptyBorder(8, 8, 8, 8)));
                return label;
            }
        });

        // Force readable text colors even when Windows' look-and-feel is active.
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(210, 235, 233));
                    c.setForeground(NAVY);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
                    c.setForeground(NAVY);
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
    }
    private JButton primaryButton(String text) { return button(text,TEAL,Color.WHITE); }
    private JButton secondaryButton(String text) { return button(text,new Color(225,235,240),NAVY); }
    private JButton dangerButton(String text) { return button(text,new Color(250,228,226),new Color(155,48,45)); }
    private JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        // Use Swing's basic UI delegate to prevent Windows native styling washing out text/colors.
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 15, 10, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showCreateEventDialog() {
        JTextField title = new JTextField(), date = new JTextField(LocalDate.now().plusDays(7).toString());
        JTextField time = new JTextField("10:00 AM"), venue = new JTextField(), capacity = new JTextField("50");
        JTextArea desc = new JTextArea(3,20); desc.setLineWrap(true); desc.setWrapStyleWord(true);
        JPanel f = new JPanel(new GridLayout(0,2,8,8));
        f.add(new JLabel("Event title *")); f.add(title); f.add(new JLabel("Date (YYYY-MM-DD) *")); f.add(date);
        f.add(new JLabel("Time *")); f.add(time); f.add(new JLabel("Venue *")); f.add(venue);
        f.add(new JLabel("Capacity *")); f.add(capacity); f.add(new JLabel("Description")); f.add(new JScrollPane(desc));
        int result=JOptionPane.showConfirmDialog(this,f,"Create New Event",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(result!=JOptionPane.OK_OPTION) return;
        try {
            if(title.getText().trim().isEmpty()||venue.getText().trim().isEmpty()||time.getText().trim().isEmpty()) throw new IllegalArgumentException("Title, time and venue are required.");
            LocalDate.parse(date.getText().trim());
            int cap=Integer.parseInt(capacity.getText().trim()); if(cap<1) throw new IllegalArgumentException("Capacity must be at least 1.");
            Event ev=new Event(nextEventId(),title.getText().trim(),date.getText().trim(),time.getText().trim(),venue.getText().trim(),cap,desc.getText().trim());
            events.add(ev); saveData(); refreshAll();
            JOptionPane.showMessageDialog(this,"Event created successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex) { showError("Please check your inputs. Date must be YYYY-MM-DD and capacity must be a positive number."); }
    }

    private void editSelectedEvent() {
        int row=eventTable.getSelectedRow(); if(row<0){showError("Select an event from the table first.");return;}
        int modelRow=eventTable.convertRowIndexToModel(row); String id=String.valueOf(eventModel.getValueAt(modelRow,0));
        Event ev=findEvent(id); if(ev==null)return;
        JTextField title=new JTextField(ev.title), date=new JTextField(ev.date), time=new JTextField(ev.time), venue=new JTextField(ev.venue), cap=new JTextField(String.valueOf(ev.capacity));
        JTextArea desc=new JTextArea(ev.description,3,20);
        JPanel f=new JPanel(new GridLayout(0,2,8,8));
        f.add(new JLabel("Event title"));f.add(title);f.add(new JLabel("Date (YYYY-MM-DD)"));f.add(date);f.add(new JLabel("Time"));f.add(time);
        f.add(new JLabel("Venue"));f.add(venue);f.add(new JLabel("Capacity"));f.add(cap);f.add(new JLabel("Description"));f.add(new JScrollPane(desc));
        if(JOptionPane.showConfirmDialog(this,f,"Edit Event #"+id,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return;
        try {
            LocalDate.parse(date.getText().trim()); int c=Integer.parseInt(cap.getText().trim());
            if(title.getText().trim().isEmpty()||venue.getText().trim().isEmpty()||time.getText().trim().isEmpty()||c<1)throw new Exception();
            long used=registrations.stream().filter(r->r.eventId.equals(id)).count(); if(c<used){showError("Capacity cannot be lower than existing registrations ("+used+").");return;}
            ev.title=title.getText().trim();ev.date=date.getText().trim();ev.time=time.getText().trim();ev.venue=venue.getText().trim();ev.capacity=c;ev.description=desc.getText().trim();
            saveData();refreshAll();JOptionPane.showMessageDialog(this,"Event updated successfully.");
        }catch(Exception ex){showError("Invalid details. Use YYYY-MM-DD and a positive capacity.");}
    }
    private void deleteSelectedEvent() {
        int row=eventTable.getSelectedRow();if(row<0){showError("Select an event from the table first.");return;}
        int mr=eventTable.convertRowIndexToModel(row);String id=String.valueOf(eventModel.getValueAt(mr,0));Event ev=findEvent(id);if(ev==null)return;
        if(registrations.stream().anyMatch(r->r.eventId.equals(id))){showError("This event has registrations. Remove its registrations first to preserve records.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete \""+ev.title+"\"?","Confirm Delete",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        events.remove(ev);saveData();refreshAll();
    }

    private void registerStudent() {
        String name=studentName.getText().trim(), id=studentId.getText().trim(), email=studentEmail.getText().trim(), dept=studentDepartment.getText().trim();
        if(name.isEmpty()||id.isEmpty()||email.isEmpty()||dept.isEmpty()){showError("Please fill in all student fields.");return;}
        if(!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")){showError("Enter a valid email address.");return;}
        if(eventChoice.getSelectedItem()==null){showError("Create an event before registering students.");return;}
        String eventId=String.valueOf(eventChoice.getSelectedItem()).split(" - ",2)[0];
        Event ev=findEvent(eventId);if(ev==null)return;
        if(registrations.stream().anyMatch(r->r.studentId.equalsIgnoreCase(id)&&r.eventId.equals(eventId))){showError("This student is already registered for this event.");return;}
        long count=registrations.stream().filter(r->r.eventId.equals(eventId)).count();
        if(count>=ev.capacity){showError("Sorry, this event has reached its capacity.");return;}
        registrations.add(new Registration(name,id,email,dept,eventId,LocalDate.now().toString()));
        saveData();refreshAll();
        studentName.setText("");studentId.setText("");studentEmail.setText("");studentDepartment.setText("");
        JOptionPane.showMessageDialog(this,"Registration completed for "+ev.title+"!","Registration Successful",JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAll() {
        refreshEventTable(searchField==null?"":searchField.getText().trim());
        refreshRegistrationTable("");
        if(eventChoice!=null){Object old=eventChoice.getSelectedItem();eventChoice.removeAllItems();
            for(Event e:events)eventChoice.addItem(e.id+" - "+e.title);
            if(old!=null)eventChoice.setSelectedItem(old.toString());
        }
        if(eventsCount!=null)eventsCount.setText(String.valueOf(events.size()));
        if(registrationsCount!=null)registrationsCount.setText(String.valueOf(registrations.size()));
        if(capacityCount!=null){int available=0;for(Event e:events){long used=registrations.stream().filter(r->r.eventId.equals(e.id)).count();available+=Math.max(0,e.capacity-(int)used);}capacityCount.setText(String.valueOf(available));}
    }
    private void refreshEventTable(String q) {
        if(eventModel==null)return;eventModel.setRowCount(0);String needle=q==null?"":q.toLowerCase();
        for(Event e:events)if((e.title+" "+e.date+" "+e.venue+" "+e.description).toLowerCase().contains(needle))
            eventModel.addRow(new Object[]{e.id,e.title,e.date,e.time,e.venue,e.capacity,e.description});
    }
    private void refreshRegistrationTable(String q) {
        if(registrationModel==null)return;registrationModel.setRowCount(0);String needle=q==null?"":q.toLowerCase();
        for(Registration r:registrations){Event e=findEvent(r.eventId);String event=e==null?"(deleted event)":e.title;
            String row=(r.name+" "+r.studentId+" "+r.email+" "+r.department+" "+event+" "+r.registeredOn).toLowerCase();
            if(row.contains(needle))registrationModel.addRow(new Object[]{r.name,r.studentId,r.email,r.department,event,r.registeredOn});
        }
    }
    private void exportParticipants() {
        JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new File("participants-export.csv"));
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(BufferedWriter w=Files.newBufferedWriter(chooser.getSelectedFile().toPath(),StandardCharsets.UTF_8)){
            w.write("Student Name,Registration No.,Email,Department,Event,Registered On\n");
            for(Registration r:registrations){Event e=findEvent(r.eventId);writeCsvRow(w,Arrays.asList(r.name,r.studentId,r.email,r.department,e==null?"":e.title,r.registeredOn));}
            JOptionPane.showMessageDialog(this,"Participant list exported successfully.");
        }catch(IOException ex){showError("Could not export file: "+ex.getMessage());}
    }

    private void loadData() {
        try {
            Files.createDirectories(DATA_DIR);
            if(Files.exists(EVENTS_FILE))for(String line:Files.readAllLines(EVENTS_FILE,StandardCharsets.UTF_8)){
                List<String> v=parseCsv(line);if(v.size()>=7&&!v.get(0).equals("id"))events.add(new Event(v.get(0),v.get(1),v.get(2),v.get(3),v.get(4),Integer.parseInt(v.get(5)),v.get(6)));
            }
            if(Files.exists(REG_FILE))for(String line:Files.readAllLines(REG_FILE,StandardCharsets.UTF_8)){
                List<String> v=parseCsv(line);if(v.size()>=6&&!v.get(0).equals("name"))registrations.add(new Registration(v.get(0),v.get(1),v.get(2),v.get(3),v.get(4),v.get(5)));
            }
        }catch(Exception ex){System.err.println("Could not load saved data: "+ex.getMessage());}
        if(events.isEmpty()&&!Files.exists(EVENTS_FILE)){
            events.add(new Event("1","Tech Fest 2026",LocalDate.now().plusDays(7).toString(),"10:00 AM","Main Auditorium",120,"Annual technology and innovation festival"));
            events.add(new Event("2","CodeSprint",LocalDate.now().plusDays(14).toString(),"02:00 PM","Computer Lab 2",60,"Competitive programming challenge"));
            events.add(new Event("3","Cultural Night",LocalDate.now().plusDays(21).toString(),"05:30 PM","Open Air Theatre",200,"Music, dance and student performances"));
            saveData();
        }
    }
    private void saveData() {
        try {
            Files.createDirectories(DATA_DIR);
            try(BufferedWriter w=Files.newBufferedWriter(EVENTS_FILE,StandardCharsets.UTF_8)){
                w.write("id,title,date,time,venue,capacity,description\n");
                for(Event e:events)writeCsvRow(w,Arrays.asList(e.id,e.title,e.date,e.time,e.venue,String.valueOf(e.capacity),e.description));
            }
            try(BufferedWriter w=Files.newBufferedWriter(REG_FILE,StandardCharsets.UTF_8)){
                w.write("name,studentId,email,department,eventId,registeredOn\n");
                for(Registration r:registrations)writeCsvRow(w,Arrays.asList(r.name,r.studentId,r.email,r.department,r.eventId,r.registeredOn));
            }
        }catch(IOException ex){showError("Unable to save data: "+ex.getMessage());}
    }
    private void writeCsvRow(BufferedWriter w,List<String> values)throws IOException {
        for(int i=0;i<values.size();i++){if(i>0)w.write(",");String s=values.get(i)==null?"":values.get(i);w.write("\""+s.replace("\"","\"\"")+"\"");}w.write("\n");
    }
    private List<String> parseCsv(String line) {
        List<String> out=new ArrayList<>();StringBuilder b=new StringBuilder();boolean quoted=false;
        for(int i=0;i<line.length();i++){char c=line.charAt(i);
            if(c=='"'){if(quoted&&i+1<line.length()&&line.charAt(i+1)=='"'){b.append('"');i++;}else quoted=!quoted;}
            else if(c==','&&!quoted){out.add(b.toString());b.setLength(0);}else b.append(c);
        }out.add(b.toString());return out;
    }
    private Event findEvent(String id){for(Event e:events)if(e.id.equals(id))return e;return null;}
    private String nextEventId(){int max=0;for(Event e:events)try{max=Math.max(max,Integer.parseInt(e.id));}catch(Exception ignored){}return String.valueOf(max+1);}
    private void showError(String msg){JOptionPane.showMessageDialog(this,msg,"Please check",JOptionPane.WARNING_MESSAGE);}

    private static class Event {
        String id,title,date,time,venue,description; int capacity;
        Event(String id,String title,String date,String time,String venue,int capacity,String description){this.id=id;this.title=title;this.date=date;this.time=time;this.venue=venue;this.capacity=capacity;this.description=description;}
    }
    private static class Registration {
        String name,studentId,email,department,eventId,registeredOn;
        Registration(String name,String studentId,String email,String department,String eventId,String registeredOn){this.name=name;this.studentId=studentId;this.email=email;this.department=department;this.eventId=eventId;this.registeredOn=registeredOn;}
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored) {}
            new Main().setVisible(true);
        });
    }
}
