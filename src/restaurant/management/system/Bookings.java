/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package restaurant.management.system;

import dao.BookingDao;
import dao.DbOperations;
import dao.ReservationtableDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import model.Booking;
import model.Reservationtable;

/**
 *
 * @author Kavini
 */
public class Bookings extends javax.swing.JFrame {
    public String emailPattern = "^[a-zA-Z0-9]+[@]+[a-zA-Z0-9]+[.]+[a-zA-Z0-9]+$";
    public String mobilePattern = "^[0-9]*$";

    /**
     * Creates new form Bookings
     */
    public Bookings() {
        initComponents();
        btnCheckAvailability.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        
        // Set the minimum selectable date to today's date
        jDateChooser2.setMinSelectableDate(new Date());
    }

    public void validateFields(){
        String name = txtName.getText();
        String email = txtEmail.getText();
        String mobile = txtMobile.getText();
        String tableNo = (String) ComboBoxTableNo.getSelectedItem();
        Date date = jDateChooser2.getDate();
        String start_time = (String) jComboBoxTime.getSelectedItem();
        String end_time = (String) jComboBoxTime1.getSelectedItem();
        if(email.matches(emailPattern) && mobile.matches(mobilePattern) && mobile.length()==10 && !name.equals("") && !tableNo.equals("") && date != null && !start_time.equals("") && end_time.equals(""))
            btnCheckAvailability.setEnabled(true);
        else
            btnCheckAvailability.setEnabled(false);
    }
    
    private void updateEndTimeOptions() {
    jComboBoxTime1.removeAllItems();
    String selectedStartTime = (String) jComboBoxTime.getSelectedItem();
    
    if (selectedStartTime != null) {
        int startHour = Integer.parseInt(selectedStartTime.split(":")[0]);

        for (int i = startHour + 1; i <= 19; i++) {
            String hour = String.format("%02d", i);
            jComboBoxTime1.addItem(hour + ":00");
        }
    }
}
    
    private void searchBookings(String searchCriteria) {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0); // Clear the table before populating with search results

        ArrayList<Booking> searchResults = BookingDao.searchBookings(searchCriteria);

