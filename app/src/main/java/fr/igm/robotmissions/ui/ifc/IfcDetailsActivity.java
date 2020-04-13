package fr.igm.robotmissions.ui.ifc;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.utils.SimpleApiCallback;
import fr.igm.robotmissions.objects.utils.Utils;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.api.IfcApi;
import io.swagger.client.model.Ifc;

public class IfcDetailsActivity extends AppCompatActivity {


    public static final String EXTRA_IFC = "IFCEXTRA";
    private static final int PICKFILE_REQUEST_CODE = 8718;

    private EditText ifcNameEditTextView;
    private TextView ifcNameTextView;

    private View lastUploadView;

    private Ifc ifc;

    private Button loadFileButton;
    private FloatingActionButton editButton;
    private FloatingActionButton saveButton;

    private boolean isEditing;
    private boolean isCreating;
    private File selectedFile;

    private View selectedFileView;
    private TextView selectedFileNameTextView;

    private IfcApi ifcApi = new IfcApi();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifc_details);

        ifc = (Ifc) getIntent().getSerializableExtra(EXTRA_IFC);
        ifcNameTextView = findViewById(R.id.ifc_name_detail_text);
        ifcNameEditTextView = findViewById(R.id.ifc_name_edit_text);

        lastUploadView = findViewById(R.id.ifc_last_upload_linear_layout);
        TextView ifcLastUploadTextView = findViewById(R.id.ifc_detail_last_upload_text);

        editButton = findViewById(R.id.ifc_edit_button);
        saveButton = findViewById(R.id.ifc_save_button);
        saveButton.setOnClickListener((_v) -> {
            try {
                saveIfc();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });
        selectedFileView = findViewById(R.id.ifc_selected_file);
        selectedFileNameTextView = findViewById(R.id.ifc_file_selected_name);

        ImageButton clearSelectedFileButton = findViewById(R.id.ifc_clear_selected_file_btn);
        clearSelectedFileButton.setOnClickListener((_v) -> clearSelectedFile());
        loadFileButton = findViewById(R.id.ifc_load_file_button);

        IfcView ifcView = findViewById(R.id.ifc_ifc_view);
        isCreating = ifc == null;
        isEditing = false;
        if (isCreating) {
            editIfc();
            ifcView.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            findViewById(R.id.ifc_last_upload_linear_layout).setVisibility(View.GONE);
            return;
        }

        ifcView.setIfc(ifc);
        saveButton.setOnClickListener((_v) -> {
            try {
                saveIfc();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });

        ifcNameTextView.setText(ifc.getName());
        ifcNameEditTextView.setText(ifc.getName());
        ifcLastUploadTextView.setText(Utils.formatDateString(ifc.getLastUpload()));
    }

    private void clearSelectedFile() {
        selectedFile = null;
        selectedFileView.setVisibility(View.GONE);
        loadFileButton.setVisibility(View.VISIBLE);
    }

    private void saveIfc() throws ApiException {
        ApiCallback<Ifc> callback = new SimpleApiCallback<Ifc>() {
            @Override
            public void onSuccess(Ifc result, int statusCode, Map<String, List<String>> responseHeaders) {
                mainHandler.post(() -> {
                    setResult(Activity.RESULT_OK); // we have created or modified
                    finish();
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_IFC, result);
                    startActivity(intent);
                });
            }
        };

        String name = ifcNameEditTextView.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this,
                    getResources().getString(R.string.name_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCreating) {
            if (selectedFile == null) {
                Toast.makeText(this,
                        getResources().getString(R.string.ifc_need_to_select_file),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            ifcApi.postIfcAsync(name, selectedFile, callback);
        } else {
            ifcApi.updateIfcAsync(ifc.getId(), name, selectedFile, callback);
        }

    }

    public void floatingButtonClick(View view) {
        if (isEditing) {
            clearSelectedFile();
            stopEditing();
        } else {
            editIfc();
        }
    }

    private void stopEditing() {
        isEditing = false;
        editButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_24dp));
        saveButton.setVisibility(View.GONE);
        ifcNameEditTextView.setVisibility(View.GONE);
        ifcNameTextView.setVisibility(View.VISIBLE);
        loadFileButton.setVisibility(View.GONE);
        lastUploadView.setVisibility(View.VISIBLE);
    }

    private void editIfc() {
        isEditing = true;
        lastUploadView.setVisibility(View.GONE);
        editButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp));
        saveButton.setVisibility(View.VISIBLE);
        ifcNameEditTextView.setVisibility(View.VISIBLE);
        ifcNameTextView.setVisibility(View.GONE);
        loadFileButton.setVisibility(View.VISIBLE);

    }

    public void loadIfcFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();

            String fileName = getFileName(fileUri);
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!fileExt.equals("ifc")) {
                Toast.makeText(this, getResources().getString(R.string.ifc_file_not_ifc), Toast.LENGTH_SHORT).show();
                return;
            }
            selectedFile = getFile(fileUri);
            selectedFileNameTextView.setText(fileName);
            selectedFileView.setVisibility(View.VISIBLE);
            loadFileButton.setVisibility(View.GONE);
        }
    }

    private File getFile(Uri fileUri) {
        File file = new File(getCacheDir(), "tmpIfcfile.ifc");
        try (OutputStream output = new FileOutputStream(file)) {
            InputStream input = getContentResolver().openInputStream(fileUri);
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
