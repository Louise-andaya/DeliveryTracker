package lamcomis.landaya.deliverytracker.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import lamcomis.landaya.deliverytracker.Adapter.SIAdapter;
import lamcomis.landaya.deliverytracker.BuildConfig;
import lamcomis.landaya.deliverytracker.Global.DatabaseHelper;
import lamcomis.landaya.deliverytracker.Global.InternetConnection;
import lamcomis.landaya.deliverytracker.Global.PromptActivity;
import lamcomis.landaya.deliverytracker.Global.SessionManager;
import lamcomis.landaya.deliverytracker.List.SIList;
import lamcomis.landaya.deliverytracker.R;

import static android.media.MediaRecorder.VideoSource.CAMERA;


public class ProcessDelivery extends AppCompatActivity {
    DatabaseHelper db;
    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    String TAG = "Location Status";
    String latitude, longitude, cur_address;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    ImageView ImageCapture;
    byte[] CaImage = null;
    Button capture, btn_sign;
    String img_receipt;

    ImageView Signature;
    String signature;
    byte[] sig = null;
    Button mClear, mGetSign, mCancel;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;

    SessionManager sessionManager;
    ProgressDialog pd;

    FloatingActionButton save;

    EditText txt_firstman, txt_secondman;

    TextView txt_customer, txt_custcode, txt_contact, txt_dtr, txt_remarks, txt_address;

    ListView list_si;
    private SIAdapter adapter;
    List<SIList>siLists;
    ArrayList si;

    String customer;
    Cursor res;

