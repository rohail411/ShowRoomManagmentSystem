package com.rohailbutt411gmail.showroommanagmentsystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Report extends AppCompatActivity {

    private DatabaseReference expenseDatabase;
    private DatabaseReference soldDatabase;
    private DatabaseReference userDatabase;
    private int expense=0;
    private int profit = 0;
    private int netProfit = 0;
    private DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    private Calendar calendar;
    private EditText edt_date2,edt_chasis,edt_date1;
    private TextView totalText;
    private ListView listView;
    private List<String> list;
    private Spinner reportUserSpinner;
    private ArrayList<String> userArrayList;
    private String filterUser;
    Date dt,dt2 ;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private ArrayList<Sold> mList;
    private ArrayList<Expense> eList;
    private Expense expense_report;
    private Sold report_sold;
    private File pdfFile;
    Sold reportBuyerName;
    Sold reportRegNo;
    Sold reportChasisNo;
    Sold reportBrand;
    Sold reportColor;
    Sold reportModel;
    Sold reportSellAmount;
    Sold reportDate;
    Sold reportUser;
    Sold reportPurchaseAmount;
    Sold reportRemainingAmount;
    Sold reportProfit;
    Sold reportPurchaseUser;
    Expense reportExpenseType;
    Expense reportExpenseUser;
    Expense reportExpenseDetail;
    Expense reportExpenseAmount;
    Expense reportExpenseDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setTitle("Report");
        expenseDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses");
        soldDatabase = FirebaseDatabase.getInstance().getReference().child("Sold");
        edt_date1 = (EditText) findViewById(R.id.date_Field1);
        edt_date2 = (EditText) findViewById(R.id.date_Field2);
        edt_chasis = (EditText) findViewById(R.id.edt_chasis_no);
        totalText = (TextView) findViewById(R.id.total);
        listView = (ListView) findViewById(R.id.report_listView);
        reportUserSpinner = (Spinner) findViewById(R.id.report_user_filter);
        list = new ArrayList<>();
        report_sold = new Sold();
        expense_report = new Expense();
        userArrayList = new ArrayList<>();
        userArrayList.add("all");
        userDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User u = dataSnapshot.getValue(User.class);
                userArrayList.add(u.getName());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,userArrayList);
        reportUserSpinner.setAdapter(userAdapter);

        reportUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterUser = userArrayList.get(position);
                if(!edt_date1.getText().toString().equals("") && !edt_date2.getText().toString().equals("")){
                    profit = 0;
                    expense = 0;
                    list.clear();
                    fetch_date_queryset();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void btn_chasis(View view) {

        search_bike_detail(edt_chasis.getText().toString());
    }
    public void search_bike_detail(String chasis_no){
        soldDatabase.orderByChild("chasis_no").equalTo(chasis_no).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Sold so = dataSnapshot.getValue(Sold.class);

                final AlertDialog.Builder builder = new AlertDialog.Builder(Report.this);
                builder.setCancelable(false);
                builder.setTitle("Buyer \t\t"+so.getBuyer_name());
                builder.setMessage("Features \t\t"+so.getBrand().toUpperCase()+"/"+so.getColor().toUpperCase()+"/"+so.getModel()+"\n"+
                        "Chasis # \t\t"+so.getChasis_no()+"\n"+"Reg # \t\t\t\t\t"+so.getReg_no().toUpperCase()+"\n"+"Sell Price \t\t"+String.valueOf(so.getSell_amount()));
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void btn_date_search(View view) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) listView.getItemAtPosition(position);
                search_bike_detail(value);
            }
        });
        netProfit = profit-expense;
        totalText.setText(String.valueOf(netProfit));

    }
    public void fetch_date_queryset(){
        mList = new ArrayList<Sold>();
        eList = new ArrayList<Expense>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try{
            String[] d1 = edt_date1.getText().toString().split("/");
            String[] d2 = edt_date2.getText().toString().split("/");
            dt =  sdf.parse(String.valueOf(Integer.parseInt(d1[2])+1900)+"/"+String.valueOf((Integer.parseInt(d1[1])+1))+"/"+d1[0]);
            dt2 =  sdf.parse(String.valueOf(Integer.parseInt(d2[2])+1900)+"/"+String.valueOf((Integer.parseInt(d2[1])+1))+"/"+d2[0]);
        }catch(Exception e){
            Log.e("Error",e.getMessage());
        }
        expenseDatabase.orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Expense o = dataSnapshot.getValue(Expense.class);
                if((dt.equals(o.getDate())|| dt.before(o.getDate()))&&(dt2.equals(o.getDate())||dt2.after(o.getDate()))){
                    expense += o.getAmount();
                    expense_report.setExpense_type(o.getExpense_type());
                    expense_report.setUser(o.getUser());
                    expense_report.setDetail(o.getDetail());
                    expense_report.setAmount(o.getAmount());
                    expense_report.setDate(o.getDate());
                    eList.add(expense_report);
                    expense_report = new Expense();

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        soldDatabase.orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Sold sold = dataSnapshot.getValue(Sold.class);
                if((dt.equals(sold.getDate())|| dt.before(sold.getDate()))&&(dt2.equals(sold.getDate())||dt2.after(sold.getDate())) && (filterUser.equals("all")|| filterUser.equals(sold.getUser().toLowerCase()))){
                    profit += sold.getProfit();
                    list.add(sold.getChasis_no());
                    report_sold.setReg_no(sold.getReg_no());
                    report_sold.setChasis_no(sold.getChasis_no());
                    report_sold.setBrand(sold.getBrand());
                    report_sold.setColor(sold.getColor());
                    report_sold.setModel(sold.getModel());
                    report_sold.setBuyer_name(sold.getBuyer_name());
                    report_sold.setUser(sold.getUser());
                    report_sold.setBuy_amount(sold.getBuy_amount());
                    report_sold.setRemaining_amount(sold.getRemaining_amount());
                    report_sold.setSell_amount(sold.getSell_amount());
                    report_sold.setDate(sold.getDate());
                    report_sold.setProfit(sold.getProfit());
                    report_sold.setPurchase_user(sold.getPurchase_user());
                    mList.add(report_sold);
                    report_sold = new Sold();

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void date_picker2(View view) {

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(Report.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                profit = 0;
                expense = 0;
                edt_date2.setText(dayOfMonth+"/"+String.valueOf(month+1)+"/"+year);
                if(!edt_date2.getText().toString().equals("") && !edt_date1.getText().toString().equals("")){
                    list.clear();
                    fetch_date_queryset();
                }
            }
        },year,month,dayOfMonth);
        datePickerDialog.show();

    }

    public void date_picker1(View view) {

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(Report.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                profit = 0;
                expense = 0;
                edt_date1.setText(dayOfMonth+"/"+String.valueOf(month+1)+"/"+year);
                if(!edt_date2.getText().toString().equals("") && !edt_date1.getText().toString().equals("")){
                    list.clear();
                    fetch_date_queryset();
                }

            }
        },year,month,dayOfMonth);
        datePickerDialog.show();
    }

    public void report_info_btn(View view) {
        final AlertDialog.Builder info = new AlertDialog.Builder(Report.this);
        info.setCancelable(false);
        info.setTitle("Report Info");
        info.setMessage("Total Profit   :\t\t\t"+String.valueOf(profit)+"\n"+"Total Expenses:\t\t\t"+String.valueOf(expense)+"\n"+
        "Net Profit:    \t\t\t\t"+String.valueOf(profit-expense));
        info.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        info.show();
    }
    private void createPdfWrapper() throws FileNotFoundException, DocumentException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            Log.e("Yarrr","Ke krna pya aa");
            createPdf();
        }
    }
    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i("File Created", "Created a new directory for PDF");
        }
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String currentdate = df.format(Calendar.getInstance().getTime());
        String pdfname = currentdate+".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{3, 3, 3, 3, 3,3,3,3,3,3,3,3});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("SR #");
        table.addCell("Reg #");
        table.addCell("Chasis #");
        table.addCell("Features");
        table.addCell("Purchase User");
        table.addCell("Seller User");
        table.addCell("Buyer Name");
        table.addCell("Purchase Amount");
        table.addCell("Sell Amount");
        table.addCell("Sell Date");
        table.addCell("Profit");
        table.addCell("Remaining Amount");

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }

        for (int i = 0; i <mList.size(); i++) {
            reportRegNo = mList.get(i);
            reportChasisNo = mList.get(i);
            reportBrand = mList.get(i);
            reportColor = mList.get(i);
            reportModel = mList.get(i);
            reportBuyerName = mList.get(i);
            reportSellAmount = mList.get(i);
            reportDate = mList.get(i);
            reportUser = mList.get(i);
            reportPurchaseAmount = mList.get(i);
            reportRemainingAmount = mList.get(i);
            reportProfit = mList.get(i);
            reportPurchaseUser = mList.get(i);
            String regNo = reportRegNo.getReg_no();
            String chasisNo = reportChasisNo.getChasis_no();
            String brand = reportBrand.getBrand();
            String color = reportColor.getColor();
            String model = reportModel.getModel();
            String buyer = reportBuyerName.getBuyer_name();
            String seller = reportUser.getUser();
            String purchaseUser = reportPurchaseUser.getPurchase_user();
            int purchaseAmount = reportPurchaseAmount.getBuy_amount();
            int sellAmount = reportSellAmount.getSell_amount();
            Date date = reportDate.getDate();
            int remainingAmount = reportRemainingAmount.getRemaining_amount();
            int profit = reportProfit.getProfit();

            table.addCell(String.valueOf(i));
            table.addCell(regNo);
            table.addCell(chasisNo);
            table.addCell(brand+"/"+color+"/"+model);
            table.addCell(purchaseUser);
            table.addCell(seller);
            table.addCell(buyer);
            table.addCell(String.valueOf(purchaseAmount));
            table.addCell(String.valueOf(sellAmount));
            table.addCell(String.valueOf(date.getDate())+"/"+String.valueOf(date.getMonth())+"/"+String.valueOf(date.getYear()));
            table.addCell(String.valueOf(profit));
            table.addCell(String.valueOf(remainingAmount));

        }
        PdfPTable tableExpense = new PdfPTable(new float[]{3,3, 3, 3, 3, 3});
        tableExpense.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableExpense.getDefaultCell().setFixedHeight(50);
        tableExpense.setTotalWidth(PageSize.A4.getWidth());
        tableExpense.setWidthPercentage(100);
        tableExpense.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        tableExpense.addCell("SR #");
        tableExpense.addCell("Expense Type");
        tableExpense.addCell("Expense Detail");
        tableExpense.addCell("User");
        tableExpense.addCell("Amount");
        tableExpense.addCell("Date");
        tableExpense.setHeaderRows(1);
        PdfPCell[] cells1 = tableExpense.getRow(0).getCells();
        for (int j = 0; j < cells1.length; j++) {
            cells1[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i <eList.size() ; i++) {
            reportExpenseType = eList.get(i);
            reportExpenseDetail = eList.get(i);
            reportExpenseUser = eList.get(i);
            reportExpenseAmount = eList.get(i);
            reportExpenseDate = eList.get(i);

            String expType = reportExpenseType.getExpense_type();
            String expdetail = reportExpenseDetail.getDetail();
            String expUser = reportExpenseUser.getUser();
            int expAmount = reportExpenseAmount.getAmount();
            Date expDate = reportExpenseDate.getDate();

            tableExpense.addCell(String.valueOf(i));
            tableExpense.addCell(expType);
            tableExpense.addCell(expdetail.substring(0,10));
            tableExpense.addCell(expUser);
            tableExpense.addCell(String.valueOf(expAmount));
            tableExpense.addCell(String.valueOf(expDate.getDate())+"/"+String.valueOf(expDate.getMonth())+"/"+String.valueOf(expDate.getYear()));

        }

        PdfPTable tableFooter = new PdfPTable(new float[]{3, 3, 3, 3});
        tableFooter.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableFooter.getDefaultCell().setFixedHeight(50);
        tableFooter.setTotalWidth(PageSize.A4.getWidth());
        tableFooter.setWidthPercentage(100);
        tableFooter.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        tableFooter.addCell("Total");
        tableFooter.addCell("Profit");
        tableFooter.addCell("Expenses");
        tableFooter.addCell("Net Profit");
        tableFooter.setHeaderRows(1);
        PdfPCell[] cells2 = tableFooter.getRow(0).getCells();
        for (int j = 0; j < cells2.length; j++) {
            cells2[j].setBackgroundColor(BaseColor.GRAY);
        }
        tableFooter.addCell("");
        tableFooter.addCell(String.valueOf(profit));
        tableFooter.addCell(String.valueOf(expense));
        tableFooter.addCell(String.valueOf(profit-expense));

        PdfWriter.getInstance(document, output);
        document.open();
        Font f = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE);
        Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);
        document.add(new Paragraph("Sohaib Bajwa Motors \n", f));
        document.add(new Paragraph("Sell Report\n\n", g));
        document.add(table);
        document.add(new Paragraph("\nExpenses\n\n",g));
        document.add(tableExpense);

        document.add(new Paragraph("\nSummary\n\n",g));
        document.add(tableFooter);

//        for (int i = 0; i < MyList1.size(); i++) {
//            document.add(new Paragraph(String.valueOf(MyList1.get(i))));
//        }
        document.close();
        Log.e("list", mList.toString());

        successAlert();
    }
    private void successAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("File Download Successfully");
        builder.setCancelable(false);
        builder.setMessage("Please Visit Documents Folder of your External Storage");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void report_download_btn(View view) {
        try {
            createPdfWrapper();
        } catch (FileNotFoundException e) {
            Log.e("Exception1",e.getMessage());
        } catch (DocumentException e) {
            Log.e("Exception2",e.getMessage());
        }
        catch (Exception e){
            Log.e("Exxception3",e.getMessage());
        }
    }
}
