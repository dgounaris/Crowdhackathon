package dgounaris.dev.sch.layout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.APIHandler.ApiClient;
import dgounaris.dev.sch.APIHandler.ApiInterface;
import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.Trophies.Trophy;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.bitmap;

/**
 * Created by DimitrisLPC on 20/5/2017.
 */

public class register_fragment extends Fragment {

    int RESULT_LOAD_IMG = 1;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    EditText username;
    EditText password;
    EditText password_reenter;
    EditText name;
    EditText surname;
    Button choosephotobtn;
    Button registerbtn;
    Bitmap profileimageselected;
    ImageView photochosen;

    public register_fragment() {
        // Required empty public constructor
    }

    public static register_fragment newInstance() {
        register_fragment fragment = new register_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_fragment, container, false);
        profileimageselected = BitmapFactory.decodeResource(getResources(), R.drawable.profile_material);
        username = (EditText) view.findViewById(R.id.username_editable);
        password = (EditText) view.findViewById(R.id.password_editable);
        password_reenter = (EditText) view.findViewById(R.id.password_reenter_editable);
        name = (EditText) view.findViewById(R.id.name_editable);
        surname = (EditText) view.findViewById(R.id.surname_editable);
        photochosen = (ImageView) view.findViewById(R.id.photo_chosen);
        photochosen.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.profile_material));
        choosephotobtn = (Button) view.findViewById(R.id.choose_photo_button);
        choosephotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        registerbtn = (Button) view.findViewById(R.id.register_button);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().equals(password_reenter.getText().toString())) {
                    Toast.makeText(getContext(), "The passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    File imgFile = File.createTempFile(System.currentTimeMillis() + "", ".png", getContext().getCacheDir());
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(imgFile));
                    profileimageselected.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", imgFile.getName(), reqFile);
                    Call<Person> registerCall = apiService.registerPerson(username.getText().toString(), password.getText().toString(), name.getText().toString(), surname.getText().toString(), body);
                    registerCall.enqueue(new Callback<Person>() {
                        @Override
                        public void onResponse(Call<Person> call, Response<Person> response) {
                            if (response.code()>=500) {
                                Toast.makeText(getContext(), "Couldn't reach server. Try again later.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (response.code()>=400) {
                                Toast.makeText(getContext(), "Bad input provided. Try again later.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Person person = response.body();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("activeperson", person);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Person> call, Throwable t) {
                            Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            private DialogInterface dialog;
            private int item;

            @Override
            public void onClick(DialogInterface dialog, int item) {
                this.dialog = dialog;
                this.item = item;
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        /*try {
            File destination = File.createTempFile(System.currentTimeMillis() + ".png", null, getContext().getCacheDir());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(destination));
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return thumbnail;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap myImage = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG && null != data) {
                try {
                    Uri selectedImage = data.getData();
                    InputStream iStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(iStream, false);
                    myImage = decoder.decodeRegion(new Rect(0,0,400,400), new BitmapFactory.Options());
                } catch (IOException ioe) {
                    //do sth
                }
            } else if (requestCode == REQUEST_CAMERA)
                myImage = onCaptureImageResult(data);
        }
        if (myImage != null) {
            profileimageselected = myImage;
            photochosen.setImageBitmap(profileimageselected);
        }
    }

}
