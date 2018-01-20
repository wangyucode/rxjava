package cn.wycode.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
    }

    public void onClick(View v) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                emitter.onNext(httpRequest());

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("wy","onSubscribe-->"+d.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        textView.setText(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("wy","onError",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("wy","onComplete");
                    }
                });
    }

    private String httpRequest() throws Exception {
        URL url = new URL("http://wycode.cn/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String result;
        try {
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }

    private String readStream(BufferedInputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
