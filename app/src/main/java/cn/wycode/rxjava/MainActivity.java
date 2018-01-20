package cn.wycode.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

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
import io.reactivex.functions.Consumer;
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
        Observable.create((ObservableOnSubscribe<String>) emitter -> emitter.onNext(httpRequest("https://wycode.cn/web/api/public/hello?message=Hello"))) //1.
                .map(s -> JSON.parseObject(s,Message.class)) //解析JSON
                .subscribeOn(Schedulers.io()) //2.
                .observeOn(AndroidSchedulers.mainThread()) //3.
                .subscribe(message -> textView.setText(message.message)); //4.
    }

    private String httpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String result = null;
        try {
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }

    private String readStream(BufferedInputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
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

    public static class Message{
        public String message;
    }
}
