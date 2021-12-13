package com.techagr;

import com.techagr.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class ReactNativePdfGeneratorModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private static final String LOG_TAG = "ReactNativePdfGenerator";

  private TextView txtViewTimestamp = null;
  private TextView txtViewTitle = null;
  private ImageView imgView = null;

  public ReactNativePdfGeneratorModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "ReactNativePdfGenerator";
  }

  // @Override
  // public Map<String, Object> getConstants() {
  // final Map<String, Object> constants = new HashMap<>();
  // constants.put(LOGO_POSITION_CENTER, "CENTER");
  // return constants;
  // }

  @ReactMethod
  public boolean generate(String albumName, String fileName, String title, String jsonBody, String timestampText,
      String timestampFormat, String imageName, String shareText) {
    LayoutInflater inflater = LayoutInflater.from(reactContext);
    ArrayList<View> arrView = new ArrayList<View>();
    View view = inflater.inflate(R.layout.pdf_layout, null);
    view.measure(595, 842);
    view.layout(0, 0, 595, 842);

    LinearLayout body = view.findViewById(R.id.documentBody);

    // TextView
    txtViewTimestamp = view.findViewById(R.id.txtViewTimestamp);
    txtViewTitle = view.findViewById(R.id.txtViewTitle);

    // ImageView - imageName supplied
    imgView = view.findViewById(R.id.imgViewLogo);
    Resources res = reactContext.getResources();
    int imgId = res.getIdentifier(imageName, "drawable", reactContext.getPackageName());
    imgView.setImageResource(imgId);

    // Set timestamp text
    SimpleDateFormat s = new SimpleDateFormat(timestampFormat);
    String format = s.format(new Date());
    txtViewTimestamp.setText(timestampText + format);
    txtViewTitle.setText(title);

    // Align TextViews
    txtViewTimestamp.setGravity(Gravity.CENTER_HORIZONTAL);
    txtViewTimestamp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    txtViewTitle.setGravity(Gravity.CENTER_HORIZONTAL);
    txtViewTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    try {
      JSONArray array = new JSONArray(jsonBody);
      Integer viewTop = 0;
      for (int i = 0; i < array.length(); i++) {
        JSONObject row = array.getJSONObject(i);
        String text = row.getString("text");
        Integer fontSize = row.getInt("fontSize");
        String fontWeight = row.getString("fontWeight");
        String textAlignment = row.getString("textAlignment");

        // Set text properties and text
        TextView child = new TextView(reactContext);
        child.setId(View.generateViewId());
        Integer number = i;
        child.setText(text + number.toString());
        child.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        child.setGravity(getGravity(textAlignment));
        viewTop += fontSize + 5;
        child.measure(595, fontSize + 5);
        child.layout(0, viewTop, 595, 2000 - viewTop);
        child.setTypeface(null, getFontWeight(fontWeight));

        if (viewTop > 600) {
          viewTop = 0;
          arrView.add(view);
          view = inflater.inflate(R.layout.pdf_layout, null);
          view.measure(595, 842);
          view.layout(0, 0, 595, 842);
          body = view.findViewById(R.id.documentBody);

          // Set text properties and text
          child = new TextView(reactContext);
          child.setId(View.generateViewId());
          number = i;
          child.setText(text + number.toString());
          child.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
          child.setGravity(getGravity(textAlignment));
          viewTop += fontSize + 5;
          child.measure(595, fontSize + 5);
          child.layout(0, viewTop, 595, 2000 - viewTop);
          child.setTypeface(null, getFontWeight(fontWeight));
        }

        body.addView(child);
      }
      arrView.add(view);
    } catch (Exception e) {
      Log.e(LOG_TAG, e.getMessage());
      return false;
    }

    s = new SimpleDateFormat("yyyyMMddhhmmss");
    String fileNameTS = fileName + s.format(new Date());
    File path = getPublicAlbumStorageDir(albumName);

    return writeFileOnDisk(path, fileNameTS, arrView, shareText);
  }

  private boolean writeFileOnDisk(File path, String fileName, ArrayList<View> arrView, String shareText) {

    try {
      File file = new File(path, fileName + ".pdf");
      FileOutputStream f = new FileOutputStream(file);

      PrintedPdfDocument document = new PrintedPdfDocument(reactContext, getPrintAttributes());

      PdfDocument.Page page;
      Integer pageNumber = 1;
      for (View content : arrView) {
        page = document.startPage(pageNumber);
        content.draw(page.getCanvas());
        document.finishPage(page);
        document.writeTo(f);
      }

      f.close();
      document.close();

      MediaScannerConnection.scanFile(reactContext, new String[] { file.getAbsolutePath() }, null, null);
      Toast.makeText(reactContext, fileName + ".pdf", Toast.LENGTH_SHORT).show();
      return shareFile(file.getPath(), shareText);

    } catch (Exception e) {
      Log.e(LOG_TAG, e.getMessage());
      return false;
    }
  }

  private boolean shareFile(String filePath, String shareText) {

    try {
      Intent intentShareFile = new Intent(Intent.ACTION_SEND);
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());

      intentShareFile.setType("application/pdf");
      intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));

      intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
      intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

      reactContext.startActivity(Intent.createChooser(intentShareFile, shareText));

      return true;
    } catch (Exception e) {
      Log.e(LOG_TAG, e.getMessage());
      return false;
    }
  }

  private PrintAttributes getPrintAttributes() {
    PrintAttributes.Builder builder = new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(new PrintAttributes.Resolution("res1", "Resolution", 72, 72))
        .setMinMargins(new PrintAttributes.Margins(20, 20, 20, 20));
    PrintAttributes printAttributes = builder.build();
    return printAttributes;
  }

  private File getPublicAlbumStorageDir(String albumName) {
    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), albumName);
    if (file.exists()) {
      Log.i(LOG_TAG, "Directory already exists");
    } else {
      if (!file.mkdirs()) {
        Log.e(LOG_TAG, "Directory not created");
      }
    }

    return file;
  }

  private Integer getGravity(String value) {
    switch (value) {
    case "left": {
      return Gravity.LEFT;
    }
    case "right": {
      return Gravity.RIGHT;
    }
    default: {
      return Gravity.CENTER_HORIZONTAL;
    }
    }
  }

  private Integer getFontWeight(String value) {
    switch (value) {
    case "bold": {
      return Typeface.BOLD;
    }
    case "bolditalic": {
      return Typeface.BOLD_ITALIC;
    }
    case "italic": {
      return Typeface.ITALIC;
    }
    default: {
      return Typeface.NORMAL;
    }
    }
  }

}