        for (Booking booking : searchResults) {
            model.addRow(new Object[] { 
                booking.getId(), 
                booking.getName(), 
                booking.getEmail(), 
                booking.getMobile(), 
                booking.getGuests(), 
                booking.getTableNo(), 
                booking.getDate(), 
                booking.getStartTime(), 
                booking.getEndTime() 
            });
        }
    }
    
    // Method to generate the report
    private void generateReport() {
        Document document = new Document();
        try {
            String filePath = "D:\\Downloads\\Bookings_Report.pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();
            addTitlePage(document);
            addContent(document);
            
            document.close();
            JOptionPane.showMessageDialog(null, "Report Generated Successfully !");
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage());
        }
    }

    private void addTitlePage(Document document) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD, BaseColor.BLUE.darker());
        Font subTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14);
        Paragraph title = new Paragraph("Restaurant Bookings Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), subTitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subTitle);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
    }

    private void addContent(Document document) throws DocumentException {
    Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    PdfPTable table = new PdfPTable(8);
    table.setWidthPercentage(100);
    
    // Define the widths for each column
    float[] columnWidths = {0.5f, 1.5f, 1.5f, 1, 1, 0.8f, 1, 0.8f}; // Adjust the widths as needed
    table.setWidths(columnWidths);

    String[] headers = {"ID", "Name", "Email", "Contact No.", "No. of Guests", "Table No.", "Date", "Start Time", "End Time"};
    for (String header : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
        BaseColor creamColor = new BaseColor(255, 253, 208);
        cell.setBackgroundColor(creamColor); // Set background color
        table.addCell(cell);
    }

    // Create font for the table content
    Font tableFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
    
    DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
    int rowCount = model.getRowCount();
    for (int i = 0; i < rowCount; i++) {
        for (int j = 0; j < model.getColumnCount(); j++) {
            // Create PdfPCell with table content font
            PdfPCell cell = new PdfPCell(new Phrase(model.getValueAt(i, j).toString(), tableFont));
            if (j == 2) { // Email field
                cell.setFixedHeight(20f); // Adjust the height for the email field as needed
            }
            table.addCell(cell);
        }
    }
    document.add(table);
    }

    private static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private final Font footerFont = new Font(Font.FontFamily.HELVETICA, 6);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(1);
            try {
                footer.setWidths(new int[]{24});
                footer.setTotalWidth(527);
                footer.setLockedWidth(true);
                footer.getDefaultCell().setFixedHeight(40);
                footer.getDefaultCell().setBorder(Rectangle.TOP);
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                footer.addCell(new Phrase("Restaurant Bookings Report - Page " + writer.getPageNumber(), footerFont));
                footer.writeSelectedRows(0, -1, 34, 50, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnAddReservation = new javax.swing.JButton();
        ComboBoxTableNo = new javax.swing.JComboBox<>();
        SpinnerGuests = new javax.swing.JSpinner();
        txtMobile = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnCheckAvailability = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        jComboBoxTime = new javax.swing.JComboBox<>();
        jComboBoxTime1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        btnDownload = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 70, 30, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/View Bills & Order Placed Details.png"))); // NOI18N
        jLabel1.setText("Edit Booking");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 270, -1));

        btnAddReservation.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnAddReservation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new product.png"))); // NOI18N
        btnAddReservation.setText("Add Booking");
        btnAddReservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddReservationActionPerformed(evt);
            }
        });
        getContentPane().add(btnAddReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, -1, -1));

        ComboBoxTableNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ComboBoxTableNoKeyReleased(evt);
            }
        });
        getContentPane().add(ComboBoxTableNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 470, 240, -1));

        SpinnerGuests.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));
        getContentPane().add(SpinnerGuests, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 430, 240, -1));

        txtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMobileKeyReleased(evt);
            }
        });
        getContentPane().add(txtMobile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 390, 240, -1));

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEmailKeyReleased(evt);
            }
        });
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 350, 240, -1));

        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });
        getContentPane().add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 310, 240, -1));

        jDateChooser2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jDateChooser2KeyReleased(evt);
            }
        });
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 510, 240, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Start Time :");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 560, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Date :");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 510, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Table No :");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 470, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Number of Guests :");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 430, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Contact Number :");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 390, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Emai :");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 350, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("ID :");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view edit delete product.png"))); // NOI18N
        jLabel10.setText("Manage Bookings");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 270, -1));

        btnCheckAvailability.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCheckAvailability.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/place order.png"))); // NOI18N
        btnCheckAvailability.setText("Check Availability");
        btnCheckAvailability.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckAvailabilityActionPerformed(evt);
            }
        });
        getContentPane().add(btnCheckAvailability, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 660, 190, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("View Bookings");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 140, -1, -1));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Email", "Contact No.", "No. of Guests", "Table No.", "Date", "Start Time", "End Time"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 190, 700, 310));

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 710, -1, -1));

        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 710, -1, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("Name :");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, -1, -1));

        lblId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblId.setText("00");
        getContentPane().add(lblId, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 270, -1, -1));

        jComboBoxTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTimeActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 560, 240, -1));

        jComboBoxTime1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTime1ActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxTime1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 600, 240, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("End Time :");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 600, -1, -1));

        btnDownload.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/View Bills & Order Placed Details.png"))); // NOI18N
        btnDownload.setText("Download Reports");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });
        getContentPane().add(btnDownload, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 130, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Search :");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 140, -1, 30));

        txtSearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 140, 90, 30));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/full-page-background.PNG"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 770));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Start Time :");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 560, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        int a= JOptionPane.showConfirmDialog(null, "Do you want to Close this ?", "Select", JOptionPane.YES_NO_OPTION);
        if(a==0){
            //setVisible(false);
            new Admin().setVisible(true);
        }
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnAddReservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddReservationActionPerformed
        // TODO add your handling code here:
        //setVisible(false);
        new AddReservation().setVisible(true);
    }//GEN-LAST:event_btnAddReservationActionPerformed

    private void btnCheckAvailabilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckAvailabilityActionPerformed
        // TODO add your handling code here:
        String tableNo = (String) ComboBoxTableNo.getSelectedItem();
        Date date = jDateChooser2.getDate();
        // Get the selected time slot
        String start_time = (String) jComboBoxTime.getSelectedItem();
        String end_time = (String) jComboBoxTime1.getSelectedItem();
        if (tableNo == null || date == null) {
            JOptionPane.showMessageDialog(null, "Please select a table, date, and time.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);

//        String query = "SELECT * FROM booking WHERE tableNo = '" + tableNo + "' AND date = '" + formattedDate + "' AND time = '" + time + "'";
//        ResultSet rs = DbOperations.getData(query);

String query = "SELECT * FROM booking WHERE tableNo = '" + tableNo + "' AND date = '" + formattedDate + "' AND (('" + start_time + "' < end_time AND '" + end_time + "' > start_time) OR ('" + start_time + "' = start_time AND '" + end_time + "' = end_time))";
    ResultSet rs = DbOperations.getData(query);

        try {
            if (rs != null && rs.next()) {
                JOptionPane.showMessageDialog(null, "Table is not available at the selected date and time.");
                btnUpdate.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Table is Available.");
                btnUpdate.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCheckAvailabilityActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        Booking booking = new Booking();
        int id = Integer.parseInt(lblId.getText());
        booking.setId(id);
        booking.setName(txtName.getText());
        booking.setEmail(txtEmail.getText());
        booking.setMobile(txtMobile.getText());
        booking.setGuests((int) SpinnerGuests.getValue());
        booking.setTableNo((String) ComboBoxTableNo.getSelectedItem());
        // Convert JDateChooser date to String
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(jDateChooser2.getDate());
        booking.setDate(formattedDate);
        booking.setStartTime((String) jComboBoxTime.getSelectedItem());
        booking.setEndTime((String) jComboBoxTime1.getSelectedItem());
        int a = JOptionPane.showConfirmDialog(null,"Do yo want to Update Booking ? ","Select",JOptionPane.YES_NO_OPTION);
        if(a==0){
            BookingDao.update(booking);
            //setVisible(false);
            new Bookings().setVisible(true);
        }
        
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtEmailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtEmailKeyReleased

    private void txtMobileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMobileKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtMobileKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) jTable2.getModel();
        ArrayList<Booking> list = BookingDao.getAllRecords();
        Iterator<Booking> itr = list.iterator();
            while(itr.hasNext()){
            Booking bookingObj = itr.next();
            dtm.addRow(new Object[] {bookingObj.getId(),bookingObj.getName(),bookingObj.getEmail(),bookingObj.getMobile(),bookingObj.getGuests(),bookingObj.getTableNo(),bookingObj.getDate(),bookingObj.getStartTime(),bookingObj.getEndTime()});
        }
            
        for (int i = 8; i <= 19; i++) {
            String hour = String.format("%02d", i);
            jComboBoxTime.addItem(hour + ":00");
        }
        
    }//GEN-LAST:event_formComponentShown

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        int index = jTable2.getSelectedRow();
        TableModel model = jTable2.getModel();
        String id = model.getValueAt(index, 0).toString();
        lblId.setText(id);
        String name = model.getValueAt(index, 1).toString();
        txtName.setText(name);
        String email = model.getValueAt(index, 2).toString();
        txtEmail.setText(email);
        String mobile = model.getValueAt(index, 3).toString();
        txtMobile.setText(mobile);
        String guests = model.getValueAt(index, 4).toString();
        SpinnerGuests.setValue(Integer.parseInt(guests));
        String tableNo = model.getValueAt(index, 5).toString();
        String date = model.getValueAt(index, 6).toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            jDateChooser2.setDate(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String start_time = model.getValueAt(index, 7).toString();  
        String end_time = model.getValueAt(index, 7).toString();  
        // Set previously selected time in jComboBoxTime
        jComboBoxTime.setSelectedItem(start_time);
        jComboBoxTime1.setSelectedItem(end_time);
        
        btnCheckAvailability.setEnabled(true);
        btnDelete.setEnabled(true);
        ComboBoxTableNo.removeAllItems();
        ComboBoxTableNo.addItem(tableNo);
        
        ArrayList<Reservationtable> reservationtableList = ReservationtableDao.getAllRecords();
        Iterator<Reservationtable> reservationtableItr = reservationtableList.iterator();
        while(reservationtableItr.hasNext()){
                Reservationtable reservationtableObj = reservationtableItr.next();
                if(!reservationtableObj.getName().equals(tableNo))
                    ComboBoxTableNo.addItem(reservationtableObj.getName());
            }
    }//GEN-LAST:event_jTable2MouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        String id = lblId.getText();
        int a = JOptionPane.showConfirmDialog(null,"Do yo want to Delete Booking ? ","Select",JOptionPane.YES_NO_OPTION);
        if(a==0){
            BookingDao.delete(id);
            //setVisible(false);
            new Bookings().setVisible(true);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jDateChooser2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDateChooser2KeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_jDateChooser2KeyReleased

    private void ComboBoxTableNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ComboBoxTableNoKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_ComboBoxTableNoKeyReleased

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtNameKeyReleased

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        // TODO add your handling code here:
        generateReport();
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        String searchCriteria = txtSearch.getText().trim();
        searchBookings(searchCriteria);
    }//GEN-LAST:event_txtSearchKeyReleased

    private void jComboBoxTime1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTime1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jComboBoxTime1ActionPerformed

    private void jComboBoxTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTimeActionPerformed
        // TODO add your handling code here:
    updateEndTimeOptions();
    }//GEN-LAST:event_jComboBoxTimeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Bookings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Bookings().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBoxTableNo;
    private javax.swing.JSpinner SpinnerGuests;
    private javax.swing.JButton btnAddReservation;
    private javax.swing.JButton btnCheckAvailability;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> jComboBoxTime;
    private javax.swing.JComboBox<String> jComboBoxTime1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblId;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtMobile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

}