    String date;
    String driver;
    String dtr_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_delivery);
        pd = new ProgressDialog(this);
        pd.setMessage("Saving Please Wait...");
        ButterKnife.bind(this);
        init();
        restoreValuesFromBundle(savedInstanceState);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        driver = user.get(SessionManager.KEY_USERID);
        startLocation();
        customer = getIntent().getExtras().getString("customer");
        txt_firstman = findViewById(R.id.firstMan);
        txt_secondman = findViewById(R.id.secondMan);
        txt_customer = findViewById(R.id.custName);
        txt_custcode = findViewById(R.id.custCode);
        txt_contact = findViewById(R.id.cntPerson);
        txt_dtr = findViewById(R.id.dtr_no);
        list_si = findViewById(R.id.si_no);
        txt_remarks = findViewById(R.id.remarks);
        txt_address = findViewById(R.id.address);
        
        dialog = new Dialog(ProcessDelivery.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(false);
        save = findViewById(R.id.btn_save);
        save.setClickable(true);
        db = new DatabaseHelper(this);
        siLists = new ArrayList<>();
        si = new ArrayList();

        ImageCapture = (ImageView)findViewById(R.id.receipt);
        capture = (Button)findViewById(R.id.receiptCapture);
        capture.setClickable(true);
        capture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    capture.setClickable(false);
                    if (ContextCompat.checkSelfPermission(ProcessDelivery.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (getFromPref(ProcessDelivery.this, ALLOW_KEY)) {

                            showAlert();

                        } else if (ContextCompat.checkSelfPermission(ProcessDelivery.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ProcessDelivery.this,
                                    Manifest.permission.CAMERA)) {
                                showAlert();
                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(ProcessDelivery.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_CAMERA);
                            }
                        }

                    } else {
                        OpenCamera();
                    }
                    return true;
                }
                return false;
            }
        });

        btn_sign = findViewById(R.id.signButton);
        btn_sign.setClickable(true);
        Signature = findViewById(R.id.Signature);
        btn_sign.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btn_sign.setClickable(false);
                    dialog_action();
                    return true;
                }
                return false;
            }
        });

        getData();
        save.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    save.setClickable(false);
                    pd.show();
                    if(signature!= null){
                        InsertData();
                    }
                    else{
                        pd.dismiss();
                        dialog_action();
                        save.setClickable(true);

                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getData() {
        res = db.getDetails(customer, "ATV");
        if(res.getCount() == 0){
            return;
        }
        else{
            while (res.moveToNext()){
                txt_firstman.setText(res.getString(7));
                txt_secondman.setText(res.getString(8));
                dtr_date = res.getString(9);
                txt_customer.setText(res.getString(2));
                txt_custcode.setText(res.getString(4));
                txt_contact.setText(res.getString(3));
                txt_dtr.setText(res.getString(5));
                SIList sinumber = new SIList();
                sinumber.setSI(res.getString(res.getColumnIndex("Si_number")));
                siLists.add(sinumber);
                txt_remarks.setText(res.getString(6));
            }
            adapter = new SIAdapter(ProcessDelivery.this, siLists);
            list_si.setAdapter(adapter);
            LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,siLists.size()*100);
            list_si.setLayoutParams(mParam);
            list_si.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    adapter.setCheckBox(position);

                }
            });

        }
    }

    //LOCATION
    private void startLocation() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void openSettings() {
        Intent intent = new Intent(this, DriverAcrivity.class);
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        SaveFunction();

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ProcessDelivery.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(ProcessDelivery.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //CAMERA
    private void OpenCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    public void onActivityResult(int requestcode,int resultcode,Intent intent) {
        super.onActivityResult(requestcode, resultcode, intent);
        if(resultcode==RESULT_OK&&requestcode==CAMERA)
        {
            Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
            ImageCapture.setImageBitmap(bitmap);
            capture.setClickable(true);
            save.setClickable(true);
            Bitmap bit = ((BitmapDrawable)ImageCapture.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            CaImage = baos.toByteArray();
            img_receipt = Base64.encodeToString(CaImage, Base64.NO_WRAP);
            Log.d("Image",img_receipt);
        }
        else{
            capture.setClickable(true);
        }
    }
    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }
    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        capture.setClickable(true);
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        capture.setClickable(true);
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(ProcessDelivery.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
        alertDialog.show();
    }
    @Override
    public void onRequestPermissionsResult
            (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (this, permission);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(ProcessDelivery.this, ALLOW_KEY, true);

                        }
                    }
                    else{
                        capture.setClickable(true);
                    }
                }
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }
    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void SaveFunction() {
        if(mCurrentLocation != null){
            latitude = String.valueOf(mCurrentLocation.getLatitude());
            longitude = String.valueOf(mCurrentLocation.getLongitude());
            try {
                Geocoder geocoder = new Geocoder(ProcessDelivery.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    Log.d(TAG, "getAddress:  address" + address);
                    Log.d(TAG, "getAddress:  city" + city);
                    cur_address = address;
                    txt_address.setText(cur_address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            pd.dismiss();
            startLocation();
            save.setClickable(true);
        }
    }

    //SIGNATURE
    private void dialog_action() {

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);

        mSignature = new signature(getApplicationContext(), null);

        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mClear.setEnabled(true);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.v("tag", "Panel Saved");
                mSignature.save(view);

                dialog.cancel();

            }
        });



        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recreate();
                //dialog.cancel();
            }
        });
        dialog.show();
    }
    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

        @SuppressLint("WrongThread")
        public void save(View v) {

            Log.v("tag", "Width: " + v.getWidth());
            Log.v("tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);

            }
            Canvas canvas = new Canvas(bitmap);
            v.draw(canvas);

            ByteArrayOutputStream sign = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 98, sign);
            Signature.setImageBitmap(bitmap);

            Bitmap bits = ((BitmapDrawable)Signature.getDrawable()).getBitmap();
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            bits = Bitmap.createScaledBitmap(bitmap, 1024, 364, false);
            bits.compress(Bitmap.CompressFormat.JPEG, 98, b);
            sig = b.toByteArray();
            signature = Base64.encodeToString(sig, Base64.DEFAULT);

            Log.d("signature", signature);

        }
    }

    //SAVEDATA
    private void InsertData() {
        for(SIList hold1: adapter.getRecProgAllData()){
            if(hold1.isCheckbox()==true){
                si.add(hold1.getSI());
            }
            else{
                pd.dismiss();
                save.setClickable(true);
                Toast.makeText(getApplicationContext(), "Please Select SI Number", Toast.LENGTH_LONG).show();
            }
        }
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        date = format.format(today);

        for (int i = 0; i < si.size(); i++) {

            String si_number = si.get(i).toString();
            String customer_name = txt_customer.getText().toString();
            String contact_person = txt_contact.getText().toString();
            String customer_code = txt_custcode.getText().toString();
            String dtr_number = txt_dtr.getText().toString();
            String remarks = txt_remarks.getText().toString();
            String firstman = txt_firstman.getText().toString();
            String secondman = txt_secondman.getText().toString();


            boolean insert = db.insertData(si_number,driver, customer_name, contact_person, customer_code, dtr_number, remarks, firstman, secondman, dtr_date, "SVD", img_receipt, signature, date,
                    latitude, longitude, cur_address);


            if (insert == true) {
                PromptActivity.showAlert(ProcessDelivery.this, "data_save");
                pd.dismiss();


            } else {
                Toast.makeText(getApplicationContext(), "Data not Inserted", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }


    }

}
