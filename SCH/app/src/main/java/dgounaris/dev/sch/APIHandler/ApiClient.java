package dgounaris.dev.sch.APIHandler;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import dgounaris.dev.sch.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DimitrisLPC on 3/8/2017.
 */

public class ApiClient {

    private static final String BASE_HOSTNAME = "10.0.2.2";
    public static final String BASE_URL = "http://10.0.2.2:3003";
    public static final String BASE_SECURE_URL = "https://10.0.2.2:8433";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            /*try {
                OkHttpClient okHttp = new OkHttpClient.Builder().sslSocketFactory(getSSLConfig(context).getSocketFactory()).hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return BASE_HOSTNAME.equalsIgnoreCase(hostname);
                    }
                }).build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_SECURE_URL)
                        .client(okHttp)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d("RETROFITURL", "Secure url");
            } catch (Exception e) {*/
                //if couldnt set certified connection, try simple http
                //Log.d("ERRORRETROFIT", e.toString());
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d("RETROFITURL", "NOT secure url");
            //}
        }
        return retrofit;
    }

    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509", "BC");

        Certificate ca, chain;
        try (InputStream cert = context.getResources().openRawResource(R.raw.wincycle_client)) {
            ca = cf.generateCertificate(cert);
        }
        Log.d("PASTFIRST", "past first");
        try (InputStream cert = context.getResources().openRawResource(R.raw.ca_chain)){
            chain = cf.generateCertificate(cert);
        }
        byte[] clientkey = InStreamToBytes(context.getResources().openRawResource(R.raw.wincycle_client_key));

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("clientcert", ca);
        keyStore.setKeyEntry("clientkey", clientkey, new Certificate[] {chain});

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

    private static byte[] InStreamToBytes(InputStream inputStream)
    {
        byte[] bytes= null;

        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte data[] = new byte[1024];
            int count;

            while ((count = inputStream.read(data)) != -1)
            {
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();
            inputStream.close();

            bytes = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bytes;
    }

}
