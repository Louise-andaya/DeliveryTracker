package lamcomis.landaya.deliverytracker.Global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.util.HashMap;

import lamcomis.landaya.deliverytracker.Admin.AdminActivity;
import lamcomis.landaya.deliverytracker.Driver.DriverAcrivity;
import lamcomis.landaya.deliverytracker.Login;
import lamcomis.landaya.deliverytracker.R;

public class PromptActivity {
    Context context;
    static DatabaseHelper myDb;
    static SessionManager  sessionManager;
    static String user_type;
    public static  void     showAlert(final Context context, final String alert_type){
        sessionManager = new SessionManager(context);
        myDb = new DatabaseHelper(context);
        HashMap<String, String> user = sessionManager.getUserDetails();
        user_type = user.get("user_type");
        String title = "";
        String message = "";
        String yes = "Yes";
        String cancel = "No";

        switch (alert_type){
            case "error":
                title = " Volley Error";
                message = "Please Contact MIS Local(5106) for this error";
                yes = "Okay";
                cancel = "";
                break;

            case "update_data":
                title = " Data is not Updated";
                message = "Do you want to Update it?";
                yes = "Yes";
                cancel = "No";
                break;

            case "empty_data":
                title = " Empty Data";
                message = "Do you want to Load it?";
                yes = "Yes";
                cancel = "No";
                break;

            case "no_data":
                title = " Empty Data";
                message = "You Don't have Delivery for today";
                yes = "Okay";
                cancel = "";
                break;
            case "internet":
                title = " Error";
                message = "Please Check Your Internet Connection";
                yes = "Okay";
                cancel = "";
                break;

            case "invalid_password":
                title = " Error";
                message = "Invalid Password";
                yes = "Okay";
                cancel = "";
                break;

            case "empty":
                title = "";
                message = "Empty Credentials";
                yes = "Okay";
                cancel = "";
                break;

            case "not_found":
                title = " Error";
                message = "User not Found";
                yes = "Okay";
                cancel = "";
                break;

            case "dont_match":
                title = "";
                message = "Password Dont Match";
                yes = "Okay";
                cancel = "";
                break;

            case "logout":
                title = " Alert";
                message = "Are you sure you want Logout?";
                yes = "Yes";
                cancel = "No";
                break;

            case "save_success":
                title = " Success";
                message = "Data Saved Successfully";
                yes = "Okay";
                cancel = "";
                break;

            case "send_sever_success":
                title = " Success";
                message = "Data Saved Successfully";
                yes = "Okay";
                cancel = "";
                break;

            case "data_save":
                title = " Success";
                message = "Data Saved Successfully";
                yes = "Okay";
                cancel = "";
                break;

            case "saving_failed":
                title = " Error";
                message = "Data not save successfully";
                yes = "Okay";
                cancel = "";
                break;

            case "success_changepass":
                title = " Success";
                message = "Password Updated Successfully";
                yes = "Okay";
                cancel = "";
                break;

            case "back":
                title = " Alert";
                message = "Are you sure you want to Exit?";
                yes = "Yes";
                cancel = "No";
                break;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setIcon(R.drawable.flat_ardent)
                .setCancelable(false)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;

                        switch (alert_type){
                            case "error":
                                dialog.cancel();
                                break;
                            case "update_data":
                                if(InternetConnection.checkConnection(context)){
                                    DriverAcrivity.SaveData(context);
                                }
                                else{
                                    PromptActivity.showAlert(context,"internet");
                                }
                                break;
                            case "empty_data":
                                if(InternetConnection.checkConnection(context)){
                                    DriverAcrivity.SaveData(context);
                                }
                                else{
                                    PromptActivity.showAlert(context,"internet");
                                }
                                break;

                            case "no_data":
                                sessionManager = new SessionManager(context);
                                sessionManager.logoutUser();
                                intent = new Intent(context, Login.class);
                                context.startActivity(intent);
                                break;
                            case "saving_failed":
                                if(InternetConnection.checkConnection(context)){
                                    myDb.deleteData();
                                    DriverAcrivity.SaveData(context);
                                }
                                else{
                                    PromptActivity.showAlert(context,"internet");
                                }
                                break;
                            case "internet":
                                dialog.cancel();
                                break;

                            case "invalid_password":
                                dialog.cancel();
                                break;

                            case "empty":
                                dialog.cancel();
                                break;

                            case "dont_match":
                                dialog.cancel();
                                break;

                            case "not_found":
                                dialog.cancel();
                                break;

                            case "back":
                                intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                                System.exit(0);
                                break;

                            case "save_success":
                                DriverAcrivity.getOfflineData(context);
                                break;

                            case "data_save":
                                intent = new Intent(context, DriverAcrivity.class);
                                context.startActivity(intent);
                                break;

                            case "send_sever_success":
                                intent = new Intent(context, DriverAcrivity.class);
                                context.startActivity(intent);
                                break;

                            case "success_changepass":
                                dialog.cancel();
                                break;

                            case "logout":
                                myDb.deleteData();
                                sessionManager = new SessionManager(context);
                                sessionManager.logoutUser();
                                Intent tohome = new Intent(context, Login.class);
                                context.startActivity(tohome);
                                break;

                        }
                    }
                });


        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (alert_type){
                    case "error":
                        break;
                    case "update_data":
                        dialog.cancel();
                        DriverAcrivity.getOfflineData(context);
                        break;
                    case "empty_data":
                        dialog.cancel();
                        break;
                    case "back":
                        dialog.cancel();
                        break;

                    case "logout":
                        dialog.cancel();
                        break;



                    default: dialog.cancel();
                }


            }
            });
        builder.create().show();

    }
}
